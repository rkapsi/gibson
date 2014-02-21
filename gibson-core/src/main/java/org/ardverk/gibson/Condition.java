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

package org.ardverk.gibson;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.mongodb.morphia.annotations.Embedded;

/**
 * A {@link Condition} is simply a JSON serializable version of a {@link Throwable}.
 */
@Embedded
public class Condition {

  private static final Console LOG = Console.getLogger(Condition.class);
  
  private static final Method STACK_TRACE = getOurStackTrace();
  
  public static Condition valueOf(Throwable throwable) {
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
  
  private List<StackTraceElement> stackTrace;
  
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

  public List<StackTraceElement> getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(List<StackTraceElement> stackTrace) {
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
  
  private void printStackTraceAsCause(StringBuilder sb, List<? extends StackTraceElement> causedTrace) {
    // Compute number of frames in common between this and caused
    int m = stackTrace.size() - 1, n = causedTrace.size() - 1;
    while (m >= 0 && n >= 0 && stackTrace.get(m).equals(causedTrace.get(n))) {
      m--;
      n--;
    }
    int framesInCommon = stackTrace.size() - 1 - m;

    sb.append("Caused by: ").append(this).append("\n");
    
    for (int i = 0; i <= m; i++) {
      sb.append("\tat ").append(stackTrace.get(i)).append("\n");
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
}
