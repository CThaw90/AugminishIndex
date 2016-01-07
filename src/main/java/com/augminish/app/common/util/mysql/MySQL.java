package com.augminish.app.common.util.mysql;

import com.augminish.app.common.util.object.PropertyHashMap;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class MySQL {

    private PropertyHashMap propertyHashMap;
    
    private PreparedStatement statement;
    private Connection connection;
    
    private ResultSetMetaData rsMetaData;
    private ResultSet rs;
    
    private List<HashMap<String, Object>> data;
    private HashMap<String, Object> row;
    
    private String hostname;
    private String username;
    private String password;
    private String database;
    private int port;
    
    private String columnName;
    private int numOfCols;
    private int sqlType;
    
    private boolean connected;

    public MySQL() {
        loadConfig();
    }

    protected boolean connect() {
        
        if (!connected) {
            
            try {
                DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database, username, password);
                connected = true;
            } catch (SQLException sql) {
                
                // TODO: Maybe put some logging info about the failed connection
            }
        }
        
        return connected;
    }
    
    

    private void loadConfig() {
        try {
            propertyHashMap = new PropertyHashMap();
            port = Integer.parseInt(propertyHashMap.get("port"));
            hostname = propertyHashMap.get("hostname");
            username = propertyHashMap.get("username");
            password = propertyHashMap.get("password");
            database = propertyHashMap.get("database");
        }
        catch (IOException ie) {
            ie.printStackTrace();
        }
    }
    
    public boolean isConnected() {
        return connected;
    }
}
