package org.ardverk.logging;

import com.basho.riak.client.cap.Retrier;
import com.basho.riak.client.raw.RawClient;
import com.basho.riak.client.raw.http.HTTPClientAdapter;

public class RiakFactory2 {

  public static DefaultRiakClient httpClient(String url) {
    RawClient client = new HTTPClientAdapter(url);
    return new DefaultRiakClient(client);
  }
  
  public static DefaultRiakClient httpClient(String url, Retrier retrier) {
    RawClient client = new HTTPClientAdapter(url);
    return new DefaultRiakClient(client, retrier);
  }
  
  private RiakFactory2() {}
}
