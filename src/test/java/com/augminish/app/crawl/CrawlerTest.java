package com.augminish.app.crawl;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

public class CrawlerTest {

    private static Crawler crawler;
    private static WebClient wc;

    @BeforeClass
    public static void initialize() {
        
        crawler = new Crawler();
        wc = new WebClient();
    }
    
    @Test
    public void getProtocolFromUrlTest() {
        Assert.assertEquals(Crawler.getProtocolFrom("http://www.augminish.com/index"), 0);
        Assert.assertEquals(Crawler.getProtocolFrom("https://regex-tutorial.com/lookback-conditional"), 1);
        Assert.assertEquals(Crawler.getProtocolFrom("https://www.facebook.com/cthaw1"), 1);
        Assert.assertEquals(Crawler.getProtocolFrom("http://www.gamnesia.com/jet-force-gemini"), 0);
    }

    @Test
    public void getDomainFromUrlTest() {
        String craigslistUrl = "https://newjersey.craigslist.org/mus/search/gig73821.html", amazonUrl = "https://www.amazon.com/ecommerce/index.html",
                regexUrl = "www.regular-expressions.info/optional.html", ec2Url = "ec2-54-196-32-193.compute-1.amazonaws.com/projects/lens-platform";

        Assert.assertEquals("Should extract the Amazon Domain name", "www.amazon.com", Crawler.getDomainFrom(amazonUrl));
        Assert.assertEquals("Should extract the Craigslist Domain name", "newjersey.craigslist.org", Crawler.getDomainFrom(craigslistUrl));
        Assert.assertEquals("Should extract the Regex Domain name", "www.regular-expressions.info", Crawler.getDomainFrom(regexUrl));
        Assert.assertEquals("Should extract the EC2 Instance Domain name", "ec2-54-196-32-193.compute-1.amazonaws.com", Crawler.getDomainFrom(ec2Url));
    }

    @Test
    public void uniqueHashTest() {
        
        String url = "https://newyork.craigslist.org/mus/search";
        
        Assert.assertNotNull(crawler.uniqueHash(url));
        crawler.addVisitedUrl(crawler.uniqueHash(url), url);
        Assert.assertNull(crawler.uniqueHash(url));
    }

    @Test
    public void buildFullUrlTest() {

        HashMap<String, Object> webSiteRow = new HashMap<String, Object>();

        webSiteRow.put("url", "/2016/01/21/the-49ers-relationship-with-kaepernick-becomes-even more-bizarre");
        webSiteRow.put("domain", "profootballtalk.nbcsports.com");
        webSiteRow.put("secure", 0);

        Assert.assertEquals("Crawler Url builder should create valid uri component",
                "http://profootballtalk.nbcsports.com/2016/01/21/the-49ers-relationship-with-kaepernick-becomes-even more-bizarre", Crawler.buildFullUrl(webSiteRow));

        webSiteRow.put("url", "/106402933672/videos/10153671119698673/?theater");
        webSiteRow.put("domain", "www.facebook.com");
        webSiteRow.put("secure", 1);

        Assert.assertEquals("Crawler Url builder shoudl create valid uri component", "https://www.facebook.com/106402933672/videos/10153671119698673/?theater",
                Crawler.buildFullUrl(webSiteRow));
    }
    
    @Test
    public void getPathFromUrlTest() {
        
        Assert.assertEquals("/2016/01/21/the-49ers-relationship-with-kaepernick-becomes-even more-bizarre",
                Crawler.getUrlPathFrom("http://profootballtalk.nbcsports.com/2016/01/21/the-49ers-relationship-with-kaepernick-becomes-even more-bizarre"));
        
        Assert.assertEquals("/106402933672/videos/10153671119698673/?theater", 
                Crawler.getUrlPathFrom("https://www.facebook.com/106402933672/videos/10153671119698673/?theater"));
        
        Assert.assertEquals("/", Crawler.getUrlPathFrom("http://www.augminish.com/"));
    }
    
    @Test
    public void addLinksToQueueTest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        String url = "http://augminish.com/tests/queue_test.html";
        String[] queuedUrls = {"http://augminish.com/tests/queue_test.html?link=0", "http://augminish.com/testing", 
                "http://augminish.com/testing/javascript_test.html", "http://augminish.com/index.html?redirect=true&testing=inProgress"
        };
        
        Document document = Jsoup.parse(wc.getPage(url).getWebResponse().getContentAsString());
        crawler.mockDocumentObject(document);
        crawler.queue(url);
        
        for (String qu : queuedUrls) {
            Assert.assertEquals(qu, crawler.getQueue().poll());
        }
        
        Assert.assertTrue(crawler.getQueue().isEmpty());
    }
}
