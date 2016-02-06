package com.augminish.app.common.util.mysql;

import com.augminish.app.common.util.mysql.helper.SqlBuilder;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

public class MySQLTest {

    private static final String TEST_TABLE = "TestingTable";
    private static final String TEST_DB = "AugminishTest";
    private static MySQL mysql;

    @BeforeClass
    public static void init() {
        mysql = new MySQL();
    }

    @Test
    public void mysqlConnectTest() {
        makeSureMySQLisDisconnected();

        Assert.assertFalse("MySQL should not be connected", mysql.isConnected());
        Assert.assertTrue("MySQL Driver should successfully connect", mysql.connect());
        Assert.assertTrue("MySQL should be connected", mysql.isConnected());
        Assert.assertTrue("MySQL Driver should disconnect successfully", mysql.disconnect());
        Assert.assertFalse("MySQL should not be connected", mysql.isConnected());
    }

    @Test
    public void mysqlCreateTest() {
        makeSureMySQLisConnected();

        Assert.assertTrue("MySQL should return true using the designated testing database", mysql.use(TEST_DB));
        Assert.assertFalse("MySQL should return false passing in a non creating query", mysql.create("DO NOT CREATE"));
        Assert.assertFalse("MySQL should return false passing in a non creating query", mysql.create("CREATEERROR TABLE"));
        Assert.assertTrue("MySQL should return true creating a database called AugminishTests", mysql.create(SqlBuilder.createDatabase(TEST_DB).commit()));
        Assert.assertTrue("MySQL should return true create a table in AugminishTests", mysql.create(
                SqlBuilder.createTable(TEST_TABLE, "id INT(11) NOT NULL AUTO_INCREMENT", "testString VARCHAR(512) NOT NULL", "PRIMARY KEY (id)").commit()));
    }

    @Test
    public void mysqlSelectTest() {
        String seed = generateSeed(System.currentTimeMillis());
        
        makeSureMySQLisConnected();
        makeSureMySQLTestDatabaseExists();
        Assert.assertTrue(mysql.use(TEST_DB));
        Assert.assertTrue(makeSureMySQLTestTableExists());
        Assert.assertTrue(makeSureMySQLTestRowExists(seed));
        
        List<HashMap<String, Object>> result = mysql.select(SqlBuilder.select(TEST_TABLE, "id", "testString").where("testString='"+seed+"'").commit());
        Assert.assertEquals("MySQL should return a hashed data object with a matching test string", seed, result.get(0).get("testString"));
        Assert.assertFalse("MySQL should return one hashed data object in a list", result.isEmpty());
    }
    
    @Test
    public void mysqlUpdateTest() {
        
        String seed = generateSeed(System.currentTimeMillis()), previous = null;
        
        makeSureMySQLisConnected();
        makeSureMySQLTestDatabaseExists();
        Assert.assertTrue(mysql.use(TEST_DB));
        Assert.assertTrue(makeSureMySQLTestTableExists());
        Assert.assertTrue(makeSureMySQLTestRowExists(seed));
        
        List<HashMap<String, Object>> result = mysql.select(SqlBuilder.select(TEST_TABLE, "id", "testString").commit());
        previous = result.get(result.size() - 1).get("testString").toString();
        
        Assert.assertTrue(mysql.update(SqlBuilder.update(TEST_TABLE, "testString").values(seed).where("testString='"+previous+"'").commit()));
        result = mysql.select(SqlBuilder.select(TEST_TABLE, "id", "testString").where("testString='"+seed+"'").commit());
        
        Assert.assertFalse("MySQL updated row should exist", result.isEmpty());
        
    }

    private static void makeSureMySQLisConnected() {
        if (mysql.isConnected() || mysql.connect()) {
            return;
        }
        else {
            throw new RuntimeException("Could not connect to MySQL Server");
        }
    }

    private static void makeSureMySQLisDisconnected() {
        if (!mysql.isConnected() || mysql.disconnect()) {
        }
    }
    
    private static boolean makeSureMySQLTestDatabaseExists() {
        return mysql.create(SqlBuilder.createDatabase(TEST_DB).commit());
    }

    private static boolean makeSureMySQLTestTableExists() {
        return mysql.create(SqlBuilder.createTable(TEST_TABLE, "id INT(11) NOT NULL AUTO_INCREMENT", "testString VARCHAR(512) NOT NULL", "PRIMARY KEY (id)").commit());
    }
    
    private static boolean makeSureMySQLTestRowExists(String s) {
        return mysql.insert(SqlBuilder.insert(TEST_TABLE, "testString").values(s).commit());
    }
    
    private static String generateSeed(long timeInMillis) {
        return new BigInteger(String.valueOf(timeInMillis)).toString(32);
    }
}
