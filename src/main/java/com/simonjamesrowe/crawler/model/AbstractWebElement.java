package com.simonjamesrowe.crawler.model;

public abstract class AbstractWebElement implements WebElement {

  protected String uri;

  @Override
  public String getUri() {
    return this.uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }
}
