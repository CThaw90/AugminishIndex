package com.augminish.app.common.util;

public class SqlBuilder {
    
    private static final String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS";
    private static final String INSERT_RECORD = "INSERT INTO";
    private static final String CLOSE_PAREN = ")";
    private static final String OPEN_PAREN = "(";
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final String END = ";";
    
    private static StringBuilder query;
    private static SqlBuilder cache;
    
    private static int status = 0;
    
    public static String createDatabase(String database) {
        StringBuilder query = new StringBuilder(CREATE_DATABASE);
        query.append(SPACE).append(database);
        return query.append(END).toString();
    }
    
    public static String createTable(String table, String... columns) {
        StringBuilder query = new StringBuilder(CREATE_TABLE);
        query.append(SPACE).append(table).append(SPACE).append(OPEN_PAREN);
        int comma = 0;
        for (String column : columns) {
            query.append((comma++ == 0 ? SPACE : COMMA) + column);
        }
        query.append(SPACE).append(CLOSE_PAREN).append(END);
        // TODO: Create Logger to display the resulting query that was created
        // Maybe implement an optional debug level option
        return query.toString();
    }
    
    public static SqlBuilder insertInto(String table, String... columns) {
        
        if (cache == null && status == 0) {
            cache = new SqlBuilder();
            SqlBuilder.insertInto(table, columns);
            
        } else if (cache != null && status == 0) {
            query = new StringBuilder(INSERT_RECORD);
            query.append(SPACE).append(table);
            status++;
        
        } else if (status != 0) {
            // Create a custom Runtime Exception class to handle illegal state exception
            throw new RuntimeException("Illegal State Exception");
        }
        
        return cache;
    }
    
    public static SqlBuilder withValues(String... values) {
        
        if (cache == null) {
            throw new RuntimeException("Invoked SqlBuilder.withValues method without calling SqlBuilder.insertInto or SqlBuilder.update");
        
        } else if (cache != null && status == 1) {
            
        }
        
        return cache;
    }
    
    public static String commit() {
        return query != null ? query.toString() : null;
    }
    
    protected SqlBuilder returnCache() {
        return cache;
    }
}
