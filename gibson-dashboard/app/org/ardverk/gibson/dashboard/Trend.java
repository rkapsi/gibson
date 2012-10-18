package org.ardverk.gibson.dashboard;

public class Trend {

  /**
   * Total number of exceptions
   */
  public final long count;
  /**
   * The diff from the previous snapshot
   */
  public final long delta;
  /**
   * Exceptions / second
   */
  public final double velocity;
  /**
   * Dougtime
   */
  public final long timestamp;

  public Trend(long count, long delta, double velocity, long timestamp) {
    this.count = count;
    this.delta = delta;
    this.velocity = velocity;
    this.timestamp = timestamp;
  }

  public int direction() {
    return (int) Math.signum(velocity);
  }

  public static Trend create(long count, Trend previous) {
    long delta = count - previous.count;
    long timestamp = System.currentTimeMillis();
    long timediff = timestamp - previous.timestamp;
    double velocity = delta - previous.delta;
    velocity = velocity / (timediff / 1000.0);
    Trend newTrend = new Trend(count, delta, velocity, timestamp);
    return newTrend;
  }
}
