/*
 * Copyright 2012 Will Benedict, Felix Berger and Roger Kapsi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.ardverk.gibson.appender;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    event.setKeywords(new KeywordsList<String>(EventUtils.keywords(event)));
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
  
  /**
   * The keywords are technically speaking a {@link Set} of strings but we want to treat it like a {@link List}.
   * Instead of making full copy of the {@link Set} we're simply wrapping it into this fake {@link List} class
   * which is sufficient for serialization purposes.
   */
  private static class KeywordsList<T> extends AbstractList<T> {
    
    private final Set<T> keywords;

    public KeywordsList(Set<T> keywords) {
      this.keywords = keywords;
    }

    @Override
    public T get(int index) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
      return keywords.iterator();
    }

    @Override
    public int size() {
      return keywords.size();
    }
  }
}
