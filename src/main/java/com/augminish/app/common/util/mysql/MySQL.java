package com.augminish.app.common.util.mysql;

import com.augminish.app.common.object.PropertyHashMap;

import java.io.IOException;

public class MySQL {

    private PropertyHashMap propertyHashMap;

    public MySQL() {
        loadConfig();
    }

    protected void connect() {

    }

    private void loadConfig() {
        try {
            propertyHashMap = new PropertyHashMap();
        }
        catch (IOException ie) {
            ie.printStackTrace();
        }
    }
}
