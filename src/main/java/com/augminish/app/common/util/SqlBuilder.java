package com.augminish.app.common.util;

public class SqlBuilder {
    
    private static final String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS";
    private static final String CLOSE_PAREN = ")";
    private static final String OPEN_PAREN = "(";
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final String END = ";";
    
    public static String createDatabase(String database) {
        StringBuilder query = new StringBuilder(CREATE_DATABASE);
        return query.append(SPACE).append(database).toString();
    }
    
    public static String createTable(String table, String... columns) {
        StringBuilder query = new StringBuilder(CREATE_TABLE);
        query.append(SPACE).append(table).append(OPEN_PAREN);
        int comma = 0;
        for (String column : columns) {
            query.append((comma++ == 0 ? SPACE : COMMA) + column);
        }
        query.append(CLOSE_PAREN).append(END);
        // TODO: Create Logger to display the resulting query that was created
        // Maybe implement an optional debug level option
        return query.toString();
    }
}
