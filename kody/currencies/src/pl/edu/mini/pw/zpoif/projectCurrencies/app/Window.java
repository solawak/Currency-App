package pl.edu.mini.pw.zpoif.projectCurrencies.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import pl.edu.mini.pw.zpoif.projectCurrencies.calculator.Calculator;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.Currency;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.CurrencyCode;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.CurrencyWithDate;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.Gold;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.JsonConverter;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.CTable;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.OneCurrencyTable;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.Table;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.TableTyp;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataReader.URLConnectionReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Window extends Application {
    private TabPane tabPane;
    private Button b = new Button( "Show" );
    private GridPane gridPane; // daty
    private LineChart goldChart;
    private Boolean toBuy = true; // kurs kupna/sprzedaży
    private CurrencyCode c1;
    private CurrencyCode c2;
    private VBox leftVBox = new VBox();
    private HBox actualityVBox = new HBox();
    private Double price; // cena za 1 jakiejś waluty
    private TextField textField = new TextField("0"); // wartość do obliczenia w kalkulatorze walut
    private String printPrice; // string z price
    private Double input; // wczytywane w kalkulatorze
    private List<String> selectedList; //lista wybranych walut do wykresu walut
    private ComboBox comboBox;// lista wyboru walut
    private ListView<String> lv = new ListView<>();
    private LineChart currencyChart;
    private DatePicker datePicker1;
    private DatePicker datePicker2;
    private GridPane calGrid;



    public static void main(String[] args) {
        launch(args);
    }

    private Scene scene;
    private Stage window;
    @Override
    public void start(Stage primaryStage) throws IOException {
        createWindow(primaryStage);
    }
    private void createWindow(Stage primaryStage) throws IOException {
        datePicker1 = getDatePicker(LocalDate.now().minusYears(1));
        datePicker2 = getDatePicker(LocalDate.now());
        datePicker1.getEditor().setDisable(true);
        datePicker2.getEditor().setDisable(true);

        VBox topBorder = setTopBorderPane();
        VBox leftBorder = setLeftBorderPane(datePicker1, datePicker2);
        VBox centreBorder = setCentreBorderPane(datePicker1, datePicker2);

        BorderPane pane = new BorderPane();
        pane.setTop(topBorder);
        pane.setLeft(leftBorder);
        pane.setCenter(centreBorder);

        scene = new Scene(pane, 1000, 600);
        window = primaryStage;
        window.setTitle("NBP rates");
        window.setScene(scene);
        window.show();
    }

    private VBox setTopBorderPane() {
        Text title = new Text();
        title.setFont(Font.font("verdena", FontWeight.BOLD, FontPosture.REGULAR, 20));
        title.setText("Currencies and gold rates");
        Separator separator = new Separator();
        separator.setHalignment(HPos.CENTER);
        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(title, separator);
        return vBox;
    }
    private VBox setLeftBorderPane(DatePicker datePicker1, DatePicker datePicker2) throws IOException {
        gridPane = setGrid(datePicker1, datePicker2);
        makeLineChartGold();
        leftVBox.getChildren().addAll(gridPane);
        setCalculator();

        return leftVBox;
    }
    private VBox setCentreBorderPane(DatePicker datePicker1, DatePicker datePicker2) throws IOException {
        tabPane = new TabPane();

        b.setOnAction(event -> {
            makeLineChartGold();
            updateByDateCentreBorderPane();
            makeCurrencyChart(datePicker1,datePicker2);
        });

        Tab tab1 = new Tab("Actuality");
        Tab tab2 = new Tab("Currencies");
        Tab tab3 = new Tab("USD and gold");
        Tab tab4 = new Tab("More info");
        tab1.setClosable(false); // żeby nie można było usunąć zakładki
        tab2.setClosable(false);
        tab3.setClosable(false);
        tab4.setClosable(false);

        // Ustawiamy to, co ma być w okienkach zakładek:
        Text aboutActuality = new Text("Rates for 4 of the most popular currencies");
        aboutActuality.setFont(Font.font(26));
        addElementsToActualityVBox();
        actualityVBox.setSpacing(16);
        VBox actualityGroup = new VBox(aboutActuality, actualityVBox);
        actualityGroup.setSpacing(10);

        tab1.setContent(actualityGroup);

        tab2.setContent(CheckBox());

        tab3.setContent(goldChart); // zawartość okienka

        Text aboutProject = new Text("Application was created by Adrianna Grudzień and Katarzyna Solawa. \n" +
                "Data orgins from NBP.");
        aboutProject.setWrappingWidth(800);
        tab4.setContent(aboutProject);


        tabPane.getTabs().addAll(tab1,tab2,tab3,tab4);

        VBox vBox = new VBox(tabPane);
        return vBox;
    }



// ----------- Aktualności -------------


    private void addElementsToActualityVBox() throws IOException {
        // EUR USD CHF GBP
        List<CurrencyRatio> ratios = createCurrencyRatiosList();

        Label[] labels = new Label[ratios.size()];
        TextFlow[] texts = new TextFlow[ratios.size()];
        for (int i = 0; i < ratios.size(); i++) {
            CurrencyRatio cR = ratios.get(i);

            String code = cR.getCode().toString();
            BigDecimal mid = new BigDecimal(cR.getMid()).setScale(2, RoundingMode.HALF_UP); //zaokrąglanie do 2 miejsc po przecinku
            BigDecimal bid = new BigDecimal(cR.getBid()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal ask = new BigDecimal(cR.getAsk()).setScale(2, RoundingMode.HALF_UP);


            Text t1 = new Text("|  "+ code + "\n" +
                    "---------------------\n"+
                    "|  average: ");

            Text av = setTextColor(mid);

            Text t2 = new Text("%"+ "\n" +
                    "|  buying: ");

            Text buying = setTextColor(bid);

            Text t3 = new Text("%"+ "\n" +
                    "|  selling: ");

            Text sell = setTextColor(ask);

            TextFlow flow = new TextFlow();
            flow.getChildren().addAll(t1,av, t2,buying,t3,sell, new Text("%"));
            texts[i] = flow;
        }
        actualityVBox.getChildren().addAll(texts);

    }
    private Text setTextColor(BigDecimal value){
        Text text = new Text();
        if (value.doubleValue() > 0){
            text.setStyle("-fx-fill: #4F8A10");

        }else if (value.doubleValue() < 0)
            text.setStyle("-fx-fill: RED");

        text.setText(String.valueOf(value.doubleValue()));
        return text;

    }

    private List<CurrencyRatio> createCurrencyRatiosList() throws IOException {
        List<Currency> yesterdayRates = getLastLastRates();
        List<Currency> todayRates = getLastRates();

        HashMap<CurrencyCode, Double> bidYesterdayMap = convertRatesToMap( 0, yesterdayRates);
        HashMap<CurrencyCode, Double> bidTodayMap = convertRatesToMap(0, todayRates);
        HashMap<CurrencyCode, Double> askYesterdayMap = convertRatesToMap(1, yesterdayRates);
        HashMap<CurrencyCode, Double> askTodayMap = convertRatesToMap(1, todayRates);

        CurrencyCode code1 = CurrencyCode.EUR;
        CurrencyCode code2 = CurrencyCode.USD;
        CurrencyCode code3 = CurrencyCode.CHF;
        CurrencyCode code4 = CurrencyCode.GBP;

        CurrencyRatio ratio1 = new CurrencyRatio(code1, bidYesterdayMap.get(code1), bidTodayMap.get(code1),
                askYesterdayMap.get(code1), askTodayMap.get(code1));
        CurrencyRatio ratio2 = new CurrencyRatio(code2, bidYesterdayMap.get(code2), bidTodayMap.get(code2),
                askYesterdayMap.get(code2), askTodayMap.get(code2));
        CurrencyRatio ratio3 = new CurrencyRatio(code3, bidYesterdayMap.get(code3), bidTodayMap.get(code3),
                askYesterdayMap.get(code3), askTodayMap.get(code3));
        CurrencyRatio ratio4 = new CurrencyRatio(code4, bidYesterdayMap.get(code4), bidTodayMap.get(code4),
                askYesterdayMap.get(code4), askTodayMap.get(code4));

        List<CurrencyRatio> ratioList = new ArrayList<>();
        ratioList.add(ratio1);
        ratioList.add(ratio2);
        ratioList.add(ratio3);
        ratioList.add(ratio4);
        return ratioList;
    }
    private List<Currency> getLastLastRates() throws IOException {
        String cTableString = URLConnectionReader.LastTableRequest(TableTyp.C, 2);
        List<Table> tableList = (List<Table>) JsonConverter.convertPeriodTable(cTableString, TableTyp.C);
        CTable table = (CTable) tableList.get(0);
        List<Currency> rates = table.getRates();
        return rates;
    }
    private static HashMap<CurrencyCode,Double> convertRatesToMap(int rateTyp, List<Currency> rates) {
        // rateTyp = {0,1}; 0 - bid, 1 - ask
        HashMap<CurrencyCode, Double> map;
        if (rateTyp == 0) {
            map = (HashMap<CurrencyCode, Double>) rates.stream()
                    .collect(Collectors.toMap(Currency::getCode, Currency::getBid)); //bid - kurs kupna
        }
        else {
            map = (HashMap<CurrencyCode, Double>) rates.stream()
                    .collect(Collectors.toMap(Currency::getCode, Currency::getAsk)); //ask - kurs kupna
        }
        return map;
    }


    // ----------- Kalkulator -------------
    private void setCalculator() throws IOException {
        // tytuł
        Label title = new Label("Currencies calculator");
        title.setFont(Font.font(24));

        // lista walut:
        ObservableList<CurrencyCode> currencyCodes = createCurrenciesObservableList();

        ComboBox<CurrencyCode> comboBox1 = new ComboBox<>(currencyCodes);
        ComboBox<CurrencyCode> comboBox2 = new ComboBox<>(currencyCodes);
        List<Currency> rates = getLastRates();
        List<CurrencyCode> codesList = createCodesList(rates);

        // wynik działania kalkulatora:
        makeInputTextFieldOnlyForDouble();
        Label calculatorResultText = new Label(printPrice);

        // guziczki
        final ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton buyButton = new RadioButton("buy");
        RadioButton sellButton = new RadioButton("sell");
        buyButton.setToggleGroup(toggleGroup);
        sellButton.setToggleGroup(toggleGroup);
        buyButton.setSelected(true);
        // akcja guziczków buy/sell:
        buyButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean isSelected) {
                if (isSelected) {
                    toBuy = true;
                }
                else {
                    toBuy = false;
                }
                try {
//                    readComboBoxes(comboBox1, comboBox2);
                    updateCalculator();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // guzik zatwierdzający:
        Button button = new Button("Calculate");
        button.setOnAction(actionEvent -> {
            try {
//                readTextField();
                if (comboBox1.getValue()!= null && comboBox2.getValue()!= null){
                    readComboBoxes(comboBox1, comboBox2);
                    updateCalculator();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // dodatkowy tekst:
        Label todayDateLabel;

        if (LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY || LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY){
            String table = URLConnectionReader.LastCurrencyRequest(TableTyp.C, CurrencyCode.USD, 1);
            OneCurrencyTable table1= JsonConverter.convertOneCurrencyTable(table);
            todayDateLabel = new Label("Data origins from (" + table1.getRates().get(0).getEffectiveDate() + ")");
        }
        else{
            todayDateLabel = new Label("Data origins from today (" + LocalDate.now().toString() + ")");
        }
        title.setAlignment(Pos.CENTER);
        calGrid = new GridPane();
        calGrid.setAlignment(Pos.CENTER);
        calGrid.setVgap(5);
        calGrid.setHgap(5);


        calGrid.add(title, 0,0, 3,1);
        calGrid.add(buyButton,0,1);
        calGrid.add(sellButton, 2,1);
        calGrid.add(comboBox1,0,2);
        calGrid.add(comboBox2, 2,2);
        calGrid.add(textField, 0,3,3,1);
        calGrid.add(button, 1,4);
        calGrid.add(todayDateLabel, 0,5,3,1);
        calGrid.add(calculatorResultText, 0,6,3,1);
        Label arrow = new Label("    \uD83E\uDC46");
        arrow.setFont(new Font("Arial", 22));
        arrow.setAlignment(Pos.CENTER);
        calGrid.add(arrow,1,2);

        leftVBox.getChildren().add(calGrid);
    }
    private void updateCalculator() throws IOException {
        List<Currency> rates = getLastRates();
        String resultString = calculateCurrency(toBuy, TableTyp.C, rates, c1, c2);
        Text resultText = new Text(resultString);
        if (calGrid.getChildren().size() == 10){
            Button clear = new Button("Clear");
            calGrid.add(clear,1,10);
            clear.setOnAction(actionEvent -> {
                if (calGrid.getChildren().size() > 10) {
                    calGrid.getChildren().remove(10, 13);
                }
            });
        }

        if (calGrid.getChildren().size() > 11){
            calGrid.getChildren().remove(11,13);
        }

        calGrid.add(resultText, 0, 7,3,1);

        // z textField:
        readTextField();
        Label x = new Label(input + " " + c1 + " = " + printPrice + " " + c2);
        x.setFont(Font.font(16));
        calGrid.add(x, 0, 9,3,1);
    }
    private void readTextField() {
        input = Double.valueOf(textField.getCharacters().toString());
        printPrice = String.valueOf(Math.round( price * input*100)/100.0);
    }
    private List<Currency> getLastRates() throws IOException {
        String cTableString = URLConnectionReader.LastTableRequest(TableTyp.C, 1);

        CTable cTable = (CTable) JsonConverter.convertTable(cTableString, TableTyp.C);
        List<Currency> rates = cTable.getRates();
        rates.add(new Currency("PLN", CurrencyCode.PLN, 1.0, 1.0));
        return rates;
    }
    private List<CurrencyCode> createCodesList(List<Currency> rates) {
        List<CurrencyCode> codesList = rates.stream().map((Currency x) -> x.getCode()).collect(Collectors.toList());
        return codesList;
    }
    private ObservableList<CurrencyCode> createCurrenciesObservableList() throws IOException {
        List<Currency> rates = getLastRates();
        List<CurrencyCode> codesList = createCodesList(rates);
        ObservableList<CurrencyCode> currencyCodes = FXCollections.observableArrayList(codesList); // możliwe opcje wyboru
        return currencyCodes;
    }
    private void readComboBoxes(ComboBox comboBox1, ComboBox comboBox2) throws IOException {
        c1 = (CurrencyCode) comboBox1.getValue();
        c2 = (CurrencyCode) comboBox2.getValue();
    }
    private String calculateCurrency(Boolean toBuy, TableTyp typ, List<Currency> rates, CurrencyCode c1, CurrencyCode c2) {
        //Kursy walut podane są w PLN
        // c1 -> c2 (Mamy c1, chcemy obliczyć ile to będzie c2)

        HashMap<CurrencyCode, Double> map = Calculator.convertRatesToMap(toBuy, typ, rates);
        Double price1 = map.get(c1);
        Double price2 = map.get(c2);

        if (price1 == null) {
            System.out.println("Nie ma danych dot. waluty " + c1 + "!");
            return null;
        }
        else if (price2 == null) {
            System.out.println("Nie ma danych dot. waluty " + c1 + "!");
            return null;
        }
        price = price1/price2;
        String result = printCalculatorInfo(c1,c2,price1,price2,price);
        return result;
    }
    private String printCalculatorInfo(CurrencyCode c1, CurrencyCode c2, Double price1, Double price2, Double price) {
        String s1 = "1 " + c1.toString() + " = " + Math.round(price1*10000)/10000.0 + " PLN";
        String s2 = "1 " + c2.toString() + " = " + Math.round(price2*10000)/10000.0 + " PLN";
        String s3 = "1 " + c1.toString() + " = " + Math.round(price*10000)/10000.0 + " " + c2;
        return (s1 + "\n" + s2 + "\n" + s3);
    }
    private void makeInputTextFieldOnlyForDouble() {
        Pattern pattern = Pattern.compile("\\d*|\\d+\\,\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        textField.setTextFormatter(formatter);
    }



    // ----------- Wykres złota -------------
    private void updateByDateCentreBorderPane() {
        Tab tab3 = tabPane.getTabs().get(2);
        tab3.setContent(goldChart);
    }
    private GridPane setGrid(DatePicker datePicker1, DatePicker datePicker2){
        GridPane gridPane = new GridPane();
        //Setting size for the pane
        gridPane.setMinSize(200, 200);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);
        Label start = new Label("From");
        Label end = new Label("To");
        gridPane.add(start, 0, 0);
        gridPane.add(datePicker1, 1, 0);
        gridPane.add(end, 0, 1);
        gridPane.add(datePicker2, 1, 1);
        gridPane.add(b, 1, 2);
        return gridPane;
    }
    private DatePicker getDatePicker(LocalDate localDate) {
        final DatePicker datePicker1 = new DatePicker();
        restrictDatePicker(datePicker1, LocalDate.of(2013, Month.JANUARY, 2), LocalDate.now());
        datePicker1.setValue(localDate);
        return datePicker1;
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
                        } else if (item.isAfter(maxDate)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);
    }
    private void makeLineChartGold() {
        LocalDate ld1 = datePicker1.getValue();
        LocalDate ld2 = datePicker2.getValue();
        String strDate1 = ld1.toString();
        String strDate2 = ld2.toString();
        goldChart = createLineChart(strDate1, strDate2);
    }
    private LineChart<Number, Number> createLineChart(String strDate1, String strDate2) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart lineChart =
                new LineChart(xAxis, yAxis);

        lineChart.setCreateSymbols(false);
        lineChart.setTitle("US Dollar vs. Gold " + strDate1 + " " + strDate2);
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Gold");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("US Dollar");



        List<Gold> goldList = (List<Gold>) periodReader(strDate1, strDate2, "USD", false);
        for (Gold g : goldList) {
            series1.getData().add(new XYChart.Data(g.getData(), g.getCena()));
        }

        OneCurrencyTable tableDollar = (OneCurrencyTable) periodReader(strDate1, strDate2, "USD", true);
        Double usdAve = tableDollar.getRates().stream().mapToDouble(CurrencyWithDate::getMid).summaryStatistics().getAverage();
        Double goldAve = goldList.stream().mapToDouble(Gold::getCena).summaryStatistics().getAverage();
        Double scale = goldAve / usdAve;
        for (CurrencyWithDate c : tableDollar.getRates()) {
            series2.getData().add(new XYChart.Data(c.getEffectiveDate(), c.getMid() * scale));
        }



        yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false);
        lineChart.getData().add(series1);
        lineChart.getData().add(series2);
        return lineChart;
    }



    // ----------- Wykres walut -------------
    private GridPane CheckBox(){
        ObservableList<String> strings = FXCollections.observableArrayList();
        for (CurrencyCode c:CurrencyCode.values()) {
            strings.add(c.toString()+" - "+ c.label);
        }
        comboBox = new ComboBox(strings);
        comboBox.setEditable(true);
        comboBoxListener(strings);
        comboBox.setOnAction(event -> {
            String item = (String) comboBox.getSelectionModel().getSelectedItem();
            if (item != ""){
                if(comboBox.getItems().contains(item)){
                    if((String) comboBox.getSelectionModel().getSelectedItem() != null) {
                        String selected = (String) comboBox.getSelectionModel().getSelectedItem();
                        updateListView(selected);

                    }
                }else{
                    comboBox.getSelectionModel().clearSelection();
                }

            }

        });
        comboBox.setMaxWidth(200);
        comboBox.getSelectionModel().selectFirst();
        comboBox.setPromptText("Please Select");


        lv.setCellFactory(param -> new XCell());
        lv.setMaxWidth(200);


        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        currencyChart =
                new LineChart(xAxis, yAxis);
        updateListView((String) comboBox.getSelectionModel().getSelectedItem());

        yAxis.setLabel("Currency value");
        xAxis.setLabel("Date");

        currencyChart.setCreateSymbols(false);
        currencyChart.setTitle("Currencies");
        currencyChart.setMinWidth(600);



        GridPane grid = new GridPane();
        GridPane.setHgrow(currencyChart, Priority.ALWAYS);


        grid.add(currencyChart,0,0, 1,2);
        grid.add(comboBox,1, 0, 1,1);
        grid.add(lv,1,1, 1,1);

        return grid;
    }

    private void updateListView(Object selectedItem){
        if (selectedItem != null || selectedItem.toString().length() > 0){
            if (lv.getItems().contains(selectedItem.toString()) == false){
                lv.getItems().add(selectedItem.toString());
                addCurrencySeries(selectedItem.toString());

            }

        }

    }

    private void comboBoxListener(ObservableList<String> strings){

        ObservableList<String> items = FXCollections.observableArrayList(strings);
        FilteredList<String> filteredItems = new FilteredList<String>(items, p -> true);

        // Add a listener to the textProperty of the combobox editor. The
        // listener will simply filter the list every time the input is changed
        // as long as the user hasn't selected an item in the list.

        comboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = comboBox.getEditor();
            final String selected = (String) comboBox.getSelectionModel().getSelectedItem();

            // This needs run on the GUI thread to avoid the error described
            // here: https://bugs.openjdk.java.net/browse/JDK-8081700.
            Platform.runLater(() -> {
                // If the no item in the list is selected or the selected item
                // isn't equal to the current input, we refilter the list.
                if (selected == null || !selected.equals(editor.getText())) {
                    filteredItems.setPredicate(item -> {
                        // We return true for any items that starts with the
                        // same letters as the input. We use toUpperCase to
                        // avoid case sensitivity.
                        if (item.toUpperCase().contains(newValue.toUpperCase())) {
                            return true;
                        } else {
                            return false;
                        }
                    });
                }
            });
        });
        comboBox.setItems(filteredItems);


    }

    private void addCurrencySeries(String selectedItem){
        LocalDate ld1 = datePicker1.getValue();
        LocalDate ld2 = datePicker2.getValue();
        String strDate1 = ld1.toString();
        String strDate2 = ld2.toString();

        createCurrencySeries(strDate1, strDate2,selectedItem );

    }

    private void makeCurrencyChart(DatePicker datePicker1, DatePicker datePicker2){
        LocalDate ld1 = datePicker1.getValue();
        LocalDate ld2 = datePicker2.getValue();
        String strDate1 = ld1.toString();
        String strDate2 = ld2.toString();

        currencyChart.getData().clear();

        for (String currency:lv.getItems()) {
            createCurrencySeries(strDate1, strDate2, currency);
        }
    }

    private void createCurrencySeries(String strDate1, String strDate2, String currency) {
        String currencyCode = currency.substring(0,3);
        XYChart.Series series = new XYChart.Series();
        series.setName(currency);
        OneCurrencyTable table = (OneCurrencyTable) periodReader(strDate1, strDate2, currencyCode, true);

        if (table != null){
            for (CurrencyWithDate c : table.getRates()) {
                series.getData().add(new XYChart.Data(c.getEffectiveDate(), c.getMid()));
            }
            currencyChart.getData().add(series);
        }

    }


    private class XCell extends ListCell<String> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("X");

        public XCell() {
            super();

            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(event -> {
                String code = getItem().substring(0,3);
                int index = getListView().getItems().indexOf(getItem());
                currencyChart.getData().remove(index);
                getListView().getItems().remove(getItem());
                comboBox.getSelectionModel().select(null);

            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
                label.setText(item);
                setGraphic(hbox);
            }
        }
    }

    private Object periodReader(String strDate1, String strDate2, String currencyCode, Boolean isCurrency){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        LocalDate dateTime1 = LocalDate.parse(strDate1, formatter);
        LocalDate dateTime2 = LocalDate.parse(strDate2, formatter);
        long daysBetween = dateTime1.until(dateTime2,ChronoUnit.DAYS);
        OneCurrencyTable table1 = null;
        String stringTable = null;
        String tmpStr2 ;
        LocalDate tmp ;
        List<Gold> goldList1 =null;

        while (daysBetween > 0){
            if (daysBetween > 367){
                tmp = dateTime1.plusDays(367);
                tmpStr2 = tmp.toString();
            }else {
                tmpStr2 = dateTime2.toString();
                tmp = dateTime2;
            }

            String tmpStr1 = dateTime1.toString();
            dateTime1 = dateTime1.plusDays(368);
            daysBetween = dateTime1.until(dateTime2, ChronoUnit.DAYS);

            try {
                if (isCurrency == true){
                    stringTable = URLConnectionReader.PeriodCurrencyRequest(TableTyp.A, CurrencyCode.valueOf(currencyCode), tmpStr1, tmpStr2);
                }else {
                    stringTable = URLConnectionReader.PeriodGoldRequest(tmpStr1, tmpStr2);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (stringTable == null) {
                daysBetween = 0;
                AlertBox.display("Error", "No data - "+ CurrencyCode.valueOf(currencyCode).label);
                lv.getItems().remove(lv.getItems().size()-1);
                return null;
            } else {
                if (isCurrency == true){
                    if (table1 == null) {
                        table1 = JsonConverter.convertOneCurrencyTable(stringTable);
                    }else{
                        OneCurrencyTable table2 = JsonConverter.convertOneCurrencyTable(stringTable);
                        for (CurrencyWithDate rate:table2.getRates()) {
                            table1.getRates().add(rate);
                        }
                    }
                }else{
                    if (goldList1 == null) {
                        goldList1 = (List<Gold>) JsonConverter.convertGoldData(stringTable);
                    }else{
                        List<Gold> goldList2 = JsonConverter.convertGoldData(stringTable);
                        for (Gold g : goldList2) {
                            goldList1.add(g);
                        }
                    }
                }
            }
        }
        if (isCurrency == true){
            return table1;
        }else{
            return goldList1;
        }


    }

}
