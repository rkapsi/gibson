package org.ardverk.gibson.dashboard;

import java.util.Date;

public class Trend {

  /**
   * Total number of exceptions.
   */
  public final long count;

  /**
   * Current exception rate.
   * Exception / minute
   */
  public final double velocity;

  /**
   * Change in velocity.
   * Exceptions / minute / minute
   */
  public final double acceleration;

  /**
   * Dougtime.
   */
  public final long timestamp;

  /**
   * The first time the event tracked by this Trend occurred.
   */
  public final Date firstOccurrence;

  /**
   * The most recent time the event tracked by this Trend occurred.
   */
  public final Date lastOccurrence;

  public Trend(long count, double velocity, double acceleration, long timestamp, Date firstOccurrence, Date lastOccurrence) {
    this.count = count;
    this.velocity = velocity;
    this.acceleration = acceleration;
    this.timestamp = timestamp;
    this.firstOccurrence = firstOccurrence;
    this.lastOccurrence = lastOccurrence;
  }

  public int direction() {
    return (int) Math.signum(acceleration);
  }

  public static Trend create(long count, Date lastOccurrence, Trend previous) {
    long timestamp = System.currentTimeMillis();
    // number of minutes between now and the previous timestamp
    double timediff = ((timestamp - previous.timestamp) / 1000.0) / 60.0;
    double velocity = (count - previous.count) / timediff;
    double acceleration = velocity - previous.velocity;
    acceleration = acceleration / timediff;
    Trend newTrend = new Trend(count, velocity, acceleration, timestamp, previous.firstOccurrence, lastOccurrence);
    return newTrend;
  }
}
