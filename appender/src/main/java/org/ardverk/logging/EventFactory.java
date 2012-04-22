package org.ardverk.logging;

import java.util.UUID;

import org.ardverk.logging.Event.Level;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class EventFactory {

  public static Event valueOf(ILoggingEvent evt) {
    Event event = new Event();
    
    event.setKey(UUID.randomUUID().toString());
    event.setCreationTime(System.currentTimeMillis());
    
    event.setThreadName(evt.getThreadName());
    event.setLoggerName(evt.getLoggerName());
    
    event.setLevel(toLevel(evt));
    event.setMarker(toMarker(evt));
    event.setMdc(evt.getMDCPropertyMap());
    
    event.setMessage(evt.getMessage());
    
    return event;
  }
  
  private static Level toLevel(ILoggingEvent evt) {
    ch.qos.logback.classic.Level lvl = evt.getLevel();
    
    if (lvl != null) {
      switch (lvl.toInt()) {
        case ch.qos.logback.classic.Level.TRACE_INT:
          return Level.TRACE;
        case ch.qos.logback.classic.Level.DEBUG_INT:
          return Level.DEBUG;
        case ch.qos.logback.classic.Level.INFO_INT:
          return Level.INFO;
        case ch.qos.logback.classic.Level.WARN_INT:
          return Level.WARN;
        case ch.qos.logback.classic.Level.ERROR_INT:
          return Level.ERROR;
      }
    }
    
    return null;
  }
  
  private static String toMarker(ILoggingEvent evt) {
    Marker marker = evt.getMarker();
    if (marker != null) {
      return marker.getName();
    }
    return null;
  }
  
  private EventFactory() {}
}
