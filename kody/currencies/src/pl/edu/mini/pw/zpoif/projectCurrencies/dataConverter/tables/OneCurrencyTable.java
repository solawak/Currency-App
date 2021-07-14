package pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables;

import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.Currency;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.CurrencyCode;
import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.CurrencyWithDate;

import java.time.LocalDate;
import java.util.List;

public class OneCurrencyTable {
    private TableTyp table;
    private String currency; //numer
    private CurrencyCode code;
    private List<CurrencyWithDate> rates;

    public OneCurrencyTable(TableTyp table, String currency, CurrencyCode code, List<CurrencyWithDate> rates) {
        this.table = table;
        this.currency = currency;
        this.code = code;
        this.rates = rates;
    }

    @Override
    public String toString() {
        return "OneCurrencyTable{" +
                "table=" + table +
                ", currency='" + currency + '\'' +
                ", code=" + code +
                ", rates=" + rates.size() +
                '}';
    }

    public List<CurrencyWithDate> getRates() {
        return rates;
    }


}
