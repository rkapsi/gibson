package org.ardverk.gibson.dashboard;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.ardverk.gibson.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class DefaultTrendService implements TrendService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultTrendService.class);
  private static final long TREND_FREQUENCY = 15;
  private static final TimeUnit TREND_TIMEUNIT = TimeUnit.SECONDS;

  private final Map<String, Pair<Trend, TrendBuilder>> trendMap =
      new ConcurrentHashMap<String, Pair<Trend, TrendBuilder>>();

  private final AtomicReference<ScheduledExecutorService> executorService = new AtomicReference<ScheduledExecutorService>(null);

  @Inject
  private EventDAO eventDAO;

  @Inject
  private EventService eventService;

  @Inject
  public void init() {
    ScheduledExecutorService oldExecutor = executorService.getAndSet(Executors.newScheduledThreadPool(1));
    if (oldExecutor != null) {
      oldExecutor.shutdownNow();
    }
    executorService.get().scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        try {
          updateTrends();
        } catch (Exception e) {
          LOG.error("Exception while updating", e);
        }
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

  private void updateTrends() throws InterruptedException {
    // eagerly build trend data for new typeNames and event signatures
    TypeItems typeItems = eventService.getTypeItems();
    for (TypeItem item : typeItems.elements) {
      if (Thread.currentThread().isInterrupted()) {
        throw new InterruptedException();
      }
      eventService.getEventItems(item.typeName);
    }

    // calculate new trend data for all known objects
    for (Map.Entry<String, Pair<Trend, TrendBuilder>> e : trendMap.entrySet()) {
      if (Thread.currentThread().isInterrupted()) {
        throw new InterruptedException();
      }
      String key = e.getKey();
      Trend trend = e.getValue().getLeft();
      try {
        TrendBuilder trendBuilder = e.getValue().getRight();
        Trend newTrend = trendBuilder.build(trend);
        trendMap.put(key, new ImmutablePair<>(newTrend, trendBuilder));
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
