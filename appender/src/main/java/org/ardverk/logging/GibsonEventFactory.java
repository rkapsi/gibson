package org.ardverk.logging;

import org.ardverk.logging.GibsonEvent.Level;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;

class GibsonEventFactory {
  
  public static GibsonEvent valueOf(ILoggingEvent evt) {
    GibsonEvent event = new GibsonEvent();
    
    event.setKey(GibsonUtils.createKey());
    event.setCreationTime(System.currentTimeMillis());
    
    event.setThreadName(evt.getThreadName());
    event.setLoggerName(evt.getLoggerName());
    
    event.setLevel(toLevel(evt));
    event.setMarker(toMarker(evt));
    event.setMdc(evt.getMDCPropertyMap());
    
    event.setMessage(evt.getMessage());
    event.setThrowable(toThrowable(evt));
    
    event.setSignature(GibsonUtils.createSignature(event));
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
  
  private static GibsonThrowable toThrowable(ILoggingEvent evt) {
    ThrowableProxy proxy = (ThrowableProxy)evt.getThrowableProxy();
    if (proxy != null) {
      return GibsonThrowable.valueOf(proxy.getThrowable());
    }
    return null;
  }
  
  private GibsonEventFactory() {}
}
