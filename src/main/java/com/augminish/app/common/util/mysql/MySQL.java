package com.augminish.app.common.util.mysql;

import com.augminish.app.common.util.object.PropertyHashMap;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MySQL {

    private static final DecimalFormat df = new DecimalFormat("#.000");
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
                connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database, username, password);
                connected = true;
            }
            catch (SQLException sql) {

                // TODO: Maybe put some logging info about the failed connection
            }
        }

        return connected;
    }

    protected boolean disconnect() {
        boolean disconnected = false;
        if (connected) {

            try {
                if (rs != null)
                    rs.close();
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
                disconnected = true;
                connected = false;

            }
            catch (SQLException sql) {
                sql.printStackTrace();
            }
        }

        return disconnected;
    }

    public boolean use(String database) {

        boolean success = true;
        if (connected) {
            success = disconnect();
        }

        if (success) {
            this.database = database;
            success = connect();
        }

        return success;
    }

    public boolean create(String query) {

        boolean created = false;
        if (connected && query.matches("^CREATE[\\s+]\\w+.*")) {

            try {
                statement = connection.prepareStatement(query);
                statement.execute();
                created = true;
            }
            catch (SQLException e) {
                // TODO: Log that there has been an SQLException or error
                e.printStackTrace();
                created = false;
            }
        }

        return created;
    }

    public List<HashMap<String, Object>> select(String query) {

        if (connected && query.matches("^SELECT[\\s+]\\w+.*")) {
            try {
                data = new ArrayList<HashMap<String, Object>>();
                statement = connection.prepareStatement(query);

                if (statement.execute(query, Statement.RETURN_GENERATED_KEYS)) {
                    rs = statement.getResultSet();

                    while (rs != null && rs.next()) {
                        rsMetaData = rs.getMetaData();
                        numOfCols = rsMetaData.getColumnCount();
                        row = new HashMap<String, Object>();

                        for (int column = 1; column <= numOfCols; column++) {
                            columnName = rsMetaData.getColumnLabel(column);
                            sqlType = rsMetaData.getColumnType(column);

                            switch (sqlType) {
                                case Types.CHAR:
                                case Types.VARCHAR:
                                case Types.DATE:
                                case Types.TIMESTAMP:
                                    row.put(columnName, rs.getString(columnName) != null ? rs.getString(columnName) : "");
                                    break;

                                case Types.INTEGER:
                                case Types.SMALLINT:
                                case Types.TINYINT:
                                case Types.BOOLEAN:
                                    row.put(columnName, rs.getInt(columnName));
                                    break;

                                case Types.DECIMAL:
                                case Types.FLOAT:
                                case Types.DOUBLE:
                                    row.put(columnName, Double.parseDouble(df.format(rs.getDouble(columnName))));
                                    break;

                                case Types.BIGINT:
                                    row.put(columnName, rs.getLong(columnName));
                                    break;

                                default:
                                    row.put(columnName, rs.getString(columnName));
                                    break;
                            }
                        }

                        data.add(new HashMap<String, Object>(row));
                    }
                }

            }
            catch (SQLException sql) {
                // TODO: Try and get this statement to catch SQLExceptions
            }
        }

        return data;
    }

    public boolean insert(String query) {

        boolean inserted = false;
        if (connected && query.matches("^INSERT[\\s+]\\w+.*")) {
            try {
                statement = connection.prepareStatement(query);
                statement.executeUpdate();
                inserted = true;
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return inserted;
    }

    public boolean update(String query) {

        boolean updated = false;
        if (connected && query.matches("^UPDATE[\\s+]\\w+.*")) {
            try {
                statement = connection.prepareStatement(query);
                statement.executeUpdate();
                updated = true;
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return updated;
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
