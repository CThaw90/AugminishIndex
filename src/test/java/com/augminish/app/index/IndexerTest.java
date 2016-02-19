package com.augminish.app.index;

import com.augminish.app.common.util.mysql.MySQL;
import com.augminish.app.common.util.object.PropertyHashMap;

import org.junit.Test;

import java.io.IOException;

public class IndexerTest {

    private static final Boolean isTesting = Boolean.TRUE;

    @Test
    public void indexSimulationTest() throws IOException, Exception {

        Indexer indexer = new Indexer(isTesting);

        PropertyHashMap propertyHashMap = new PropertyHashMap("./.ignore/config-test.properties");
        indexer.mockPropertyHashMapObject(propertyHashMap);

        MySQL mysql = new MySQL();
        mysql.use("AugminishTest");
        indexer.mockMySQLObject(mysql);

    }
}