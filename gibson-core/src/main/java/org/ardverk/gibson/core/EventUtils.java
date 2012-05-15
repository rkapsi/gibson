package org.ardverk.gibson.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public class EventUtils {

  private static final String ALGORITHM = "MD5";
  
  private static final byte[] NULL = { 'n', 'u', 'l', 'l' };
  
  private static final int MIN_KEYWORD_LENGTH = 3;
  
  public static List<String> keywords(String value) {
    List<String> keywords = new ArrayList<String>();
    keywords(value, keywords);
    return keywords;
  }

  public static List<String> keywords(Event event) {
    if (event == null) {
      throw new NullPointerException("event");
    }
    
    List<String> keywords = new ArrayList<String>();
    
    String message = event.getMessage();
    if (message != null) {
      keywords(message, keywords);
    }
    
    Condition condition = event.getCondition();
    if (condition != null) {
      keywords(condition, keywords);
    }
    
    return !keywords.isEmpty() ? keywords : null;
  }
  
  private static void keywords(Condition condition, List<String> dst) {
    String message = condition.getMessage();
    if (message != null) {
      keywords(message, dst);
    }
    
    Condition cause = condition.getCause();
    if (cause != null) {
      keywords(cause, dst);
    }
  }
  
  private static void keywords(String value, List<String> dst) {
    int length = value.length();
    StringBuilder sb = new StringBuilder(length);
    
    for (int i = 0; i < length; i++) {
      char ch = value.charAt(i);
      if (!Character.isLetterOrDigit(ch)) {
        if (sb.length() >= MIN_KEYWORD_LENGTH) {
          dst.add(sb.toString());
        }
        
        sb.setLength(0);
        continue;
      }
      
      sb.append(Character.toLowerCase(ch));
    }
    
    if (sb.length() >= MIN_KEYWORD_LENGTH) {
      dst.add(sb.toString());
    }
  }
  
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
  
  private EventUtils() {}
  
  private static void append(MessageDigest md, Condition condition) {
    if (condition != null) {
      append(md, condition.getTypeName());
      append(md, condition.getStackTrace());
      append(md, condition.getCause());
    }
  }
  
  private static void append(MessageDigest md, List<? extends StackTraceElement> elements) {
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
