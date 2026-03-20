package com.lianghua.domain.market.model.vo;

import com.alibaba.cola.exception.Assert;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 股票代码值对象
 * 格式示例：000001.SZ、600036.SH、BABA.US
 */
@Getter
@EqualsAndHashCode
public class StockSymbol {

    private final String code;
    private final String market;
    private final String fullSymbol;

    private StockSymbol(String code, String market) {
        this.code = code;
        this.market = market;
        this.fullSymbol = code + "." + market;
    }

    public static StockSymbol of(String fullSymbol) {
        Assert.notNull(fullSymbol, "股票代码不能为空");
        Assert.isTrue(!fullSymbol.isBlank(), "股票代码不能为空");
        String[] parts = fullSymbol.split("\\.");
        Assert.isTrue(parts.length == 2, "股票代码格式错误，应为 CODE.MARKET 格式");
        return new StockSymbol(parts[0], parts[1]);
    }

    public boolean isShanghai() {
        return "SH".equalsIgnoreCase(market);
    }

    public boolean isShenzhen() {
        return "SZ".equalsIgnoreCase(market);
    }

    public boolean isUS() {
        return "US".equalsIgnoreCase(market);
    }

    @Override
    public String toString() {
        return fullSymbol;
    }
}
