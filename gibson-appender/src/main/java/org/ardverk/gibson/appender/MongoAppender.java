package org.ardverk.gibson.appender;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ardverk.gibson.core.DatastoreFactory;
import org.ardverk.gibson.core.Event;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;

import com.mongodb.MongoURI;

/**
 * The {@link MongoAppender} sends {@link Event}s to MongoDB.
 */
public class MongoAppender extends AppenderBase<ILoggingEvent> {
  
  private final Console console = new Console();
  
  private volatile MongoURI endpoint = DatastoreFactory.ENDPOINT;
  
  private volatile String database = DatastoreFactory.DATABASE;
  
  private volatile Transport transport = null;
  
  private volatile Set<String> markers = null;
  
  @Override
  public void start() {
    if (endpoint == null) {
      console.error("Endpoint is not defined");
      return;
    }

    if (database == null) {
      console.error("Database name is not defined");
      return;
    }
    
    transport = new MongoTransport(console, database);
    
    try {
      transport.connect(endpoint);
    } catch (IOException err) {
      if (console.isErrorEnabled()) {
        console.error("Failed to connect: " + endpoint, err);
      }
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
        console.error("IOException", err);
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
    Transport transport = this.transport;
    
    if (transport != null && transport.isConnected()) {
      
      // Skip LoggingEvents that don't have a StackTrace
      IThrowableProxy proxy = evt.getThrowableProxy();
      if (proxy == null) {
        return;
      }
      
      // Skip LoggingEvents that don't have a matching Marker
      if (markers != null) {
        Marker marker = evt.getMarker();
        if (marker != null && markers.contains(marker.getName())) {
          return;
        }
      }
      
      Event event = EventFactory.createEvent(evt);
      if (event != null) {
        transport.send(event);
      }
    }
  }
}
