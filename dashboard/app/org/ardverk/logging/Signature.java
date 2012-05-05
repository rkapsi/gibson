package org.ardverk.logging;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public class Signature {

  private static final String ALGORITHM = "MD5";
  
  private static final byte[] NULL = { 'n', 'u', 'l', 'l' };
  
  public static GibsonEvent sign(GibsonEvent event) {
    if (event == null) {
      throw new NullPointerException("event");
    }
    
    Signature signature = new Signature();
    
    signature.append(event.getLogger());
    signature.append(event.getMarker());
    signature.append(event.getLevel());
    signature.append(event.getThrowable());
    
    event.setSignature(signature.toString());
    return event;
  }
  
  private final MessageDigest md = createMessageDigest();
  
  private Signature() {}
  
  private void append(GibsonThrowable throwable) {
    if (throwable != null) {
      append(throwable.getClassName());
      append(throwable.getStackTrace());
      append(throwable.getCause());
    }
  }
  
  private void append(StackTraceElement[] elements) {
    if (elements != null) {
      for (StackTraceElement element : elements) {
        append(element);
      }
    }
  }
  
  private void append(StackTraceElement element) {
    if (element != null) {
      append(element.getClassName());
      append(element.getMethodName());
      append(element.getFileName());
      append(element.getLineNumber());
    }
  }
  
  private void append(Enum<?> value) {
    if (value != null) {
      append(value.getClass().getName());
      append(value.name());
    }
    
    append(NULL);
    append(NULL);
  }
  
  private void append(String value) {
    append(StringUtils.getBytesUtf16(value));
  }
  
  private void append(int value) {
    md.update((byte)(value >>> 24));
    md.update((byte)(value >>> 16));
    md.update((byte)(value >>>  8));
    md.update((byte)(value       ));
  }
  
  private void append(byte[] value) {
    md.update(value != null ? value : NULL);
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
      throw new IgnorableException("NoSuchAlgorithmException: " + ALGORITHM, err);
    }
  }
}
