package pl.edu.mini.pw.zpoif.projectCurrencies.app;

import pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold.CurrencyCode;

import java.util.ArrayList;
import java.util.List;

public class CurrencyRatio { // wskaźniki
    private CurrencyCode code;
    private Double bidYesterday;
    private Double bidToday;
    private Double askYesterday;
    private Double askToday;

    public CurrencyRatio(CurrencyCode code, Double bidYesterday, Double bidToday, Double askYesterday, Double askToday) {
        this.code = code;
        this.bidYesterday = bidYesterday;
        this.bidToday = bidToday;
        this.askYesterday = askYesterday;
        this.askToday = askToday;
    }


    Double getMid() {
        return 100*(bidToday+askToday)/(bidYesterday+askToday) - 100;
    }
    Double getBid() {
        return 100*bidToday/bidYesterday - 100;
    }
    Double getAsk() {
        return 100*askToday/askYesterday - 100;
    }

    public CurrencyCode getCode() {
        return code;
    }
}
