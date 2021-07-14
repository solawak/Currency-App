package pl.edu.mini.pw.zpoif.projectCurrencies.test;

import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.CurrencyCode;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.Gold;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.JsonConverter;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.OneCurrencyTable;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.Table;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.TableTyp;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataReader.URLConnectionReader;

import java.io.IOException;
import java.util.List;

public class ReaderAndConverterTest {
    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        readTest();
//        convertTest();
//        converOneCurrencyTableTest();
    }

    private static void converOneCurrencyTableTest() throws IOException {
        System.out.println();
        System.out.println("Konwersja JSON -> java");
        System.out.println("JEDNA WYBRANA WALUTA:");
        String stringTable = URLConnectionReader.PeriodCurrencyRequest(TableTyp.A, CurrencyCode.USD, "2019-11-20", "2019-11-25");
        OneCurrencyTable table1 = JsonConverter.convertOneCurrencyTable(stringTable);
        System.out.println(table1.toString());

        String stringTable2 = URLConnectionReader.CurrencyRequest(TableTyp.A, CurrencyCode.USD);
        OneCurrencyTable table2 = JsonConverter.convertOneCurrencyTable(stringTable2);
        System.out.println(table2.toString());
    }
    private static void convertTest() throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        System.out.println();
        System.out.println("Konwersja JSON -> java");
        System.out.println("WALUTY:");
        String aTable = URLConnectionReader.TableRequest(TableTyp.A);
        Table table1 = JsonConverter.convertTable(aTable, TableTyp.A);
        System.out.println(table1.toString());

        String bTable = URLConnectionReader.TableRequest(TableTyp.B);
        Table table2 = JsonConverter.convertTable(bTable, TableTyp.B);
        System.out.println(table2.toString());

        String cTable = URLConnectionReader.TableRequest(TableTyp.C);
        Table table3 = JsonConverter.convertTable(cTable, TableTyp.C);
        System.out.println(table3.toString());

        System.out.println();
        System.out.println("ZŁOTO:");
        String goldData = URLConnectionReader.PeriodGoldRequest( "2019-11-20", "2019-11-25");
        List<Gold> goldList = JsonConverter.convertGoldData(goldData);
        System.out.println(goldList);
    }

    private static void readTest() throws IOException {
        System.out.println("Dane dotyczące walut:");
        System.out.println(URLConnectionReader.TableRequest(TableTyp.A));
        String a = URLConnectionReader.LastTableRequest(TableTyp.C, 2);
        List<Table> b = JsonConverter.convertPeriodTable(a, TableTyp.C);
        System.out.println(URLConnectionReader.LastTableRequest(TableTyp.C, 6));
        System.out.println(URLConnectionReader.DateTableRequest(TableTyp.C, "2020-10-20"));
        System.out.println(URLConnectionReader.TodayTableRequest(TableTyp.A));
        System.out.println(URLConnectionReader.PeriodTableRequest(TableTyp.A, "2019-11-20", "2019-11-25"));

        System.out.println();
        System.out.println("Dane dotyczące wybranej waluty:");
        System.out.println(URLConnectionReader.CurrencyRequest(TableTyp.A, CurrencyCode.USD));
        System.out.println(URLConnectionReader.LastCurrencyRequest(TableTyp.C, CurrencyCode.USD, 6));
        System.out.println(URLConnectionReader.DateCurrencyRequest(TableTyp.C, CurrencyCode.USD, "2020-10-20"));
        System.out.println(URLConnectionReader.TodayCurrencyRequest(TableTyp.A, CurrencyCode.USD));
        System.out.println(URLConnectionReader.PeriodCurrencyRequest(TableTyp.A, CurrencyCode.USD, "2019-11-20", "2019-11-25"));

        System.out.println();
        System.out.println("Dane dotyczące złota:");
        System.out.println(URLConnectionReader.GoldRequest());
        System.out.println(URLConnectionReader.LastGoldRequest( 6));
        System.out.println(URLConnectionReader.DateGoldRequest( "2020-10-20"));
        System.out.println(URLConnectionReader.TodayGoldRequest());
        System.out.println(URLConnectionReader.PeriodGoldRequest( "2020-01-09", "2021-01-09"));
    }
}
