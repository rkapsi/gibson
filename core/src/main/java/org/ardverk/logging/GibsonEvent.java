package org.ardverk.logging;

import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.basho.riak.client.convert.RiakKey;

@JsonSerialize(include = Inclusion.NON_NULL)
public class GibsonEvent {
  
  public static enum Level {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR;
  }
  
  @RiakKey
  private String key;
  
  private long creationTime;
  
  private String logger;
  
  private String marker;
  
  private Level level;
  
  private String threadName;
  
  private String message;
  
  private GibsonThrowable throwable;
  
  private StackTraceElement[] callerData;
  
  private Map<String, String> mdc;
  
  private String signature;
  
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public long getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(long creationTime) {
    this.creationTime = creationTime;
  }

  public String getLogger() {
    return logger;
  }

  public void setLogger(String logger) {
    this.logger = logger;
  }

  public String getMarker() {
    return marker;
  }

  public void setMarker(String marker) {
    this.marker = marker;
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }
  
  public String getThreadName() {
    return threadName;
  }

  public void setThreadName(String threadName) {
    this.threadName = threadName;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public GibsonThrowable getThrowable() {
    return throwable;
  }

  public void setThrowable(GibsonThrowable throwable) {
    this.throwable = throwable;
  }
  
  public StackTraceElement[] getCallerData() {
    return callerData;
  }

  public void setCallerData(StackTraceElement[] callerData) {
    this.callerData = callerData;
  }

  public Map<String, String> getMdc() {
    return mdc;
  }

  public void setMdc(Map<String, String> mdc) {
    this.mdc = mdc;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("l=").append(logger)
      .append(", s=").append(signature);
    
    if (throwable != null) {
      sb.append(", t=").append(throwable.toStringValue());
    }
    
    return sb.toString();
  }
}
