package com.lianghua.infrastructure.ai.bytedance;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lianghua.domain.analysis.gateway.AIAnalysisGateway;
import com.lianghua.domain.analysis.model.vo.AIProvider;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 字节跳动豆包AI网关实现
 * 文档：https://www.volcengine.com/docs/82379/1263482
 */
@Slf4j
@Component
public class ByteDanceAIGatewayImpl implements AIAnalysisGateway {

    private static final String API_URL = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    @Value("${lianghua.ai.bytedance.api-key:}")
    private String apiKey;

    @Value("${lianghua.ai.bytedance.model:doubao-pro-4k}")
    private String model;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public String generateAnalysis(AIProvider provider, String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("字节跳动API Key未配置，返回模拟数据");
            return buildMockResponse(prompt);
        }
        try {
            JSONObject requestBody = buildRequestBody(prompt);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toJSONString(), JSON_TYPE))
                    .build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("字节跳动豆包API调用失败，状态码：{}", response.code());
                    return buildMockResponse(prompt);
                }
                String responseBody = response.body() != null ? response.body().string() : "";
                return parseResponse(responseBody);
            }
        } catch (IOException e) {
            log.error("调用字节跳动豆包API异常", e);
            return buildMockResponse(prompt);
        }
    }

    @Override
    public boolean supports(AIProvider provider) {
        return AIProvider.BYTEDANCE == provider;
    }

    private JSONObject buildRequestBody(String prompt) {
        JSONObject body = new JSONObject();
        body.put("model", model);
        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一名专业的A股量化分析师，擅长技术分析和基本面分析。请用中文回答，并严格按照JSON格式输出分析结果。");
        messages.add(systemMessage);
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        body.put("messages", messages);
        body.put("temperature", 0.7);
        body.put("max_tokens", 2000);
        return body;
    }

    private String parseResponse(String responseBody) {
        try {
            JSONObject response = JSON.parseObject(responseBody);
            JSONArray choices = response.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                if (message != null) {
                    return message.getString("content");
                }
            }
        } catch (Exception e) {
            log.error("解析字节跳动豆包API响应失败", e);
        }
        return responseBody;
    }

    private String buildMockResponse(String prompt) {
        return "{\n"
                + "  \"overallScore\": 72,\n"
                + "  \"recommendation\": \"BUY\",\n"
                + "  \"targetPrice\": 12.00,\n"
                + "  \"technicalSummary\": \"[字节跳动豆包模拟分析] 股票技术形态良好，成交量配合，突破前期高点，建议关注买入机会\",\n"
                + "  \"fundamentalSummary\": \"公司盈利能力较强，行业景气度高，具备一定投资价值\",\n"
                + "  \"riskWarnings\": [\"大盘系统性风险\", \"个股流动性风险\", \"请设置合理止损\"],\n"
                + "  \"fullAnalysis\": \"[字节跳动豆包] 这是模拟分析内容，实际使用需配置有效的API Key。\"\n"
                + "}";
    }
}
