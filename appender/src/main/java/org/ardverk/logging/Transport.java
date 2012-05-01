package org.ardverk.logging;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;

import org.slf4j.Logger;

/**
 * The {@link Transport} interface defines a simple mechanism to send message(s)
 * to a remote host.
 */
interface Transport extends Closeable {

  /**
   * Returns {@code true} if the {@link Transport} is connected.
   */
  public boolean isConnected();

  /**
   * Binds the {@link Transport} to the given host+port.
   */
  public void connect(String host, int port) throws IOException;

  /**
   * Binds the {@link Transport} to the given {@link InetAddress} and port.
   */
  public void connect(InetAddress addr, int port) throws IOException;

  /**
   * Binds the {@link Transport} to the given {@link SocketAddress}.
   */
  public void connect(SocketAddress address) throws IOException;

  /**
   * Sends the given message.
   */
  public void send(GibsonEvent event);
  
  /**
   * Appenders and their inner guts can't use {@link Logger}s (if a logger writes to an appender and
   * the appender in turn writes to a logger you're creating a infinite recursion).
   */
  public static interface Status {

    public boolean isInfoEnabled();
    
    public boolean isErrorEnabled();
    
    public void info(String message);
    
    public void error(String message, Throwable t);
  }
}
