package pl.edu.mini.pw.zpoif.projectCurrencies.dataConverter.tables;

import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.Currency;
import java.time.LocalDate;
import java.util.List;

public class CTable extends Table {
    private LocalDate tradingDate;

    public CTable(TableTyp table, String no, LocalDate tradingDate, LocalDate effectiveDate, List<Currency> rates) {
        super(table, no, effectiveDate, rates);
        this.tradingDate = tradingDate;
    }

    public String toString() {
        return "CTable{" +
                "table = " + table +
                ", no = '" + no + '\'' +
                ", tradingDate = " + tradingDate +
                ", effectiveDate = " + effectiveDate +
                ", rates.size = " + rates.size() +
                '}';
    }

    public List<Currency> getRates() {
        return rates;
    }
}
