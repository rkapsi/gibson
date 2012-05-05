package controllers;


import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;


public class Application extends Controller {
  private static GibsonService service = new MockService();
  
  public static Result index() {
    List<ExceptionSummary> summaries = service.getSummary();
    return ok(index.render("Your new application is ready."));
  }
  
}