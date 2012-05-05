package org.ardverk.gibson.dashboard;

import org.ardverk.logging.GibsonEvent;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.RiakFactory;
import com.basho.riak.client.RiakRetryFailedException;
import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.bucket.DomainBucket;
import com.basho.riak.client.cap.DefaultRetrier;
import com.basho.riak.client.cap.Retrier;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class RiakModule extends AbstractModule {
  
  private static final String URL = "http://10.0.10.215:8098/riak";
  
  private static final Retrier RETRIER = DefaultRetrier.attempts(1);
  
  private static final String BUCKET_NAME = "slf4j";
  
  private static final int NVAL = 1;
  
  private static final int R = 1;
  
  private static final int W = 1;
  
  private static final int DW = 0;
  
  @Override
  protected void configure() {
  }
  
  @Provides
  IRiakClient getRiakClient() throws RiakException {
    return RiakFactory.httpClient(URL);
  }
  
  @Provides
  Bucket getBucket(IRiakClient client) throws RiakRetryFailedException {
    return client.createBucket(BUCKET_NAME)
      .withRetrier(RETRIER)
      .nVal(NVAL)
      .execute();
  }
  
  @Provides
  DomainBucket<GibsonEvent> getDomainBucket(IRiakClient client, Bucket bucket) throws RiakException {
    return DomainBucket.builder(bucket, GibsonEvent.class)
        .retrier(RETRIER)
        .r(R)
        .w(W)
        .dw(DW)
        .build();
  }
}
