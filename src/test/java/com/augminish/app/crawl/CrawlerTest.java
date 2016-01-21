package com.augminish.app.crawl;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

public class CrawlerTest {

    private static Crawler crawler;

    @BeforeClass
    public static void initialize() {

        crawler = new Crawler();
    }

    @Test
    public void getDomainFromUrlTest() {
        String craigslistUrl = "https://newjersey.craigslist.org/mus/search/gig73821.html", amazonUrl = "https://www.amazon.com/ecommerce/index.html",
                regexUrl = "www.regular-expressions.info/optional.html", ec2Url = "ec2-54-196-32-193.compute-1.amazonaws.com/projects/lens-platform";

        Assert.assertEquals("Should extract the Amazon Domain name", "www.amazon.com", crawler.getDomainFrom(amazonUrl));
        Assert.assertEquals("Should extract the Craigslist Domain name", "newjersey.craigslist.org", crawler.getDomainFrom(craigslistUrl));
        Assert.assertEquals("Should extract the Regex Domain name", "www.regular-expressions.info", crawler.getDomainFrom(regexUrl));
        Assert.assertEquals("Should extract the EC2 Instance Domain name", "ec2-54-196-32-193.compute-1.amazonaws.com", crawler.getDomainFrom(ec2Url));
    }

    @Test
    public void uniqueHashTest() {
        String[] hashPermutation = { "1234567", "2345678", "3456789", "4567890", "12345678", "23456789", "34567890", "123456789", "234567890", "1234567890" };
        String hash = "1234567890";

        for (String uniqueHash : hashPermutation) {
            Assert.assertEquals("Crawler unique hashing algorithm should match as expected", uniqueHash, crawler.uniqueHash(hash));
            crawler.addVisitedUrl(uniqueHash, hash);
        }

        Assert.assertNull("Crawler unique hashing algorithm should return null after running out of hashing schemes", crawler.uniqueHash(hash));
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
}
