package com.scheduler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseUtil {
    private static final String DB_URL = "jdbc:mysql://team-8-da-instance-1.cra04a2yok40.ap-south-1.rds.amazonaws.com:3306/task_scheduler";
    private static final String USER = "admin";
    private static final String PASS = "team8awsDA";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
