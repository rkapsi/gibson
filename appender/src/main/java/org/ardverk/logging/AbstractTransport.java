package org.ardverk.logging;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * An abstract implementation of {@link Transport}.
 */
abstract class AbstractTransport implements Transport {

  protected final Logger logger;
  
  public AbstractTransport(Logger logger) {
    this.logger = logger;
  }
  
  @Override
  public void connect(String host, int port) throws IOException {
    connect(new InetSocketAddress(host, port));
  }

  @Override
  public void connect(InetAddress addr, int port) throws IOException {
    connect(new InetSocketAddress(addr, port));
  }
  
  public static interface Logger {

    public void info(String message);
    
    public void info(String message, Throwable t);
    
    public void error(String message);
    
    public void error(String message, Throwable t);
  }
}
