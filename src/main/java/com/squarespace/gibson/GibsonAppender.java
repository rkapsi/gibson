/*
 * Copyright 2012-2014 Will Benedict, Felix Berger, Roger Kapsi, Doug
 * Jones and Squarespace Inc.
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

package com.squarespace.gibson;

import java.util.Iterator;

import org.slf4j.MDC;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;

/**
 * The {@link GibsonAppender} calculates the unique "signature" of a log statement
 * with an {@link Exception} and makes it available in the {@value Gibson#SIGNATURE}
 * {@link MDC} property.
 */
public class GibsonAppender extends AppenderBase<ILoggingEvent> 
    implements AppenderAttachable<ILoggingEvent> {
  
  private static final Console LOG = Console.getLogger(GibsonAppender.class);

  private final AppenderAttachableImpl<ILoggingEvent> appenders = new AppenderAttachableImpl<>();
  
  private volatile int count = 0;
  
  @Override
  public void start() {
    for (Iterator<? extends Appender<?>> it 
          = appenders.iteratorForAppenders(); it.hasNext(); ) {
      it.next().start();
    }
    
    super.start();
  }

  @Override
  public void stop() {
    for (Iterator<? extends Appender<?>> it 
          = appenders.iteratorForAppenders(); it.hasNext(); ) {
      it.next().stop();
    }
    
    super.stop();
  }

  @Override
  protected void append(ILoggingEvent evt) {
    if (count >= 1) {
      try {
        process(evt);
      } catch (Exception err) {
        LOG.error(Gibson.MARKER, "Exception", err);
      }
    }
    
    appenders.appendLoopOnAppenders(evt);
  }
  
  @Override
  public void addAppender(Appender<ILoggingEvent> appender) {
    if (++count >= 2) {
      addWarn("There is already an Appender registered.");
    }
    
    appenders.addAppender(appender);
  }

  @Override
  public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
    return appenders.iteratorForAppenders();
  }

  @Override
  public Appender<ILoggingEvent> getAppender(String name) {
    return appenders.getAppender(name);
  }

  @Override
  public boolean isAttached(Appender<ILoggingEvent> appender) {
    return appenders.isAttached(appender);
  }

  @Override
  public void detachAndStopAllAppenders() {
    appenders.detachAndStopAllAppenders();
  }

  @Override
  public boolean detachAppender(Appender<ILoggingEvent> appender) {
    return appenders.detachAppender(appender);
  }

  @Override
  public boolean detachAppender(String name) {
    return appenders.detachAppender(name);
  }

  private void process(ILoggingEvent evt) {
    // Skip LoggingEvents that don't have a StackTrace
    IThrowableProxy proxy = evt.getThrowableProxy();
    if (proxy == null) {
      return;
    }
    
    // Skip LoggingEvents that originate from Gibson itself.
    Marker marker = evt.getMarker();
    if (marker != null && marker.equals(Gibson.MARKER)) {
      return;
    }
    
    String signature = GibsonUtils.signature(evt);
    if (signature != null) {
      MDC.put(Gibson.SIGNATURE, signature);
    }
  }
}
