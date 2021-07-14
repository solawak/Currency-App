package pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables;

import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.Currency;

import java.time.LocalDate;
import java.util.List;

public class ABTable extends Table{

    public ABTable(TableTyp table, String no, LocalDate effectiveDate, List<Currency> rates) {
        super(table, no, effectiveDate, rates);
    }

    public String toString() {
        return "ABTable{" +
                "table = " + table +
                ", no = '" + no + '\'' +
                ", effectiveDate = " + effectiveDate +
                ", rates.size = " + rates.size() +
                '}';
    }
}
