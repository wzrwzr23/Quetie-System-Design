package io.catroll.iot.task;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    public static void getConnection() {

        // JDBC URL
        String connectionUrl = "jdbc:sqlserver://trydbserver.database.windows.net:1433;"
                + "database=trydb;"
                + "user=sqladmin@trydbserver;"
                + "password=" + "Capstone1" + ";"
                + "encrypt=true;"
                + "trustServerCertificate=false;"
                + "hostNameInCertificate=*.database.windows.net;"
//                + "loginTimeout=30;"
                ;

        try {
            Log.d(TAG, "getConnection: driver=====");

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (
             Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Feature1")) {

            while (resultSet.next()) {
                // Assuming you know the column names and types, adjust this accordingly
                String column1 = resultSet.getString("number_of_people"); // Replace 'column1' with your actual column name
                // Add more columns as needed

                Log.d(TAG, "getConnection: number_of_people: " + column1);
                // Print more columns as needed
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static final String TAG = "DBConnection";
}
