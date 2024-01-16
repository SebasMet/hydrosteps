package com.example.hydrosteps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.io.IOException;

public class HydroStepsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        switchScene("login-view.fxml", stage);
        stage.setTitle("Hello!");
        stage.show();
    }
    public static void switchScene(String fxmlPath, Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HydroStepsApplication.class.getResource(fxmlPath));
            Scene newScene = new Scene(fxmlLoader.load());
            stage.setScene(newScene);

            if (fxmlPath.equals("dashboard.fxml")) {
                DashboardController dashboardController = fxmlLoader.getController();
                startMicrobitReader(dashboardController);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startMicrobitReader(DashboardController controller) {
        MicrobitReader reader = new MicrobitReader(controller);
        Thread readerThread = new Thread(reader);
        readerThread.start();

    }


    public static void main(String[] args) {
//        args[0] = "joe";
        System.out.println(args[0]);
        try {
            DatabaseConnection.getInstance(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        MicrobitReader reader = new MicrobitReader();
//        Thread readerThread = new Thread(reader);
//        readerThread.start();

        launch();
    }
}