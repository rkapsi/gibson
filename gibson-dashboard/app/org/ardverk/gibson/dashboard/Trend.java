package org.ardverk.gibson.dashboard;

public class Trend {

  public final long count;
  public final long delta;
  public final long velocity;

  public Trend(long count, long delta, long velocity) {
    this.count = count;
    this.delta = delta;
    this.velocity = velocity;
  }

  public static Trend create(long count, Trend previous) {
    long delta = count - previous.count;
    long velocity = delta - previous.delta;
    Trend newTrend = new Trend(count, delta, velocity);
    return newTrend;
  }
}
