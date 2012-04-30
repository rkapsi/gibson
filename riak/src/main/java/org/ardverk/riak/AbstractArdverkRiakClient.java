package org.ardverk.riak;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;

import com.basho.riak.client.IRiakObject;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.bucket.FetchBucket;
import com.basho.riak.client.bucket.WriteBucket;
import com.basho.riak.client.cap.Retrier;
import com.basho.riak.client.query.BucketKeyMapReduce;
import com.basho.riak.client.query.BucketMapReduce;
import com.basho.riak.client.query.IndexMapReduce;
import com.basho.riak.client.query.LinkWalk;
import com.basho.riak.client.query.NodeStats;
import com.basho.riak.client.query.SearchMapReduce;
import com.basho.riak.client.raw.Transport;
import com.basho.riak.client.raw.query.indexes.IndexQuery;

abstract class AbstractArdverkRiakClient implements ArdverkRiakClient {
  
  /**
   * Returns the {@link Retrier}.
   */
  public abstract Retrier getRetrier();
  
  @Override
  public ArdverkRiakClient setClientId(final byte[] clientId) throws RiakException {
    if (clientId == null || clientId.length != 4) {
      throw new IllegalArgumentException("Client Id must be 4 bytes long");
    }
    
    getRetrier().attempt(new Callable<Void>() {
      public Void call() throws IOException {
        getClient().setClientId(clientId);
        return null;
      }
    });

    return this;
  }

  @Override
  public byte[] generateAndSetClientId() throws RiakException {
    byte[] clientId = getRetrier().attempt(new Callable<byte[]>() {
      public byte[] call() throws IOException {
        return getClient().generateAndSetClientId();
      }
    });
  
    return clientId;
  }

  @Override
  public byte[] getClientId() throws RiakException {
    byte[] clientId = getRetrier().attempt(new Callable<byte[]>() {
      public byte[] call() throws IOException {
        return getClient().getClientId();
      }
    });
  
    return clientId;
  }

  @Override
  public Set<String> listBuckets() throws RiakException {
    try {
      return getClient().listBuckets();
    } catch (IOException err) {
      throw new RiakException("IOException", err);
    }
  }

  @Override
  public FetchBucket fetchBucket(String bucketName) {
    return new FetchBucket(getClient(), bucketName, getRetrier());
  }

  @Override
  public WriteBucket updateBucket(Bucket bucket) {
    return new WriteBucket(getClient(), bucket, getRetrier());
  }

  @Override
  public WriteBucket createBucket(String bucketName) {
    return new WriteBucket(getClient(), bucketName, getRetrier());
  }

  @Override
  public LinkWalk walk(IRiakObject startObject) {
    return new LinkWalk(getClient(), startObject);
  }

  @Override
  public BucketKeyMapReduce mapReduce() {
    return new BucketKeyMapReduce(getClient());
  }

  @Override
  public BucketMapReduce mapReduce(String bucket) {
    return new BucketMapReduce(getClient(), bucket);
  }

  @Override
  public SearchMapReduce mapReduce(String bucket, String query) {
    return new SearchMapReduce(getClient(), bucket, query);
  }

  @Override
  public IndexMapReduce mapReduce(IndexQuery query) {
    return new IndexMapReduce(getClient(), query);
  }

  @Override
  public void ping() throws RiakException {
    try {
      getClient().ping();
    } catch (IOException err) {
      throw new RiakException("IOException", err);
    }
  }

  @Override
  public Transport getTransport() {
    return getClient().getTransport();
  }

  @Override
  public void shutdown() {
    getClient().shutdown();
  }
  
  @Override
  public void close() {
    shutdown();
  }

  @Override
  public Iterable<NodeStats> stats() throws RiakException {
    try {
      return getClient().stats();
    } catch (IOException err) {
      throw new RiakException("IOException", err);
    }
  }
}
