package com.augminish.app.common.util.mysql.helper;

public class SqlBuilder {

    private static SqlBuilderState status;
    private static StringBuilder query;
    private static SqlBuilder cache;

    public SqlBuilder() {
        setStatus(SqlBuilderState.OPEN);
    }

    public static SqlBuilder createDatabase(String database) {

        if (cache == null) {
            cache = new SqlBuilder();
            SqlBuilder.createDatabase(database);

        }
        else if (cache != null && statusIS(SqlBuilderState.OPEN)) {
            query = new StringBuilder("CREATE DATABASE IF NOT EXISTS");
            query.append(" ").append(database);
            setStatus(SqlBuilderState.CREATE);
        }
        else {
            throw new RuntimeException("Illegal State Exception");
        }

        return cache;
    }

    public static SqlBuilder createTable(String table, String... columns) {

        if (cache == null) {
            cache = new SqlBuilder();
            SqlBuilder.createTable(table, columns);

        }
        else if (cache != null && statusIS(SqlBuilderState.OPEN)) {
            query = new StringBuilder("CREATE TABLE IF NOT EXISTS");
            query.append(" ").append(table).append(" ");
            query.append(inputColumns(columns));
            setStatus(SqlBuilderState.CREATE);
        }
        else {
            throw new RuntimeException("Illegal State Exception");
        }

        return cache;
    }

    public static SqlBuilder select(String table, String... columns) {

        if (cache == null) {
            cache = new SqlBuilder();
            SqlBuilder.select(table, columns);

        }
        else if (cache != null && statusIS(SqlBuilderState.OPEN)) {
            query = new StringBuilder("SELECT").append(inputColumns(columns, true));
            query.append("FROM ").append(table);
            setStatus(SqlBuilderState.SELECT);
        }
        return cache;
    }

    public static SqlBuilder select(String table, SqlEntity... columns) {

        if (cache == null) {
            cache = new SqlBuilder();
            SqlBuilder.select(table, columns);
        }
        else if (cache != null) {
            query = new StringBuilder("SELECT").append(inputColumns(columns, true));
            query.append("FROM ").append(table);
            setStatus(SqlBuilderState.SELECT);
        }
        return cache;
    }

    public static SqlBuilder insert(String table, String... columns) {

        if (cache == null) {
            cache = new SqlBuilder();
            SqlBuilder.insert(table, columns);

        }
        else if (cache != null && statusIS(SqlBuilderState.OPEN)) {
            query = new StringBuilder("INSERT INTO ").append(table).append(" ");
            query.append(inputColumns(columns));
            setStatus(SqlBuilderState.INSERT);

        }
        else if (statusIS(SqlBuilderState.OPEN)) {
            // Create a custom Runtime Exception class to handle illegal state exception
            throw new RuntimeException("Illegal State Exception");
        }
        else {
            throw new RuntimeException("Illegal State Exception");
        }

        return cache;
    }

    public static SqlBuilder insert(String table, SqlEntity... columns) {

        if (cache == null) {
            cache = new SqlBuilder();
            SqlBuilder.insert(table, columns);
        }
        else if (cache != null && statusIS(SqlBuilderState.OPEN)) {
            query = new StringBuilder("INSERT INTO ").append(table).append(" ");
            query.append(inputColumns(columns));
            setStatus(SqlBuilderState.INSERT);
        }
        else if (statusIS(SqlBuilderState.OPEN)) {
            // Create a custom Runtime Exception class to handle illegal state exception
            throw new RuntimeException("Illegal State Exception");
        }
        else {
            throw new RuntimeException("Illegal State Exception");
        }

        return cache;
    }

    public static SqlBuilder update(String table, String... columns) {

        if (cache == null) {
            cache = new SqlBuilder();
            SqlBuilder.update(table, columns);

        }
        else if (cache != null && statusIS(SqlBuilderState.OPEN)) {
            query = new StringBuilder("UPDATE ").append(table).append(" SET ").append(inputSets(columns));
            setStatus(SqlBuilderState.UPDATE);

        }
        else if (statusIS(SqlBuilderState.OPEN)) {
            throw new RuntimeException("Illegal State Exception");
        }

        return cache;
    }

