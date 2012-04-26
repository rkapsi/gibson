package org.ardverk.logging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.RiakFactory;
import com.basho.riak.client.bucket.Bucket;

public class RiakTransport extends AbstractTransport {
  
  private static final Logger LOG = LoggerFactory.getLogger(RiakTransport.class);
  
  private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactory() {
    
    private final AtomicInteger counter = new AtomicInteger();
    
    @Override
    public Thread newThread(Runnable r) {
      Thread thread = new Thread(r, "RiakTransportThread-" + counter.getAndIncrement());
      thread.setDaemon(true);
      return thread;
    }
  });
  
  private static final int DEFAULT_PORT = 8098;
  
  private final String bucketName = "slf4j";
  
  private final int r = 1;
  
  private final int w = 1;
  
  private final int n = 1;
  
  @Override
  public boolean isConnected() {
    return false;
  }

  @Override
  public void connect(final SocketAddress address) throws IOException {
    
    String host = ((InetSocketAddress)address).getHostName();
    if (host == null) {
      host = "localhost";
    }
    
    int port = ((InetSocketAddress)address).getPort();
    if (port == -1) {
      port = DEFAULT_PORT;
    }
    
    String url = "http://" + host + ":" + port + "/riak";
    
    if (LOG.isInfoEnabled()) {
      LOG.info("Connecting to: " + url);
    }
    
    try {
      final IRiakClient client = RiakFactory.httpClient(url);
      final Bucket bucket = client.createBucket(bucketName).r(r).w(w).nVal(n).execute();
      
      Runnable task = new Runnable() {
        @Override
        public void run() {
          try {
            process(bucket);
          } finally {
            client.shutdown();
          }
        }
      };
    } catch (RiakException err) {
      throw new IOException("RiakException", err);
    }
  }
  
  private void process(Bucket bucket) {
    
  }

  @Override
  public void send(GibsonEvent event) throws IOException {
  }

  @Override
  public void close() throws IOException {
  }
}
