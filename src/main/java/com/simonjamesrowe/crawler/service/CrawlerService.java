package com.simonjamesrowe.crawler.service;

import com.simonjamesrowe.crawler.model.Image;
import com.simonjamesrowe.crawler.model.Page;
import com.simonjamesrowe.crawler.model.WebElement;
import com.simonjamesrowe.crawler.model.dao.CrawlerDao;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;

public class CrawlerService {

  private static final String XPATH_TITLE_SELECTOR = "//head/title";
  private static final String XPATH_IMAGE_SELECTOR = "//img";

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
    NodeList nodeList =
        (NodeList)
            xPath.compile(XPATH_IMAGE_SELECTOR).evaluate(xmlDocument, XPathConstants.NODESET);

    if (nodeList.getLength() > 0) {
      page.setContents(new ArrayList<>());
    }

    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      Image image = new Image();
      image.setUri(getAbsoluteUri(uri, node.getAttributes().getNamedItem("src").getNodeValue()));
      page.getContents().add(image);
    }

    return page;
  }

  protected String getAbsoluteUri(String uri, String src) {
    if (src.startsWith("http")) {
      return src;
    }

    String relativeSrc = src.startsWith("/") ? src.substring(1) : src;
    if (src.startsWith("/")) {
      int domainPathSeperator = uri.indexOf("/", 7);
      if (domainPathSeperator == -1) {
        return uri + "/" + relativeSrc;
      } else {
        return uri.substring(0, domainPathSeperator + 1) + relativeSrc;
      }
    } else {
      int lastPath = uri.lastIndexOf("/");
      if (lastPath == 6) {
        return uri + "/" + relativeSrc;
      } else {
        return uri.substring(0, lastPath + 1) + relativeSrc;
      }
    }
  }
}
