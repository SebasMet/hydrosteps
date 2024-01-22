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
    private static boolean isMicrobitReaderStarted = false;
    private static MicrobitReader reader = null;
    @Override
    public void start(Stage stage) throws IOException {
        switchScene("login-view.fxml", stage);
        stage.setTitle("Hydrosteps!");
        stage.show();
    }
    public static void switchScene(String fxmlPath, Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HydroStepsApplication.class.getResource(fxmlPath));
            Scene newScene = new Scene(fxmlLoader.load(), 1600, 800 );
            stage.setScene(newScene);

            if (fxmlPath.equals("dashboard.fxml") && !isMicrobitReaderStarted) {
                DashboardController dashboardController = fxmlLoader.getController();
                System.out.println("MICROBITREADER SETUP");
                startMicrobitReader(dashboardController);
                isMicrobitReaderStarted = true;
            }
            else if (fxmlPath.equals("dashboard.fxml")) {
                DashboardController dashboardController = fxmlLoader.getController();
                System.out.println("MICROBITREADER UPDATE");
                reader.updateController(dashboardController);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startMicrobitReader(DashboardController controller) throws SQLException {
        reader = new MicrobitReader(controller);
        Thread readerThread = new Thread(reader);
        readerThread.start();
    }


    public static void main(String[] args) {
//        System.out.println(args[0]);
//        try {
//            DatabaseConnection.getInstance(args[0]);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
////        MicrobitReader reader = new MicrobitReader();
////        Thread readerThread = new Thread(reader);
////        readerThread.start();
//
//        launch();

        try {
            BluetoothDiscovery discovery = new BluetoothDiscovery();
            discovery.discoverDevices();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}