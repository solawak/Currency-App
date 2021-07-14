package pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables;

import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.Currency;

import java.time.LocalDate;
import java.util.List;

public abstract class Table {
    protected TableTyp table;
    protected String no; //numer
    protected LocalDate effectiveDate;
    protected List<Currency> rates;

    public Table(TableTyp table, String no, LocalDate effectiveDate, List<Currency> rates) {
        this.table = table;
        this.no = no;
        this.effectiveDate = effectiveDate;
        this.rates = rates;
    }

    public List<Currency> getRates() {
        return rates;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public abstract String toString();
}
