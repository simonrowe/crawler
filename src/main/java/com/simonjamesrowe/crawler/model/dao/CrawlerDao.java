package com.simonjamesrowe.crawler.model.dao;

import org.w3c.dom.Document;

public interface CrawlerDao {

  Document webContent(String uri);
}
