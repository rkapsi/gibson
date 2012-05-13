package org.ardverk.gibson.appender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.ardverk.gibson.core.DatastoreFactory;
import org.ardverk.gibson.core.Event;

import com.google.code.morphia.Datastore;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

class MongoTransport implements Transport {
  
  private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(
      new TransportThreadFactory(MongoTransport.class));
  
  public static final int PORT = 8098;
  
  private List<Event> queue = new ArrayList<Event>();
  
  private final Console console;
  
  private final String database;
  
  private boolean open = true;
  
  private boolean connected = false;
  
  private Future<?> future = null;
  
  public MongoTransport(Console console, String database) {
    this.console = console;
    this.database = database;
  }

  @Override
  public synchronized boolean isConnected() {
    return connected;
  }

  @Override
  public synchronized void connect(MongoURI uri) throws IOException {
    if (!open) {
      throw new IOException("closed");
    }
    
    if (connected) {
      throw new IOException("connected");
    }
    
    if (console.isInfoEnabled()) {
      console.info("Connecting to: " + uri);
    }
    
    Mongo mongo = new Mongo(uri);
    
    DatastoreFactory factory = new DatastoreFactory(mongo);
    final Datastore datastore = factory.createDatastore(database);
    
    Runnable task = new Runnable() {
      @Override
      public void run() {
        try {
          process(datastore);
        } catch (InterruptedException err) {
          console.error("InterruptedException", err);
        } finally {
          destroy(datastore);
        }
      }
    };
    
    this.future = EXECUTOR.submit(task);
    this.connected = true;
  }
  
  private void destroy(Datastore datastore) {
    try {
      close();
    } finally {
      datastore.getMongo().close();
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
  
  private void process(Datastore datastore) throws InterruptedException {
    
    while (true) {
      
      List<Event> events = null;
      
      synchronized (this) {
        while (open && queue.isEmpty()) {
          wait();
        }
        
        if (!open) {
          break;
        }
        
        events = queue;
        queue = new ArrayList<Event>();
      }
      
      try {
        datastore.save(events);
      } catch (Exception err) {
        console.error("Exception", err);
      }
    }
  }
  
  @Override
  public void send(Event event) {
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
