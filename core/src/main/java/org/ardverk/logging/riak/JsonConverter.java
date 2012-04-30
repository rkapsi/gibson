package org.ardverk.logging.riak;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.ardverk.logging.GibsonModule;
import org.codehaus.jackson.map.ObjectMapper;

import com.basho.riak.client.IRiakObject;
import com.basho.riak.client.RiakLink;
import com.basho.riak.client.builders.RiakObjectBuilder;
import com.basho.riak.client.cap.VClock;
import com.basho.riak.client.convert.ConversionException;
import com.basho.riak.client.convert.Converter;
import com.basho.riak.client.convert.KeyUtil;
import com.basho.riak.client.convert.NoKeySpecifedException;
import com.basho.riak.client.convert.RiakIndexConverter;
import com.basho.riak.client.convert.RiakJacksonModule;
import com.basho.riak.client.convert.RiakLinksConverter;
import com.basho.riak.client.convert.UsermetaConverter;
import com.basho.riak.client.http.util.Constants;
import com.basho.riak.client.query.indexes.RiakIndexes;

public class JsonConverter<T> implements Converter<T> {
  
  private final UsermetaConverter<T> usermetaConverter = new UsermetaConverter<T>();
  private final RiakIndexConverter<T> riakIndexConverter = new RiakIndexConverter<T>();
  private final RiakLinksConverter<T> riakLinksConverter = new RiakLinksConverter<T>();
  
  private final ObjectMapper mapper;
  
  private final Class<T> clazz;
  
  private final String bucket;
  
  public JsonConverter(Class<T> clazz, String bucket) {
    this(new ObjectMapper(), clazz, bucket);
  }
  
  public JsonConverter(ObjectMapper mapper, Class<T> clazz, String bucket) {
    this.mapper = mapper;
    this.clazz = clazz;
    this.bucket = bucket;
    
    mapper.registerModule(new RiakJacksonModule());
    mapper.registerModule(new GibsonModule());
  }
  
  public ObjectMapper getObjectMapper() {
    return mapper;
  }
  
  @Override
  public IRiakObject fromDomain(T domainObject, VClock vclock) throws ConversionException {
    String key = KeyUtil.getKey(domainObject);
    
    if (key == null) {
        throw new NoKeySpecifedException(domainObject);
    }
    
    try {
      byte[] data = mapper.writeValueAsBytes(domainObject);
      
      Map<String, String> usermetaData = usermetaConverter.getUsermetaData(domainObject);
      RiakIndexes indexes = riakIndexConverter.getIndexes(domainObject);
      Collection<RiakLink> links = riakLinksConverter.getLinks(domainObject);
      
      return RiakObjectBuilder.newBuilder(bucket, key)
          .withValue(data)
          .withVClock(vclock)
          .withUsermeta(usermetaData)
          .withIndexes(indexes)
          .withLinks(links)
          .withContentType(Constants.CTYPE_JSON_UTF8)
          .build();
      
    } catch (IOException err) {
      throw new JsonConversionException("IOException", err);
    }
  }

  @Override
  public T toDomain(IRiakObject riakObject) throws ConversionException {
    if (riakObject == null) {
      return null;
    }
    
    try {
      T domainObject = mapper.readValue(riakObject.getValue(), clazz);
      KeyUtil.setKey(domainObject, riakObject.getKey());
      usermetaConverter.populateUsermeta(riakObject.getMeta(), domainObject);
      riakIndexConverter.populateIndexes(
          new RiakIndexes(riakObject.allBinIndexes(), 
              riakObject.allIntIndexes()), domainObject);
      riakLinksConverter.populateLinks(riakObject.getLinks(), domainObject);
      
      return domainObject;
    } catch (IOException err) {
      throw new JsonConversionException("IOException", err);
    }
  }
}
