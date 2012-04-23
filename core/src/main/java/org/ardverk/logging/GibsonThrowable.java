package org.ardverk.logging;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GibsonThrowable {

  private static final Logger LOG = LoggerFactory.getLogger(GibsonThrowable.class);
  
  private static final Method STACK_TRACE = getOurStackTrace();
  
  public static GibsonThrowable valueOf(Throwable throwable) {
    if (throwable instanceof IgnorableGibsonException) {
      return null;
    }
    
    return create(throwable);
  }
  
  private static GibsonThrowable create(Throwable throwable) {
    GibsonThrowable proxy = new GibsonThrowable();
    
    proxy.setType(throwable.getClass().getName());
    proxy.setMessage(throwable.getMessage());
    proxy.setStackTrace(getStackTrace(throwable));
    
    Throwable cause = throwable.getCause();
    if (cause != null && cause != throwable) {
      proxy.setCause(create(cause));
    }
    
    return proxy;
  }
  
  private String type;
  
  private String message;
  
  @JsonSerialize(using=Serializer.class)
  @JsonDeserialize(using=Deserializer.class)
  private StackTraceElement[] stackTrace;
  
  private GibsonThrowable cause;
  
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public GibsonThrowable getCause() {
    return cause;
  }

  public void setCause(GibsonThrowable cause) {
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
    return (message != null) ? (type + ": " + message) : type;
  }
  
  private static StackTraceElement[] getStackTrace(Throwable throwable) {
    if (STACK_TRACE != null) {
      try {
        return (StackTraceElement[])STACK_TRACE.invoke(throwable);
      } catch (IllegalAccessException err) {
        LOG.error("IllegalAccessException", new IgnorableGibsonException("IllegalAccessException", err));
      } catch (InvocationTargetException err) {
        LOG.error("InvocationTargetException", new IgnorableGibsonException("InvocationTargetException", err));
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
      LOG.error("NoSuchMethodException", new IgnorableGibsonException("NoSuchMethodException", err));
    }
    return method;
  }
  
  public static class Serializer extends JsonSerializer<StackTraceElement> {

    @Override
    public Class<StackTraceElement> handledType() {
      return StackTraceElement.class;
    }

    @Override
    public void serialize(StackTraceElement value, JsonGenerator jgen,
        SerializerProvider provider) throws IOException,
        JsonProcessingException {
      
      jgen.writeStartObject();
      jgen.writeObjectField("className", value.getClassName());
      jgen.writeObjectField("methodName", value.getMethodName());
      jgen.writeObjectField("fileName", value.getFileName());
      jgen.writeObjectField("lineNumber", value.getLineNumber());
      jgen.writeEndObject();
    }
  }
  
  public static class Deserializer extends JsonDeserializer<StackTraceElement> {
    @Override
    public StackTraceElement deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      
      String className = jp.readValueAs(String.class);
      String methodName = jp.readValueAs(String.class);
      String fileName = jp.readValueAs(String.class);
      int lineNumber = jp.readValueAs(Integer.class);
      
      return new StackTraceElement(className, methodName, fileName, lineNumber);
    }
  }
  
  private static class IgnorableGibsonException extends IllegalStateException {
    
    private static final long serialVersionUID = 5248569070496124230L;

    public IgnorableGibsonException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
