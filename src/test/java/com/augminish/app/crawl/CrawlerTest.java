package com.augminish.app.crawl;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CrawlerTest {

    private static Crawler crawler;

    @BeforeClass
    public static void initialize() {

        crawler = new Crawler();
    }

    @Test
    public void getDomainFromUrlTest() {
        String craigslist_url = "https://newjersey.craigslist.org/mus/search/gig73821.html", 
               amazon_url = "https://www.amazon.com/ecommerce/index.html",
               regex_url = "www.regular-expressions.info/optional.html";

        Assert.assertEquals("Should extract the Amazon Domain name", "www.amazon.com", crawler.getDomainFrom(amazon_url));
        Assert.assertEquals("Should extract the Craigslist Domain name", "newjersey.craigslist.org", crawler.getDomainFrom(craigslist_url));
        Assert.assertEquals("Should extract the Regex Domain name", "www.regular-expressions.info", crawler.getDomainFrom(regex_url));
    }
}
