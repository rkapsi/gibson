package org.ardverk.logging;

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
    
    return new IllegalStateException(message, new IllegalStateException("cause"));
  }
}
