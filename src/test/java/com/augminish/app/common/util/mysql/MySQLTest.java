package com.augminish.app.common.util.mysql;

import org.junit.BeforeClass;
import org.junit.Test;

import com.augminish.app.common.util.SqlBuilder;

import org.junit.Assert;

public class MySQLTest {
    
    private static final String CREATE_ERROR_X = "CREATEERROR TABLE";
    private static final String CREATE_ERROR = "DO NOT CREATE";
    private static final String TEST_TABLE = "IndexerTests";
    private static final String TEST_DB = "Test";
    private static MySQL mysql;
    
    @BeforeClass
    public static void init() {
        mysql = new MySQL();
    }
    
    @Test
    public void mysqlConnectTest() {
        makeSureMySQLisDisconnected();
        
        Assert.assertFalse("MySQL should not yet be connected", mysql.isConnected());
        Assert.assertTrue("MySQL Driver should successfully connect", mysql.connect());
        Assert.assertTrue("MySQL should be connected", mysql.isConnected());
        Assert.assertTrue("MySQL Driver should disconnect successfully", mysql.disconnect());
        Assert.assertFalse("MySQL should not be connected", mysql.isConnected());
    }
    
    @Test
    public void mysqlCreateTest() {
        makeSureMySQLisConnected();
        
        Assert.assertFalse("MySQL should return false passing in a non creating query", mysql.create(CREATE_ERROR));
        Assert.assertFalse("MySQL should return false passing in a non creating query", mysql.create(CREATE_ERROR_X));
        Assert.assertTrue("MySQL should return true creating a database called Test", mysql.create(SqlBuilder.createDatabase(TEST_DB)));
    }
    
    @Test
    public void mysqlSelectTest() {
        createTestTable();
        
    }
    
    private static void makeSureMySQLisConnected() {
        if (mysql.isConnected() || mysql.connect()) {} else { throw new RuntimeException ("Could not connect to MySQL Server"); }
    }
    
    private static void makeSureMySQLisDisconnected() {
        if (!mysql.isConnected() || mysql.disconnect()) {}
    }
    
 // CREATE TABLE IndexerTests (id INT(11) NOT NULL AUTO_INCREMENT, testString VARCHAR(512) NOT NULL, PRIMARY KEY (id));
    private static void createTestTable() {
        mysql.create(SqlBuilder.createTable(TEST_TABLE, "id INT(11) NOT NULL AUTO_INCREMENT", "testString VARCHAR(512) NOT NULL", "PRIMARY KEY (id)"));
    }
    
    
}
