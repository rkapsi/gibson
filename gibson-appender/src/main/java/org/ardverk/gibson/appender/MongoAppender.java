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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.ardverk.gibson.Console;
import org.ardverk.gibson.Event;
import org.ardverk.gibson.Gibson;
import org.ardverk.gibson.transport.MongoTransport;
import org.ardverk.gibson.transport.Transport;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;

import com.mongodb.MongoURI;

/**
 * The {@link MongoAppender} sends {@link Event}s to MongoDB.
 */
public class MongoAppender extends AppenderBase<ILoggingEvent> {
  
  public static final String TAG_PROPERTY = "gibson-tag";
  
  public static final String UNINITIALIZED_TAG = MongoAppender.class.getName() + "__gibson-uninitilized-tag__";
  
  private static final Console LOG = Console.getLogger(MongoAppender.class);
    
  private volatile MongoURI uri = Gibson.URI;
  
  private volatile String tag = UNINITIALIZED_TAG;
  
  private volatile Transport transport = null;
  
  private volatile Set<String> markers = null;
  
  @Override
  public void start() {
    if (uri == null) {
      LOG.error(Gibson.MARKER, "Endpoint is not defined");
      return;
    }
    
    transport = new MongoTransport(uri);
    
    try {
      transport.connect();
    } catch (Exception err) {
      if (LOG.isErrorEnabled()) {
        LOG.error(Gibson.MARKER, "Failed to connect: " + uri, err);
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
      } catch (Exception err) {
        LOG.error(Gibson.MARKER, "Exception", err);
      }
    }
  }
  
  // Called from logback.xml
  public void setUri(String uri) {
    this.uri = new MongoURI(uri);
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
  }
  
  private String getTag() {
    if (tag == UNINITIALIZED_TAG) {
      synchronized (this) {
        if (tag == UNINITIALIZED_TAG) {
          tag = getContext().getProperty(TAG_PROPERTY);
        }
      }
    }
    
    return tag;
  }
}
