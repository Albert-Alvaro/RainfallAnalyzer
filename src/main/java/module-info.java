module com.example.rainfallanalyzerandvisualizer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires org.apache.commons.csv;

    opens com.example.rainfallanalyzerandvisualizer to javafx.fxml;
    exports com.example.rainfallanalyzerandvisualizer;
}