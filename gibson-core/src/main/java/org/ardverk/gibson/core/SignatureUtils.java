package org.ardverk.gibson.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public class SignatureUtils {

  private static final String ALGORITHM = "MD5";
  
  private static final byte[] NULL = { 'n', 'u', 'l', 'l' };
  
  public static String signature(Event event) {
    if (event == null) {
      throw new NullPointerException("event");
    }
    
    MessageDigest md = createMessageDigest(ALGORITHM);
    
    append(md, event.getLogger());
    append(md, event.getMarker());
    append(md, event.getLevel());
    append(md, event.getCondition());
    
    return Base64.encodeBase64URLSafeString(md.digest());
  }
  
  private SignatureUtils() {}
  
  private static void append(MessageDigest md, Condition condition) {
    if (condition != null) {
      append(md, condition.getTypeName());
      append(md, condition.getStackTrace());
      append(md, condition.getCause());
    }
  }
  
  private static void append(MessageDigest md, StackTraceElement[] elements) {
    if (elements != null) {
      for (StackTraceElement element : elements) {
        append(md, element);
      }
    }
  }
  
  private static void append(MessageDigest md, StackTraceElement element) {
    if (element != null) {
      append(md, element.getClassName());
      append(md, element.getMethodName());
      append(md, element.getFileName());
      append(md, element.getLineNumber());
    }
  }
  
  private static void append(MessageDigest md, Enum<?> value) {
    append(md, value.getClass());
    append(md, value.name());
  }
  
  private static void append(MessageDigest md, Class<?> value) {
    append(md, value.getName());
  }
  
  private static void append(MessageDigest md, String value) {
    append(md, StringUtils.getBytesUtf8(value));
  }
  
  private static void append(MessageDigest md, int value) {
    md.update((byte)(value >>> 24));
    md.update((byte)(value >>> 16));
    md.update((byte)(value >>>  8));
    md.update((byte)(value       ));
  }
  
  private static void append(MessageDigest md, byte[] value) {
    md.update(value != null ? value : NULL);
  }
  
  private static MessageDigest createMessageDigest(String algorithm) {
    try {
      return MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException err) {
      throw new IgnorableException("NoSuchAlgorithmException: " + algorithm, err);
    }
  }
}
