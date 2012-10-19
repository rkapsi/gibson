package org.ardverk.gibson.dashboard;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.ardverk.gibson.Event;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class DefaultTrendService implements TrendService {

  private static final long TREND_FREQUENCY = 15;
  private static final TimeUnit TREND_TIMEUNIT = TimeUnit.SECONDS;

  private final Map<String, Pair<Trend, Callable<Long>>> trendMap = new ConcurrentHashMap<String, Pair<Trend, Callable<Long>>>();

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

  @Inject
  private EventDAO eventDAO;

  @Inject
  public void init() {
    executorService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        updateTrends();
      }
    }, TREND_FREQUENCY, TREND_FREQUENCY, TREND_TIMEUNIT);
  }

  @Override
  public Trend getTrendForEvent(final Event event) {
    return getTrendInfo(event.getSignature(), new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        return eventDAO.getEventCount(event.getSignature());
      }
    });
  }

  @Override
  public Trend getTrendForType(final String type) {
    return getTrendInfo(type, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        return eventDAO.getTypeNameCount(type);
      }
    });
  }

  @Override
  public void clear() {
    trendMap.clear();
  }

  private Trend getTrendInfo(String key, Callable<Long> countGetter) {
    Pair<Trend, Callable<Long>> pair = trendMap.get(key);
    if (pair == null) {
      try {
        long count = countGetter.call();
        Trend trend = new Trend(count, count, count, System.currentTimeMillis());
        pair = new ImmutablePair<Trend, Callable<Long>>(trend, countGetter);
        trendMap.put(key, pair);
      } catch (Exception e) {
        // TODO better way to handle this?
        throw new RuntimeException(e);
      }
    }

    return pair.getLeft();
  }

  private void updateTrends() {
    for (Map.Entry<String, Pair<Trend, Callable<Long>>> e : trendMap.entrySet()) {
      String key = e.getKey();
      Trend trend = e.getValue().getLeft();
      try {
        Callable<Long> counter = e.getValue().getRight();
        if (counter != null) {
          long count = counter.call();
          Trend newTrend = Trend.create(count, trend);
          trendMap.put(key, new ImmutablePair<Trend, Callable<Long>>(newTrend, counter));
        }
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
