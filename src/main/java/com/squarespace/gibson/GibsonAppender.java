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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * 
 */
public class GibsonAppender extends AppenderBase<ILoggingEvent> {
  
  public static final String TAG_PROPERTY = "gibson-tag";
  
  public static final String UNINITIALIZED_TAG = GibsonAppender.class.getName() + ".UNINITIALIZED_TAG";
  
  private static final Console LOG = Console.getLogger(GibsonAppender.class);
  
  @Override
  public void start() {
    
    
    super.start();
  }

  @Override
  public void stop() {
    super.stop();

  }

  @Override
  protected void append(ILoggingEvent evt) {
    // Make sure we're never entering a recursion if there are any problems with the appender.
    try {
      //process(evt);
    } catch (Exception err) {
      LOG.error(Gibson.MARKER, "Exception", err);
    }
  }
  
  /*private void process(ILoggingEvent evt) {
    // Skip LoggingEvents that don't have a StackTrace
    IThrowableProxy proxy = evt.getThrowableProxy();
    if (proxy == null) {
      return;
    }
    
    Marker marker = evt.getMarker();
    if (marker != null) {
      // Skip LoggingEvents that don't have a matching Marker
      if (markers != null && !markers.contains(marker.getName())) {
        return;
      }
      
      // Skip LoggingEvents that originate from Gibson itself.
      if (marker.equals(Gibson.MARKER)) {
        return;
      }
    }
    
    Transport transport = this.transport;
    if (transport != null && transport.isConnected()) {
      
      Event event = EventFactory.createEvent(evt, getTag());
      if (event != null) {
        transport.send(event);
      }
    }
  }*/
}
