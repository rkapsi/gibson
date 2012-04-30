package org.ardverk.logging;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public class SignatureUtils {

  public static GibsonEvent sign(GibsonEvent event) {
    if (event == null) {
      throw new NullPointerException("event");
    }
    
    Signature signature = new Signature();
    
    signature.append(event.getLoggerName());
    signature.append(event.getMarker());
    signature.append(event.getLevel());
    signature.append(event.getThrowable());
    
    event.setSignature(signature.toString());
    return event;
  }
  
  private SignatureUtils() {}
  
  private static class Signature {
    
    private static final String ALGORITHM = "MD5";
    
    private static final byte[] NULL = { 'n', 'u', 'l', 'l' };
    
    private final MessageDigest md = createMessageDigest();
    
    private Signature() {}
    
    private Signature append(GibsonThrowable throwable) {
      if (throwable != null) {
        append(throwable.getClassName());
        append(throwable.getStackTrace());
        append(throwable.getCause());
      }
      
      return this;
    }
    
    private Signature append(StackTraceElement[] elements) {
      if (elements != null) {
        for (StackTraceElement element : elements) {
          append(element);
        }
      }
      return this;
    }
    
    private Signature append(StackTraceElement element) {
      if (element != null) {
        append(element.getClassName());
        append(element.getMethodName());
        append(element.getFileName());
        append(element.getLineNumber());
      }
      return this;
    }
    
    private Signature append(Enum<?> value) {
      if (value != null) {
        return append(value.getClass().getName()).append(value.name());
      }
      
      return append(NULL).append(NULL);
    }
    
    private Signature append(String value) {
      return append(StringUtils.getBytesUtf16(value));
    }
    
    private Signature append(int value) {
      md.update((byte)(value >>> 24));
      md.update((byte)(value >>> 16));
      md.update((byte)(value >>>  8));
      md.update((byte)(value       ));
      return this;
    }
    
    private Signature append(byte[] value) {
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
        throw new IgnorableException("NoSuchAlgorithmException: " + ALGORITHM, err);
      }
    }
  }
}
