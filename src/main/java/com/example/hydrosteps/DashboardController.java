package com.example.hydrosteps;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

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
    @FXML
    private Label totalMlWaterConsumed;

    private IntegerProperty someInteger = new SimpleIntegerProperty(0);

    public void initialize() throws SQLException  {
        dbConnection = DatabaseConnection.getInstance(null);
        this.userData = dbConnection.getUserData();


        userNameLabel.setText(userData[0]);
        userWeightLabel.setText("Weight: " + userData[1]);
        userHeightLabel.setText("Height: " + userData[2]);
    }

    public void updateTotalSteps(String newText) {
            System.out.println("ACTUAL VALUE " + newText);
            System.out.println("2 over");
            Platform.runLater(() -> {
                totalStepsLabel.setText("Total Steps: " + newText);
            });
    }

    public void updateTotalMlConsumed(String totalAmound) {
        Platform.runLater(() -> {
            totalMlWaterConsumed.setText("Total ML Water Consumed: " + totalAmound);
        });


    }

    @FXML
    protected void handleLogout() {
        HydroStepsApplication.switchScene("login-view.fxml", (Stage) totalStepsLabel.getScene().getWindow());

    }
}
