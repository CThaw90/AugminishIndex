package com.augminish.app.common.util.mysql.helper;

import com.augminish.app.common.util.strings.StaticString;

import org.junit.Assert;
import org.junit.Test;

public class SqlBuilderTest {
    
    @Test
    public void createDatabaseTest() {
        Assert.assertEquals(StaticString.CREATE_DATABASE_TEST_ASSERT_DESCRIPTION, 
                StaticString.CREATE_DATABASE_VALUE, SqlBuilder.createDatabase(StaticString.DATABASE_NAME).commit());
    }
    
    @Test
    public void createTableTest() {
        Assert.assertEquals(StaticString.CREATE_TABLE_TEST_ASSERT_DESCRIPTION, 
                StaticString.CREATE_TABLE_VALUE, SqlBuilder.createTable(StaticString.TABLE_NAME, 
                        "id INT(11) NOT NULL AUTO_INCREMENT",
                        "testString VARCHAR(512) NOT NULL",
                        "PRIMARY KEY (id)").commit());
    }
    
    @Test(expected = RuntimeException.class)
    public void prematureCommitTest() {
        Assert.assertEquals(StaticString.PREMATURE_COMMIT_TEST_ASSERT_DESCRIPTION, 
                StaticString.INSERT_INTO_TABLE, SqlBuilder.insert(StaticString.TABLE_NAME, "id", "testString").commit());
    }
    
    @Test
    public void resetTest() {
        SqlBuilder.insert(StaticString.TABLE_NAME, "id", "testString");
        Assert.assertNotNull(StaticString.RESET_TEST_NOT_NULL_ASSERT_DESCRIPTION, SqlBuilder.getCache());
        SqlBuilder.reset();
        Assert.assertNull(StaticString.RESET_TEST_NULL_ASSERT_DESCRIPTION, SqlBuilder.getCache());
    }
    
    @Test
    public void insertIntoWithValuesTest() {
        Assert.assertEquals(StaticString.INSERT_INTO_WITH_VALUES_TEST_ASSERT_DESCRIPTION, 
                StaticString.INSERT_INTO_TABLE, 
                SqlBuilder.insert(StaticString.TABLE_NAME, "id", "testString").values("12345", "RANDOMLY_INSERTED_STRING").commit());
    }
    
    @Test
    public void updateWithValuesTest() {
        Assert.assertEquals(StaticString.UPDATE_WITH_VALUES_TEST, 
                StaticString.UPDATE_TABLE,
                SqlBuilder.update(StaticString.TABLE_NAME, "id", "testString").values("12345", "RANDOMLY_UPDATED_STRING").commit());
    }
    
    @Test
    public void updateWithValuesWhereTest() {
        Assert.assertEquals(StaticString.UPDATE_WITH_VALUES_WHERE_TEST_ASSERT_DESCRIPTION, 
                StaticString.UPDATE_TABLE_WHERE,
                SqlBuilder.update(StaticString.TABLE_NAME, "id", "testString").values("12345", "RANDOMLY_UPDATED_STRING")
                .where("id=12345 AND testString='RANDOMLY_UPDATED_STRING'").commit());
    }
    
    @Test
    public void updateWithValuesEscaped() {
        Assert.assertEquals(StaticString.UPDATE_WITH_VALUES_ESCAPED_ASSERT_DESCRIPTION,
                StaticString.UPDATE_TABLE_ESCAPED,
                SqlBuilder.update(StaticString.TABLE_NAME, "id", "testString").values("12345", "ESCAPED='RANDOMLY_UPDATED_STRING'").commit());
    }
    
    @Test
    public void selectFromTest() {
        Assert.assertEquals(StaticString.SELECT_FROM_TEST_ASSERT_DESCRIPTION, 
                StaticString.SELECT_FROM_TABLE, 
                SqlBuilder.select(StaticString.TABLE_NAME, "id", "testString").commit());
    }
    
    @Test
    public void selectFromWhereTest() {
        Assert.assertEquals(StaticString.SELECT_FROM_TEST_WHERE_ASSERT_DESCRIPTION, 
                StaticString.SELECT_FROM_TABLE_WHERE,
                SqlBuilder.select(StaticString.TABLE_NAME, "id", "testString")
                .where("id=12345 AND testString='RANDOMLY_INSERTED_STRING'").commit());
    }
}
