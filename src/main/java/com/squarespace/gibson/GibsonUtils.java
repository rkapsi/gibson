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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;

/**
 * An utility class for Gibson.
 */
class GibsonUtils {

  private static final Logger LOG = LoggerFactory.getLogger(GibsonUtils.class);
  
  /** Using MD5 for speed and it's sufficient for this use-case. */
  private static final String ALGORITHM = "MD5";
  
  private static final MessageDigestFactory FACTORY = newMessageDigestFactory(ALGORITHM);
  
  private static final byte[] NULL = { 'n', 'u', 'l', 'l' };

  private static final Method STACK_TRACE = getOurStackTrace();
  
  public static String signature(ILoggingEvent evt) {
    if (evt == null) {
      throw new NullPointerException("event");
    }
    
    MessageDigest md = FACTORY.newMessageDigest();
    
    append(md, evt.getLoggerName());
    append(md, getMarker(evt));
    append(md, getLevel(evt));
    
    ThrowableProxy proxy = (ThrowableProxy)evt.getThrowableProxy();
    if (proxy != null) {
      append(md, proxy.getThrowable());
    }
    
    return Base64.encodeBase64URLSafeString(md.digest());
  }
  
  private GibsonUtils() {}
  
  private static void append(MessageDigest md, Throwable throwable) {
    if (throwable != null) {
      append(md, throwable.getClass());
      append(md, getStackTrace(throwable));
      
      Throwable cause = throwable.getCause();
      if (cause != null && cause != throwable) {
        append(md, cause);
      }
    }
  }
  
  private static void append(MessageDigest md, List<? extends StackTraceElement> elements) {
    if (elements != null) {
      for (StackTraceElement element : elements) {
        append(md, element);
      }
    }
  }
  
  private static void append(MessageDigest md, StackTraceElement element) {
    if (element != null) {
      append(md, element.getClassName());
      append(md, element.getMethodName());
      append(md, element.getFileName());
      append(md, element.getLineNumber());
    }
  }
  
  private static void append(MessageDigest md, Class<?> value) {
    append(md, value.getName());
  }
  
  private static void append(MessageDigest md, String value) {
    append(md, StringUtils.getBytesUtf8(value));
  }
  
  private static void append(MessageDigest md, int value) {
    md.update((byte)(value >>> 24));
    md.update((byte)(value >>> 16));
    md.update((byte)(value >>>  8));
    md.update((byte)(value       ));
  }
  
  private static void append(MessageDigest md, byte[] value) {
    md.update(value != null ? value : NULL);
  }
  
  private static String getMarker(ILoggingEvent evt) {
    Marker marker = evt.getMarker();
    if (marker != null) {
      return marker.getName();
    }
    return null;
  }
  
  private static int getLevel(ILoggingEvent evt) {
    ch.qos.logback.classic.Level lvl = evt.getLevel();
    
    if (lvl != null) {
      return lvl.toInt();
    }
    
    return -1;
  }
  
  private static List<StackTraceElement> getStackTrace(Throwable throwable) {
    StackTraceElement[] elements = null;
    if (STACK_TRACE != null) {
      try {
        elements = (StackTraceElement[])STACK_TRACE.invoke(throwable);
      } catch (IllegalAccessException err) {
        LOG.error(Gibson.MARKER, "IllegalAccessException", err);
      } catch (InvocationTargetException err) {
        LOG.error(Gibson.MARKER, "InvocationTargetException", err);
      }
    }
    
    if (elements == null) {
      elements = throwable.getStackTrace();
    }
    
    if (elements != null) {
      return Arrays.asList(elements);
    }
    
    return null;
  }
  
  /**
   * {@link Throwable#getStackTrace()} returns a copy. We can try to use the getOurStackTrace()
   * method instead but it's a private.
   */
  private static Method getOurStackTrace() {
    Method method = null;
    try {
      method = Throwable.class.getDeclaredMethod("getOurStackTrace");
      method.setAccessible(true);
    } catch (NoSuchMethodException err) {
      LOG.error(Gibson.MARKER, "NoSuchMethodException", err);
    }
    return method;
  }
  
  /**
   * Creates and returns a {@link MessageDigestFactory} for the given algorithm.
   */
  private static MessageDigestFactory newMessageDigestFactory(final String algorithm) {
    
    final MessageDigest md = DigestUtils.getDigest(algorithm);
    
    return new MessageDigestFactory() {
      @Override
      public MessageDigest newMessageDigest() {
        // It is faster to clone a MessageDigest than to look it up via SPI.
        try {
          return (MessageDigest)md.clone();
        } catch (CloneNotSupportedException err) {
          LOG.warn(Gibson.MARKER, "CloneNotSupportedException: {}", algorithm, err);
        }
        
        return DigestUtils.getDigest(algorithm);
      }
    };
  }
  
  /**
   * A factory for {@link MessageDigest}s.
   */
  private static interface MessageDigestFactory {
    
    /**
     * Creates and returns a {@link MessageDigest}.
     */
    public MessageDigest newMessageDigest();
  }
}
