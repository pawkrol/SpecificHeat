package edu.agh;

import edu.agh.utils.Pair;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Controller {

    private final Resolver resolver = new Resolver();

    @FXML
    private VBox vbox;

    @FXML
    private Label fileNameLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button resolveButton;

    @FXML
    private Button showChartsButton;

    @FXML
    public void onOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Input File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text", "*.txt")
        );
        File file = fileChooser.showOpenDialog(vbox.getScene().getWindow());

        if (file == null) return;

        fileNameLabel.setText(file.getName());

        List<Pair> data = new FileProcessor().getDataFromFile(file);
        resolver.setData(data);

        resolveButton.setDisable(false);
        statusLabel.setText("Data ready");
    }

    @FXML
    public void onResolve() {
        statusLabel.setText("Calculating...");

        resolver.resolve(() ->
            Platform.runLater(() -> {
                showChartsButton.setDisable(false);
                statusLabel.setText("Ready");
            })
        );
    }

    @FXML
    public void onShowCharts() {

        //TODO? każdy wykres ma oddzielny przycisk
        //showTemperatureSpecificHeatChart();
        //showInterpolatedTemperatureSpecificHeatChart();
        showTemperatureEnthalpyChart();

    }

    //to tylko sugestie żebyś nie musiał się namyślać jak ci się nie chce to jebać
    //TODO? zapis wyników do pliku na przycisk
    //TODO usunąc kropki bo wykresy nieczytelne
    //TODO odznaczenie (kolorem, liniami) zakresu przemiany na wykresie jeśli się da
    private void showTemperatureSpecificHeatChart() {
        Stage stage = new Stage();
        stage.setTitle("Temperature Specific Heat Chart");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Specific Heat");
        yAxis.setLabel("Temperature");

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);

        XYChart.Series series = new XYChart.Series();
        resolver.getTemperatureSpecificHeatPairs()
                .forEach(pair ->
                    series.getData().add(new XYChart.Data(pair.getLeftValue(), pair.getRightValue()))
                );

        lineChart.getData().add(series);

        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void showInterpolatedTemperatureSpecificHeatChart() {
        Stage stage = new Stage();
        stage.setTitle("Interpolated Temperature Specific Heat Chart");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Specific Heat");
        yAxis.setLabel("Temperature");

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        XYChart.Series series = new XYChart.Series();
        resolver.getInterpolatedTemperatureSpecificHeatPairs()
                .forEach(pair ->
                        series.getData().add(new XYChart.Data(pair.getLeftValue(), pair.getRightValue()))
                );

        lineChart.getData().add(series);

        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void showTemperatureEnthalpyChart() {
        Stage stage = new Stage();
        stage.setTitle("Temperature Enthalpy Chart");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Temperature");
        yAxis.setLabel("Enthalpy");

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();

        resolver.getTemperatureEnthalpyPairs()
                .forEach(pair ->
                        series1.getData().add(new XYChart.Data(pair.getLeftValue(), pair.getRightValue()))
                );

        lineChart.getData().add(series1);

        resolver.getNewTemperatureEnthalpyPairs()
                .forEach(pair ->
                        series2.getData().add(new XYChart.Data(pair.getLeftValue(), pair.getRightValue()))
                );

        //lineChart.getData().add(series1);
        lineChart.getData().add(series2);

        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();

    }

}
