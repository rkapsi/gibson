package org.ardverk.logging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;

/**
 * The {@link RiakAppender} sends {@link EventTuple}s to Riak.
 */
public class RiakAppender extends AppenderBase<ILoggingEvent> {
  
  private static final InetSocketAddress ENDPOINT = new InetSocketAddress("localhost", RiakTransport.PORT);
  
  private final TransportStatus status = new TransportStatus();
  
  private volatile SocketAddress endpoint = ENDPOINT;
  
  private volatile Transport transport = null;
  
  private volatile String bucket = null;
  
  private volatile Set<String> markers = null;
  
  private volatile boolean hasStackTrace = true;
  
  private volatile int r = 1;
  
  private volatile int w = 1;
  
  private volatile int dw = 0;
  
  private volatile int nVal = 1;
  
  @Override
  public void start() {
    if (endpoint == null) {
      addError("Endpoint is not defined");
      return;
    }

    if (bucket == null) {
      addError("Bucket name is not defined");
      return;
    }
    
    transport = new RiakTransport(status, bucket, r, w, dw, nVal);

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
  
  // Called from logback.xml
  public void setBucket(String bucket) {
    this.bucket = bucket;
  }
  
  // Called from logback.xml
  public void setMarkers(String markers) {
    if (markers != null) {
      String[] tokens = markers.split(",");
      
      Set<String> dst = new HashSet<String>();
      for (String token : tokens) {
        if ((token = trimToNull(token)) != null) {
          dst.add(token);
        }
      }
      
      if (!dst.isEmpty()) {
        this.markers = dst;
      }
    }
  }

  // Called from logback.xml
  public void setEndpoint(String endpoint) {
    this.endpoint = parse(endpoint, false);
  }

  @Override
  protected void append(ILoggingEvent event) {
    Transport transport = this.transport;
    
    if (transport != null && transport.isConnected()) {
      
      // Skip LoggingEvents that don't have a StackTrace
      if (hasStackTrace) {
        IThrowableProxy proxy = event.getThrowableProxy();
        if (proxy == null) {
          return;
        }
      }
      
      // Skip LoggingEvents that don't have a matching Marker
      if (markers != null) {
        Marker marker = event.getMarker();
        if (marker != null && markers.contains(marker.getName())) {
          return;
        }
      }
      
      GibsonEvent ge = Factory.valueOf(event);
      if (ge != null) {
        transport.send(ge);
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
  
  private static String trimToNull(String value) {
    if (value != null) {
      if (!(value = value.trim()).isEmpty()) {
        return value;
      }
    }
    return null;
  }
  
  private class TransportStatus implements Transport.Status {

    @Override
    public boolean isInfoEnabled() {
      return true;
    }

    @Override
    public boolean isErrorEnabled() {
      return true;
    }

    @Override
    public void info(String message) {
      addInfo(message);
    }

    @Override
    public void error(String message, Throwable t) {
      addError(message, t);
    }
  }
}
