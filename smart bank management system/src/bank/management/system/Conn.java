package bank.management.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Conn {
    private Connection connection;
    private Statement statement;

    public Conn() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/smartbanksystem",
                    "root", "Parmarsatyam09@#"
            );
            statement = connection.createStatement();
        } catch (Exception e) {
            System.out.println("Connection Error: " + e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public void close() {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (Exception e) {
            System.out.println("Error closing connection: " + e);
        }
    }
}
