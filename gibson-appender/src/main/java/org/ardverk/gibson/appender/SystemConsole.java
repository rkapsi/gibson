package org.ardverk.gibson.appender;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import ch.qos.logback.core.AppenderBase;

/**
 * An appender can't use {@link Logger}s. I would cause an infinite recursion. Logback has 
 * {@link AppenderBase#addInfo(String)} and so on methods but it's not very useful. This
 * simple console class writes stuff simple to {@link System#out} and {@link System#err}.
 */
class SystemConsole implements Console {

  private static final boolean INFO = Boolean.parseBoolean(System.getProperty("SystemConsole.INFO", "true"));
  private static final boolean ERROR = Boolean.parseBoolean(System.getProperty("SystemConsole.ERROR", "true"));
  
  @Override
  public boolean isInfoEnabled() {
    return INFO;
  }
  
  @Override
  public boolean isErrorEnabled() {
    return ERROR;
  }
  
  @Override
  public void info(String message) {
    if (INFO) {
      System.out.println(message);
    }
  }
  
  @Override
  public void error(String message) {
    error(message, null);
  }
  
  @Override
  public void error(String message, Throwable t) {
    if (!ERROR) {
      return;
    }
    
    if (t == null) {
      System.err.println(message);
      return;
    }
    
    StringWriter sw = new StringWriter();
    try {
      sw.write(message);
      sw.write('\n');
      PrintWriter pw = new PrintWriter(sw);
      try {
        t.printStackTrace(pw);
      } finally {
        IOUtils.closeQuietly(pw);
      }
    } finally {
      IOUtils.closeQuietly(sw);
    }
    
    System.err.println(sw.toString());
  }
}
