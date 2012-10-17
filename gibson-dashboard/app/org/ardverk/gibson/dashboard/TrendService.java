package org.ardverk.gibson.dashboard;


import com.google.inject.ImplementedBy;
import org.ardverk.gibson.Event;

@ImplementedBy(DefaultTrendService.class)
public interface TrendService {

  public Trend getTrendForEvent(Event event);
  public Trend getTrendForType(String type);
}
