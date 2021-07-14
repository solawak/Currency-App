package pl.edu.mini.pw.zpoif.projectCurrencies.currencyAndGold;

public enum CurrencyCode {
    THB("Thai Baht"),
    USD("US dollar"),
    AUD("Australian Dollar"),
    HKD("Hong Kong Dollar"),
    CAD("Canadian Dollar"),
    NAD("New Zealand Dollar"),
    SGD("Singapore Dollar"),
    EUR("Euro"),
    HUF("Hungarian Forint"),
    CHF("Swiss Franc"),
    GBP("British Pound"),
    UAH("hUkrainian Hryvnia"),
    JPY("Japanese Yen"),
    CZK("Czech Koruna"),
    DKK("Danish Krone"),
    ISK("Icelandic Krona"),
    NOK("Norwegian Krone"),
    SEK("Swedish Krona"),
    HRK("Croatian Kuna"),
    RON("Romanian Leu"),
    BGN("Bulgarian Lev"),
    TRY("Turkish Lira"),
    ILS("Israeli Shekel"),
    CLP("Chilean Peso"),
    PHP("Philippine Peso"),
    MXN("Mexican Peso"),
    ZAR("South African Rand"),
    BRL("Brazilian Real"),
    MYR("Malaysian Ringgit"),
    RUB("Russian Ruble"),
    IDR("Indonesian Rupiah"),
    INR("Indian Rupee"),
    KRW("South Korean Won"),
    CNY("Chinese Yuan Renminbi"),
    XDR("SDR (IMF)"),
    PLN("Polish Złoty");
    public final String label;

    private CurrencyCode(String label) {
        this.label = label;
    }

    public static CurrencyCode valueOfLabel(String label) {
        for (CurrencyCode e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
