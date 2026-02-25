package com.lianghua.domain.market.model.valobj;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Price {
    BigDecimal value;
    String currency;

    public static Price of(BigDecimal value) {
        return new Price(value, "CNY");
    }

    public Price add(Price other) {
        return new Price(this.value.add(other.value), this.currency);
    }

    public Price subtract(Price other) {
        return new Price(this.value.subtract(other.value), this.currency);
    }

    @Override
    public String toString() {
        return value + " " + currency;
    }
}
