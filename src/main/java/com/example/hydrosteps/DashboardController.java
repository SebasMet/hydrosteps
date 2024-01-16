package com.example.hydrosteps;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.SQLException;

public class DashboardController {
    private DatabaseConnection dbConnection;
    private String[] userData;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userWeightLabel;
    @FXML
    private Label userHeightLabel;
    @FXML
    private Label totalStepsLabel;

    private IntegerProperty someInteger = new SimpleIntegerProperty(0);

    public void initialize() throws SQLException  {
        dbConnection = DatabaseConnection.getInstance(null);
        this.userData = dbConnection.getUserData();


        userNameLabel.setText(userData[0]);
        userWeightLabel.setText("Weight: " + userData[1]);
        userHeightLabel.setText("Height: " + userData[2]);
    }

    public void updateInteger(byte[] newValue) {
        String newText = new String(newValue);
        if(!isStringEmptyOrWhitespace(newText)) {
            System.out.println("ACTUAL VALUE " + newText);
            Platform.runLater(() -> {
                totalStepsLabel.setText("Total Steps: " + newText);
            });
        }
    }

    private boolean isStringEmptyOrWhitespace(String str) {
        return str == null || str.trim().isEmpty();
    }
}
