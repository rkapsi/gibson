package org.ardverk.logging;

import static org.ardverk.logging.GibsonUtils.MAPPER;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import com.basho.riak.client.IRiakObject;
import com.basho.riak.client.builders.RiakObjectBuilder;
import com.basho.riak.client.cap.VClock;
import com.basho.riak.client.convert.ConversionException;
import com.basho.riak.client.convert.Converter;
import com.basho.riak.client.convert.NoKeySpecifedException;
import com.basho.riak.client.http.util.Constants;

public class GibsonEventConverter implements Converter<GibsonEvent> {
  
  private final ObjectMapper mapper;
  
  private final String bucket;
  
  public GibsonEventConverter(String bucket) {
    this(MAPPER, bucket);
  }
  
  public GibsonEventConverter(ObjectMapper mapper, String bucket) {
    this.mapper = mapper;
    this.bucket = bucket;
  }
  
  @Override
  public IRiakObject fromDomain(GibsonEvent event, VClock vclock) throws ConversionException {
    String key = event.getKey();
    if (key == null) {
        throw new NoKeySpecifedException(event);
    }
    
    try {
      byte[] data = mapper.writeValueAsBytes(event);
      
      return RiakObjectBuilder.newBuilder(bucket, key)
          .withValue(data)
          .withVClock(vclock)
          .withContentType(Constants.CTYPE_JSON_UTF8)
          .build();
    } catch (IOException err) {
      throw new GibsonEventConversionException("IOException", err);
    }
  }

  @Override
  public GibsonEvent toDomain(IRiakObject riakObject) throws ConversionException {
    if (riakObject == null) {
      return null;
    }
    
    try {
      GibsonEvent event = mapper.readValue(riakObject.getValue(), GibsonEvent.class);
      event.setKey(riakObject.getKey());
      return event;
    } catch (IOException err) {
      throw new GibsonEventConversionException("IOException", err);
    }
  }
}
