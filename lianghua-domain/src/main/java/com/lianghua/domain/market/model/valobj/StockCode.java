package com.lianghua.domain.market.model.valobj;

import lombok.Value;

@Value
public class StockCode {
    String code;
    String market; // SH, SZ, BJ

    public static StockCode of(String fullCode) {
        if (fullCode == null || !fullCode.contains(".")) {
            throw new IllegalArgumentException("Invalid stock code format: " + fullCode);
        }
        String[] parts = fullCode.split("\\.");
        return new StockCode(parts[0], parts[1]);
    }

    public String getFullCode() {
        return code + "." + market;
    }
}
