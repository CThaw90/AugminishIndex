package com.augminish.app.common.util;

import com.augminish.app.common.util.strings.StaticString;

import org.junit.Assert;
import org.junit.Test;

public class SqlBuilderTest {
    
    @Test
    public void createDatabaseTest() {
        Assert.assertEquals(StaticString.CREATE_DATABASE_TEST_ASSERT_DESCRIPTION, 
                StaticString.CREATE_DATABASE_VALUE, SqlBuilder.createDatabase(StaticString.DATABASE_NAME));
    }
    
    @Test
    public void createTableTest() {
        Assert.assertEquals(StaticString.CREATE_TABLE_TEST_ASSERT_DESCRIPTION, 
                StaticString.CREATE_TABLE_VALUE, SqlBuilder.createTable(StaticString.TABLE_NAME, 
                        "id INT(11) NOT NULL AUTO_INCREMENT",
                        "testString VARCHAR(512) NOT NULL",
                        "PRIMARY KEY (id)"));
    }
    
    @Test(expected = RuntimeException.class)
    public void prematureCommitTest() {
        Assert.assertEquals(StaticString.PREMATURE_COMMIT_TEST_ASSERT_DESCRIPTION, 
                StaticString.INSERT_INTO_TABLE, SqlBuilder.insertInto(StaticString.TABLE_NAME, "id", "testString").commit());
    }
    
    @Test
    public void inserIntoWithValuesTest() {
        Assert.assertEquals(StaticString.INSERT_INTO_WITH_VALUES_TEST_ASSERT_DESCRIPTION, 
                StaticString.INSERT_INTO_TABLE, 
                SqlBuilder.insertInto(StaticString.TABLE_NAME, "id", "testString").withValues("12345", "RANDOMLY_INSERTED_STRING").commit());
    }
    
    @Test
    public void updateWithValuesTest() {
        Assert.assertEquals(StaticString.UPDATE_WITH_VALUES_TEST, 
                StaticString.UPDATE_TABLE,
                SqlBuilder.update(StaticString.TABLE_NAME, "id", "testString").withValues("12345", "RANDOMLY_UPDATED_STRING").commit());
    }
}
