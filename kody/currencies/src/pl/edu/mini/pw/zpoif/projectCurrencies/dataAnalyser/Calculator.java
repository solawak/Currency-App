package pl.edu.mini.pw.zpoif.projectCurrencies.dataAnalyser;

import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.Currency;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.CurrencyCode;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.TableTyp;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Calculator {
    public static Double calculateCurrency(TableTyp typ, List<Currency> rates, CurrencyCode c1, CurrencyCode c2) {
        //Kursy walut podane są w PLN
        // c1 -> c2 (Mamy c1, chcemy obliczyć ile to będzie c2)

        HashMap<CurrencyCode, Double> map = convertRatesToMap(typ, rates);
        Double price1 = map.get(c1);
        Double price2 = map.get(c2);

        if (price1 == null) {
            System.out.println("Nie ma danych dt. waluty " + c1 + "!");
            return null;
        }
        else if (price2 == null) {
            System.out.println("Nie ma danych dt. waluty " + c1 + "!");
            return null;
        }
        Double price = price1/price2;
        printCalculatorInfo(c1,c2,price1,price2,price);
        return price;
    }

    private static void printCalculatorInfo(CurrencyCode c1, CurrencyCode c2, Double price1, Double price2, Double price) {
        System.out.println("1 " + c1.toString() + " = " + price1 + " PLN");
        System.out.println("1 " + c2.toString() + " = " + price2 + " PLN");
        System.out.println("1 " + c1.toString() + " = " + price + " " + c2);
    }

    private static HashMap<CurrencyCode,Double> convertRatesToMap(TableTyp typ, List<Currency> rates) {
        HashMap<CurrencyCode, Double> map;
        if (typ == TableTyp.A || typ == TableTyp.B) {
            map = (HashMap<CurrencyCode, Double>) rates.stream()
                    .collect(Collectors.toMap(Currency::getCode, Currency::getMid));
        }
        else {
            map = (HashMap<CurrencyCode, Double>) rates.stream()
                    .collect(Collectors.toMap(Currency::getCode, Currency::getBid)); //bid - kurs kupna
        }
        return map;
    }

}
