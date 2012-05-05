package controllers;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
    exceptions.add(new UniqueException(5000, new NullPointerException()));
    exceptions.add(new UniqueException(2000, new IOException()));
    exceptions.add(new UniqueException(3, new Throwable()));

    return exceptions;
  }
}
