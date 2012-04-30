package org.ardverk.logging;

import com.basho.riak.client.cap.DefaultRetrier;
import com.basho.riak.client.cap.Retrier;
import com.basho.riak.client.raw.RawClient;

public class DefaultRiakClient extends AbstractRiakClient {

  protected final RawClient client;
  
  protected final Retrier retrier;
  
  public DefaultRiakClient(RawClient client) {
    this(client, DefaultRetrier.attempts(3));
  }
  
  public DefaultRiakClient(RawClient client, Retrier retrier) {
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
