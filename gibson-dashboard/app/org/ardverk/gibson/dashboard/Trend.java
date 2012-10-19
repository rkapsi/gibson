package org.ardverk.gibson.dashboard;

public class Trend {

  /**
   * Total number of exceptions.
   */
  public final long count;
  /**
   * Current exception rate.
   * Exception / second
   */
  public final double velocity;
  /**
   * Change in velocity.
   * Exceptions / second / second
   */
  public final double acceleration;
  /**
   * Dougtime.
   */
  public final long timestamp;

  public Trend(long count, double velocity, double acceleration, long timestamp) {
    this.count = count;
    this.velocity = velocity;
    this.acceleration = acceleration;
    this.timestamp = timestamp;
  }

  public int direction() {
    return (int) Math.signum(acceleration);
  }

  public static Trend create(long count, Trend previous) {
    long timestamp = System.currentTimeMillis();
    double timediff = (timestamp - previous.timestamp) / 1000.0;
    double velocity = (count - previous.count) / timediff;
    double acceleration = velocity - previous.velocity;
    acceleration = acceleration / timediff ;
    Trend newTrend = new Trend(count, velocity, acceleration, timestamp);
    return newTrend;
  }
}
