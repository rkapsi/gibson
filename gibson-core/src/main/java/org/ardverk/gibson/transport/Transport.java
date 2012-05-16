package org.ardverk.gibson.transport;

import java.io.Closeable;
import java.io.IOException;

import org.ardverk.gibson.Event;

/**
 * The {@link Transport} interface defines a simple mechanism to send message(s)
 * to a remote host.
 */
public interface Transport extends Closeable {

  /**
   * Returns {@code true} if the {@link Transport} is connected.
   */
  public boolean isConnected();

  /**
   * Connects the {@link Transport} to some (possibly remote) endpoint.
   */
  public void connect() throws IOException;

  /**
   * Sends the given message.
   */
  public void send(Event event);
}
