package controllers;

import static org.ardverk.gibson.dashboard.Context.injector;

import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.exceptions;

public class Application extends Controller {
  
  public static Result index() {
    GibsonService service = injector().getInstance(GibsonService.class);
    List<ExceptionSummary> summaries = service.getSummary();
    return ok(index.render(summaries));
  }


  public static Result exceptions(String className) {
    GibsonService service = injector().getInstance(GibsonService.class);
    List<UniqueException> uniqueExceptions = service.getExceptions(className);
    return ok(exceptions.render(uniqueExceptions));
  }
}
