package org.ardverk.riak;

import com.basho.riak.client.cap.DefaultRetrier;
import com.basho.riak.client.cap.Retrier;
import com.basho.riak.client.raw.RawClient;

public class DefaultArdverkRiakClient extends AbstractArdverkRiakClient {

  protected final RawClient client;
  
  protected final Retrier retrier;
  
  public DefaultArdverkRiakClient(RawClient client) {
    this(client, DefaultRetrier.attempts(3));
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
