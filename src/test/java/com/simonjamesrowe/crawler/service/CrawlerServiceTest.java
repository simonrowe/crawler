package com.simonjamesrowe.crawler.service;

import com.simonjamesrowe.crawler.model.Image;
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
import static org.junit.Assert.assertNull;
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

  @Test
  public void testCrawlWithSinglePageTitleAndImages() throws Exception {
    String uri = "http://www.example.com";
    given(crawlerDao.webContent(eq(uri))).willReturn(pageContent("home.html"));
    WebElement result = crawlerService.crawl(uri);
    assertNotNull(result);
    assertTrue(result instanceof Page);
    Page page = (Page) result;
    assertEquals("http://www.example.com", result.getUri());
    assertEquals("Example.com - A great example of simplicity", page.getTitle());
    assertNotNull(page.getContents());
    assertTrue(page.getContents().size() >= 1);
    assertEquals(
        "http://www.example.com/us.jpg",
        page.getContents().stream().filter(pc -> pc instanceof Image).findFirst().get().getUri());
  }

  @Test
  public void testLinksAndImagesOnSinglePage()  throws Exception{
    String uri = "http://www.example.com";
    given(crawlerDao.webContent(eq(uri))).willReturn(pageContent("home.html"));
    Page page  = crawlerService.crawl(uri);

    assertEquals("http://www.example.com", page.getUri());
    assertEquals("Example.com - A great example of simplicity", page.getTitle());
    assertNotNull(page.getContents());
    assertTrue(page.getContents().size() >= 1);
    assertEquals(
            "http://www.example.com/us.jpg",
            page.getContents().stream().filter(pc -> pc instanceof Image).findFirst().get().getUri());
    Page about = findPage(page, "http://www.example.com/about.html");
    assertNotNull(about);
    assertEquals("http://www.example.com/about.html", about.getUri());
    assertEquals("About Us", about.getTitle());
    assertNull(about.getContents());

    Page events = findPage(page, "http://www.example.com/events");
    assertNotNull(events);
    assertEquals("http://www.example.com/events", events.getUri());
    assertEquals("Events", events.getTitle());
    assertNull(events.getContents());

    Page staff = findPage(page, "http://www.example.com/staff");
    assertNotNull(staff);
    assertEquals("http://www.example.com/staff", staff.getUri());
    assertEquals("Staff", staff.getTitle());
    assertNull(staff.getContents());

    //site crawler does not include anchors
    Page sectionOne = findPage(page, "http://www.example.com/#sectionOne");
    assertNull(sectionOne);

    Page external = findPage(page, "http://www.external.com/externalLink");
    assertNotNull(external);
    assertEquals("http://www.external.com/externalLink", external.getUri());
    assertEquals("External Link", external.getTitle());
    assertNull(external.getContents());

  }



  private Page findPage(Page page, String link) {
    return (Page) page.getContents().stream().filter(c -> c instanceof Page && c.getUri().equalsIgnoreCase(link)).findFirst().orElse(null);
  }

  @Test
  public void testGetAbsoluteUrl() {
    assertEquals(
        "http://www.example.com/img.jpg",
        crawlerService.getAbsoluteUri("http://www.example.com", "img.jpg"));
    assertEquals(
        "http://www.example.com/img.jpg",
        crawlerService.getAbsoluteUri("http://www.example.com/", "img.jpg"));
    assertEquals(
        "http://www.example.com/img.jpg",
        crawlerService.getAbsoluteUri("http://www.example.com/", "/img.jpg"));
    assertEquals(
        "http://www.example.com/img.jpg",
        crawlerService.getAbsoluteUri("http://www.example.com/something", "/img.jpg"));
    assertEquals(
        "http://www.example.com/img.jpg",
        crawlerService.getAbsoluteUri("http://www.example.com/something/", "/img.jpg"));
    assertEquals(
        "http://www.example.com/img.jpg",
        crawlerService.getAbsoluteUri("http://www.example.com/something", "img.jpg"));
    assertEquals(
        "http://www.example.com/something/img.jpg",
        crawlerService.getAbsoluteUri("http://www.example.com/something/", "img.jpg"));
    assertEquals(
        "http://www.other.com/img.jpg",
        crawlerService.getAbsoluteUri(
            "http://www.example.com/something/", "http://www.other.com/img.jpg"));
  }

  private Document pageContent(String file) throws Exception {
    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = builderFactory.newDocumentBuilder();
    return builder.parse(new ClassPathResource(file).getInputStream());
  }
}
