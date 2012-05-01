package org.ardverk.riak;

import com.basho.riak.client.cap.DefaultRetrier;
import com.basho.riak.client.cap.Retrier;
import com.basho.riak.client.raw.RawClient;

/**
 * Default implementation of {@link ArdverkRiakClient}.
 */
public class DefaultArdverkRiakClient extends AbstractArdverkRiakClient {

  private static final Retrier RETRIER = DefaultRetrier.attempts(3);
  
  protected final RawClient client;
  
  protected final Retrier retrier;
  
  public DefaultArdverkRiakClient(RawClient client) {
    this(client, RETRIER);
  }
  
  public DefaultArdverkRiakClient(RawClient client, Retrier retrier) {
    this.client = client;
    this.retrier = retrier;
  }

  @Override
  public RawClient getClient() {
    return client;
  }
  
  @Override
  public Retrier getRetrier() {
    return retrier;
  }
}
