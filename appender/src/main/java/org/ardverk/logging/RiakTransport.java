package org.ardverk.logging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.RiakFactory;
import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.bucket.DomainBucket;
import com.basho.riak.client.cap.DefaultRetrier;

class RiakTransport extends AbstractTransport {
  
  private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(
      new TransportThreadFactory(RiakTransport.class));
  
  private static final String URL = "http://%s:%s/riak";
  
  public static final int PORT = 8098;
  
  private List<GibsonEvent> queue = new ArrayList<GibsonEvent>();
  
  private final String bucketName;
  
  private final int r;
  
  private final int w;
  
  private final int dw;
  
  private final int nVal;
  
  private boolean open = true;
  
  private boolean connected = false;
  
  private Future<?> future = null;
  
  public RiakTransport(Status status, String bucketName, 
      int r, int w, int dw, int nVal) {
    super(status);
    
    this.bucketName = bucketName;
    this.r = r;
    this.w = w;
    this.dw = dw;
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
    
    if (status.isInfoEnabled()) {
      status.info("Connecting to: " + url);
    }
    
    try {
      final IRiakClient client = RiakFactory.httpClient(url);
      
      Runnable task = new Runnable() {
        @Override
        public void run() {
          try {
            process(client);
          } catch (InterruptedException err) {
            status.error("InterruptedException", err);
          } catch (RiakException err) {
            status.error("RiakException", err);
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
    try {
      close();
    } finally {
      client.shutdown();
    }
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
    
    DomainBucket<GibsonEvent> domainBucket 
      = DomainBucket.builder(bucket, GibsonEvent.class)
      .withConverter(new GibsonEventConverter(bucketName))
      .retrier(DefaultRetrier.attempts(1))
      .r(r)
      .w(w)
      .dw(dw)
      .build();
    
    while (true) {
      
      List<GibsonEvent> events = null;
      
      synchronized (this) {
        while (open && queue.isEmpty()) {
          wait();
        }
        
        if (!open) {
          break;
        }
        
        events = queue;
        queue = new ArrayList<GibsonEvent>();
      }
      
      try {
        for (GibsonEvent event : events) {
          domainBucket.store(event);
          
          /*String key = event.getKey();
          GibsonEvent value = domainBucket.fetch(key);
          System.out.println(key + " -> " + value);*/
        }
        
        /*int index = 0;
        for (String key : bucket.keys()) {
          System.out.println((index++ + ": ") + key);
        }*/
      } catch (Exception err) {
        status.error("Exception", err);
      }
    }
  }

  @Override
  public void send(GibsonEvent event) {
    if (event == null) {
      throw new NullPointerException("event");
    }
    
    synchronized (this) {
      if (open && connected) {
        queue.add(event);
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
  
  private static class TransportThreadFactory implements ThreadFactory {

    private final AtomicInteger counter = new AtomicInteger();
    
    private final String name;
    
    public TransportThreadFactory(Class<?> clazz) {
      this.name = clazz.getSimpleName() + "Thread";
    }
    
    @Override
    public Thread newThread(Runnable r) {
      Thread thread = new Thread(r, name + "-" + counter.getAndIncrement());
      thread.setDaemon(true);
      return thread;
    }
  }
}
