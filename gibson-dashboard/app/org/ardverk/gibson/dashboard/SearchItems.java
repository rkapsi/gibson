package org.ardverk.gibson.dashboard;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import play.data.validation.Constraints.Required;

public class SearchItems extends Items {

  public static final SearchItems EMPTY = new SearchItems(
      null, Collections.<String>emptySet(), 
      Collections.<SearchItem>emptyList(), 0L);
  
  @Required
  public final String query;
  
  @Required
  public final Set<? extends String> keywords;
  
  @Required
  public final List<? extends SearchItem> elements;
  
  @Required
  public final long count;
  
  public SearchItems(String query, Set<? extends String> keywords, 
      List<? extends SearchItem> elements, long count) {
    this.query = query;
    this.keywords = keywords;
    this.elements = elements;
    this.count = count;
  }
  
  @Override
  public int size() {
    return elements != null ? elements.size() : 0;
  }
}
