package com.augminish.app.common.util.object;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

public class PropertyHashMapTest {
    private static PropertyHashMap propertyHashMap;
    private String[] seedTest = {
            "http://localhost/music.html", 
            "https://newjersey.craigslist.org/search/muc",
            "https://www.facebook.com/cthaw1"
    };
    
    

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
        
    //    Assert.assertEquals("PropertyHashMap should return values split into matching array", )
    }
}
