package com.example.rainfallanalyzerandvisualizer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import textio.TextIO;

/**
 * CP2406 Albert Alvaro (14328932)
 * Rainfall Data Visualizer - Beta Version
 * This Java class will draw a stacked bar chart which displays the analyzed data from the analyzed rainfall data folder
 * processed by the rainfall analyzer class.
 */
public class RainfallVisualizer extends Application{

    static String filename;
    public static void main(String[] args) {
        System.out.println("Enter filename: (Enter return to exit program)");
        try {
            var path = TextIO.getln();
            filename = "src/main/resources/analyzed_rainfalldata/" + path;
            TextIO.readFile(filename);
            launch();
        } catch (IllegalArgumentException e) {
            System.out.println("No file of such name found");
            Platform.exit();
        }
        Platform.exit();
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane(getRainfallBarChart());
        root.setStyle("-fx-border-width: 4px; -fx-border-color: #444");
        Scene scene = new Scene(root, 1200, 600);
        stage.setScene(scene);
        stage.setTitle("Rainfall Visualizer Alpha");
        stage.show();
        stage.setResizable(false);
    }

    /**
     * This method will draw the stacked bar chart which will be displayed using the data from a specified file in the
     * analyzed rainfall data folder
     * @return This method returns a StackedBarChart
     */
    public static StackedBarChart<String, Number> getRainfallBarChart(){
        //Creation of the X and Y axis
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Months");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Rainfall (mm)");

        StackedBarChart<String, Number> rainfallChart = new StackedBarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> totals = new XYChart.Series<>();
        totals.setName("Total Rainfall");
        XYChart.Series<String, Number> min = new XYChart.Series<>();
        min.setName("Minimum Rainfall");
        XYChart.Series<String, Number> max = new XYChart.Series<>();
        max.setName("Max Rainfall");

        //Used for ignoring the header of the CSV file
        TextIO.getln();

        while(!TextIO.eof()) {
            String[] line = TextIO.getln().trim().strip().split(",");
            min.getData().add(new XYChart.Data<>(line[0]+"/"+line[1], Double.parseDouble(line[3])));
            max.getData().add(new XYChart.Data<>(line[0]+"/"+line[1], Double.parseDouble(line[4])));
            totals.getData().add(new XYChart.Data<>(line[0]+"/"+line[1], Double.parseDouble(line[2])));
        }

        if (filename == null ){
            rainfallChart.setTitle("No data loaded");
        } else rainfallChart.setTitle("Analyzed Rainfall Data Chart");
        rainfallChart.setCategoryGap(0.0);
        rainfallChart.setVerticalGridLinesVisible(false);
        rainfallChart.setHorizontalGridLinesVisible(false);

        rainfallChart.getData().add(min);
        rainfallChart.getData().add(max);
        rainfallChart.getData().add(totals);

        return rainfallChart;
    }


}
