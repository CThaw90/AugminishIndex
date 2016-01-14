package com.augminish.app.common.util.strings;

public class StaticString {
    
    /* SqlBuilder Static String variables */
    public static final String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS";
    public static final String SELECT_RECORD = "SELECT * FROM ";
    public static final String INSERT_RECORD = "INSERT INTO";
    public static final String UPDATE_RECORD = "UPDATE ";
    
    
    /* SqlBuilderTest Static String variables */
    public static final String CREATE_DATABASE_VALUE = "CREATE DATABASE IF NOT EXISTS TESTING_DATABASE;";
    public static final String CREATE_TABLE_VALUE = "CREATE TABLE IF NOT EXISTS TESTING_TABLE ( " + 
                                                     "id INT(11) NOT NULL AUTO_INCREMENT," + 
                                                     "testString VARCHAR(512) NOT NULL," +
                                                     "PRIMARY KEY (id) );";
    public static final String SELECT_FROM_TABLE_WHERE = "SELECT id,testString FROM TESTING_TABLE WHERE id=12345" +
                                                         " AND testString='RANDOMLY_INSERTED_STRING';";
    public static final String SELECT_FROM_TABLE = "SELECT id,testString FROM TESTING_TABLE;";
    public static final String INSERT_INTO_TABLE = "INSERT INTO TESTING_TABLE ( id,testString )" +
                                                    " VALUES ( ?&12345,?&RANDOMLY_INSERTED_STRING );";
    public static final String UPDATE_TABLE_WHERE = "UPDATE TESTING_TABLE SET id=?&12345,testString=?&RANDOMLY_UPDATED_STRING WHERE id=12345 AND testString=" +
                                                    "'RANDOMLY_UPDATED_STRING';";
    public static final String UPDATE_TABLE = "UPDATE TESTING_TABLE SET id=?&12345,testString=?&RANDOMLY_UPDATED_STRING;";
    public static final String DATABASE_NAME = "TESTING_DATABASE";
    public static final String TABLE_NAME = "TESTING_TABLE";
    
    /* SqlBuilderTest Assertion Descriptions */
    public static final String CREATE_DATABASE_TEST_ASSERT_DESCRIPTION = "SqlBuilder should return a valid database query";
    public static final String CREATE_TABLE_TEST_ASSERT_DESCRIPTION = "SqlBuilder should return a valid table query";
    public static final String PREMATURE_COMMIT_TEST_ASSERT_DESCRIPTION = "SqlBuilder should throw RuntimeException when premature commit method invoked";
    public static final String INSERT_INTO_WITH_VALUES_TEST_ASSERT_DESCRIPTION = "SqlBuilder should return a valid Insert query with values";
    public static final String UPDATE_WITH_VALUES_TEST = "SqlBuilder should return a valid update query with values";
    public static final String UPDATE_WITH_VALUES_WHERE_TEST_ASSERT_DESCRIPTION = "SqlBuilder should return a valid update query with values and where clause";
    public static final String RESET_TEST_NOT_NULL_ASSERT_DESCRIPTION = "The SqlBuilder cache should not be null";
    public static final String RESET_TEST_NULL_ASSERT_DESCRIPTION = "The SqlBuilder cache should be set to null";
    public static final String SELECT_FROM_TEST_WHERE_ASSERT_DESCRIPTION = "The SqlBuilder should return select query string with valid where clause";
    public static final String SELECT_FROM_TEST_ASSERT_DESCRIPTION = "The SqlBuilder should return a select query string";
    
    /* MySQLTest Static String variables */
    public static final String CREATE_ERROR_X = "CREATEERROR TABLE";
    public static final String CREATE_ERROR = "DO NOT CREATE";
    public static final String TEST_TABLE = "IndexerTests";
    public static final String TEST_DB = "Test";
    
    /* MySQLTest Assertion Descriptions */
    public static final String MYSQL_CONNECT_TEST_DISCONNECTED_ASSERT_DESCRIPTION = "MySQL should not be connected";
    public static final String MYSQL_CONNECT_TEST_SHOULD_CONNECT_ASSERT_DESCRIPTION = "MySQL Driver should successfully connect";
    public static final String MYSQL_CONNECT_TEST_SHOULD_BE_CONNECTED_ASSERT_DESCRIPTION = "MySQL should be connected";
    public static final String MYSQL_CONNECT_TEST_DISCONNECT_ASSERT_DESCRIPTION = "MySQL Driver should disconnect successfully";
    
    public static final String MYSQL_CREATE_TEST_INVALID_QUERY_ASSERT_DESCRIPTION = "MySQL should return false passing in a non creating query";
    public static final String MYSQL_CREATE_TEST_CREATE_DB_ASSERT_DESCRIPTION = "MySQL should return true creating a database called Test";
    
    /* Static String Common Symbols */
    public static final String RIGHT_PAREN = ")";
    public static final String LEFT_PAREN = "(";
    public static final String SEMI_COLON = ";";
    public static final String SPACE = " ";
    public static final String COMMA = ",";
    public static final String EMPTY = "";
    
    /* Static String SQL KeyWord Variables */
    public static final String SELECT = "SELECT";
    public static final String VALUES = "VALUES";
    public static final String WHERE = "WHERE";
    public static final String FROM = "FROM";
    public static final String SET = "SET";
    public static final String AS = "AS";
    
}
