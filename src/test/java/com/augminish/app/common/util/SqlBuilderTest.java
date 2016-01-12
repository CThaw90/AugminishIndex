package com.augminish.app.common.util;

import org.junit.Assert;
import org.junit.Test;

public class SqlBuilderTest {
    
    private static final String CREATE_DATABASE_VALUE = "CREATE DATABASE IF NOT EXISTS TESTING_DATABASE;";
    private static final String CREATE_TABLE_VALUE = "CREATE TABLE IF NOT EXISTS TESTING_TABLE ( " + 
                                                     "id INT(11) NOT NULL AUTO_INCREMENT," + 
                                                     "testString VARCHAR(512) NOT NULL," +
                                                     "PRIMARY KEY (id) );";
    private static final String INSERT_INTO_TABLE = "INSERT INTO TESTING_TABLE ( id,testString )";
    private static final String WITH_VALUES = " VALUES ( ?&12345,?&RANDOMLY_INSERTED_STRING );";
    private static final String DATABASE_NAME = "TESTING_DATABASE";
    private static final String TABLE_NAME = "TESTING_TABLE";
    
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
    
    @Test(expected = RuntimeException.class)
    public void prematureCommitTest() {
        Assert.assertEquals("SqlBuilder should throw RuntimeException when premature commit method invoked", 
                INSERT_INTO_TABLE, SqlBuilder.insertInto(TABLE_NAME, "id", "testString").commit());
    }
    
    @Test
    public void inserIntoWithValuesTest() {
        Assert.assertEquals("SqlBuilder should return a valid Insert query with values", 
                INSERT_INTO_TABLE + WITH_VALUES, 
                SqlBuilder.insertInto(TABLE_NAME, "id", "testString").withValues("12345", "RANDOMLY_INSERTED_STRING").commit());
    }
}
