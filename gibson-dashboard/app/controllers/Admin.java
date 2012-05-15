package controllers;

import static org.ardverk.gibson.dashboard.Context.injector;

import org.ardverk.gibson.dashboard.EventService;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin;

public class Admin extends Controller {

  public static Result index() {
    return ok(admin.render());
  }
  
  public static Result drop() {
    EventService service = injector().getInstance(EventService.class);
    service.drop();
    return redirect("/");
  }
}
