package pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold;

import java.time.LocalDate;

public class Gold {
    private String data; //data publikacji
    private Double cena; //wyliczona w NBP cena 1 g złota (w próbie 1000)

    public Gold(String data, Double cena) {
        this.data = data;
        this.cena = cena;
    }

    public Double getCena() {
        return cena;
    }

    public String getData() {
        return data;
    }
}
