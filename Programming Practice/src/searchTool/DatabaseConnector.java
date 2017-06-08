package searchTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by binlix26 on 30/03/17.
 *
 * The purpose of this code snippet is to connect to my local MySQL database.
 */
public class DatabaseConnector {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String USERNAME = "synonym";
    static final String DB_URL = "jdbc:mysql://localhost/synonym?useSSL=false";
    static final String PASSWORD = "synonym";

    public Connection initDB() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        System.out.println("Driver loaded.");

        Connection conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
        System.out.println("Database connected.");

        return conn;
    }
}
