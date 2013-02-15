package org.ardverk.gibson.dashboard;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.ardverk.gibson.Event;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class DefaultTrendService implements TrendService {

  private static final long TREND_FREQUENCY = 15;
  private static final TimeUnit TREND_TIMEUNIT = TimeUnit.SECONDS;

  private final Map<String, Pair<Trend, TrendBuilder>> trendMap =
      new ConcurrentHashMap<String, Pair<Trend, TrendBuilder>>();

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

  @Inject
  private EventDAO eventDAO;

  private volatile ScheduledFuture<?> scheduledFuture;

  @Inject
  public void init() {
    scheduledFuture = executorService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        updateTrends();
      }
    }, TREND_FREQUENCY, TREND_FREQUENCY, TREND_TIMEUNIT);
  }

  @Override
  public Trend getTrendForEvent(final Event event) {
    return getTrendInfo(event.getSignature(), new TrendBuilder() {
      @Override
      public Trend build(Trend previous) {
        long count = eventDAO.getEventCount(event.getSignature());
        Date lastOccurrence = eventDAO.getEventLastOccurrence(event.getSignature());
        Trend trend;
        if (previous != null) {
          trend = Trend.create(count, lastOccurrence, previous);
        } else {
          Date firstOccurrence = eventDAO.getEventFirstOccurrence(event.getSignature());
          trend = new Trend(count, count, count, System.currentTimeMillis(), firstOccurrence, lastOccurrence);
        }
        return trend;
      }
    });
  }

  @Override
  public Trend getTrendForType(final String type) {
    return getTrendInfo(type, new TrendBuilder() {
      @Override
      public Trend build(Trend previous) {
        long count = eventDAO.getTypeNameCount(type);
        Date lastOccurrence = eventDAO.getTypeNameLastOccurrence(type);
        Trend trend;
        if (previous != null) {
          trend = Trend.create(count, lastOccurrence, previous);
        } else {
          Date firstOccurrence = eventDAO.getTypeNameFirstOccurrence(type);
          trend = new Trend(count, count, count, System.currentTimeMillis(), firstOccurrence, lastOccurrence);
        }
        return trend;
      }
    });
  }

  @Override
  public void clear() {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
    trendMap.clear();
    // restart executor service
    init();
  }

  private interface TrendBuilder {
    public Trend build(Trend previous);
  }

  private Trend getTrendInfo(String key, TrendBuilder trendBuilder) {
    Pair<Trend, TrendBuilder> pair = trendMap.get(key);
    if (pair == null) {
      try {
        Trend trend = trendBuilder.build(null);
        pair = new ImmutablePair<Trend, TrendBuilder>(trend, trendBuilder);
        trendMap.put(key, pair);
      } catch (Exception e) {
        // TODO better way to handle this?
        throw new RuntimeException(e);
      }
    }

    return pair.getLeft();
  }

  private void updateTrends() {
    for (Map.Entry<String, Pair<Trend, TrendBuilder>> e : trendMap.entrySet()) {
      String key = e.getKey();
      Trend trend = e.getValue().getLeft();
      try {
        TrendBuilder trendBuilder = e.getValue().getRight();
        Trend newTrend = trendBuilder.build(trend);
        trendMap.put(key, new ImmutablePair<Trend, TrendBuilder>(newTrend, trendBuilder));
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
