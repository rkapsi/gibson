package controllers;


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
}
