package com.example.hydrosteps;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.sql.SQLException;

public class MicrobitReader implements Runnable {

    private volatile boolean running = true;
    private SerialPort comPort;
    private StringBuilder buffer = new StringBuilder();
    private DashboardController dashboardController;
    private DatabaseConnection dbConnection;


    public MicrobitReader(DashboardController dashboardController) throws SQLException {
        this.dbConnection = DatabaseConnection.getInstance(null);
        this.dashboardController = dashboardController;
        comPort = SerialPort.getCommPort("COM3");
        comPort.setBaudRate(115200);
    }

    public void updateController(DashboardController newController) {
        this.dashboardController = newController;
    }

    @Override
    public void run() {
        if (comPort.openPort()) {
            System.out.println("[DEBUG] Port is open.");
        } else {
            System.out.println("[ERROR] Failed to open port.");
            return;
        }

        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                byte[] newData = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(newData, newData.length);
//                System.out.println("Read " + numRead + " bytes.");
                String stringData = new String(newData);
                buffer.append(stringData);
//                System.out.println(stringData);
                if (buffer.toString().contains("\n")) {
                    String completeData = buffer.toString();
                    System.out.println("[DEBUG] String data: " + completeData);
                    if (completeData.contains("ML")) {
                        dashboardController.updateTotalMlConsumed(completeData.replaceAll("\\D", ""));
                    } else {
                        System.out.println("[DEBUG] UPDATE STEPS");
                        dashboardController.updateTotalSteps(completeData.replaceAll("\\D", ""));
                        //dbConnection.incrementTotalSteps();
                    }
                    buffer = new StringBuilder();
                    System.out.println("[DEBUG] RESET" + buffer);
                }
            }
        });


        while (running) {
            try {
                Thread.sleep(1000); // !!!Prevent tight looping
            } catch (InterruptedException e) {
                System.out.println("[ERROR] Thread interrupted");
                break;
            }
        }

        comPort.closePort();
        System.out.println("[DEBUG] Port closed.");
    }

    public void stop() {
        running = false;
    }
}