package org.ardverk.gibson.appender;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ardverk.gibson.Event;
import org.ardverk.gibson.Gibson;
import org.ardverk.gibson.transport.MongoTransport;
import org.ardverk.gibson.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;

import com.mongodb.MongoURI;

/**
 * The {@link MongoAppender} sends {@link Event}s to MongoDB.
 */
public class MongoAppender extends AppenderBase<ILoggingEvent> {
  
  private static final Logger LOG = LoggerFactory.getLogger(MongoAppender.class);
  
  private volatile MongoURI endpoint = Gibson.ENDPOINT;
  
  private volatile String database = Gibson.DATABASE;
  
  private volatile Transport transport = null;
  
  private volatile Set<String> markers = null;
  
  @Override
  public void start() {
    if (endpoint == null) {
      LOG.error(Gibson.MARKER, "Endpoint is not defined");
      return;
    }

    if (database == null) {
      LOG.error(Gibson.MARKER, "Database name is not defined");
      return;
    }
    
    transport = new MongoTransport(endpoint, database);
    
    try {
      transport.connect();
    } catch (IOException err) {
      if (LOG.isErrorEnabled()) {
        LOG.error(Gibson.MARKER, "Failed to connect: " + endpoint, err);
      }
      return;
    }
    
    super.start();
  }

  @Override
  public void stop() {
    super.stop();

    Transport transport = this.transport;
    if (transport != null) {
      try {
        transport.close();
      } catch (IOException err) {
        LOG.error(Gibson.MARKER, "IOException", err);
      }
    }
  }
  
  // Called from logback.xml
  public void setEndpoint(String endpoint) {
    this.endpoint = new MongoURI(endpoint);
  }
 
  // Called from logback.xml
  public void setDatabase(String database) {
    this.database = database;
  }
  
  // Called from logback.xml
  public void setMarkers(String markers) {
    if (markers != null) {
      String[] tokens = markers.split(",");
      
      Set<String> dst = new HashSet<String>();
      for (String token : tokens) {
        if ((token = StringUtils.trimToNull(token)) != null) {
          dst.add(token);
        }
      }
      
      if (!dst.isEmpty()) {
        this.markers = dst;
      }
    }
  }

  @Override
  protected void append(ILoggingEvent evt) {
    // Make sure we're never entering a recursion if there are any problems with the appender.
    try {
      process(evt);
    } catch (Exception err) {
      LOG.error(Gibson.MARKER, "Exception", err);
    }
  }
  
  private void process(ILoggingEvent evt) {
    // Skip LoggingEvents that don't have a StackTrace
    IThrowableProxy proxy = evt.getThrowableProxy();
    if (proxy == null) {
      return;
    }
    
    Marker marker = evt.getMarker();
    if (marker != null) {
      // Skip LoggingEvents that don't have a matching Marker
      if (markers != null && markers.contains(marker.getName())) {
        return;
      }
      
      // Skip LoggingEvents that originate from Gibson itself.
      if (marker.equals(Gibson.MARKER)) {
        return;
      }
    }
    
    Transport transport = this.transport;
    if (transport != null && transport.isConnected()) {
      
      Event event = EventFactory.createEvent(evt);
      if (event != null) {
        transport.send(event);
      }
    }
  }
}
