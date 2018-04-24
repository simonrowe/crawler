package com.simonjamesrowe.crawler.service;

import com.simonjamesrowe.crawler.model.Page;
import com.simonjamesrowe.crawler.model.WebElement;
import com.simonjamesrowe.crawler.model.dao.CrawlerDao;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class CrawlerService {

  private static final String XPATH_TITLE_SELECTOR = "//head/title";

  private XPath xPath;

  private CrawlerDao crawlerDao;

  public CrawlerService() {
    this.xPath = XPathFactory.newInstance().newXPath();
  }

  public WebElement crawl(String uri) throws XPathExpressionException {
    Document xmlDocument = crawlerDao.webContent(uri);
    Node title =
        (Node) xPath.compile(XPATH_TITLE_SELECTOR).evaluate(xmlDocument, XPathConstants.NODE);
    Page page = new Page();
    page.setUri(uri);
    page.setTitle(title.getTextContent());
    return page;
  }
}
