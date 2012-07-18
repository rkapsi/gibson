package org.ardverk.gibson;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Marker;

/**
 * A dummy logger to work around a chicken-egg problem. It outputs everything to {@link System#out} and {@link System#err}.
 */
public class Console {

  public static Console getLogger(Class<?> clazz) {
    return new Console(clazz != null ? clazz.getSimpleName() : null);
  }
  
  private final String name;
  
  private Console(String name) {
    this.name = name;
  }
  
  public boolean isInfoEnabled() {
    return true;
  }
  
  public void info(String message, Throwable t) {
    info(null, message, t);
  }
  
  public void info(Marker marker, String message) {
    info(marker, message, null);
  }
  
  public void info(Marker marker, String message, Throwable t) {
    if (isInfoEnabled()) {
      log(System.out, marker, message, t);
    }
  }
  
  public boolean isWarnEnabled() {
    return true;
  }
  
  public void warn(String message, Throwable t) {
    warn(null, message, t);
  }
  
  public void warn(Marker marker, String message) {
    warn(marker, message, null);
  }
  
  public void warn(Marker marker, String message, Throwable t) {
    if (isWarnEnabled()) {
      log(System.out, marker, message, t);
    }
  }
  
  public boolean isErrorEnabled() {
    return true;
  }
  
  public void error(String message, Throwable t) {
    error(null, message, t);
  }
  
  public void error(Marker marker, String message) {
    error(marker, message, null);
  }
  
  public void error(Marker marker, String message, Throwable t) {
    if (isErrorEnabled()) {
      log(System.err, marker, message, t);
    }
  }
  
  private void log(PrintStream stream, Marker marker, String message, Throwable t) {
    if (message != null || t != null) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      
      if (name != null) {
        pw.print(name);
        pw.print(" ");
      }
      
      if (message != null) {
        pw.println(message);
      }
      
      if (t != null) {
        t.printStackTrace(pw);
      }
      
      pw.close();
      stream.println(sw.toString());
    }
  }
}
