package org.ardverk.logging;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * An abstract implementation of {@link Transport}.
 */
abstract class AbstractTransport implements Transport {

  @Override
  public void connect(String host, int port) throws IOException {
    connect(new InetSocketAddress(host, port));
  }

  @Override
  public void connect(InetAddress addr, int port) throws IOException {
    connect(new InetSocketAddress(addr, port));
  }
}
