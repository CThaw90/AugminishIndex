package com.augminish.app.index;

import com.augminish.app.common.util.file.FileHandler;
import com.augminish.app.common.util.mysql.MySQL;
import com.augminish.app.common.util.object.PropertyHashMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class IndexerTest {

    private static final Boolean isTesting = Boolean.TRUE;

    @BeforeClass
    public static void init() throws IOException {
        PropertyHashMap p = new PropertyHashMap("./.ignore/config-test.properties");
        Assert.assertTrue(p.contains("file.cache"));
    }

    @Test
    public void indexSimulationTest() throws IOException, Exception {

        Indexer indexer = new Indexer(isTesting);

        indexer.mockPropertyHashMapObject(new PropertyHashMap());

        MySQL mysql = new MySQL();
        mysql.use("AugminishTest");
        indexer.mockMySQLObject(mysql);

        indexer.mockQueueObject(new LinkedList<HashMap<String, Object>>());
        indexer.mockFileHandlerObject(new FileHandler());

        Assert.assertTrue(mysql.query("TRUNCATE HyperTexts;"));
        indexer.index();
    }
}