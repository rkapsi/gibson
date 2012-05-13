package org.ardverk.gibson.appender;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;

import org.ardverk.gibson.core.Event;


import com.mongodb.MongoURI;

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
   * Binds the {@link Transport} to the given {@link SocketAddress}.
   */
  public void connect(MongoURI uri) throws IOException;

  /**
   * Sends the given message.
   */
  public void send(Event event);
}
