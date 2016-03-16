package com.augminish.app.common.util.mysql.helper;

public class SqlEntity {

    private SqlDataType sqlDataType;
    private SqlType sqlType;
    private String value;
    private String alias;

    private SqlEntity(String value, SqlType sqlType) {
        this.sqlType = sqlType;
        this.value = value;
    }

    public static SqlEntity column(Object object) {
        return column(object.toString());
    }

    public static SqlEntity column(String name) {
        return new SqlEntity(name, SqlType.COLUMN);
    }

    public SqlEntity as(Object object) {
        return as(object.toString());
    }

    public SqlEntity as(String name) {
        if (!isAnSqlColumn()) {
            throw new RuntimeException("Illegal call type exception");
        }
        sqlType = SqlType.ALIAS;
        alias = name;
        return this;
    }

    public static SqlEntity value(Object object) {
        return value(object.toString());
    }

    public static SqlEntity value(String name) {
        return new SqlEntity(name, SqlType.VALUE);
    }

    public SqlEntity integer() {
        setSqlDataType(SqlDataType.INT);
        return this;
    }

    public SqlEntity varchar() {
        setSqlDataType(SqlDataType.VARCHAR);
        return this;
    }

    public SqlEntity timestamp() {
        setSqlDataType(SqlDataType.TIMESTAMP);
        return this;
    }

    public SqlEntity text() {
        setSqlDataType(SqlDataType.TEXT);
        return this;
    }

    protected boolean isAnSqlColumn() {
        return SqlType.COLUMN.equals(sqlType);
    }

    protected boolean isAnSqlValue() {
        return SqlType.VALUE.equals(sqlType);
    }

    public static String escape(String value) {
        return value.replaceAll("\\'", "\\\\'");
    }

    private enum SqlDataType {

        INTEGER, SMALLINT, TINYINT, BOOLEAN, LONG, INT, BIGINT,

        DECIMAL, FLOAT, DOUBLE,

        CHAR, VARCHAR, TEXT,

        TIMESTAMP, DATE
    }

    private enum SqlType {
        COLUMN, VALUE, ALIAS
    }

    private void setSqlDataType(SqlDataType sqlDataType) {
        if (!isAnSqlValue() || sqlDataType == null) {
            throw new RuntimeException("Illegal call type exception");
        }

        this.sqlDataType = sqlDataType;
    }

    private String representValue() {

        StringBuilder rep = new StringBuilder();
        if (sqlDataType == null) {
            sqlDataType = SqlDataType.VARCHAR;
        }
        switch (sqlDataType) {

            case INT:

                rep.append(value);
                break;

            case VARCHAR:
            case TIMESTAMP:
            case TEXT:
            default:
                rep.append("'");
                rep.append(escape(value));
                rep.append("'");
                break;

        }

        return rep.toString();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        switch (sqlType) {

            case COLUMN:
                s.append(value);
                break;

            case VALUE:
                s.append(representValue());
                break;

            case ALIAS:
                s.append(value);
                s.append(" AS ");
                s.append(alias);
                break;

            default:
                break;
        }

        return s.toString();
    }
}
