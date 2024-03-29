package com.example.hydrosteps;

import java.sql.*;
import java.io.File;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private int currentUserId;
    private String url = "jdbc:sqlite:src/hydrosteps.db";

    private DatabaseConnection(String args) throws SQLException {
        try {
            this.connection = DriverManager.getConnection(url);
            System.out.println("[DEBUG] Connection to SQLite has been established.");
            if("setup".equals(args)) {
                System.out.println("[DEBUG] Creating DB");
                setupDatabase();
            }

        } catch (SQLException e) {
            System.out.println("[FAILED] Cannot connect to database: " + e.getMessage());
            throw e;
        }
    }

    public static DatabaseConnection getInstance(String args) throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection(args);
        } else if (instance.getConnection().isClosed()) {
            instance = new DatabaseConnection(args);
        }

        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean checkCredentials(String username, String password) {
        String query = "SELECT userID FROM User WHERE username = ? AND password = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    currentUserId = resultSet.getInt("userID"); // Retrieve and store the userID
                    return true; // Return true since user is found
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Error checking credentials: " + e.getMessage());
        }
        return false;
    }

    public String[] getUserData() {
        String query = "SELECT * FROM User WHERE userID = ?";

        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setString(1, String.valueOf(this.currentUserId));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String fullname = rs.getString("fullname");
                    float weight = rs.getFloat("weight");
                    float height = rs.getFloat("height");

                    String[] userData = {fullname, String.valueOf(weight), String.valueOf(height)};
                    return userData;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("[ERROR] userID not found");
        return null;
    }

    public int getTotalStepsOfCurrentUser() {
        String query = "SELECT * FROM User WHERE userID = ?";

        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setString(1, String.valueOf(this.currentUserId));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int totalSteps = rs.getInt("totalSteps");
                    return totalSteps;

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("[ERROR] userID not found");
        return 0;
    }

    public void addGoalToCurrentUser(String goal) {
        String updateQuery = "UPDATE User SET goals = ? WHERE userID = ?";

        try (PreparedStatement pstmt = this.connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, goal);
            pstmt.setInt(2, this.currentUserId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[ERROR] Error updating goals: " + e.getMessage());
        }
    }

    public String getGoalsOfCurrentUser() {
        String query = "SELECT goals FROM User WHERE userID = ?";
        String userGoals = "";

        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setInt(1, this.currentUserId);

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    userGoals = rs.getString("goals");
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Error retrieving goals: " + e.getMessage());
        }
        return userGoals;
    }

    public int incrementTotalSteps() {
        String getStepsQuery = "SELECT totalSteps FROM User WHERE userID = ?";
        String updateStepsQuery = "UPDATE User SET totalSteps = ? WHERE userID = ?";
        int totalSteps = 0;

        try (PreparedStatement getStepsStmt = this.connection.prepareStatement(getStepsQuery);
             PreparedStatement updateStepsStmt = this.connection.prepareStatement(updateStepsQuery)) {

            getStepsStmt.setInt(1, this.currentUserId);
            try (ResultSet rs = getStepsStmt.executeQuery()) {
                if (rs.next()) {
                    totalSteps = rs.getInt("totalSteps");
                    totalSteps++;

                    updateStepsStmt.setInt(1, totalSteps);
                    updateStepsStmt.setInt(2, this.currentUserId);
                    updateStepsStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Error incrementing total steps: " + e.getMessage());
            return -1; // Return -1 or another appropriate value to indicate an error
        }
        return totalSteps; // Return the new total steps
    }


    private void setupDatabase() {
        try (Statement statement = connection.createStatement()) {
            System.out.println("[DEBUG] start");
            statement.execute("PRAGMA foreign_keys = OFF;");

            statement.execute("DROP TABLE IF EXISTS HydroStep;");
            statement.execute("DROP TABLE IF EXISTS User;");
            statement.execute("DROP TABLE IF EXISTS Doctor;");
            statement.execute("DROP TABLE IF EXISTS Admin;");

            statement.execute("PRAGMA foreign_keys = ON;");

            statement.execute("CREATE TABLE User (" +
                    "userID INT PRIMARY KEY," +
                    "doctorID INT," +
                    "fullname VARCHAR(255)," +
                    "username VARCHAR(255)," +
                    "password VARCHAR(255)," +
                    "phonenumber VARCHAR(15)," +
                    "emailaddress VARCHAR(255)," +
                    "weight DECIMAL(5,2)," +
                    "height DECIMAL(5,2)," +
                    "goals VARCHAR(255)," +
                    "totalSteps BIGINT);");

            statement.execute("CREATE TABLE Doctor (" +
                    "doctorID INT PRIMARY KEY," +
                    "userID INT," +
                    "fullname VARCHAR(255)," +
                    "username VARCHAR(255)," +
                    "password VARCHAR(255)," +
                    "phonenumber VARCHAR(15)," +
                    "emailaddress VARCHAR(255)," +
                    "FOREIGN KEY (userID) REFERENCES User(userID));");

            statement.execute("CREATE TABLE Admin (" +
                    "adminID INT PRIMARY KEY," +
                    "userID INT," +
                    "fullname VARCHAR(255)," +
                    "username VARCHAR(255)," +
                    "password VARCHAR(255)," +
                    "phonenumber VARCHAR(15)," +
                    "emailaddress VARCHAR(255)," +
                    "FOREIGN KEY (userID) REFERENCES User(userID));");

            statement.execute("CREATE TABLE HydroStep (" +
                    "userID INT PRIMARY KEY," +
                    "consumed_ml INT," +
                    "goals_completed BOOLEAN," +
                    "total_steps INT," +
                    "FOREIGN KEY (userID) REFERENCES User(userID));");

            statement.execute("INSERT INTO User (userID, doctorID, fullname, username, password, phonenumber, emailaddress, weight, height, totalSteps) VALUES " +
                    "(1, 101, 'Jennifer Lee', 'jenlee', 'pass123', '999000111', 'jennifer@example.com', 68.5, 162.0, 0), " +
                    "(2, 102, 'Brian Wilson', 'brianw', 'pass124', '123456789', 'brian@example.com', 85.0, 190.0, 0), " +
                    "(3, 103, 'Linda Johnson', 'lindaj', 'pass125', '777888999', 'linda@example.com', 55.0, 150.0, 0), " +
                    "(4, 104, 'Chris Taylor', 'christ', 'pass126', '333444555', 'chris@example.com', 70.0, 165.0, 0), " +
                    "(5, 105, 'Sarah Adams', 'sarahad', 'pass127', '111222333', 'sarah@example.com', 62.5, 160.0, 0), " +
                    "(6, 106, 'David White', 'davidw', 'pass128', '444555666', 'david@example.com', 75.0, 175.0, 0), " +
                    "(7, 107, 'Laura Davis', 'laurad', 'pass129', '987654321', 'laura@example.com', 60.0, 155.0, 0), " +
                    "(8, 108, 'Alex Turner', 'alext', 'pass130', '555666777', 'alex@example.com', 72.0, 170.0, 0), " +
                    "(9, 109, 'Megan Harris', 'meganh', 'pass131', '444333222', 'megan@example.com', 63.0, 158.0, 0), " +
                    "(10, 110, 'Jason Miller', 'jasonm', 'pass132', '888777666', 'jason@example.com', 78.0, 180.0, 0);");

            statement.execute("INSERT INTO Doctor (doctorID, userID, fullname, username, password, phonenumber, emailaddress) VALUES " +
                    "(101, 1, 'Dr. Smith', 'drsmith', 'docpass1', '111222333', 'dr.smith@example.com'), " +
                    "(102, 2, 'Dr. Williams', 'drwilliams', 'docpass2', '444555666', 'dr.williams@example.com'), " +
                    "(103, 3, 'Dr. Davis', 'drdavis', 'docpass3', '777888999', 'dr.davis@example.com'), " +
                    "(104, 4, 'Dr. Taylor', 'drtaylor', 'docpass4', '333444555', 'dr.taylor@example.com'), " +
                    "(105, 5, 'Dr. Miller', 'drmilller', 'docpass5', '999000111', 'dr.miller@example.com'), " +
                    "(106, 6, 'Dr. Anderson', 'dranderson', 'docpass6', '123456789', 'dr.anderson@example.com');");


            statement.execute("INSERT INTO Admin (adminID, userID, fullname, username, password, phonenumber, emailaddress) VALUES " +
                    "(1, 5, 'Admin1', 'admin1', 'adminpass1', '777888999', 'admin1@example.com'), " +
                    "(2, 2, 'Admin2', 'admin2', 'adminpass2', '111223344', 'admin2@example.com');");


            statement.execute("INSERT INTO HydroStep (userID, consumed_ml, goals_completed, total_steps) VALUES " +
                    "(1, 1500, true, 8000), " +
                    "(2, 2000, false, 6000), " +
                    "(3, 1200, true, 10000), " +
                    "(4, 1800, false, 7500), " +
                    "(5, 2500, true, 12000), " +
                    "(6, 1600, false, 5000), " +
                    "(7, 2000, true, 9000), " +
                    "(8, 3000, false, 8000), " +
                    "(9, 1400, true, 11000), " +
                    "(10, 2200, false, 7000);");



            System.out.println("[DEBUG] Database setup completed.");
        } catch (SQLException e) {
            System.out.println("[ERROR] Error setting up database: " + e.getMessage());
        }

    }
}
