package org.ardverk.gibson.dashboard;

import org.ardverk.gibson.Event;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class DefaultTrendService implements TrendService {

  private static final long TREND_FREQUENCY = 15;
  private static final TimeUnit TREND_TIMEUNIT = TimeUnit.SECONDS;

  private final Map<String, Callable<Long>> counterMap = new HashMap<String, Callable<Long>>();
  private final Map<String, Trend> trendMap = new HashMap<String, Trend>();

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
  public synchronized void clear() {
    trendMap.clear();
    counterMap.clear();
  }

  private synchronized Trend getTrendInfo(String key, Callable<Long> countGetter) {
    Trend trend = null;
    if (trendMap.containsKey(key)) {
      trend = trendMap.get(key);
    } else {
      try {
        long count = countGetter.call();
        trend = new Trend(count, count, count);
        trendMap.put(key, trend);
        counterMap.put(key, countGetter);
      } catch (Exception e) {
        // TODO better way to handle this?
        throw new RuntimeException(e);
      }
    }

    return trend;
  }

  private synchronized void updateTrends() {
    for (Map.Entry<String, Trend> e : trendMap.entrySet()) {
      String key = e.getKey();
      Trend trend = e.getValue();
      try {
        long count = counterMap.get(key).call();
        Trend newTrend = Trend.create(count, trend);
        trendMap.put(key, newTrend);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
