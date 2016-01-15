package com.augminish.app.common.util.mysql.helper;

import com.augminish.app.common.util.strings.StaticString;

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
            query = new StringBuilder(StaticString.CREATE_DATABASE);
            query.append(StaticString.SPACE).append(database);
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
            query = new StringBuilder(StaticString.CREATE_TABLE);
            query.append(StaticString.SPACE).append(table).append(StaticString.SPACE);
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
            query = new StringBuilder(StaticString.SELECT).append(inputColumns(columns, true));
            query.append(StaticString.FROM).append(StaticString.SPACE);
            query.append(table);
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
            query = new StringBuilder(StaticString.INSERT_RECORD).append(StaticString.SPACE);
            query.append(table).append(StaticString.SPACE);
            query.append(inputColumns(columns));
            status = SqlBuilderState.INSERT;

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
            query = new StringBuilder(StaticString.UPDATE_RECORD).append(table);
            query.append(StaticString.SPACE + StaticString.SET).append(StaticString.SPACE);
            query.append(inputSets(columns));
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
            query.append(StaticString.SPACE).append(StaticString.VALUES);
            query.append(StaticString.SPACE).append(StaticString.LEFT_PAREN);
            int comma = 0;
            for (String value : values) {
                query.append((comma++ == 0 ? StaticString.SPACE : StaticString.COMMA) + escape(value));
            }
            query.append(StaticString.SPACE).append(StaticString.RIGHT_PAREN);
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

    public SqlBuilder where(String clause) {

        if (cache == null || statusIS(SqlBuilderState.OPEN)) {
            throw new RuntimeException("Illegal State Exception");

        }
        else if (statusIS(SqlBuilderState.VALUES) || statusIS(SqlBuilderState.SELECT)) {
            query.append(StaticString.SPACE).append(StaticString.WHERE);
            query.append(StaticString.SPACE).append(clause);
        }
        return cache;
    }

    public String commit() {
        if (!statusIS(SqlBuilderState.VALUES) && !statusIS(SqlBuilderState.SELECT) && !statusIS(SqlBuilderState.CREATE)) {
            reset(); // Make this call from a Custom ExceptionHandler
            throw new RuntimeException("Illegal State Exception. Incomplete Sql query construction detected");
        }
        String sql = query.append(StaticString.SEMI_COLON).toString();
        setStatus(SqlBuilderState.OPEN);
        query = new StringBuilder();

        return sql;
    }

    public static String escape(String value) {
        return "'" + value.replaceAll("'", "\\'") + "'";
    }

    public static void reset() {
        cache = null;
    }

    private static String inputColumns(String[] columns) {
        return inputColumns(columns, false);
    }

    private static String inputColumns(String[] columns, boolean withoutParen) {
        StringBuilder c = new StringBuilder(withoutParen ? StaticString.EMPTY : StaticString.LEFT_PAREN);
        int comma = 0;
        for (String column : columns) {
            c.append((comma++ == 0 ? StaticString.SPACE : StaticString.COMMA) + column);
        }
        c.append(StaticString.SPACE).append(withoutParen ? StaticString.EMPTY : StaticString.RIGHT_PAREN);
        return c.toString();
    }

    private static String inputSets(String[] columns) {
        StringBuilder c = new StringBuilder();
        int comma = 0;
        for (String column : columns) {
            c.append((comma++ == 0 ? "" : StaticString.COMMA) + (column + "=?"));
        }
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
