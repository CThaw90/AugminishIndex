package com.augminish.app.crawl;

import com.augminish.app.common.util.file.FileHandler;
import com.augminish.app.common.util.mysql.MySQL;
import com.augminish.app.common.util.mysql.helper.SqlBuilder;
import com.augminish.app.common.util.object.PropertyHashMap;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CrawlerTest {

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

        Crawler crawler = new Crawler();
        
        HashMap<String, String> visited = new HashMap<String, String>();
        String url = "https://newyork.craigslist.org/mus/search";

        crawler.mockVisitedObject(visited);
        Assert.assertNotNull(crawler.uniqueHash(url));
        
        visited.put(crawler.uniqueHash(url), url);
        crawler.mockVisitedObject(visited);
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
        
        String parent = "http://augminish.com/tests/crawltest/index.html";
        String[] childs = {"https://www.twitter.com/YeahISaidItUMad", "/tests/crawltest/profile.html", "followers.html", "following.html",
                "/tests/crawltest/settings.html", "/tests/crawltest/job_board.html", "notifications.html", "accounts.html"},
                 
                urls = {"https://www.twitter.com/YeahISaidItUMad", "http://www.augminish.com/tests/crawltest/profile.html",
                        "http://www.augminish.com/tests/crawltest/followers.html", "http://www.augminish.com/tests/crawltest/following.html",
                        "http://www.augminish.com/tests/crawltest/settings.html", "http://www.augminish.com/tests/crawltest/job_board.html",
                        "http://www.augminish.com/tests/crawltest/notifications.html", "http://www.augminish.com/tests/crawltest/accounts.html"};
        
        for (int i = 0; i < childs.length; i++) {
            Assert.assertEquals("Asserting buildFullUrl(String, String): ", urls[i], Crawler.buildFullUrl(parent, childs[i]));
        }
    }

    @Test
    public void getPathFromUrlTest() {

        Assert.assertEquals("/2016/01/21/the-49ers-relationship-with-kaepernick-becomes-even more-bizarre",
                Crawler.getUrlPathFrom("http://profootballtalk.nbcsports.com/2016/01/21/the-49ers-relationship-with-kaepernick-becomes-even more-bizarre"));

        Assert.assertEquals("/106402933672/videos/10153671119698673/?theater", Crawler.getUrlPathFrom("https://www.facebook.com/106402933672/videos/10153671119698673/?theater"));

        Assert.assertEquals("/", Crawler.getUrlPathFrom("http://www.augminish.com/"));
    }

    @Test
    public void addLinksToQueueTest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        String url = "http://augminish.com/tests/queue_test.html";
        String[] queuedUrls = { "http://www.augminish.com/tests/queue_test.html?link=0", "http://www.augminish.com/testing", "http://www.augminish.com/testing/javascript_test.html",
                "http://www.augminish.com/index.html?redirect=true&testing=inProgress" };

        WebClient webClient = new WebClient();
        Crawler crawler = new Crawler();
        
        Document document = Jsoup.parse(webClient.getPage(url).getWebResponse().getContentAsString());
        crawler.mockQueueObject(new LinkedList<String>());
        crawler.mockDocumentObject(document);
        crawler.queue(url);

        for (String qu : queuedUrls) {
            Assert.assertEquals(qu, crawler.getQueueObject().poll());
        }

        Assert.assertTrue(crawler.getQueueObject().isEmpty());
        webClient.close();
    }

    @Test
    public void crawlSimulationTest() throws IOException, Exception {
        
        Crawler crawler = new Crawler();
        
        WebClient webClient = new WebClient();
        WebClientOptions webClientOptions = webClient.getOptions();
        webClientOptions.setThrowExceptionOnScriptError(false);
        webClientOptions.setCssEnabled(false);
        crawler.mockWebClientObject(webClient);
         
        MySQL mysql = new MySQL();
        mysql.use("AugminishTest");
        crawler.mockMySQLObject(mysql);
        
        PropertyHashMap propertyHashMap = new PropertyHashMap("./.ignore/cache/config-test.properties");
        crawler.mockPropertyHashMapObject(propertyHashMap);
        
        FileHandler fileHandler = new FileHandler();
        fileHandler.rmdir("./.ignore/cache/www.augminish.com");
        fileHandler.rmdir("./.ignore/cache/augminish.com");
        crawler.mockFileHandlerObject(fileHandler);
        
        HashMap<String, String> ignore = new HashMap<String, String>();
        ignore.put("www.facebook.com", "https://facebook.com/");
        ignore.put("www.twitter.com", "https://twitter.com/");
        crawler.mockIgnoredObject(ignore);
        
        HashMap<String, String> visited = new HashMap<String, String>();
        crawler.mockVisitedObject(visited);
        
        Queue<String> queue = new LinkedList<String>();
        queue.add("http://augminish.com/tests/crawltest/index.html");
        crawler.mockQueueObject(queue);
        
        if(!mysql.query("TRUNCATE WebSites;")) {
            Assert.fail("[FATAL]: MySQL truncate query failed");
        }
        crawler.crawl();
        
        List<HashMap<String, Object>> sites = mysql.select(SqlBuilder.select("WebSites", "id", "domain", "url", "secure", "hash", "indexed", "needsUpdate", "lastUpdate").commit());
        String[] urls = {"http://augminish.com/tests/crawltest/index.html?ckattempt=1", 
                "http://www.augminish.com/tests/crawltest/profile.html?ckattempt=1",
                "http://www.augminish.com/tests/crawltest/followers.html",
                "http://www.augminish.com/tests/crawltest/following.html",
                "http://www.augminish.com/tests/crawltest/settings.html",
                "http://www.augminish.com/tests/crawltest/job_board.html",
                "http://www.augminish.com/tests/crawltest/notifications.html",
                "http://www.augminish.com/tests/crawltest/accounts.html",
                "http://www.augminish.com/tests/crawltest/index.html",
                "http://www.augminish.com/tests/crawltest/profile.html"
        }, hash = {"aHR0cDo", "HR0cDov", "R0cDovL", "0cDovL3", "cDovL3d", "DovL3d3", "ovL3d3d", "vL3d3dy", "L3d3dy5", "3d3dy5h"};
        int index = 0;
        
        for (HashMap<String, Object> site : sites) {
            
            Assert.assertEquals("Should match id", index + 1, site.get("id"));
            Assert.assertEquals("Should match domain", Crawler.getDomainFrom(urls[index]), site.get("domain"));
            Assert.assertEquals("Should match url", Crawler.getUrlPathFrom(urls[index]), site.get("url"));
            Assert.assertEquals("Should match secure", 0, site.get("secure"));
            Assert.assertEquals("Should match hash", hash[index], site.get("hash"));
            Assert.assertEquals("Should match indexed", 0, site.get("indexed"));
            Assert.assertEquals("Should match needsUpdate", 0, site.get("needsUpdate"));
            Assert.assertNotNull("LastUpdate should not be Null", site.get("lastUpdate"));
            index++;
        }
        
        webClient.close();
    }
}
