package org.ardverk.logging;

import static org.ardverk.logging.GibsonUtils.MAPPER;

import java.io.IOException;

import junit.framework.TestCase;

import org.ardverk.logging.GibsonEvent.Level;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;

public class GibsonModuleTest {

  @Test
  public void json() throws JsonGenerationException, JsonMappingException, IOException {
    GibsonEvent event = new GibsonEvent();
    event.setCreationTime(System.currentTimeMillis());
    event.setLoggerName(GibsonModuleTest.class.getName());
    event.setLevel(Level.ERROR);
    event.setThreadName(Thread.currentThread().getName());
    
    event.setMessage("Hello World");
    event.setThrowable(GibsonThrowable.valueOf(new IllegalStateException()));
    
    Signature.sign(event);
    
    // Serialize
    String json1 = MAPPER.writeValueAsString(event);
    
    // Deserialize
    GibsonEvent other = MAPPER.readValue(json1, GibsonEvent.class);
    
    // Serialize again and they should be the same
    String json2 = MAPPER.writeValueAsString(other);
    TestCase.assertEquals(json1, json2);
  }
}
