package com.example.hydrosteps;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class MicrobitReader implements Runnable {

    private volatile boolean running = true;
    private SerialPort comPort;

    private DashboardController dashboardController;

    public MicrobitReader(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
        comPort = SerialPort.getCommPort("COM3");
        comPort.setBaudRate(115200);
    }

    @Override
    public void run() {
        if (comPort.openPort()) {
            System.out.println("Port is open.");
        } else {
            System.out.println("Failed to open port.");
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
                System.out.println("Read " + numRead + " bytes.");
                System.out.println(new String(newData));
                dashboardController.updateInteger(newData);

            }
        });


        while (running) {
            try {
                Thread.sleep(1000); // !!!Prevent tight looping
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted");
                break;
            }
        }

        comPort.closePort();
        System.out.println("Port closed.");
    }

    public void stop() {
        running = false;
    }
}