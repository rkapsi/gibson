package org.ardverk.logging;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;

public class GibsonModule extends SimpleModule {

  private static final String NAME = "GibsonModule";
  
  private static final Version VERSION = new Version(1, 0, 0, null);
  
  private static final String CLASS_NAME = "className";
  
  private static final String METHOD_NAME = "methodName";
  
  private static final String FILE_NAME = "fileName";
  
  private static final String LINE_NUMBER = "lineNumber";
  
  public GibsonModule() {
    super(NAME, VERSION);
    
    addSerializer(new Serializer());
    addDeserializer(StackTraceElement.class, new Deserializer());
  }
  
  private static class Serializer extends JsonSerializer<StackTraceElement> {

    @Override
    public Class<StackTraceElement> handledType() {
      return StackTraceElement.class;
    }

    @Override
    public void serialize(StackTraceElement element, JsonGenerator jgen,
        SerializerProvider provider) throws IOException,
        JsonProcessingException {
      
      jgen.writeStartObject();
      jgen.writeObjectField(CLASS_NAME, element.getClassName());
      jgen.writeObjectField(METHOD_NAME, element.getMethodName());
      jgen.writeObjectField(FILE_NAME, element.getFileName());
      jgen.writeObjectField(LINE_NUMBER, element.getLineNumber());
      jgen.writeEndObject();
    }
  }
  
  private static class Deserializer extends JsonDeserializer<StackTraceElement> {
    @Override
    public StackTraceElement deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      
      JsonNode node = jp.readValueAsTree();
      String className = asText(node, CLASS_NAME);
      String methodName = asText(node, METHOD_NAME);
      String fileName = asText(node, FILE_NAME);
      int lineNumber = node.get(LINE_NUMBER).asInt();
      
      return new StackTraceElement(className, methodName, fileName, lineNumber);
    }
    
    private static String asText(JsonNode node, String key) {
      JsonNode value = node.get(key);
      return value != null ? value.asText() : null;
    }
  }
}
