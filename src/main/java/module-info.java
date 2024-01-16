module com.example.hydrosteps {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fazecast.jSerialComm;


    opens com.example.hydrosteps to javafx.fxml;
    exports com.example.hydrosteps;
}