    public SqlBuilder values(String... values) {

        if (statusIS(SqlBuilderState.OPEN)) {
            throw new RuntimeException("Invoked SqlBuilder.withValues method without calling SqlBuilder.insertInto or SqlBuilder.update");

        }
        else if (statusIS(SqlBuilderState.INSERT)) {
            query.append(" VALUES (");
            String delimiter = " ";
            for (String value : values) {
                query.append(delimiter);
                query.append(escape(value));
                delimiter = ",";
            }
            query.append(" )");
            setStatus(SqlBuilderState.VALUES);

        }
        else if (statusIS(SqlBuilderState.UPDATE)) {
            int r = 0;
            for (String value : values) {
                while (r < query.length()) {
                    if (query.charAt(r) == '?') {
                        query.replace(r, r + 1, escape(value));
                        r++;
                        break;
                    }

                    r++;
                }
            }
            setStatus(SqlBuilderState.VALUES);
        }

        return cache;
    }

    public SqlBuilder values(SqlEntity... values) {

        if (statusIS(SqlBuilderState.OPEN)) {
            throw new RuntimeException("Invoked SqlBuilder.withValues method without calling SqlBuilder.insertInto or SqlBuilder.update");
        }
        else if (statusIS(SqlBuilderState.INSERT)) {
            String delimiter = " ";
            query.append(" VALUES (");
            for (SqlEntity value : values) {
                query.append(delimiter);
                query.append(value);
                delimiter = ",";
            }
            query.append(" )");
            setStatus(SqlBuilderState.VALUES);
        }
        else if (statusIS(SqlBuilderState.UPDATE)) {
            int r = 0;
            for (SqlEntity value : values) {
                while (r < query.length()) {
                    if (query.charAt(r) == '?') {
                        query.replace(r, r + 1, value.toString());
                        r++;
                        break;
                    }

                    r++;
                }
            }
            setStatus(SqlBuilderState.VALUES);
        }

        return cache;
    }

    public SqlBuilder where(String clause) {

        if (cache == null || statusIS(SqlBuilderState.OPEN)) {
            throw new RuntimeException("Illegal State Exception");

        }
        else if (statusIS(SqlBuilderState.VALUES) || statusIS(SqlBuilderState.SELECT)) {
            query.append(" WHERE ").append(clause);
        }
        return cache;
    }

    public String commit() {
        if (!statusIS(SqlBuilderState.VALUES) && !statusIS(SqlBuilderState.SELECT) && !statusIS(SqlBuilderState.CREATE)) {
            reset(); // Make this call from a Custom ExceptionHandler
            throw new RuntimeException("Illegal State Exception. Incomplete Sql query construction detected");
        }
        String sql = query.append(";").toString();
        setStatus(SqlBuilderState.OPEN);
        query = new StringBuilder();

        return sql;
    }

    public static String escape(String value) {
        return "'" + value.replaceAll("\\'", "\\\\'") + "'";
    }

    public static void reset() {
        cache = null;
    }

    private static String inputColumns(String[] columns) {
        return inputColumns(columns, false);
    }

    private static String inputColumns(String[] columns, boolean withoutParen) {
        StringBuilder c = new StringBuilder(withoutParen ? "" : "(");
        String delimiter = " ";
        for (String column : columns) {
            c.append(delimiter);
            c.append(column);
            delimiter = ",";
        }
        c.append(" ").append(withoutParen ? "" : ")");
        return c.toString();
    }

    private static String inputSets(String[] columns) {
        StringBuilder c = new StringBuilder();
        String delimiter = "";
        for (String column : columns) {
            c.append(delimiter);
            c.append(column);
            c.append("=?");

            delimiter = ",";
        }
        return c.toString();
    }

    private static String inputColumns(SqlEntity[] columns) {
        return inputColumns(columns, false);
    }

    private static String inputColumns(SqlEntity[] columns, boolean withoutParen) {
        StringBuilder c = new StringBuilder(withoutParen ? "" : "(");
        String delimiter = " ";
        for (SqlEntity column : columns) {
            c.append(delimiter);
            c.append(column);
            delimiter = ",";
        }

        c.append(" ").append(withoutParen ? "" : ")");
        return c.toString();
    }

    private static final boolean statusIS(SqlBuilderState s) {
        return status.equals(s);
    }

    private static final void setStatus(SqlBuilderState s) {
        status = s;
    }

    protected static SqlBuilder getCache() {
        return cache;
    }

    protected enum SqlBuilderState {

        OPEN,
        CREATE,
        SELECT,
        INSERT,
        UPDATE,
        VALUES;
    }
}
