package org.ardverk.logging;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

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

class JsonConverter<T> implements Converter<T> {

  private final UsermetaConverter<T> usermeta = new UsermetaConverter<T>();
  private final RiakIndexConverter<T> riakIndex = new RiakIndexConverter<T>();
  private final RiakLinksConverter<T> riakLinks = new RiakLinksConverter<T>();
  
  protected final ObjectMapper mapper;
  
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
    
    Map<String, String> usermetaData = usermeta.getUsermetaData(domainObject);
    RiakIndexes indexes = riakIndex.getIndexes(domainObject);
    Collection<RiakLink> links = riakLinks.getLinks(domainObject);
    
    try {
      byte[] data = mapper.writeValueAsBytes(domainObject);
      
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
      
      usermeta.populateUsermeta(riakObject.getMeta(), domainObject);
      riakIndex.populateIndexes(
          new RiakIndexes(riakObject.allBinIndexes(), 
              riakObject.allIntIndexes()), domainObject);
      riakLinks.populateLinks(riakObject.getLinks(), domainObject);
      
      return domainObject;
    } catch (IOException err) {
      throw new JsonConversionException("IOException", err);
    }
  }
}
