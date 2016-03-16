package com.augminish.app.common.util.mysql.helper;

import org.junit.Assert;
import org.junit.Test;

public class SqlBuilderTest {

    private static final String INSERT_WITH_SQL_ENTITY = "INSERT INTO TestingTable ( id,testString ) VALUES ( 12345,'RANDOMLY_INSERTED_STRING' );";
    private static final String INSERT_SQL = "INSERT INTO TestingTable ( id,testString ) VALUES ( '12345','RANDOMLY_INSERTED_STRING' );";

    private static final String TEST_TABLE = "TestingTable";
    private static final String TEST_DB = "AugminishTest";

    @Test
    public void createDatabaseTest() {
        Assert.assertEquals("SqlBuilder should return a valid database query",
                "CREATE DATABASE IF NOT EXISTS " + TEST_DB + ";", SqlBuilder.createDatabase(TEST_DB).commit());
    }

    @Test
    public void createTableTest() {
        Assert.assertEquals("SqlBuilder should return a valid table query",
                "CREATE TABLE IF NOT EXISTS TestingTable ( id INT(11) NOT NULL AUTO_INCREMENT,testString VARCHAR(512) NOT NULL,PRIMARY KEY (id) );",
                SqlBuilder.createTable(TEST_TABLE, "id INT(11) NOT NULL AUTO_INCREMENT", "testString VARCHAR(512) NOT NULL", "PRIMARY KEY (id)").commit());
    }

    @Test(expected = RuntimeException.class)
    public void prematureCommitTest() {
        Assert.assertEquals("SqlBuilder should throw RuntimeException when premature commit method invoked", INSERT_SQL,
                SqlBuilder.insert(TEST_TABLE, "id", "testString").commit());
    }

    @Test
    public void resetTest() {
        SqlBuilder.insert(TEST_TABLE, "id", "testString");
        Assert.assertNotNull("The SqlBuilder cache should not be null", SqlBuilder.getCache());
        SqlBuilder.reset();
        Assert.assertNull("The SqlBuilder cache should be set to null", SqlBuilder.getCache());
    }

    @Test
    public void insertIntoWithValuesTest() {
        Assert.assertEquals("SqlBuilder should return a valid Insert query with values", INSERT_SQL,
                SqlBuilder.insert(TEST_TABLE, "id", "testString").values("12345", "RANDOMLY_INSERTED_STRING").commit());
    }

    @Test
    public void insertIntoWithSqlEntityValuesTest() {
        Assert.assertEquals("SqlBuilder should return a valid Insert query with sql entity values", INSERT_WITH_SQL_ENTITY,
                SqlBuilder.insert(TEST_TABLE, SqlEntity.column("id"), SqlEntity.column("testString")).values(SqlEntity.value("12345").integer(), SqlEntity.value(
                        "RANDOMLY_INSERTED_STRING").text()).commit());
    }

    @Test
    public void updateWithValuesTest() {
        Assert.assertEquals("SqlBuilder should return a valid update query with values",
                "UPDATE TestingTable SET id='12345',testString='RANDOMLY_UPDATED_STRING';",
                SqlBuilder.update(TEST_TABLE, "id", "testString").values("12345", "RANDOMLY_UPDATED_STRING").commit());
    }

    @Test
    public void updateWithValuesWhereTest() {
        Assert.assertEquals("SqlBuilder should return a valid update query with values and where clause",
                "UPDATE TestingTable SET id='12345',testString='RANDOMLY_UPDATED_STRING' WHERE id=12345 AND testString='RANDOMLY_UPDATED_STRING';",
                SqlBuilder.update(TEST_TABLE, "id", "testString").values("12345", "RANDOMLY_UPDATED_STRING")
                        .where("id=12345 AND testString='RANDOMLY_UPDATED_STRING'").commit());
    }

    @Test
    public void updateWithValuesEscaped() {
        Assert.assertEquals("SqlBuilder should return a valid update query with escaped values",
                "UPDATE TestingTable SET id='12345',testString='ESCAPED=\\'RANDOMLY_UPDATED_STRING\\'';",
                SqlBuilder.update(TEST_TABLE, "id", "testString").values("12345", "ESCAPED='RANDOMLY_UPDATED_STRING'").commit());
    }

    @Test
    public void selectFromTest() {
        Assert.assertEquals("The SqlBuilder should return a select query string",
                "SELECT id,testString FROM TestingTable;", SqlBuilder.select(TEST_TABLE, "id", "testString").commit());
    }

    @Test
    public void selectFromWhereTest() {
        Assert.assertEquals("The SqlBuilder should return select query string with valid where clause",
                "SELECT id,testString FROM TestingTable WHERE id=12345 AND testString='RANDOMLY_INSERTED_STRING';",
                SqlBuilder.select(TEST_TABLE, "id", "testString").where("id=12345 AND testString='RANDOMLY_INSERTED_STRING'").commit());
    }
}
