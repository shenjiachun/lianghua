package com.lianghua.domain;

import com.lianghua.domain.analysis.model.vo.AIProvider;
import com.lianghua.domain.analysis.model.vo.Recommendation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 领域值对象单元测试
 */
class DomainValueObjectTest {

    @Test
    void testAIProviderFromCode() {
        assertEquals(AIProvider.ALIYUN, AIProvider.fromCode("ALIYUN"));
        assertEquals(AIProvider.BYTEDANCE, AIProvider.fromCode("BYTEDANCE"));
        assertEquals(AIProvider.ALIYUN, AIProvider.fromCode("aliyun"));
    }

    @Test
    void testAIProviderInvalidCode() {
        assertThrows(IllegalArgumentException.class, () -> AIProvider.fromCode("UNKNOWN"));
    }

    @Test
    void testRecommendationFromScore() {
        assertEquals(Recommendation.BUY, Recommendation.fromScore(80));
        assertEquals(Recommendation.BUY, Recommendation.fromScore(70));
        assertEquals(Recommendation.HOLD, Recommendation.fromScore(69));
        assertEquals(Recommendation.HOLD, Recommendation.fromScore(40));
        assertEquals(Recommendation.SELL, Recommendation.fromScore(39));
        assertEquals(Recommendation.SELL, Recommendation.fromScore(0));
    }

    @Test
    void testAIProviderAttributes() {
        assertEquals("ALIYUN", AIProvider.ALIYUN.getCode());
        assertEquals("阿里云通义千问", AIProvider.ALIYUN.getName());
        assertEquals("qwen-turbo", AIProvider.ALIYUN.getDefaultModel());

        assertEquals("BYTEDANCE", AIProvider.BYTEDANCE.getCode());
        assertEquals("字节跳动豆包", AIProvider.BYTEDANCE.getName());
        assertEquals("doubao-pro-4k", AIProvider.BYTEDANCE.getDefaultModel());
    }

    @Test
    void testRecommendationAttributes() {
        assertEquals("BUY", Recommendation.BUY.getCode());
        assertEquals("买入", Recommendation.BUY.getDesc());
        assertEquals("SELL", Recommendation.SELL.getCode());
        assertEquals("卖出", Recommendation.SELL.getDesc());
    }
}
