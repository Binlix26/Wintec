package searchTool;

import java.sql.*;

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

    public PreparedStatement initDB() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        System.out.println("Driver loaded.");

        Connection conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
        System.out.println("Database connected.");

        String sql = "SELECT * FROM Words WHERE word = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        return statement;
    }
}
