package com.example.hydrosteps;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void handleLogin() {
        DatabaseConnection dbConnection = null;
        try {
            dbConnection = DatabaseConnection.getInstance(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        System.out.println("Username: " + username + ", Password: " + password);

         if(dbConnection.checkCredentials(username, password)) {
             System.out.println("Valid asf");
         }
         else {
             System.out.println("not valid");
         }
    }
}