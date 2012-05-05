package controllers;

import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
//import static org.ardverk.gibson.dashboard.Context.injector;
import views.html.exceptions;

public class Application extends Controller {
  
  private static GibsonService service = new MockService();
  
  public static Result index() {
    // injector().getInstance(String.class)
    List<ExceptionSummary> summaries = service.getSummary();
    return ok(index.render(summaries));
  }


  public static Result exceptions(String className) {
    List<UniqueException> uniqueExceptions = service.getExceptions(className);
    return ok(exceptions.render(uniqueExceptions));
  }
}
