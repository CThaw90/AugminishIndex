package com.augminish.app;

import com.augminish.app.common.util.object.PropertyHashMap;
import com.augminish.app.crawl.Crawler;
import com.augminish.app.index.Indexer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static PropertyHashMap propertyHashMap;
    private Thread crawler, indexer;

    public static void main(String[] args) throws IOException {

        // Loading the configurations properties from config.properties file
        propertyHashMap = new PropertyHashMap();

        // Start Application
        new Main().start();
    }

    private void start() throws IOException {
        
        Runtime.getRuntime().addShutdownHook(new Thread(new CleanUpModule()));
        List<String> seed = Arrays.asList(propertyHashMap.getSeedAsArray()), 
                   ignore = Arrays.asList(propertyHashMap.getIgnoredAsArray());
        
        
        crawler = new Thread(new Crawler(seed, ignore), "com.augminish.app.crawl.Crawler");
        crawler.start();

        indexer = new Thread(new Indexer(propertyHashMap.get("file.cache")), "com.augminish.app.index.Indexer");
        indexer.start();

        try {
            crawler.join();
            indexer.join();
        }
        catch (InterruptedException ie) {
            // TODO: Log an interrupted exception happened
            ie.printStackTrace();
        }
    }
    
    private class CleanUpModule implements Runnable {
        
        public void run() {
            System.out.println("Augminish Indexer stopped");
        }
    }
}
