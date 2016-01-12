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
    private static SqlBuilderState status; 
    
    private static StringBuilder query;
    private static SqlBuilder cache;
    
    public SqlBuilder() {
        setStatus(SqlBuilderState.OPEN);
    }
    
    public static String createDatabase(String database) {
        StringBuilder query = new StringBuilder(CREATE_DATABASE);
        query.append(SPACE).append(database);
        return query.append(END).toString();
    }
    
    public static String createTable(String table, String... columns) {
        StringBuilder query = new StringBuilder(CREATE_TABLE);
        query.append(SPACE).append(table).append(SPACE);
        query.append(inputColumns(columns));
        query.append(END);
        // TODO: Create Logger to display the resulting query that was created
        // Maybe implement an optional debug level option
        return query.toString();
    }
    
    public static SqlBuilder insertInto(String table, String... columns) {
        
        if (cache == null) {
            cache = new SqlBuilder();
            SqlBuilder.insertInto(table, columns);
            
        } else if (cache != null && status == SqlBuilderState.OPEN) {
            query = new StringBuilder(INSERT_RECORD).append(SPACE);
            query.append(table).append(SPACE);
            query.append(inputColumns(columns));
            status = SqlBuilderState.INSERT;
        
        } else if (statusIS(SqlBuilderState.OPEN)) {
            // Create a custom Runtime Exception class to handle illegal state exception
            throw new RuntimeException("Illegal State Exception");
        }
        
        return cache;
    }
    
    public SqlBuilder withValues(String... values) {
        
        if (statusIS(SqlBuilderState.OPEN)) {
            throw new RuntimeException("Invoked SqlBuilder.withValues method without calling SqlBuilder.insertInto or SqlBuilder.update");
        
        } else if (statusIS(SqlBuilderState.INSERT)) {
            query.append(SPACE).append("VALUES").append(SPACE).append(OPEN_PAREN);
            int comma = 0;
            for (String value : values) {
                query.append((comma++ == 0 ? SPACE : COMMA) + "?&" + value);
            }
            query.append(SPACE).append(CLOSE_PAREN);
            setStatus(SqlBuilderState.VALUES);
            
        } else if (statusIS(SqlBuilderState.UPDATE)){
            
        }
        
        return cache;
    }
    
    public String commit() {
        if (!statusIS(SqlBuilderState.VALUES)) {
            throw new RuntimeException("Illegal State Exception. Incomplete Sql query construction detected");
        }

        return query.append(END).toString();
    }
    
    private static String inputColumns(String[] columns) {
        StringBuilder c = new StringBuilder(OPEN_PAREN);
        int comma = 0;
        for (String column : columns) {
            c.append((comma++ == 0 ? SPACE : COMMA) + column);
        }
        c.append(SPACE).append(CLOSE_PAREN);
        return c.toString();
    }
    
    private static final boolean statusIS(SqlBuilderState s) {
        return status.equals(s);
    }
    
    private static final void setStatus(SqlBuilderState s) {
        status = s;
    }
    
    protected SqlBuilder returnCache() {
        return cache;
    }
    
    protected enum SqlBuilderState {
        
        OPEN ,
        SELECT , 
        INSERT , 
        UPDATE , 
        VALUES ;
    }
}
