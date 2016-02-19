package com.augminish.app.index;

import com.augminish.app.common.util.file.FileHandler;
import com.augminish.app.common.util.mysql.MySQL;
import com.augminish.app.common.util.mysql.helper.SqlBuilder;
import com.augminish.app.common.util.object.PropertyHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Indexer extends Thread {

    private Queue<HashMap<String, Object>> queue;
    private static String cacheDir;
    private boolean running;

    private PropertyHashMap propertyHashMap;
    private FileHandler fileHandler;
    private MySQL mysql;

    private Document document;
    private boolean testing;

    public Indexer() {

        queue = new LinkedList<HashMap<String, Object>>();
        fileHandler = new FileHandler();
        mysql = new MySQL();

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
            // TODO: [LOGGER] Log any unforeseen RuntimeException thrown by the crawler
            re.printStackTrace();
        }
        catch (Exception e) {
            // TODO: [LOGGER] Log all missed Exception that were thrown by the crawler
            e.printStackTrace();
        }
    }

    protected void index() throws IOException, Exception {

        loadIndex();
        while (running) {

            HashMap<String, Object> index = queue.poll();
            document = Jsoup.parse(fileHandler.read(constructFilePath(index)));

            if (queue.isEmpty() && !testing) {
                loadIndex();
            }
        }
    }

    private void loadIndex() throws IOException {

        propertyHashMap = new PropertyHashMap();
        queue.addAll(mysql.select(SqlBuilder.select("WebSites", "id", "domain", "url", "secure", "hash", "indexed", "needsUpdate", "lastUpdate").where(
                "WebSites.indexed = 0 AND WebSites.needsUpdate = 0").commit()));

        cacheDir = propertyHashMap.get("file.cache");
        running = !queue.isEmpty();
    }

    protected static String constructFilePath(HashMap<String, Object> index) {
        return cacheDir + "/" + index.get("domain") + "/" + index.get("hash");
    }

    protected void mockMySQLObject(MySQL mysql) {
        this.mysql = mysql;
    }

    protected void mockPropertyHashMapObject(PropertyHashMap propertyHashMap) {
        this.propertyHashMap = propertyHashMap;
    }

    protected void mockQueueObject(Queue<HashMap<String, Object>> queue) {
        this.queue = queue;
    }

    protected void mockFileHandlerObject(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    protected void mockCacheDirectory(String cacheDir) {
        Indexer.cacheDir = cacheDir;
    }

    protected MySQL getMySQLObject() {
        return mysql;
    }

    protected PropertyHashMap getPropertyHashMapObject() {
        return propertyHashMap;
    }

    protected Queue<HashMap<String, Object>> getQueue() {
        return queue;
    }

    protected FileHandler getFileHandlerObject() {
        return fileHandler;
    }

    protected String getCacheDir() {
        return Indexer.cacheDir;
    }
}