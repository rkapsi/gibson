package org.ardverk.gibson.core;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexes;

@Embedded
@Indexes({
  @Index("typeName")
})
public class Condition {

  private static final Logger LOG = LoggerFactory.getLogger(Condition.class);
  
  private static final Method STACK_TRACE = getOurStackTrace();
  
  public static Condition valueOf(Throwable throwable) {
    if (throwable instanceof IgnorableException) {
      return null;
    }
    
    return create(throwable);
  }
  
  private static Condition create(Throwable throwable) {
    Condition condition = new Condition();
    
    condition.setTypeName(throwable.getClass().getName());
    condition.setMessage(throwable.getMessage());
    condition.setStackTrace(getStackTrace(throwable));
    
    Throwable cause = throwable.getCause();
    if (cause != null && cause != throwable) {
      condition.setCause(create(cause));
    }
    
    return condition;
  }
  
  private String typeName;
  
  private String message;
  
  private StackTraceElement[] stackTrace;
  
  private Condition cause;
  
  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String type) {
    this.typeName = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public StackTraceElement[] getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(StackTraceElement[] stackTrace) {
    this.stackTrace = stackTrace;
  }

  public Condition getCause() {
    return cause;
  }

  public void setCause(Condition cause) {
    this.cause = cause;
  }

  public void printStackTrace() {
    printStackTrace(System.err);
  }
  
  public void printStackTrace(PrintStream s) {
    s.println(toStringValue());
  }
  
  public void printStackTrace(PrintWriter pw) {
    pw.println(toStringValue());
  }
  
  public String toStringValue() {
    StringBuilder sb = new StringBuilder();
    
    sb.append(this).append("\n");
    
    for (StackTraceElement element : stackTrace) {
      sb.append("\tat ").append(element).append("\n");
    }
    
    if (cause != null) {
      cause.printStackTraceAsCause(sb, stackTrace);
    }
    
    return sb.toString();
  }
  
  private void printStackTraceAsCause(StringBuilder sb, StackTraceElement[] causedTrace) {
    // Compute number of frames in common between this and caused
    int m = stackTrace.length - 1, n = causedTrace.length - 1;
    while (m >= 0 && n >= 0 && stackTrace[m].equals(causedTrace[n])) {
      m--;
      n--;
    }
    int framesInCommon = stackTrace.length - 1 - m;

    sb.append("Caused by: ").append(this).append("\n");
    
    for (int i = 0; i <= m; i++) {
      sb.append("\tat ").append(stackTrace[i]).append("\n");
    }
    
    if (framesInCommon != 0) {
      sb.append("\t... ").append(framesInCommon).append(" more\n");
    }
    
    // Recurse if we have a cause
    if (cause != null) {
      cause.printStackTraceAsCause(sb, stackTrace);
    }
  }

  @Override
  public String toString() {
    return (message != null) ? (typeName + ": " + message) : typeName;
  }
  
  private static StackTraceElement[] getStackTrace(Throwable throwable) {
    if (STACK_TRACE != null) {
      try {
        return (StackTraceElement[])STACK_TRACE.invoke(throwable);
      } catch (IllegalAccessException err) {
        LOG.error("IllegalAccessException", new IgnorableException("IllegalAccessException", err));
      } catch (InvocationTargetException err) {
        LOG.error("InvocationTargetException", new IgnorableException("InvocationTargetException", err));
      }
    }
    
    return throwable.getStackTrace();
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
      LOG.error("NoSuchMethodException", new IgnorableException("NoSuchMethodException", err));
    }
    return method;
  }
}
