package pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold;

public class Currency {
    private String currency; //nazwa waluty
    private CurrencyCode code; //kod waluty
    private Double mid; //przeliczony kurs średni waluty
    private Double bid; //przeliczony kurs kupna waluty
    private Double ask; //przeliczony kurs sprzedaży waluty

    // table A or B:
    public Currency(String currency, CurrencyCode code, Double mid) {
        this.currency = currency;
        this.code = code;
        this.mid = mid;
    }
    // table C:
    public Currency(String currency, CurrencyCode code, Double bid, Double ask) {
        this.currency = currency;
        this.code = code;
        this.bid = bid;
        this.ask = ask;
    }

    public CurrencyCode getCode() {
        return code;
    }

    public Double getMid() {
        return mid;
    }

    public Double getBid() {
        return bid;
    }

    public Double getAsk() {
        return ask;
    }

}
