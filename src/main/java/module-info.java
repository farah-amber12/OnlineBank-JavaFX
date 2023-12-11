module com.example.onlinebank {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.onlinebank to javafx.fxml;
    exports com.example.onlinebank;
}