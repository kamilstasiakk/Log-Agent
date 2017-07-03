package pl.kamilstasiak.repositories;


import pl.kamilstasiak.entities.LogRecord;
import pl.kamilstasiak.entities.Log;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Kamil on 2016-10-31.
 */
public class LogsRepository {

    public enum Status {
        OK, NAME_OCCUPIED, ERROR, LOG_TYPE_DOESNT_EXIST
    }
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/";
    static final String DB_URL_NO_SSL = "?autoReconnect=true&useSSL=false";

    //  Database credentials
    static final String USER = "kamil";
    static final String PASS = "pelikan22";

    Connection conn = null;
    Statement stmt = null;

    public LogsRepository() {
        super();
        try {
            Class.forName("com.mysql.jdbc.Driver");

            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL + DB_URL_NO_SSL, USER, PASS);

            System.out.println("Creating database...");
            stmt = conn.createStatement();

            String sql = "CREATE DATABASE IF NOT EXISTS LOGS";
            stmt.executeUpdate(sql);
            System.out.println("Database created successfully...");

            conn.setCatalog("LOGS");
            System.out.println("Changed database successfully...");


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }


    private boolean checkIfThisLogExist(String logName)  {
        //getting names of tables from current database
        try{
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                if (rs.getString(3).equals(logName)) {
                    return true;
                }
            }
            rs.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        return false;
    }

    private List<String> getAllLogNames() {
        List<String> logTypesNames = new ArrayList<>();
        try{
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                logTypesNames.add(rs.getString(3));
            }
            rs.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        return logTypesNames;
    }

    public Status createNewLog(String name, List<String> fieldNames) {


        if (!checkIfThisLogExist(name)) {
            try {
                System.out.println("Creating table in given database...");
                stmt = conn.createStatement();
                StringBuilder sql = new StringBuilder();
                sql.append("CREATE TABLE IF NOT EXISTS " + name);
                sql.append("(id INTEGER not NULL AUTO_INCREMENT, ");
                sql.append(" source_id VARCHAR(255), ");
                sql.append(" time TIMESTAMP, ");
                for (String fieldName : fieldNames) {
                    sql.append(fieldName + " VARCHAR(255),");
                }
                sql.append(" PRIMARY KEY ( id ))");

                stmt.executeUpdate(sql.toString());
                System.out.println("Created table in given database...");
            } catch (SQLException e) {
                e.printStackTrace();
                return Status.ERROR;
            }
        } else {
            return Status.NAME_OCCUPIED;
        }
        return Status.OK;
    }

