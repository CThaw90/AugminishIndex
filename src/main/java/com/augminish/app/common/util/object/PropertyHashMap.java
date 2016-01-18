package com.augminish.app.common.util.object;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

public class PropertyHashMap {

    private static HashMap<String, String> hashMap;
    private static Properties properties;
    private static InputStream inputStream;

    public PropertyHashMap() throws IOException {

        if (hashMap == null) {
            
            inputStream = new FileInputStream("./.ignore/config.properties");
            properties = new Properties();
            properties.load(inputStream);

            if (inputStream != null) {
                inputStream.close();
            }

            if (properties != null)
                initialize();
        }
    }

    private void initialize() {

        hashMap = new HashMap<String, String>();
        Enumeration< ? > enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            hashMap.put(key, properties.getProperty(key));
        }
    }

    public String get(String key) {
        return hashMap.get(key);
    }
    
    public String[] getSeedAsArray() {
        return hashMap.containsKey("seed") ? hashMap.get("seed").split(",") : null;
    }
}
