/*
 * Copyright 2012 Will Benedict, Felix Berger and Roger Kapsi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package controllers;

import static org.ardverk.gibson.dashboard.Context.injector;

import org.apache.commons.lang.StringUtils;
import org.ardverk.gibson.dashboard.EventItem;
import org.ardverk.gibson.dashboard.EventItems;
import org.ardverk.gibson.dashboard.EventService;
import org.ardverk.gibson.dashboard.SearchItems;
import org.ardverk.gibson.dashboard.TypeItems;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.event;
import views.html.events;
import views.html.search;
import views.html.types;

public class Main extends Controller {
  
  private static final int KEYWORD_LIMIT = 100;
  
  public static Result index() {
    EventService service = injector().getInstance(EventService.class);
    TypeItems items = service.getTypeItems();
    return ok(types.render(items));
  }
  
  public static Result events(String typeName) {
    EventService service = injector().getInstance(EventService.class);
    EventItems items = service.getEventItems(typeName);
    
    if (items == null || items.isEmpty()) {
      return notFound(typeName);
    }
    
    return ok(events.render(items));
  }
  
  public static Result event(String typeName, String signature) {
    EventService service = injector().getInstance(EventService.class);
    EventItem item = service.getEvent(typeName, signature);
    
    if (item == null) {
      return notFound(typeName + ", " + signature);
    }
    
    return ok(event.render(item, KEYWORD_LIMIT));
  }
  
  public static Result search(String q) {
    if ((q = StringUtils.trimToNull(q)) == null) {
      return redirect("/");
    }
    
    EventService service = injector().getInstance(EventService.class);
    SearchItems items = service.query(q);
    return ok(search.render(items));
  }
}
