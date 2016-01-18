package com.augminish.app;

import com.augminish.app.common.util.object.PropertyHashMap;
import com.augminish.app.crawl.Crawler;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    
    private static PropertyHashMap propertyHashMap;
    private static final String SEED = "seed";
    private Thread crawler, indexer;
    
    
    public static void main(String[] args) throws IOException {
        
        // Loading the configurations properties from config.properties file
        propertyHashMap = new PropertyHashMap();
        
        // Start Application
        new Main().start();
    }
    
    private void start() throws IOException {
        
        crawler = new Thread(new Crawler(Arrays.asList(propertyHashMap.get(SEED).split(","))), "com.augminish.app.crawl.Crawler");
        crawler.start();
        
        indexer = new Thread();
        indexer.start();
        
        try {
            crawler.join();
            indexer.join();
        } catch (InterruptedException ie) {
            // TODO: Log an interrupted exception happened
            ie.printStackTrace();
        }
    }
}
