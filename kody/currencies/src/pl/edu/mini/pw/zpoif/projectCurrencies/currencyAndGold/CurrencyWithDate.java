package pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold;

import java.time.LocalDate;

public class CurrencyWithDate {
    // nazwa oraz kod waluty przechowywane są w tabeli OneCurrencyTable;
    // klasa potrzebna do danych dt. jednej waluty

    private Double mid; //przeliczony kurs średni waluty
    private Double bid; //przeliczony kurs kupna waluty
    private Double ask; //przeliczony kurs sprzedaży waluty
    private String no; //numer
    private String effectiveDate;
//    private String stringEffectiveDate;

    //    public CurrencyWithDate(String no, LocalDate effectiveDate, Double mid) {
//        this.no = no;
//        this.effectiveDate = effectiveDate;
//        this.mid = mid;
//    }
    public CurrencyWithDate(String no, String EffectiveDate, Double mid) {
        this.no = no;
        this.effectiveDate = EffectiveDate;
        this.mid = mid;
    }
//    public CurrencyWithDate(String no, LocalDate effectiveDate, Double bid, Double ask) {
//        this.no = no;
//        this.effectiveDate = effectiveDate;
//        this.bid = bid;
//        this.ask = ask;
//    }


    public Double getMid() {
        return mid;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }
}
