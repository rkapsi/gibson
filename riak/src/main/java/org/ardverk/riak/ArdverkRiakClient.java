package org.ardverk.riak;

import java.io.Closeable;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.cap.Retrier;
import com.basho.riak.client.raw.RawClient;

public interface ArdverkRiakClient extends IRiakClient, Closeable {

  /**
   * Returns the underlying Riak {@link RawClient}
   */
  public RawClient getClient();
  
  /**
   * Returns the underlying Riak {@link Retrier}
   */
  public Retrier getRetrier();
}
