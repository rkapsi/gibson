package org.ardverk.gibson;

import org.ardverk.gibson.endpoints.Admin;
import org.ardverk.gibson.endpoints.Show;
import org.ardverk.gibson.endpoints.Login;
import org.ardverk.gibson.endpoints.Search;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

/**
 * 
 */
public class GibsonService extends Service<GibsonConfiguration> {
  
  @Override
  public void initialize(Bootstrap<GibsonConfiguration> bootstrap) {
    bootstrap.setName("Gibson");
    bootstrap.addBundle(new AssetsBundle("/assets/"));
  }

  @Override
  public void run(GibsonConfiguration configuration, Environment environment) throws Exception {
    environment.addResource(Show.class);
    environment.addResource(Login.class);
    environment.addResource(Search.class);
    environment.addResource(Admin.class);
  }

  public static void main(String[] args) throws Exception {
    new GibsonService().run(args);
  }
}
