package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    //JDBC: Java database connectivity(driver) to make java connects with
    //Oracle database, downloaded .jar file.
    private static final String URL = "YOUR jdbc: ";
    private static final String USER = "system";
    private static final String PASSWORD = "YourNewPassword123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
