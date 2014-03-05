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

import org.slf4j.MDC;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;

/**
 * The {@link GibsonAppender} calculates the unique "signature" of a log statement
 * with an {@link Exception} and makes it available in the {@value Gibson#SIGNATURE}
 * {@link MDC} property.
 */
public class GibsonAppender extends AppenderBase<ILoggingEvent> {
  
  private static final Console LOG = Console.getLogger(GibsonAppender.class);

  private volatile Appender<ILoggingEvent> appender = null;
  
  @Override
  public void start() {
    Appender<ILoggingEvent> appender = this.appender;
    if (appender == null) {
      addError("There are no Appender(s) registered.");
      return;
    }
    
    appender.start();
    super.start();
  }

  @Override
  public void stop() {
    Appender<ILoggingEvent> appender = this.appender;
    if (appender != null) {
      appender.stop();
      this.appender = null;
    }
    
    super.stop();
  }

  @Override
  protected void append(ILoggingEvent evt) {
    Appender<ILoggingEvent> appender = this.appender;
    if (appender != null) {
      
      try {
        process(evt);
      } catch (Exception err) {
        LOG.error(Gibson.MARKER, "Exception", err);
      }
      
      appender.doAppend(evt);
    }
  }
  
  public void addAppender(Appender<ILoggingEvent> appender) {
    if (this.appender != null) {
      addError("It is not possible to have more than one Appender");
      return;
    }
    
    this.appender = appender;
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
