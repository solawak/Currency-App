package pl.edu.mini.pw.zpoif.projectCurrencies.dataReader;

import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.CurrencyCode;
import pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables.TableTyp;

import java.net.*;
import java.io.*;
import java.util.List;

public class URLConnectionReader {


    private static String Request(String url) throws IOException {
        URL nbp = null;
        try {
            nbp = new  URL("http://api.nbp.pl/api/" + url);
        } catch (MalformedURLException e) {
            e.printStackTrace ();
        }
        HttpURLConnection connection   = null;
        try {
            connection = (HttpURLConnection) nbp.openConnection ();
        } catch (IOException e) {
            e.printStackTrace ();
        }

        try {
            if(connection.getResponseCode () !=200){
                System.out.println("Response  code: "+connection.getResponseCode () +" - "+connection.getResponseMessage ());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace ();
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            return inputLine;
        in.close();
        return null;
    }




    /*table type: A B C*/
    public static String TableRequest(TableTyp tableType) throws IOException {
        return Request("exchangerates/tables/" + tableType.toString() + "/");
    }

    public static String LastTableRequest(TableTyp tableType, Integer topCount) throws IOException {
        return Request("exchangerates/tables/" + tableType.toString()  + "/last/" + String.valueOf(topCount));
    }

    public static String TodayTableRequest(TableTyp tableType) throws IOException {
        return Request("exchangerates/tables/" + tableType.toString()  + "/today");
    }

    public static String DateTableRequest(TableTyp tableType, String date) throws IOException {
        return Request("exchangerates/tables/" + tableType.toString()  + "/" + date);
    }

    public static String PeriodTableRequest(TableTyp tableType, String starDate, String endDate) throws IOException {
        return Request("exchangerates/tables/" + tableType.toString()  + "/" + starDate + "/" + endDate);
    }



    /*code : USD, CHF, ...*/

    public static String CurrencyRequest(TableTyp tableType, CurrencyCode code) throws IOException {
        return Request("exchangerates/rates/" + tableType.toString() + "/" + code + "/");
    }

    public static String LastCurrencyRequest(TableTyp tableType, CurrencyCode code, Integer topCount) throws IOException {
        return Request("exchangerates/rates/" + tableType.toString() + "/" + code +  "/last/" + String.valueOf(topCount));

    }

    public static String TodayCurrencyRequest(TableTyp tableType, CurrencyCode code) throws IOException {
        return Request("exchangerates/rates/" + tableType.toString() + "/" + code + "/today");
    }

    public static String DateCurrencyRequest(TableTyp tableType, CurrencyCode code, String date) throws IOException {
        return Request("exchangerates/rates/" + tableType.toString() + "/" + code + "/" + date);
    }

    public static String PeriodCurrencyRequest(TableTyp tableType, CurrencyCode code, String starDate, String endDate) throws IOException {
        return Request("exchangerates/rates/" + tableType.toString() + "/" + code + "/" + starDate + "/" + endDate);
    }






    public static String GoldRequest() throws IOException {
        return Request("cenyzlota/");
    }

    public static String LastGoldRequest(Integer topCount) throws IOException {
        return Request("cenyzlota/" + "last/" + String.valueOf(topCount));

    }

    public static String TodayGoldRequest() throws IOException {
        return Request("cenyzlota/" + "today");
    }

    public static String DateGoldRequest( String date) throws IOException {
        return Request("cenyzlota/" + date);
    }

    public static String PeriodGoldRequest( String starDate, String endDate) throws IOException {
        return Request("cenyzlota/" + starDate + "/" + endDate);
    }

    public List<String> getData() {
        return null;
    }
}
