package com.augminish.app.common.util.mysql;

import org.junit.BeforeClass;
import org.junit.Test;

import com.augminish.app.common.util.mysql.helper.SqlBuilder;
import com.augminish.app.common.util.strings.StaticString;

import org.junit.Assert;

public class MySQLTest {
    
    private static MySQL mysql;
    
    @BeforeClass
    public static void init() {
        mysql = new MySQL();
    }
    
    @Test
    public void mysqlConnectTest() {
        makeSureMySQLisDisconnected();
        
        Assert.assertFalse(StaticString.MYSQL_CONNECT_TEST_DISCONNECTED_ASSERT_DESCRIPTION, mysql.isConnected());
        Assert.assertTrue(StaticString.MYSQL_CONNECT_TEST_SHOULD_CONNECT_ASSERT_DESCRIPTION, mysql.connect());
        Assert.assertTrue(StaticString.MYSQL_CONNECT_TEST_SHOULD_BE_CONNECTED_ASSERT_DESCRIPTION, mysql.isConnected());
        Assert.assertTrue(StaticString.MYSQL_CONNECT_TEST_DISCONNECT_ASSERT_DESCRIPTION, mysql.disconnect());
        Assert.assertFalse(StaticString.MYSQL_CONNECT_TEST_DISCONNECTED_ASSERT_DESCRIPTION, mysql.isConnected());
    }
    
    @Test
    public void mysqlCreateTest() {
        makeSureMySQLisConnected();
        
        Assert.assertFalse(StaticString.MYSQL_CREATE_TEST_INVALID_QUERY_ASSERT_DESCRIPTION, mysql.create(StaticString.CREATE_ERROR));
        Assert.assertFalse(StaticString.MYSQL_CREATE_TEST_INVALID_QUERY_ASSERT_DESCRIPTION, mysql.create(StaticString.CREATE_ERROR_X));
        Assert.assertTrue(StaticString.MYSQL_CREATE_TEST_CREATE_DB_ASSERT_DESCRIPTION, 
                mysql.create(SqlBuilder.createDatabase(StaticString.TEST_DB).commit()));
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
    
 // CREATE TABLE IndexerTests (id INT(11) NOT NULL AUTO_INCREMENT, testString VARCHAR(512) NOT NULL, PRIMARY KEY (id)); //
    private static void createTestTable() {
        mysql.create(SqlBuilder.createTable(StaticString.TEST_TABLE, 
                "id INT(11) NOT NULL AUTO_INCREMENT", "testString VARCHAR(512) NOT NULL", "PRIMARY KEY (id)").commit());
    }
}
