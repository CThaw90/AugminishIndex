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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    private HashMap<String, String> ignore;
    private Queue<String> queue;

    private PropertyHashMap propertyHashMap;
    private FileHandler filehandler;
    private MySQL mysql;

    private WebResponse response;
    private WebClient webClient;

    private Document document;

    public Crawler(List<String> seeds, List<String> ignored) {

        webClient = new WebClient(com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX_38);
        visited = new HashMap<String, String>();
        queue = new LinkedList<String>(seeds);
        filehandler = new FileHandler();
        mysql = new MySQL();
        ignore(ignored);
    }

    /*** Constructor used for JUnit testing purposes ***/
    protected Crawler() {
        visited = new HashMap<String, String>();
        queue = new LinkedList<String>();
    }

    @Override
    public void run() {
        try {
            crawl();
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

    protected void crawl() throws IOException {

        webClient.getOptions().setCssEnabled(false);
        propertyHashMap = new PropertyHashMap();

        loadOutdatedWebSites();
        loadVisitedWebSites();
        String url, content;

        while (!queue.isEmpty()) {
            try {
                if (!ignore.containsKey(getDomainFrom(queue.peek()))) {
                    response = webClient.getPage(queue.poll()).getWebResponse();
                    url = response.getWebRequest().getUrl().toExternalForm();
                    content = response.getContentAsString();
        
                    if (save(url, content)) {
                        // TODO: Place this logic in another thread for efficiency
                        document = Jsoup.parse(stripHtmlCodes(content));
                        queue(url);
                    }
                }
            }
            catch (FailingHttpStatusCodeException fhse) {
                // TODO: Store Http Status Code Exception for human investigation
            }
            catch (MalformedURLException mue) {
                // TODO: Store a malformed Url for human investigation
            }
        }

        // TODO: [LOGGER] Publicly log Crawler has run out of queues
    }
    
    protected void queue(String url) {
        
        Elements attributes = document.child(0).select("a");
        for (Element href : attributes) {
            if (href.hasAttr("href") && !href.attr("href").replaceAll("[\\s]+", "").isEmpty()) {
                queue.add(buildFullUrl(url, href.attr("href")));
            }
        }
    }
    
    protected static String buildFullUrl(String parent, String child) {
        
        StringBuilder url = new StringBuilder();
        
        if (!child.matches("http(s)?://.*")) {
            url.append(parent.replaceAll("//.*", ""));
            url.append("//" + getDomainFrom(parent));
            if (!child.startsWith("/")) {
                url.append(getUrlPathFrom(parent));
            }
        }
                
        url.append(child);
        return url.toString();
    }

    protected String uniqueHash(String url) {
        
        String hash = org.apache.commons.codec.binary.Base64.encodeBase64String(url.getBytes());
        int fullLength = hash.length(), offset = 0, start = 0, end = 7;
        boolean unique = false;

        while (!unique && !(start == 0 && end > fullLength)) {
            String subset = hash.substring(start + offset, end + offset);
            unique = !visited.containsKey(subset);
            if (!unique && !visited.get(subset).equals(url)) {
                offset = (offset + end < fullLength ? offset + 1 : 0);
                end = (end <= fullLength && offset == 0 ? end + 1 : end);
                
            } else if (!unique) {
                unique = false;
                break;
            }
        }

        return unique ? hash.substring(start + offset, end + offset) : null;
    }
    
    protected static int getProtocolFrom(String url) {
        return url.startsWith("https") ? 1 : 0;
    }

    protected static String getDomainFrom(String url) {
        return url.replaceAll("http(s)?://", "").replaceAll("/.*", "");
    }

    protected static String getUrlPathFrom(String url) {
        return url.replaceAll("http(s)?://[\\w]+\\.(.*?)(?=/)", "");
    }

    private boolean save(String url, String content) throws IOException {
        String secure = String.valueOf(getProtocolFrom(url)), domain = getDomainFrom(url), 
                path = getUrlPathFrom(url), hash = uniqueHash(url);
        boolean saved = hash != null && 
                filehandler.save(propertyHashMap.get("file.cache") + "/" + domain, hash, content) && 
                mysql.insert(SqlBuilder.insert(WebSitesTable, "domain", "url", "secure", "hash").values(domain, path, secure, hash).commit());
        
        if (saved) {
            visited.put(hash, url);
        }

        return saved;
    }

    private void loadVisitedWebSites() {

        List<HashMap<String, Object>> values = new ArrayList<HashMap<String, Object>>();
        boolean connected = mysql.isConnected();
        if (!connected) {
            connected = mysql.use(propertyHashMap.get("mysql.database"));
        }
        if (connected) {
            values = mysql.select(SqlBuilder.select(WebSitesTable, "domain", "url", "secure", "hash", "needsUpdate").where("needsUpdate = 0").commit());
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
            values = mysql.select(SqlBuilder.select(WebSitesTable, "domain", "url", "secure", "hash", "needsUpdate").where("needsUpdate = 1").commit());
            for (HashMap<String, Object> value : values) {
                queue.add(buildFullUrl(value));
            }
        }
        else {
            // TODO: Create a recovery algorithm for a failed MySQL connection
        }
    }
    
    private void ignore(List<String> ignored) {
        ignore = new HashMap<String, String>();
        for (String _ignore : ignored) {
            ignore.put(getDomainFrom(_ignore), _ignore);
        }
    }
    
    protected static String stripHtmlCodes (String content) {
        return content.replaceAll("&(.*?);", " ");
    }

    protected static String buildFullUrl(HashMap<String, Object> webSiteRow) {
        StringBuilder fullUrl = new StringBuilder();
        fullUrl.append((Integer) webSiteRow.get("secure") == 1 ? "https://" : "http://");
        fullUrl.append(webSiteRow.get("domain")).append(webSiteRow.get("url"));
        return fullUrl.toString();
    }

    /*** Methods used for JUnit testing purposes ONLY ***/

    protected void addVisitedUrl(String hash, String url) {
        visited.put(hash, url);
    }

    protected String getVisitedUrl(String key) {
        return visited.get(key);
    }
    
    protected void mockDocumentObject(Document document) {
        this.document = document;
    }
    
    protected Queue<String> getQueue() {
        return queue;
    }
}
