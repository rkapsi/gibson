package org.ardverk.logging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * The {@link RiakAppender} sends {@link EventTuple}s to Riak.
 */
public class RiakAppender extends AppenderBase<ILoggingEvent> {
  
  private final TransportLogger logger = new TransportLogger();
  
  private volatile SocketAddress endpoint = null;
  
  private volatile Transport transport = null;
  
  private volatile String bucketName = null;
  
  private volatile int r = 1;
  
  private volatile int w = 1;
  
  private volatile int nVal = 1;
  
  @Override
  public void start() {
    if (endpoint == null) {
      addError("Endpoint is not defined");
      return;
    }

    if (bucketName == null) {
      addError("Bucket name is not defined");
      return;
    }
    
    transport = new RiakTransport(logger, bucketName, r, w, nVal);

    try {
      transport.connect(endpoint);
    } catch (IOException err) {
      addError("Failed to bind: " + endpoint, err);
      return;
    }
    
    super.start();
  }

  @Override
  public void stop() {
    super.stop();

    if (transport != null) {
      try {
        transport.close();
      } catch (IOException err) {
        addError("IOException", err);
      }
    }
  }
  
  //Called from logback.xml
  public void setBucketName(String bucketName) {
    this.bucketName = bucketName;
  }

  // Called from logback.xml
  public void setEndpoint(String endpoint) {
    this.endpoint = parse(endpoint, false);
  }

  @Override
  protected void append(ILoggingEvent event) {
    if (transport != null && transport.isConnected()) {
      EventTuple tuple = EventTupleFactory.valueOf(event);
      if (tuple != null) {
        transport.send(tuple);
      }
    }
  }

  private static SocketAddress parse(String hostPort, boolean hostRequired) {
    int p = hostPort.indexOf(":");
    if (p == -1) {

      if (hostRequired) {
        throw new IllegalArgumentException("No hostname defined: " + hostPort);
      }

      return new InetSocketAddress(Integer.parseInt(hostPort.trim()));
    }

    String host = hostPort.substring(0, p).trim();
    int port = Integer.parseInt(hostPort.substring(++p).trim());
    return new InetSocketAddress(host, port);
  }
  
  /**
   * 
   */
  private class TransportLogger implements AbstractTransport.Logger {

    @Override
    public void info(String message) {
      addInfo(message);
    }

    @Override
    public void info(String message, Throwable t) {
      addInfo(message, t);
    }

    @Override
    public void error(String message) {
      addError(message);
    }

    @Override
    public void error(String message, Throwable t) {
      addError(message, t);
    }
  }
}
