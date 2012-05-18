package controllers;

import static controllers.Login.isAuthenticated;
import static org.ardverk.gibson.dashboard.Context.injector;

import org.ardverk.gibson.dashboard.EventService;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin;
import views.html.types;

public class Admin extends Controller {

  public static Result index() {
    if (!isAuthenticated()) {
      return redirect("/login");
    }
    
    return ok(admin.render());
  }
  
  public static Result drop() {
    if (!isAuthenticated()) {
      return redirect("/login");
    }
    
    EventService service = injector().getInstance(EventService.class);
    service.drop();
    return redirect("/");
  }
}
