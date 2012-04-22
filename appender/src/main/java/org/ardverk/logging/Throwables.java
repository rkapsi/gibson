package org.ardverk.logging;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

class Throwables {

  public static class Serializer extends JsonSerializer<Throwable> {

    @Override
    public Class<Throwable> handledType() {
      return Throwable.class;
    }

    @Override
    public void serialize(Throwable value, JsonGenerator jgen,
        SerializerProvider provider) throws IOException,
        JsonProcessingException {
    }
  }
  
  public static class Deserializer extends JsonDeserializer<Throwable> {

    @Override
    public Throwable deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      return null;
    }
  }
}
