package com.augminish.app.common.util;

import org.junit.Assert;
import org.junit.Test;

public class SqlBuilderTest {
    
    private static final String CREATE_DATABASE_VALUE = "CREATE DATABASE IF NOT EXISTS TESTING DATABASE;";
    private static final String CREATE_TABLE_VALUE = "CREATE TABLE IF NOT EXISTS TESTING TABLE ( " + 
                                                     "id INT(11) NOT NULL AUTO_INCREMENT," + 
                                                     "testString VARCHAR(512) NOT NULL," +
                                                     "PRIMARY KEY (id) );";
    private static final String DATABASE_NAME = "TESTING DATABASE";
    private static final String TABLE_NAME = "TESTING TABLE";
    
    @Test
    public void createDatabaseTest() {
        Assert.assertEquals("SqlBuilder should return a valid database query", 
                CREATE_DATABASE_VALUE, SqlBuilder.createDatabase(DATABASE_NAME));
    }
    
    @Test
    public void createTableTest() {
        Assert.assertEquals("SqlBuilder should return a valid table query", 
                CREATE_TABLE_VALUE, SqlBuilder.createTable(TABLE_NAME, 
                        "id INT(11) NOT NULL AUTO_INCREMENT",
                        "testString VARCHAR(512) NOT NULL",
                        "PRIMARY KEY (id)"));
    }
    
    public void insertIntoTest() {
        
    }
    
    public void withValuesTest() {
        
    }
}
