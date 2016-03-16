package com.augminish.app.common.util.mysql.helper;

import org.junit.Assert;
import org.junit.Test;

public class SqlEntityTest {

    @Test(expected = RuntimeException.class)
    public void asViolationTest() {
        SqlEntity.value(null).as(null);
    }

    @Test(expected = RuntimeException.class)
    public void integerViolationTest() {
        SqlEntity.column(null).integer();
    }

    @Test(expected = RuntimeException.class)
    public void textViolationTest() {
        SqlEntity.column(null).text();
    }

    @Test(expected = RuntimeException.class)
    public void varcharViolationTest() {
        SqlEntity.column(null).varchar();
    }

    @Test
    public void sqlEntityAliasTest() {
        Assert.assertEquals("Testing AS Test", SqlEntity.column("Testing").as("Test").toString());
        Assert.assertEquals("zipcode AS location", SqlEntity.column("zipcode").as("location").toString());
    }

    @Test
    public void sqlEntityValueTest() {
        Assert.assertEquals("'2015-09-01 12:44:01'", SqlEntity.value("2015-09-01 12:44:01").timestamp().toString());
        Assert.assertEquals("'RANDOM_GENERATED_STRING'", SqlEntity.value("RANDOM_GENERATED_STRING").toString());
        Assert.assertEquals("'12345'", SqlEntity.value("12345").varchar().toString());
        Assert.assertEquals("12345", SqlEntity.value("12345").integer().toString());
        Assert.assertEquals("51839", SqlEntity.value("51839").integer().toString());
    }
}
