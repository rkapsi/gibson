package org.ardverk.gibson.appender;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);
  
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
    "Builder"
  };
  
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
    while (true) {
      try {
        LOG.error(log(), createThrowable(msg(), 5 + GENERATOR.nextInt(10)));
      } catch (Exception err) {
        LOG.error("Excpetion", err);
      }
      Thread.sleep(50);
    }
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
