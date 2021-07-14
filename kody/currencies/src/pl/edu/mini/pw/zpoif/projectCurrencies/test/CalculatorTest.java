package pl.edu.mini.pw.zpoif.projectCurrencies.test;

import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.Currency;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.CurrencyCode;
import pl.edu.mini.pw.zpoif.projectCurrencies.calculator.Calculator;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.JsonConverter;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.Table;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.TableTyp;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataReader.URLConnectionReader;

import java.io.IOException;
import java.util.List;

public class CalculatorTest {
    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        calculatorTest();
    }

    private static void calculatorTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
        String aTable = URLConnectionReader.TableRequest(TableTyp.C);
        Table table1 = JsonConverter.convertTable(aTable, TableTyp.C);
        List<Currency> rates = table1.getRates();
        Boolean toBuy; // czy chcę kupić? (można wybrać kurs kupna/sprzedaży)
        System.out.println("Kurs kupna:");
        Calculator.calculateCurrency(true, TableTyp.C, rates, CurrencyCode.XDR, CurrencyCode.EUR);

        System.out.println();
        String cTable = URLConnectionReader.TableRequest(TableTyp.C);
        Table table2 = JsonConverter.convertTable(cTable, TableTyp.C);
        List<Currency> rates2 = table2.getRates();
        System.out.println("Kurs sprzedaży:");
        Calculator.calculateCurrency(false, TableTyp.C, rates2, CurrencyCode.XDR, CurrencyCode.EUR);
    }
}
