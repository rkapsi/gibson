package controllers;

import static org.ardverk.gibson.dashboard.Context.injector;
import play.Configuration;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;

public class Login extends Controller {

  private static final String AUTHENTICATED_KEY = "authenticated";
  
  private static final String USERNAME_KEY = "gibson.admin.username";
  
  private static final String PASSWORD_KEY = "gibson.admin.password";
  
  public static Result index(boolean logout) {
    if (logout) {
      session().remove(AUTHENTICATED_KEY);
      return redirect("/");
    }
    
    return ok(login.render());
  }
  
  public static Result login() {
    if (isAuthenticated()) {
      return redirect("/");
    }
    
    DynamicForm form = form().bindFromRequest();
    String username = form.get("username");
    String password = form.get("password");
    
    if (username != null && password != null) {
      Configuration configuration = injector().getInstance(Configuration.class);
      String u = configuration.getString(USERNAME_KEY);
      String p = configuration.getString(PASSWORD_KEY);
      
      if (u.equals(username) && p.equals(password)) {
        session(AUTHENTICATED_KEY, "true");
        return redirect("/admin");
      }
    }
    
    return unauthorized("Not Authorized");
  }
  
  public static boolean isAuthenticated() {
    return Boolean.parseBoolean(session(AUTHENTICATED_KEY));
  }
}
