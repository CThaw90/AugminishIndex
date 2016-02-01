package com.augminish.app.common.util.object;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

public class PropertyHashMap {

    private static HashMap<String, String> hashMap;
    private static InputStream inputStream;
    private static Properties properties;
    
    public PropertyHashMap() throws IOException {
        this("./.ignore/config.properties");
    }

    public PropertyHashMap(String configFile) throws IOException {

        if (hashMap == null) {

            inputStream = new FileInputStream(configFile);
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

    public boolean contains(String key) {
        return hashMap.containsKey(key);
    }

    public String[] getSeedAsArray() {
        return hashMap.containsKey("crawler.seed") ? hashMap.get("crawler.seed").split(",") : null;
    }

    public String[] getIgnoredAsArray() {
        return hashMap.containsKey("crawler.ignore") ? hashMap.get("crawler.ignore").split(",") : null;
    }

    protected String[] getSeedAsArray(boolean test) {
        return hashMap.containsKey("crawler.seedTest") ? hashMap.get("crawler.seedTest").split(",") : null;
    }
}
