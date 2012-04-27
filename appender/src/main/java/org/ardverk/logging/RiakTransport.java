package org.ardverk.logging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.RiakFactory;
import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.bucket.DomainBucket;
import com.basho.riak.client.cap.DefaultRetrier;

class RiakTransport extends AbstractTransport {
  
  private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(
      new SimpleThreadFactory("RiakTransportThread", true));
  
  private static final String URL = "http://%s:%s/riak";
  
  public static final int PORT = 8098;
  
  private List<EventTuple> queue = new ArrayList<EventTuple>();
  
  private final String bucketName;
  
  private final int r;
  
  private final int w;
  
  private final int nVal;
  
  private boolean open = true;
  
  private boolean connected = false;
  
  private Future<?> future = null;
  
  public RiakTransport(Logger logger, String bucketName, int r, int w, int nVal) {
    super(logger);
    
    this.bucketName = bucketName;
    this.r = r;
    this.w = w;
    this.nVal = nVal;
  }

  @Override
  public synchronized boolean isConnected() {
    return connected;
  }

  @Override
  public synchronized void connect(SocketAddress address) throws IOException {
    if (!open) {
      throw new IOException("closed");
    }
    
    if (connected) {
      throw new IOException("connected");
    }
    
    String url = toURL(address);
    
    logger.info("Connecting to: " + url);
    
    try {
      final IRiakClient client = RiakFactory.httpClient(url);
      
      Runnable task = new Runnable() {
        @Override
        public void run() {
          try {
            process(client);
          } catch (InterruptedException err) {
            logger.error("InterruptedException", err);
          } catch (RiakException err) {
            logger.error("RiakException", err);
          } finally {
            destroy(client);
          }
        }
      };
      
      this.future = EXECUTOR.submit(task);
      this.connected = true;
      
    } catch (RiakException err) {
      throw new IOException("RiakException", err);
    }
  }
  
  private void destroy(IRiakClient client) {
    close();
    client.shutdown();
  }
  
  @Override
  public synchronized void close() {
    if (open) {
      open = false;
      connected = false;
      
      if (future != null) {
        future.cancel(true);
      }
      
      notifyAll();
    }
  }
  
  private void process(IRiakClient client) throws RiakException, InterruptedException {
    
    Bucket bucket = client.createBucket(bucketName)
        .nVal(nVal)
        .execute();
    
    DomainBucket<EventTuple> domainBucket 
      = DomainBucket.builder(bucket, EventTuple.class)
      .withConverter(new EventTupleConverter(bucketName))
      .retrier(DefaultRetrier.attempts(1))
      .r(r)
      .w(w)
      .build();
    
    while (true) {
      
      List<EventTuple> tuples = null;
      
      synchronized (this) {
        while (open && queue.isEmpty()) {
          wait();
        }
        
        if (!open) {
          break;
        }
        
        tuples = queue;
        queue = new ArrayList<EventTuple>();
      }
      
      try {
        for (EventTuple tuple : tuples) {
            domainBucket.store(tuple);
        }
      } catch (RiakException err) {
        logger.error("RiakException", err);
      }
    }
  }

  @Override
  public void send(EventTuple tuple) {
    if (tuple == null) {
      throw new NullPointerException("tuple");
    }
    
    synchronized (this) {
      if (open && connected) {
        queue.add(tuple);
        notifyAll();
      }
    }
  }

  private static String toURL(SocketAddress address) {
    InetSocketAddress isa = (InetSocketAddress)address;
    
    String hostname = isa.getHostName();
    if (hostname == null) {
      hostname = "localhost";
    }
    
    int port = isa.getPort();
    if (port == -1) {
      port = PORT;
    }
    
    return String.format(URL, hostname, port);
  }
}
