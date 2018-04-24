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
  private static final String XPATH_IMAGE_SELECTOR = "//img[@src]";
  private static final String XPATH_ANCHOR_SELECTOR = "//a[@href]";
  private static final String ANCHOR = "a";
  private static final String IMAGE = "img";

  private static final String XPATH_LINK_IMAGE_SELECTOR = String.format("%s|%s",XPATH_IMAGE_SELECTOR, XPATH_ANCHOR_SELECTOR);

  private XPath xPath;

  private CrawlerDao crawlerDao;

  public CrawlerService() {
    this.xPath = XPathFactory.newInstance().newXPath();
  }

  public Page crawl(String uri) throws XPathExpressionException {
    Document xmlDocument = crawlerDao.webContent(uri);
    Node title =
        (Node) xPath.compile(XPATH_TITLE_SELECTOR).evaluate(xmlDocument, XPathConstants.NODE);
    Page page = new Page();
    page.setUri(uri);
    page.setTitle(title.getTextContent());
    NodeList nodeList =
        (NodeList)
            xPath.compile(XPATH_LINK_IMAGE_SELECTOR).evaluate(xmlDocument, XPathConstants.NODESET);

    if (nodeList.getLength() > 0) {
      page.setContents(new ArrayList<>());
    }

    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (IMAGE.equalsIgnoreCase(node.getNodeName())) {
        addImageElement(uri, page, node);
      } else {
        addPageElement(uri, page, node);
      }
    }

    return page;
  }

  private void addPageElement(String uri, Page page, Node node) {
    String href = node.getAttributes().getNamedItem("href").getNodeValue();
    if (!href.contains("#")){
      Page newPage = new Page();
      newPage.setUri(getAbsoluteUri(uri, href));
      newPage.setTitle(node.getTextContent());
      page.getContents().add(newPage);
    }
  }

  private void addImageElement(String uri, Page page, Node node) {
    Image image = new Image();
    image.setUri(getAbsoluteUri(uri, node.getAttributes().getNamedItem("src").getNodeValue()));
    page.getContents().add(image);
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
