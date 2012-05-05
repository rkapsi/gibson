package controllers;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ardverk.logging.GibsonEvent;
import org.ardverk.logging.GibsonThrowable;


public class MockService implements GibsonService {
  @Override
  public List<ExceptionSummary> getSummary() {
    List<ExceptionSummary> summaries = new ArrayList<ExceptionSummary>();
    summaries.add(new ExceptionSummary(5000, "com.java.IOException"));
    summaries.add(new ExceptionSummary(2000, "com.java.NullPointerException"));
    summaries.add(new ExceptionSummary(3, "com.ardverk.FelixSmellsAwfulError"));

    return summaries;
  }


  @Override
  public List<UniqueException> getExceptions(String className) {
    List<UniqueException> exceptions = new ArrayList<UniqueException>();
    exceptions.add(newEvent(5000, new NullPointerException()));
    exceptions.add(newEvent(2000, new IOException()));
    exceptions.add(newEvent(3, new Throwable()));

    return exceptions;
  }

  private UniqueException newEvent(int count, Throwable throwable) {
    GibsonThrowable t = GibsonThrowable.valueOf(throwable);

    GibsonEvent event = new GibsonEvent();
    event.setThrowable(t);

    return new UniqueException(count, event);
  }
}
