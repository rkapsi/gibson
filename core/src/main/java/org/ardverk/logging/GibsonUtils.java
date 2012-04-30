package org.ardverk.logging;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;
import org.codehaus.jackson.map.ObjectMapper;

public class GibsonUtils {

  private static final SecureRandom GENERATOR = new SecureRandom();
  
  public static final ObjectMapper MAPPER = new ObjectMapper();
  
  static {
    MAPPER.registerModule(new GibsonModule());
  }
  
  public static String createKey() {
    byte[] value = new byte[20];
    GENERATOR.nextBytes(value);
    return Hex.encodeHexString(value);
  }
  
  private GibsonUtils() {}
}
