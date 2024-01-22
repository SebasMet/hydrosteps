package com.example.hydrosteps;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

public class DashboardController {
    private DatabaseConnection dbConnection;
    private String[] userData;
    private int[] goals;
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
    @FXML
    private TextField inputField1;
    @FXML
    private TextField inputField2;
    @FXML
    private TextField inputField3;
    @FXML
    private Circle circle1;
    @FXML
    private Circle circle2;
    @FXML
    private Circle circle3;
    @FXML
    private Text circleText1;
    @FXML
    private Text circleText2;
    @FXML
    private Text circleText3;

    @FXML
    private ImageView dashboardImage;

    private IntegerProperty someInteger = new SimpleIntegerProperty(0);

    public void initialize() throws SQLException  {
        dbConnection = DatabaseConnection.getInstance(null);
        this.userData = dbConnection.getUserData();
        this.setUpGoals();

        Image image = new Image("https://i.ibb.co/9v3C5mn/kisspng-splash-drawing-water-clip-art-water-cliparts-transparent-5ab16378d8b1c8-50206660152157477688.png");
        dashboardImage.setImage(image);


        userNameLabel.setText(userData[0]);
        userWeightLabel.setText("Weight: " + userData[1]);
        userHeightLabel.setText("Height: " + userData[2]);
    }

    private void setUpGoals() {
        String goalsString = dbConnection.getGoalsOfCurrentUser();
        if (goalsString != null && !goalsString.isEmpty()) {
            String[] goalsArray = goalsString.split(",");

            this.goals = new int[goalsArray.length];

            for (int i = 0; i < goalsArray.length; i++) {
                try {
                    this.goals[i] = Integer.parseInt(goalsArray[i].trim());
                } catch (NumberFormatException e) {
                    System.out.println("[ERROR] Error parsing goal as integer: " + e.getMessage());
                }
            }
            this.circleText1.setText(String.valueOf(this.goals[0]));
            this.circleText2.setText(String.valueOf(this.goals[1]));
            this.circleText3.setText(String.valueOf(this.goals[2]));
        } else {
            System.out.println("[ERROR] No goals found for the current user.");
        }
    }

    private void checkGoals() {
        if(this.goals == null) {
            return;
        }
        int totalStepsOfCurrentUser = dbConnection.getTotalStepsOfCurrentUser();

        if(goals[0] <= totalStepsOfCurrentUser) {
            this.circle1.setFill(Color.GREEN);
        }
        if(goals[1] <= totalStepsOfCurrentUser) {
            this.circle2.setFill(Color.GREEN);
        }
        if(goals[2] <= totalStepsOfCurrentUser) {
            this.circle3.setFill(Color.GREEN);
        }
    }

    public void updateTotalSteps(String newText) {
        Platform.runLater(() -> {
            totalStepsLabel.setText("Total Steps: " +  dbConnection.incrementTotalSteps());
            this.checkGoals();
        });
    }

    public void updateTotalMlConsumed(String totalAmount) {
        Platform.runLater(() -> {
            totalMlWaterConsumed.setText("Total ML Water Consumed: " + totalAmount);
        });


    }

    @FXML
    protected void handleLogout() {
        HydroStepsApplication.switchScene("login-view.fxml", (Stage) totalStepsLabel.getScene().getWindow());

    }

    @FXML
    protected void handleGoalChange() {
        try{
            int goal1 = Integer.parseInt(inputField1.getText());
            int goal2 = Integer.parseInt(inputField2.getText());
            int goal3 = Integer.parseInt(inputField3.getText());

            Integer[] goals = {goal1, goal2, goal3};
            String userGoals = "";

            Arrays.sort(goals);

            for (int i = 0; i < goals.length; i++) {
                userGoals += String.valueOf(goals[i]);
                if(i + 1 != goals.length) {
                    userGoals += ",";
                }
            }
            dbConnection.addGoalToCurrentUser(userGoals);
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
        }

        this.circle1.setFill(Color.GRAY);
        this.circle2.setFill(Color.GRAY);
        this.circle3.setFill(Color.GRAY);
        this.setUpGoals();
    }
}
