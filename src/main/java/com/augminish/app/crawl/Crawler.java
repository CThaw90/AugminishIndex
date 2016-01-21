package com.augminish.app.crawl;

import com.augminish.app.common.util.file.FileHandler;
import com.augminish.app.common.util.mysql.MySQL;
import com.augminish.app.common.util.mysql.helper.SqlBuilder;
import com.augminish.app.common.util.object.PropertyHashMap;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Crawler extends Thread {

    private static final String WebSitesTable = "`SearchIndex`.WebSites";

    private HashMap<String, String> visited;
    private Queue<String> queue;

    private PropertyHashMap propertyHashMap;
    private FileHandler filehandler;
    private MySQL mysql;

    private WebResponse response;
    private WebClient webclient;

    private Document document;

    public Crawler(List<String> seeds) {

        webclient = new WebClient(com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX_38);
        visited = new HashMap<String, String>();
        queue = new LinkedList<String>(seeds);
        filehandler = new FileHandler();
        mysql = new MySQL();

    }

    /*** Constructor used for JUnit testing purposes ***/
    protected Crawler() {
        visited = new HashMap<String, String>();
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

        propertyHashMap = new PropertyHashMap();

        String url, domain, content;
        loadOutdatedWebSites();
        loadVisitedWebSites();

        while (!queue.isEmpty()) {

            response = webclient.getPage(queue.poll()).getWebResponse();
            url = response.getWebRequest().getUrl().toExternalForm();
            content = response.getContentAsString();
            domain = getDomainFrom(url);

            save(domain, url, content);
            // TODO: Place this logic in another thread for efficiency
            document = Jsoup.parse(content);
        }

        // TODO: [LOGGER] Publicly log Crawler has run out of queues
    }

    protected String uniqueHash(String hash) {

        int fullLength = hash.length(), offset = 0, start = 0, end = 7;
        boolean unique = false;

        while (!unique && !(start == 0 && end > fullLength)) {

            unique = !visited.containsKey(hash.substring(start + offset, end + offset));
            if (!unique) {
                offset = (offset + end < fullLength ? offset + 1 : 0);
                end = (end <= fullLength && offset == 0 ? end + 1 : end);
            }
        }

        return unique ? hash.substring(start + offset, end + offset) : null;
    }

    protected String getDomainFrom(String url) {
        return url.replaceAll("http(s)?://", "").replaceAll("/.*", "");
    }

    protected String getURIComponentFrom(String url) {
        return null;
    }

    private void save(String domain, String url, String content) throws IOException {
        String hashedUrl = uniqueHash(org.apache.commons.codec.binary.Base64.encodeBase64String(url.getBytes()));
        if (filehandler.save(domain + "/" + hashedUrl, content)) {

        }
        else {
            throw new IOException("Could not save file");
        }
    }

    private void loadVisitedWebSites() {

        List<HashMap<String, Object>> values = new ArrayList<HashMap<String, Object>>();
        boolean connected = mysql.isConnected();
        if (!connected) {
            connected = mysql.use(propertyHashMap.get("mysql.database"));
        }
        if (connected) {
            values = mysql.select(SqlBuilder.select(WebSitesTable, "domain", "url", "secure", "hash", "needsUpdate").where("needsUpdate == 0").commit());
            for (HashMap<String, Object> value : values) {
                visited.put((String) value.get("hash"), buildFullUrl(value));
            }
        }
        else {
            // TODO: Create a recovery algorithm for a failed MySQL connection
        }
    }

    private void loadOutdatedWebSites() {

        List<HashMap<String, Object>> values = new ArrayList<HashMap<String, Object>>();
        boolean connected = mysql.isConnected();
        if (!connected) {
            connected = mysql.use(propertyHashMap.get("mysql.database"));
        }
        if (connected) {
            values = mysql.select(SqlBuilder.select(WebSitesTable, "domain", "url", "secure", "hash", "needsUpdate").where("needsUpdate == 1").commit());
            for (HashMap<String, Object> value : values) {
                queue.add(buildFullUrl(value));
            }
        }
        else {
            // TODO: Create a recovery algorithm for a failed MySQL connection
        }
    }

    protected static String buildFullUrl(HashMap<String, Object> webSiteRow) {
        StringBuilder fullUrl = new StringBuilder();
        fullUrl.append((Integer) webSiteRow.get("secure") == 1 ? "https://" : "http://");
        fullUrl.append(webSiteRow.get("domain")).append(webSiteRow.get("url"));
        return fullUrl.toString();
    }

    /*** Methods used for JUnit testing purposes ***/

    protected void addVisitedUrl(String hash, String url) {
        visited.put(hash, url);
    }

    protected String getVisitedUrl(String key) {
        return visited.get(key);
    }
}
