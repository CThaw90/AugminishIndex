package com.augminish.app.index;

import com.augminish.app.common.util.file.FileHandler;
import com.augminish.app.common.util.mysql.MySQL;
import com.augminish.app.common.util.mysql.helper.SqlBuilder;
import com.augminish.app.common.util.object.PropertyHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Indexer extends Thread {

    private Queue<HashMap<String, Object>> queue;
    private static String cacheDir;
    private boolean running;

    private FileHandler fileHandler;
    private MySQL mysql;

    private Document document;

    private String WebSitesTable;

    private boolean skipLoadingIndex;
    private boolean testing;

    public Indexer() {
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutDown()));
    }

    // Constructor for unit testing purposes only
    protected Indexer(boolean testing) {
        this.testing = testing;
    }

    @Override
    public void run() {
        try {
            index();
        }
        catch (IOException ie) {
            // TODO: [LOGGER] Log that indexer has thrown an IOException
            ie.printStackTrace();
        }
        catch (RuntimeException re) {
            // TODO: [LOGGER] Log any unforeseen RuntimeException thrown by the indexer
            re.printStackTrace();
        }
        catch (Exception e) {
            // TODO: [LOGGER] Log all missed Exception that were thrown by the indexer
            e.printStackTrace();
        }
    }

    protected void index() throws IOException, Exception {
        loadIndex();
        while (running) {
            try {
                HashMap<String, Object> index = queue.poll();
                document = Jsoup.parse(fileHandler.read(constructFilePath(index)));
                index(document, index);

                if (queue.isEmpty() && !testing) {
                    Thread.sleep(1000);
                    loadIndex();
                }

                running = !queue.isEmpty();
            }
            catch (IOException ie) {
                // TODO: [LOGGER] Log that indexer has thrown an IOException
                throw new IOException(ie);
            }
        }
    }

    private void index(Document document, HashMap<String, Object> webpageInfo) {
        Elements elements = document.getAllElements();
        List<HashMap<String, Object>> result;
        boolean mysqlSuccess = false;
        for (Element element : elements) {

            if (isNotRootDocument(element)) {
                String tagName = element.nodeName(), hasContent = element.ownText().isEmpty() ? "0" : "1";
                int webSiteId = (int) webpageInfo.get("id");
                mysqlSuccess = mysql.insert(SqlBuilder.insert("HyperTexts", "tag", "webSiteId", "hasContent").values(
                        tagName, String.valueOf(webSiteId), hasContent).commit());
                if (hasContent.equals("1") && mysqlSuccess) {
                    result = mysql.select(SqlBuilder.select("HyperTexts", "MAX(id) AS id").where(
                            "webSiteId = " + webSiteId + " group by webSiteId").commit());

                    mysqlSuccess = mysql.insert(SqlBuilder.insert("Content", "hyperTextId", "content").values(
                            String.valueOf(result.get(0).get("id")), element.ownText()).commit());

                    if (mysqlSuccess) {
                        HashMap<String, Integer> wordFrequency = getWordFrequency(element.ownText());
                        for (String word : wordFrequency.keySet()) {
                            mysqlSuccess = mysql.insert(SqlBuilder.insert("WordFrequency", "word", "frequency", "hyperTextId", "webSiteId").values(
                                    word, wordFrequency.get(word).toString(), String.valueOf(result.get(0).get("id")), String.valueOf(webSiteId)).commit());
                        }
                    }
                }
            }
        }

        if (mysqlSuccess) {
            mysql.update(SqlBuilder.update(WebSitesTable, "indexed").values("1").where("id = " + webpageInfo.get("id")).commit());
        }
        else {
            // TODO: [LOGGER] Log that this webpage could not be properly indexed due to mysql insertion error
        }
    }

    private void loadIndex() throws IOException {
        queue = new LinkedList<HashMap<String, Object>>();
        fileHandler = new FileHandler();
        load(new PropertyHashMap());
        mysql = new MySQL(true);
        if (skipLoadingIndex)
            return;

        queue.addAll(mysql.select(SqlBuilder.select(WebSitesTable, "id", "domain", "url", "secure", "hash", "indexed", "needsUpdate", "lastUpdate").where(
                WebSitesTable + ".indexed = 0 AND " + WebSitesTable + ".needsUpdate = 0").commit()));

        running = !queue.isEmpty();
    }

    private void load(PropertyHashMap propertyHashMap) {
        if (propertyHashMap.contains("indexer.cacheTable")) {
            WebSitesTable = propertyHashMap.get("indexer.cacheTable");
        }
        else {
            WebSitesTable = "WebSites";
        }

        if (propertyHashMap.contains("indexer.cacheDir")) {
            cacheDir = propertyHashMap.get("indexer.cacheDir");
        }
        else if (propertyHashMap.contains("file.cache")) {
            cacheDir = propertyHashMap.get("file.cache");
        }
        else {
            cacheDir = "./.ignore/cache";
        }
    }

    protected static HashMap<String, Integer> getWordFrequency(String text) {
        HashMap<String, Integer> wordFrequency = new HashMap<String, Integer>();
        String[] words = sanitize(text).split(" ");
        for (String word : words) {

            if (wordFrequency.containsKey(word.trim())) {
                wordFrequency.put(word.trim(), wordFrequency.get(word.trim()) + 1);
            }
            else if (!word.trim().isEmpty()) {
                wordFrequency.put(word.trim(), 1);
            }
        }

        return wordFrequency;
    }

    protected static String sanitize(String data) {
        return data.replaceAll("[^\\w\\s]", "");
    }

    protected static boolean isNotRootDocument(Element element) {
        return !element.nodeName().equals("#document");
    }

    protected static String constructFilePath(HashMap<String, Object> index) {
        return cacheDir + "/" + index.get("domain") + "/" + index.get("hash");
    }

    protected void skipLoadingIndex() {
        skipLoadingIndex = true;
    }

    private class ShutDown implements Runnable {

        @Override
        public void run() {
            System.out.println("Augminish Indexer Module stopped ===== ");
            System.out.println("Remaining WebSites to be indexed: ");
            for (HashMap<String, Object> q : queue) {

                System.out.print(createRow(q.get("id")));
                System.out.print(createRow(q.get("domain")));
                System.out.print(createRow(q.get("url")));
                System.out.print(createRow(q.get("secure")));
                System.out.print(createRow(q.get("hash")));
                System.out.println(createRow(q.get("lastUpdate")));

                System.out.print(createValue(q.get("id")));
                System.out.print(createValue(q.get("domain")));
                System.out.print(createValue(q.get("url")));
                System.out.print(createValue(q.get("secure")));
                System.out.print(createValue(q.get("hash")));
                System.out.print(createValue(q.get("lastUpdate")));
            }
        }

        private String createRow(Object o) {
            StringBuilder row = new StringBuilder("--");
            int index = 0, length = strlen(o);
            while (index++ < length) {
                row.append("-");
            }

            return row.toString();
        }

        private int strlen(Object o) {
            return o.toString().length();
        }

        private String createValue(Object o) {
            StringBuilder value = new StringBuilder("| ");
            value.append(o).append(" ");
            return value.toString();
        }
    }
}