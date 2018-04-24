package com.simonjamesrowe.crawler.service;

import com.simonjamesrowe.crawler.model.Page;
import com.simonjamesrowe.crawler.model.WebElement;
import com.simonjamesrowe.crawler.model.dao.CrawlerDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CrawlerServiceTest {

  @Mock private CrawlerDao crawlerDao;

  @InjectMocks private CrawlerService crawlerService;

  @Test
  public void testCrawlWithSinglePageTitle() throws Exception {
    String uri = "http://www.example.com";
    given(crawlerDao.webContent(eq(uri))).willReturn(pageContent("home.html"));
    WebElement result = crawlerService.crawl(uri);
    assertNotNull(result);
    assertTrue(result instanceof Page);
    Page page = (Page) result;
    assertEquals("http://www.example.com", result.getUri());
    assertEquals("Example.com - A great example of simplicity", page.getTitle());
  }

  private Document pageContent(String file) throws Exception {
    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = builderFactory.newDocumentBuilder();
    return builder.parse(new ClassPathResource(file).getInputStream());
  }
}
