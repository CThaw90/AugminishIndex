package com.augminish.app.common.util.mysql;

import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.Assert;
// CREATE TABLE IndexerTests (id INT(11) NOT NULL AUTO_INCREMENT, testString VARCHAR(256) NOT NULL, PRIMARY KEY (id));
public class MySQLTest {

    private static MySQL mysql;
    
    @BeforeClass
    public static void init() {
        mysql = new MySQL();
    }
    
    @Test
    public void mysqlConnectTest() {
        
        Assert.assertFalse("MySQL should not yet be connected", mysql.isConnected());
        Assert.assertTrue("MySQL Driver should successfully connect", mysql.connect());
        Assert.assertTrue("MySQL should be connected", mysql.isConnected());
    }
}
