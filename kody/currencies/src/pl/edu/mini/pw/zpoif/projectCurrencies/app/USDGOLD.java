package pl.edu.mini.pw.zpoif.projectCurrencies.app;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.*;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.*;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.*;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataReader.URLConnectionReader;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class USDGOLD extends Application {
    private Scene scene1, scene2;
    private Stage window;

    @Override
    public void start( Stage primaryStage ) {
        setAndShowDataPickerWindow(primaryStage);
    }

    private void setAndShowDataPickerWindow(Stage primaryStage) {
        final DatePicker datePicker1 = getDatePicker(LocalDate.now().minusYears(1));
        final DatePicker datePicker2 = getDatePicker(LocalDate.now());
        Button b = new Button( "Pokaż wykres" );
        b.setOnAction(event -> setAndShowLineChartWindow(datePicker1, datePicker2));
        GridPane gridPane =  setGrid(datePicker1, datePicker2, b);
        window = primaryStage;
        window.setTitle("Wybierz przedział czasowy");
        scene1 = new Scene(gridPane);
        window.setScene( scene1 );
        window.show();
    }

    private void setAndShowLineChartWindow(DatePicker datePicker1, DatePicker datePicker2){
        LocalDate ld1 = datePicker1.getValue();
        LocalDate ld2 = datePicker2.getValue();
        String strDate1 = ld1.toString();
        String strDate2 = ld2.toString();
        LineChart lineChart = createLineChart(strDate1,strDate2);
        window.setTitle("Line Chart Sample");
        Button back = new Button("Return");
        back.setOnAction(e -> window.setScene(scene1));
        VBox layout = new VBox();
        layout.getChildren().addAll(back, lineChart);
        scene2 = new Scene(layout,800,600);
        window.setScene(scene2);
        window.show();
    }


    private LineChart<Number, Number> createLineChart(String strDate1, String strDate2){
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart lineChart =
                new LineChart(xAxis,yAxis);

        lineChart.setCreateSymbols(false);
        lineChart.setTitle("Dolar vs. złoto " + strDate1 + " " + strDate2);
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Złoto");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("USD");

        String goldData = null;
        String stringTable = null;
        try {
            goldData = URLConnectionReader.PeriodGoldRequest( strDate1, strDate2);
            stringTable = URLConnectionReader.PeriodCurrencyRequest(TableTyp.A, CurrencyCode.USD, strDate1, strDate2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (goldData == null || stringTable == null){
            AlertBox.display("Błąd", "Brak danych");

        }else{

            List<Gold> goldList = JsonConverter.convertGoldData(goldData);
            int i = 0;
            for (Gold g :goldList){
                series1.getData().add(new XYChart.Data(g.getData(), g.getCena()));
                i++;
            }

            OneCurrencyTable table1 = JsonConverter.convertOneCurrencyTable(stringTable);
            Double usdAve = table1.getRates().stream().mapToDouble(CurrencyWithDate::getMid).summaryStatistics().getAverage();
            Double goldAve = goldList.stream().mapToDouble(Gold::getCena).summaryStatistics().getAverage();
            Double scale = goldAve/usdAve;
            i = 0;
            for (CurrencyWithDate c :table1.getRates()){
                series2.getData().add(new XYChart.Data(c.getEffectiveDate(), c.getMid()*scale));
                i++;
            }
            yAxis.setLabel("Cena złota/ Cena dolara x " + String.valueOf(scale.intValue()/10*10));

        }

        yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false);
        lineChart.getData().add(series1);
        lineChart.getData().add(series2);
        return lineChart;
    }

    private DatePicker getDatePicker(LocalDate localDate) {
        final DatePicker datePicker1 = new DatePicker();
        restrictDatePicker(datePicker1, LocalDate.of(2013, Month.JANUARY, 2), LocalDate.now());
        datePicker1.setValue(localDate);
        return datePicker1;
    }

    private GridPane setGrid(DatePicker datePicker1, DatePicker datePicker2, Button b){
        GridPane gridPane = new GridPane();
        //Setting size for the pane
        gridPane.setMinSize(400, 200);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);
        Label start = new Label("Od kiedy");
        Label end = new Label("Do kiedy");
        gridPane.add(start, 0, 0);
        gridPane.add(datePicker1, 1, 0);
        gridPane.add(end, 0, 1);
        gridPane.add(datePicker2, 1, 1);
        gridPane.add(b, 1, 2);
        return gridPane;
    }

    private void restrictDatePicker(DatePicker datePicker, LocalDate minDate, LocalDate maxDate) {
        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(minDate)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }else if (item.isAfter(maxDate)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);
    }



    public static void main(String[] args) {
        launch(args);
    }
}
