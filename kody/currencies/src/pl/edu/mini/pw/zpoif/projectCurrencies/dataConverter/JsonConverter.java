package pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.Currency;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.CurrencyCode;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.CurrencyWithDate;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.Gold;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JsonConverter {

    public static Table convertTable(String stringTable, TableTyp typ) {
        JSONArray jsonArray = parseArrayToJson(stringTable);
        JSONObject jsonObj = (JSONObject) jsonArray.get(0);

        Gson gson = new Gson();

        TableTyp tableTyp = gson.fromJson(String.valueOf(jsonObj.get("table")), TableTyp.class);
        String no = (String) jsonObj.get("no");

        JSONArray jsonArrayCurrencies = (JSONArray) jsonObj.get("rates");
        List<Currency> rates = readJSONArrayToListOfCurrencies(jsonArrayCurrencies);

        Table table;

        LocalDate effectiveDate = readDateFromString((String) jsonObj.get("effectiveDate"));
        LocalDate tradingDate;
        if (typ.equals(TableTyp.C)) {
            tradingDate = readDateFromString((String) jsonObj.get("tradingDate"));
            table = new CTable(tableTyp, no, tradingDate, effectiveDate, rates);
        }
        else { // A or B
            table = new ABTable(tableTyp, no, effectiveDate, rates);
        }

        return table;
    }

    /*kopia metody wyżej dostosowana do przedziałów czasowych*/
    public static List<Table> convertPeriodTable(String stringTable, TableTyp typ) {
        JSONArray jsonArray = parseArrayToJson(stringTable);
//        JSONObject jsonObj = (JSONObject) jsonArray.get(0);
        List<Table> tableList = new ArrayList<>();
        for (Object jsonObject : jsonArray){
            JSONObject jsonObj = (JSONObject) jsonObject;
            Gson gson = new Gson();

            TableTyp tableTyp = gson.fromJson(String.valueOf(jsonObj.get("table")), TableTyp.class);
            String no = (String) jsonObj.get("no");

            JSONArray jsonArrayCurrencies = (JSONArray) jsonObj.get("rates");
            List<Currency> rates = readJSONArrayToListOfCurrencies(jsonArrayCurrencies);

            Table table;

            LocalDate effectiveDate = readDateFromString((String) jsonObj.get("effectiveDate"));
            LocalDate tradingDate;
            if (typ.equals(TableTyp.C)) {
                tradingDate = readDateFromString((String) jsonObj.get("tradingDate"));
                table = new CTable(tableTyp, no, tradingDate, effectiveDate, rates);
            }
            else { // A or B
                table = new ABTable(tableTyp, no, effectiveDate, rates);
            }
            tableList.add(table);



        }
        return tableList;

    }

    // dla tabeli jednej wybranej waluty:
    public static OneCurrencyTable convertOneCurrencyTable(String stringTable) {
        JSONObject jsonObj = parseStringToJsonObject(stringTable);

        Gson gson = new Gson();

        TableTyp tableTyp = gson.fromJson(String.valueOf(jsonObj.get("table")), TableTyp.class);
        String currency = (String) jsonObj.get("currency");
        CurrencyCode code = gson.fromJson(String.valueOf(jsonObj.get("code")), CurrencyCode.class);

        JSONArray jsonArrayData = (JSONArray) jsonObj.get("rates");
        List<CurrencyWithDate> rates = readJSONArrayToListOfCurrencyWithDate(jsonArrayData);
        // A or B - "no":  , "effectiveDate":  ,"mid":  ;
        // C - "no":  , "effectiveDate":  ,"bid":  ,"ask":  ;
        // effectiveDate - String

        OneCurrencyTable table = new OneCurrencyTable(tableTyp,currency,code,rates);
        return table;
    }



    public static List<Gold> convertGoldData(String data) {
        JSONArray jsonArray = parseArrayToJson(data);
        List<Gold> goldList = readJSONArrayToListOfGold(jsonArray);
        return goldList;
    }

    private static JSONArray parseArrayToJson(String stringArray) {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) parser.parse(stringArray);
        } catch (org.json.simple.parser.ParseException e) {
            System.out.println("Problem with parsing String to JSONArray!");
        }
        return jsonArray;
    }
    private static JSONObject parseStringToJsonObject(String s) {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(s);
        } catch (ParseException e) {
            System.out.println("Problem with parsing String to JSONObject!");
        }
        return json;
    }

    private static LocalDate readDateFromString(String s) {
        LocalDate date = LocalDate.parse(s);
        return date;
    }

    private static List<Currency> readJSONArrayToListOfCurrencies(JSONArray jsonArrayCurrencies) {
        Gson gson = new Gson();
        List<Currency> rates = new ArrayList<>();
        Currency tmpCurrency;
        for (int i = 0; i < jsonArrayCurrencies.size(); i++) {
            tmpCurrency = gson.fromJson(String.valueOf(jsonArrayCurrencies.get(i)), Currency.class);
            rates.add(tmpCurrency);
        }
        return rates;
    }
    private static List<CurrencyWithDate> readJSONArrayToListOfCurrencyWithDate(JSONArray jsonArrayCurrenciesWithDate) {
        Gson gson = new Gson();
        List<CurrencyWithDate> rates = new ArrayList<>();
        CurrencyWithDate tmpCurrency;
        for (int i = 0; i < jsonArrayCurrenciesWithDate.size(); i++) {
            tmpCurrency = gson.fromJson(String.valueOf(jsonArrayCurrenciesWithDate.get(i)), CurrencyWithDate.class);
            rates.add(tmpCurrency);
        }
        return rates;
    }

    private static List<Gold> readJSONArrayToListOfGold(JSONArray jsonArrayGold) {
        Gson gson = new Gson();
        List<Gold> rates = new ArrayList<>();
        Gold tmpCurrency;
        for (int i = 0; i < jsonArrayGold.size(); i++) {
            tmpCurrency = gson.fromJson(String.valueOf(jsonArrayGold.get(i)), (Type) Gold.class);
            rates.add(tmpCurrency);
        }
        return rates;
    }

}