    public Status createNewLogRecord(String logName, String source, String time, List<String> otherFields) {
        if (checkIfThisLogExist(logName)) {
            try {
                StringBuilder insertRecord = new StringBuilder();
                insertRecord.append("INSERT INTO " + logName + " VALUES (0, ");
                insertRecord.append("'" + source + "', ?");
                for (String field : otherFields) {
                    insertRecord.append(", '" + field + "'");
                }
                insertRecord.append(")");
                PreparedStatement pstmt = conn.prepareStatement(insertRecord.toString());
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date date = format.parse(time);
                java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
                pstmt.setTimestamp(1, sqlDate);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return Status.ERROR;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return Status.OK;
        }
        else {
            return Status.LOG_TYPE_DOESNT_EXIST;
        }
    }



    public List<LogRecord> getLogsRecords(String sourceFilter, String startDate, String endDate, String logFilter) {
        System.out.println("Creating statement...");
        List<LogRecord>  logsRecords = new ArrayList<LogRecord>();
        List<String> logNames = getAllLogNames();
        StringBuilder sql;
        for(String logName : logNames) {
            try {
                Log log = getLog(logName);
                List<String> fieldsNames = log.getFields();
                stmt = conn.createStatement();
                sql = new StringBuilder();
                    sql.append("SELECT * FROM ");
                    sql.append(logName);
                    sql.append(" WHERE time > '");
                    sql.append(startDate);
                    sql.append("' AND time < '");
                    sql.append(endDate);
                    sql.append("'");
                if (logFilter != null) {
                    sql.append(" ");
                    sql.append(logFilter);
                }
                    if(sourceFilter != null) {
                        sql.append(" AND source_id = \"");
                        sql.append(sourceFilter);
                        sql.append("\"");
                    }
                ResultSet rs = stmt.executeQuery(sql.toString());
                //STEP 5: Extract data from result set
                while (rs.next()) {
                    //Retrieve by column name
                    String source;
                    String time;
                    int id;
                    List<String> otherFieldsValues = null;
                    id = rs.getInt("id");
                    source = rs.getString("source_id");
                    time = rs.getTimestamp("time").toString();
                    for (int index = 3; index < fieldsNames.size(); index++) {
                        if (otherFieldsValues == null) {
                            otherFieldsValues = new ArrayList<>();
                        }
                        otherFieldsValues.add(rs.getString(fieldsNames.get(index)));
                    }
                    logsRecords.add(new LogRecord(logName, source, time,otherFieldsValues,id));
                }
                rs.close();
            } catch (SQLException se) {
                //Handle errors for JDBC
                se.printStackTrace();
            } catch (Exception e) {
                //Handle errors for Class.forName
                e.printStackTrace();
            }
        }
        return logsRecords;
    }

    public List<LogRecord> getLogRecords(String logName, String startDate, String endDate, String sourceFilter, String logFilter) {
        System.out.println("Creating statement...");
        List<LogRecord> logRecords = new ArrayList<LogRecord>();

        StringBuilder sql;
        if (checkIfThisLogExist(logName)) {
            try {
                Log log = getLog(logName);
                List<String> fieldsNames = log.getFields();
                stmt = conn.createStatement();
                sql = new StringBuilder();
                sql.append("SELECT * FROM ");
                sql.append(logName);
                sql.append(" WHERE time > '");
                sql.append(startDate);
                sql.append("' AND time < '");
                sql.append(endDate);
                sql.append("'");
                if (logFilter != null) {
                    sql.append(" ");
                    sql.append(logFilter);
                }
                if(sourceFilter != null) {
                    sql.append(" AND source_id = \"");
                    sql.append(sourceFilter);
                    sql.append("\"");
                }
                ResultSet rs = stmt.executeQuery(sql.toString());
                //STEP 5: Extract data from result set
                while (rs.next()) {
                    //Retrieve by column name
                    String source;
                    String time;
                    int id;
                    List<String> otherFieldsValues = null;
                    id = rs.getInt("id");
                    source = rs.getString("source_id");
                    time = rs.getTimestamp("time").toString();
                    for (int index = 3; index < fieldsNames.size(); index++) {
                        if (otherFieldsValues == null) {
                            otherFieldsValues = new ArrayList<>();
                        }
                        otherFieldsValues.add(rs.getString(fieldsNames.get(index)));
                    }
                    logRecords.add(new LogRecord(logName, source, time,otherFieldsValues, id));
                }
                rs.close();
            } catch (SQLException se) {
                //Handle errors for JDBC
                se.printStackTrace();
            } catch (Exception e) {
                //Handle errors for Class.forName
                e.printStackTrace();
            }
        }
        return logRecords;
    }



    public Log getLog(String logName) {

        ResultSet rs = null;
        List<String> columnsNames = new ArrayList<>();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + logName + " LIMIT 1");
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            System.out.println("columns names:");
            for (int i = 1; i <= columnCount; i++ ) {
                String name = rsmd.getColumnName(i);
                System.out.println(name);
                columnsNames.add(name);
            }
            return new Log(logName, columnsNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            throw new LogException("No log named like this");
        } catch (LogException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteLogRecord(String logName, int id) {
        try {
            stmt = conn.createStatement();
            String sql = "DELETE FROM " +logName +
                    " WHERE id = " + id;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
