package pl.edu.mini.pw.zpoif.projectCurrencies.calculator;

import javafx.scene.text.Text;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.Currency;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.CurrencyCode;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.TableTyp;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Calculator {
    private static Double price;
    public static String calculateCurrency(Boolean toBuy, TableTyp typ, List<Currency> rates, CurrencyCode c1, CurrencyCode c2) {
        //Kursy walut podane są w PLN
        // c1 -> c2 (Mamy c1, chcemy obliczyć ile to będzie c2)

        HashMap<CurrencyCode, Double> map = convertRatesToMap(toBuy, typ, rates);
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
//        System.out.println(result);
        return result;
    }
    public static Double getPrice() {
        return price;
    }

    private static String printCalculatorInfo(CurrencyCode c1, CurrencyCode c2, Double price1, Double price2, Double price) {
        String s1 = "1 " + c1.toString() + " = " + price1 + " PLN";
        String s2 = "1 " + c2.toString() + " = " + price2 + " PLN";
        String s3 = "1 " + c1.toString() + " = " + price + " " + c2;
        return (s1 + "\n" + s2 + "\n" + s3);
    }

    public static HashMap<CurrencyCode,Double> convertRatesToMap(Boolean toBuy, TableTyp typ, List<Currency> rates) {
        HashMap<CurrencyCode, Double> map;
        if (typ == TableTyp.A || typ == TableTyp.B) {
            map = (HashMap<CurrencyCode, Double>) rates.stream()
                    .collect(Collectors.toMap(Currency::getCode, Currency::getMid));
        }
        else {
            if (toBuy) {
                map = (HashMap<CurrencyCode, Double>) rates.stream()
                        .collect(Collectors.toMap(Currency::getCode, Currency::getBid)); //bid - kurs kupna
            }
            else {
                map = (HashMap<CurrencyCode, Double>) rates.stream()
                        .collect(Collectors.toMap(Currency::getCode, Currency::getAsk)); //bid - kurs kupna
            }

        }
        return map;
    }

}
