package com.augminish.app;

import com.augminish.app.crawl.Crawler;
import com.augminish.app.index.Indexer;

public class Main {

    private Thread crawler, indexer;
    private static long startTime;

    public static void main(String[] args) {

        // Start Application
        startTime = System.currentTimeMillis();
        new Main().start();
    }

    private void start() {

        Runtime.getRuntime().addShutdownHook(new Thread(new CleanUpModule()));

        crawler = new Thread(new Crawler(), "com.augminish.app.crawl.Crawler");
        crawler.start();

        indexer = new Thread(new Indexer(), "com.augminish.app.index.Indexer");
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

        @Override
        public void run() {
            System.out.println("Augminish Indexer stopped ===== ");
            System.out.println("Runtime : " + (System.currentTimeMillis() - startTime) + " ms.");
        }
    }
}
