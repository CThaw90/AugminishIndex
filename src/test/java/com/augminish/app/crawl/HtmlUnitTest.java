package com.augminish.app.crawl;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class HtmlUnitTest {

    private static final String RESPONSE = "<!DOCTYPE><html><head><title>Javascript Test</title></head><body><h3>It Works!!</h3><p>Javascript should be enabled</p></body></html>";
    private WebClient webClient = null;
    private WebResponse response = null;

    @Test
    public void downloadWebpageWithHtmlUnitTest() throws FailingHttpStatusCodeException, IOException {

        webClient = new WebClient();
        response = webClient.getPage("http://augminish.com/tests/javascript_test.html").getWebResponse();

        Assert.assertEquals("WebPage response should match as expected", RESPONSE, response.getContentAsString());
    }
}
