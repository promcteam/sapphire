package com.gotofinal.darkrise.economy;

import me.travja.darkrise.core.legacy.util.DeserializationWorker;
import me.travja.darkrise.core.legacy.util.SerializationBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class Price implements ConfigurationSerializable {
    private CurrencyType currency;

    private double amount;

    public Price(CurrencyType currency, double amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Price(Map<String, Object> map) {
        DeserializationWorker dw = DeserializationWorker.start(map);
        this.currency = dw.getEnum("currency", CurrencyType.class, CurrencyType.MONEY);
        this.amount = dw.getDouble("amount");
    }

    public CurrencyType getCurrency() {
        return this.currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Map<String, Object> serialize() {
        return SerializationBuilder.start(2).append("currency", this.currency).append("amount", Double.valueOf(this.amount)).build();
    }

    public static Price of(String currency, double amount) {
        return new Price(CurrencyType.valueOf(currency.toUpperCase()), amount);
    }
}
