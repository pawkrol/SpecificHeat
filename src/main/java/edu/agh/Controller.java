package edu.agh;

import edu.agh.utils.FunctionConverter;
import edu.agh.utils.Functions;
import edu.agh.utils.Pair;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.math3.analysis.UnivariateFunction;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable{

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
    private Button tempSHChartButton;

    @FXML
    private Button interpTempSHChartButton;

    @FXML
    private Button tempEnthalpyChartButton;

    @FXML
    private TextField t1Input;

    @FXML
    private TextField t2Input;

    @FXML
    private TextField enthalpyInput;

    @FXML
    private ComboBox<Pair<String, UnivariateFunction>> functionsSelect;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        functionsSelect.setConverter(new FunctionConverter());
        functionsSelect.getItems().addAll(FunctionConverter.getStringUnivariateFunctionPairs());
        functionsSelect.getSelectionModel().select(0);
    }

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

        List<Pair<Double, Double>> data = new FileProcessor().getDataFromFile(file);
        resolver.setData(data);

        resolveButton.setDisable(false);
        statusLabel.setText("Data ready");
    }

    @FXML
    public void onResolve() {
        statusLabel.setText("Calculating...");

        double t1 = Double.parseDouble(t1Input.getText());
        double t2 = Double.parseDouble(t2Input.getText());
        double enthalpy = Double.parseDouble(enthalpyInput.getText());
        UnivariateFunction function = functionsSelect.getSelectionModel()
                .getSelectedItem()
                .getRightValue();

        resolver.setT1(t1);
        resolver.setT2(t2);
        resolver.setEnthalpy(enthalpy);
        resolver.setFunction(function);

        resolver.resolve(() ->
            Platform.runLater(() -> {
                tempSHChartButton.setDisable(false);
                interpTempSHChartButton.setDisable(false);
                tempEnthalpyChartButton.setDisable(false);
                statusLabel.setText("Ready");

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Output File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Text", "*.txt")
                );
                File file = fileChooser.showSaveDialog(vbox.getScene().getWindow());

                new FileProcessor().saveToFile(file, resolver.getTemperatureEnthalpyPairs(), "temp", "enthalpy");
            })
        );
    }

    @FXML
    public void onTempSHChartButton() {
        showTemperatureSpecificHeatChart();
    }

    @FXML
    public void onInterpTempSHChartButton() {
        showInterpolatedTemperatureSpecificHeatChart();
    }

    @FXML
    public void onTempEnthalpyChartButton() {
        showTemperatureEnthalpyChart();
    }

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
        lineChart.setCreateSymbols(false);

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
        lineChart.setCreateSymbols(false);

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

        lineChart.getData().add(series2);

        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

}
