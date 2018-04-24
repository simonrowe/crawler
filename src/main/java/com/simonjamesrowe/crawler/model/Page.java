package com.simonjamesrowe.crawler.model;

import java.util.List;

public class Page extends AbstractWebElement {

  private String name;
  private String title;
  private List<WebElement> contents;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<WebElement> getContents() {
    return contents;
  }

  public void setContents(List<WebElement> contents) {
    this.contents = contents;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
