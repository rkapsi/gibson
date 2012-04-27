package org.ardverk.logging;

import java.io.IOException;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class GibsonModuleTest {

  @Test
  public void json() throws JsonGenerationException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new GibsonModule());
    
    GibsonEvent event = new GibsonEvent();
    event.setCreationTime(System.currentTimeMillis());
    event.setLoggerName(GibsonModuleTest.class.getName());
    event.setLevel(Level.ERROR);
    event.setThreadName(Thread.currentThread().getName());
    
    event.setMessage("Hello World");
    event.setThrowable(GibsonThrowable.valueOf(new IllegalStateException()));
    
    event.setSignature(Signature.valueOf(event));
    
    // Serialize
    String json1 = mapper.writeValueAsString(event);
    
    // Deserialize
    GibsonEvent other = mapper.readValue(json1, GibsonEvent.class);
    
    // Serialize again and they should be the same
    String json2 = mapper.writeValueAsString(other);
    TestCase.assertEquals(json1, json2);
  }
}
