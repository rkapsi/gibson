package org.ardverk.gibson.dashboard.riak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.ardverk.logging.GibsonEvent;
import org.ardverk.logging.GibsonThrowable;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.bucket.DomainBucket;
import com.google.inject.Singleton;

import controllers.ExceptionSummary;
import controllers.GibsonService;
import controllers.UniqueException;

@Singleton
public class RiakGibsonService implements GibsonService {

  @Inject
  private IRiakClient client;
  
  @Inject
  private Bucket bucket;
  
  @Inject
  private DomainBucket<GibsonEvent> domainBucket;
  
  @Override
  public List<ExceptionSummary> getSummary() {
    foo();
    
    List<ExceptionSummary> summaries = new ArrayList<ExceptionSummary>();
    summaries.add(new ExceptionSummary(5000, "com.java.IOException"));
    summaries.add(new ExceptionSummary(2000, "com.java.NullPointerException"));
    summaries.add(new ExceptionSummary(3, "com.ardverk.FelixSmellsAwfulError"));
    return summaries;
  }

  @Override
  public List<UniqueException> getExceptions(String className) {
    return Collections.emptyList();
  }
  
  private void foo() {
    try {
      bar();
    } catch (RiakException err) {
      err.printStackTrace();
    }
  }
  
  private void bar() throws RiakException {
    Map<String, AtomicInteger> map = new HashMap<String, AtomicInteger>();
    
    for (String key : bucket.keys()) {
      GibsonEvent event = domainBucket.fetch(key);
      
      GibsonThrowable throwable = event.getThrowable();
      if (throwable == null) {
        continue;
      }
      
      String clazz = throwable.getClassName();
      AtomicInteger counter = map.get(clazz);
      if (counter == null) {
        counter = new AtomicInteger();
        map.put(clazz, counter);
      }
      counter.incrementAndGet();
    }
    
    for (Map.Entry<String, AtomicInteger> entry : map.entrySet()) {
      System.out.println(entry);
    }
  }
  
  /*private static String foo(String name) {
    InputStream in = play.Play.application().resourceAsStream(name);
    if (in == null) {
      throw new IllegalArgumentException(name);
    }
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      byte[] buffer = new byte[8*1204];
      int len = -1;
      while ((len = in.read(buffer)) != -1) {
        baos.write(buffer, 0, len);
      }
      
      return new String(baos.toByteArray(), "UTF-8");
    } catch (IOException err) {
      throw new IllegalStateException("IOException", err);
    } finally {
      closeQuietly(in);
    }
  }
  
  private static void closeQuietly(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException err) {}
    }
  }*/
}
