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

package org.ardverk.gibson;

import java.util.HashMap;
import java.util.Map;

import com.google.code.morphia.converters.SimpleValueConverter;
import com.google.code.morphia.converters.TypeConverter;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * This is a JSON serializer for {@link StackTraceElement}s.
 */
class StackTraceElementConverter extends TypeConverter implements SimpleValueConverter {
  
  public StackTraceElementConverter() {
    super(StackTraceElement.class);
  }
  
  @Override
  protected boolean isSupported(Class<?> clazz, MappedField extra) {
    return clazz.isAssignableFrom(StackTraceElement.class);
  }
  
  @Override
  public Object decode(@SuppressWarnings("rawtypes") Class type, Object value, MappedField extra) throws MappingException {
    if (value == null) {
      return null;
    }
    
    @SuppressWarnings("unchecked")
    Map<String, ?> map = (Map<String, ?>)value;
    
    String clazz = (String)map.get("class");
    String method = (String)map.get("method");
    String file = (String)map.get("file");
    int line = (Integer)map.get("line");
    
    return new StackTraceElement(clazz, method, file, line);
  }

  @Override
  public Object encode(Object value, MappedField extra) {
    if (value == null) {
      return null;
    }
    
    StackTraceElement element = (StackTraceElement)value;
    
    Map<String, Object> map = new HashMap<>(4, 1.0f);
    map.put("class", element.getClassName());
    map.put("method", element.getMethodName());
    map.put("file", element.getFileName());
    map.put("line", element.getLineNumber());
    return map;
  }
}