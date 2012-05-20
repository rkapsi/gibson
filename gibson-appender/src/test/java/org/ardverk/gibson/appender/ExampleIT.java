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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ardverk.gibson.transport.MongoTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a simple test class that produces a lot of fake logging messages.
 */
public class ExampleIT {
  
  private static final Logger[] LOGGERS = {
    LoggerFactory.getLogger(Class.class),
    LoggerFactory.getLogger(System.class),
    LoggerFactory.getLogger(MongoTransport.class),
    LoggerFactory.getLogger(ClassLoader.class),
  };
  
  private static final Random GENERATOR = new Random();
  
  private static final Class<?>[] TYPES = {
    IOException.class,
    NullPointerException.class,
    IllegalArgumentException.class,
    IllegalStateException.class,
    UnsupportedOperationException.class,
    NoSuchMethodException.class,
    ClassNotFoundException.class,
    ArithmeticException.class,
    ArrayIndexOutOfBoundsException.class,
    IndexOutOfBoundsException.class,
  };
  
  private static final String[] MESSAGES = {
    "Abstract",
    "Provider",
    "State",
    "Bad",
    "User",
    "Factory",
    "Facy",
    "Builder",
    "Hello",
    "World",
    "test"
  };
  
  private static Logger logger() {
    return LOGGERS[GENERATOR.nextInt(LOGGERS.length)];
  }
  
  private static String log() {
    int count = 1 + GENERATOR.nextInt(4);
    return message(count);
  }
  
  private static String msg() {
    int count = 1 + GENERATOR.nextInt(2);
    return message(count);
  }
  
  private static String message(int count) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) {
      sb.append(MESSAGES[GENERATOR.nextInt(MESSAGES.length)]).append(' ');
    }
    return sb.toString().trim();
  }
  
  public static void main(String[] args) throws InterruptedException {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            logger().error(log(), createThrowable(msg(), 5 + GENERATOR.nextInt(10)));
          } catch (Exception err) {
            logger().error("Excpetion", err);
          }
          
          try { Thread.sleep(25); } catch (InterruptedException ignore) {}
        }
      }
    };
    
    ExecutorService executor = Executors.newCachedThreadPool();
    
    for (int i = 0; i < 4; i++) {
      executor.execute(task);
    }
    
    Thread.sleep(Long.MAX_VALUE);
  }
  
  private static Throwable createThrowable(String message, int stack) {
    if (0 < stack) {
      return createThrowable(message, --stack);
    }
    
    Throwable throwable = newThrowable(message);
    
    if (Math.random() < 0.25) {
      throwable.initCause(createThrowable(msg(), 5 + GENERATOR.nextInt(10)));
    }
    
    return throwable;
  }
  
  private static Throwable newThrowable(String message) {
    int index = GENERATOR.nextInt(TYPES.length);
    Class<?> clazz = TYPES[index];
    
    try {
      Constructor<?> constructor = clazz.getConstructor(String.class);
      return (Throwable)constructor.newInstance(message);
    } catch (Exception err) {
      return err;
    }
  }
}
