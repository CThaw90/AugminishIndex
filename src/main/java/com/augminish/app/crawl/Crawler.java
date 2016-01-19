package com.augminish.app.crawl;

import com.augminish.app.common.util.file.FileHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Crawler extends Thread {

    private HashMap<String, String> visited;
    private Queue<String> queue;

    private FileHandler filehandler;

    private WebResponse response;
    private WebClient webclient;

    private Document document;

    public Crawler(List<String> seeds) {

        webclient = new WebClient(com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX_38);
        queue = new LinkedList<String>(seeds);
        filehandler = new FileHandler();
    }

    protected Crawler() {

    }

    @Override
    public void run() {
        try {
            crawl();

        }
        catch (FailingHttpStatusCodeException fhse) {
            // TODO: Store Http Status Code Exception for human investigation
        }
        catch (MalformedURLException mue) {
            // TODO: Store a malformed Url for human investigation
        }
        catch (IOException ie) {
            // TODO: [LOGGER] Log that crawler has thrown an IOException
        }
        catch (RuntimeException re) {
            // TODO: [LOGGER] Log any unforeseen RuntimeException thrown by the crawler
        }
        catch (Exception e) {
            // TODO: [LOGGER] Log all missed Exceptions that were thrown by the crawler
        }
    }

    protected void crawl() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

        String url, domain, content;
        while (!queue.isEmpty()) {

            response = webclient.getPage(queue.poll()).getWebResponse();
            url = response.getWebRequest().getUrl().toExternalForm();
            content = response.getContentAsString();
            domain = getDomainFrom(url);

            saveToFile(domain, verifyHash(url), content);
            // TODO: Place this logic in another thread for efficiency
            document = Jsoup.parse(content);
        }

        // TODO: [LOGGER] Publicly log Crawler has run out of queues
    }

    protected String verifyHash(String url) {
        return url;
    }

    protected String getDomainFrom(String url) {
        return url.replaceAll("http(s)?://", "").replaceAll("/.*", "");
    }

    private void saveToFile(String domain, String url, String content) throws IOException {
        String hashedUrl = org.apache.commons.codec.binary.Base64.encodeBase64String(url.getBytes()).substring(0, 7);
        if (filehandler.save(domain + "/" + hashedUrl, content)) {

        }
        else {
            throw new IOException("Could not save file");
        }
    }
}
