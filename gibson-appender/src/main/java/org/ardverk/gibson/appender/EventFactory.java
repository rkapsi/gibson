package org.ardverk.gibson.appender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.ardverk.gibson.Condition;
import org.ardverk.gibson.Event;
import org.ardverk.gibson.EventUtils;
import org.ardverk.gibson.Level;
import org.bson.types.ObjectId;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;

/**
 * This factory turns {@link ILoggingEvent}s into {@link Event}.
 */
class EventFactory {
  
  public static Event createEvent(ILoggingEvent evt) {
    Event event = new Event();
    
    event.setId(ObjectId.get());
    event.setCreationTime(new Date());
    
    event.setThread(evt.getThreadName());
    event.setLogger(evt.getLoggerName());
    
    event.setLevel(toLevel(evt));
    event.setMarker(toMarker(evt));
    event.setMdc(evt.getMDCPropertyMap());
    
    event.setMessage(evt.getMessage());
    event.setCondition(toCondition(evt));
    
    if (evt.hasCallerData()) {
      StackTraceElement[] elements = evt.getCallerData();
      if (elements != null && elements.length >= 1) {
        event.setCallerData(Arrays.asList(elements));
      }
    }
    
    event.setSignature(EventUtils.signature(event));
    event.setKeywords(new ArrayList<String>(EventUtils.keywords(event)));
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
  
  private static Condition toCondition(ILoggingEvent evt) {
    ThrowableProxy proxy = (ThrowableProxy)evt.getThrowableProxy();
    if (proxy != null) {
      return Condition.valueOf(proxy.getThrowable());
    }
    return null;
  }
  
  private EventFactory() {}
}
