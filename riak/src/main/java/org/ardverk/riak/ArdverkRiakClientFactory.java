package org.ardverk.riak;

import com.basho.riak.client.cap.Retrier;
import com.basho.riak.client.raw.RawClient;
import com.basho.riak.client.raw.http.HTTPClientAdapter;

public class ArdverkRiakClientFactory {

  public static ArdverkRiakClient httpClient(String url) {
    RawClient client = new HTTPClientAdapter(url);
    return new DefaultArdverkRiakClient(client);
  }
  
  public static ArdverkRiakClient httpClient(String url, Retrier retrier) {
    RawClient client = new HTTPClientAdapter(url);
    return new DefaultArdverkRiakClient(client, retrier);
  }
  
  private ArdverkRiakClientFactory() {}
}
