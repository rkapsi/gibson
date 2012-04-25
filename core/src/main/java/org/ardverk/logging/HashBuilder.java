package org.ardverk.logging;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public class HashBuilder {
  
  private static final String ALGORITHM = "MD5";
  
  private static final byte[] NULL = { 'n', 'u', 'l', 'l' };
  
  public static String valueOf(GibsonEvent event) {
    HashBuilder fp = new HashBuilder();
    
    if (event != null) {
      fp.append(event.getLoggerName());
      fp.append(event.getMarker());
      fp.append(event.getLevel());
      fp.append(event.getThrowable());
    }
    
    return fp.toString();
  }
  
  private final MessageDigest md = createMessageDigest();
  
  private HashBuilder() {}
  
  private HashBuilder append(GibsonThrowable throwable) {
    if (throwable != null) {
      append(throwable.getClassName());
      append(throwable.getStackTrace());
      append(throwable.getCause());
    }
    
    return this;
  }
  
  private HashBuilder append(StackTraceElement[] elements) {
    if (elements != null) {
      for (StackTraceElement element : elements) {
        append(element);
      }
    }
    return this;
  }
  
  private HashBuilder append(StackTraceElement element) {
    if (element != null) {
      append(element.getClassName());
      append(element.getMethodName());
      append(element.getFileName());
      append(element.getLineNumber());
    }
    return this;
  }
  
  private HashBuilder append(Enum<?> value) {
    if (value != null) {
      return append(value.getClass().getName()).append(value.name());
    }
    
    return append(NULL).append(NULL);
  }
  
  private HashBuilder append(String value) {
    return append(StringUtils.getBytesUtf16(value));
  }
  
  private HashBuilder append(int value) {
    md.update((byte)(value >>> 24));
    md.update((byte)(value >>> 16));
    md.update((byte)(value >>>  8));
    md.update((byte)(value       ));
    return this;
  }
  
  private HashBuilder append(byte[] value) {
    md.update(value != null ? value : NULL);
    return this;
  }
  
  @Override
  public String toString() {
    byte[] value = Base64.encodeBase64(md.digest(), false, true);
    return StringUtils.newStringIso8859_1(value);
  }
  
  private static MessageDigest createMessageDigest() {
    try {
      return MessageDigest.getInstance(ALGORITHM);
    } catch (NoSuchAlgorithmException err) {
      throw new NonSerializableException("NoSuchAlgorithmException: " + ALGORITHM, err);
    }
  }
}
