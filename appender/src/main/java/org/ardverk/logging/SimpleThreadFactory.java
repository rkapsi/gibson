package org.ardverk.logging;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class SimpleThreadFactory implements ThreadFactory {

  private final AtomicInteger counter = new AtomicInteger();
  
  private final String name;
  
  private final boolean daemon;
  
  public SimpleThreadFactory(String name) {
    this(name, false);
  }

  public SimpleThreadFactory(String name, boolean daemon) {
    this.name = name;
    this.daemon = daemon;
  }
  
  @Override
  public Thread newThread(Runnable r) {
    Thread thread = new Thread(r, name + "-" + counter.getAndIncrement());
    thread.setDaemon(daemon);
    return thread;
  }
}
