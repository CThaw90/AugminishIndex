package com.augminish.app.common.util.object;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropertyHashMapTest {
    private static final String[] seedTest = { "https://newyork.craigslist.org/search/muc", "https://newjersey.craigslist.org/search/muc", "https://pittsburgh.craigslist.org/search/muc" };
    private static PropertyHashMap propertyHashMap;

    @BeforeClass
    public static void init() throws IOException {
        propertyHashMap = new PropertyHashMap();
    }

    @Test
    public void seedsAsListTest() {
        List<String> seeds = new ArrayList<String>();
        seeds.add(seedTest[0]);
        seeds.add(seedTest[1]);
        seeds.add(seedTest[2]);

        Assert.assertArrayEquals("PropertyHashMap should return values split into matching array", seedTest, propertyHashMap.getSeedAsArray());
        Assert.assertEquals("PropertyHashMap should return values split into matching list", seeds, Arrays.asList(propertyHashMap.getSeedAsArray()));

    }
}
