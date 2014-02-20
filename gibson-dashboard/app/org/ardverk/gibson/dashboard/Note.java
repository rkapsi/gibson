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

package org.ardverk.gibson.dashboard;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.types.ObjectId;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity(Note.COLLECTION)
@Indexes({
  @Index(value = "signature", unique = true)
})
public class Note {

  public static final String COLLECTION = "Notes";
  
  @Id
  private ObjectId id;
  
  private Date addedOn;
  
  private Date updatedOn;
  
  private String text;
  
  private String signature;
  
  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }
  
  public Date getAddedOn() {
    return addedOn;
  }
  
  public void setAddedOn(Date addedOn) {
    this.addedOn = addedOn;
  }
  
  public Date getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(Date updatedOn) {
    this.updatedOn = updatedOn;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }
  
  @Override
  public String toString() {
    return new ToStringBuilder(this).toString();
  }
}
