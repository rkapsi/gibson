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

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.ardverk.gibson.dashboard.Note;
import org.ardverk.gibson.dashboard.NoteService;

import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;

public class Notes extends Controller {
  
  public static Result save() {
    DynamicForm form = form().bindFromRequest();
    
    String signature = form.get("signature");
    String url = form.get("url");
    
    String text = StringUtils.trimToNull(form.get("note"));
    
    if (signature == null || url == null) {
      return badRequest();
    }
    
    NoteService service = injector().getInstance(NoteService.class);
    if (text == null) {
      service.delete(signature);
    } else {
      Note note = service.find(signature);
      if (note == null) {
        note = new Note();
        note.setSignature(signature);
        note.setAddedOn(new Date());
      }
      
      note.setText(text);
      note.setUpdatedOn(new Date());
      service.save(note);
    }
    
    return redirect(url);
  }
}
