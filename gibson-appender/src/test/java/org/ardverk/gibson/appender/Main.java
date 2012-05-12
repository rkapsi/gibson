package org.ardverk.gibson.appender;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);
  
  public static void main(String[] args) throws InterruptedException {
    while (true) {
      LOG.error("Hello", createThrowable("Exception", 5));
      Thread.sleep(1000);
    }
  }
  
  private static Throwable createThrowable(String message, int stack) {
    if (0 < stack) {
      return createThrowable(message, --stack);
    }
    
    double rnd = Math.random();
    
    if (rnd < 0.25) {
      return new IOException(message, new IllegalStateException("cause"));
    } else if (rnd < 0.50) {
      return new NullPointerException(message);
    } else if (rnd < 0.75) {
      return new NumberFormatException(message);
    }
    
    return new IllegalStateException(message, new IllegalStateException("cause"));
  }
}
