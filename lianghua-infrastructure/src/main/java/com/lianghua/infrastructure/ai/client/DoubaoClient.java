package com.lianghua.infrastructure.ai.client;

import com.alibaba.cola.exception.BizException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class DoubaoClient {

    private static final Logger log = LoggerFactory.getLogger(DoubaoClient.class);
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;

    @Value("${ai.bytedance.api-key:}")
    private String apiKey;

    @Value("${ai.bytedance.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    public DoubaoClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public String chat(String modelId, String systemPrompt, String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("ByteDance API key not configured, returning mock response");
            return buildMockResponse(modelId, userPrompt);
        }

        JSONObject requestBody = buildRequestBody(modelId, systemPrompt, userPrompt);
        String url = baseUrl + "/chat/completions";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toJSONString(), JSON_MEDIA_TYPE))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "no body";
                throw new BizException("ByteDance Doubao API call failed: " + response.code() + " - " + errorBody);
            }
            String responseBody = response.body() != null ? response.body().string() : "";
            return parseContent(responseBody);
        } catch (IOException e) {
            throw new BizException("ByteDance Doubao API network error: " + e.getMessage());
        }
    }

    private JSONObject buildRequestBody(String modelId, String systemPrompt, String userPrompt) {
        JSONObject body = new JSONObject();
        body.put("model", modelId);

        List<Map<String, String>> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            messages.add(Map.of("role", "system", "content", systemPrompt));
        }
        messages.add(Map.of("role", "user", "content", userPrompt));
        body.put("messages", messages);
        body.put("temperature", 0.7);
        body.put("max_tokens", 2048);
        return body;
    }

    private String parseContent(String responseBody) {
        try {
            JSONObject json = JSON.parseObject(responseBody);
            JSONArray choices = json.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                if (message != null) {
                    return message.getString("content");
                }
            }
            throw new BizException("Invalid response format from Doubao: " + responseBody);
        } catch (Exception e) {
            if (e instanceof BizException) throw e;
            throw new BizException("Failed to parse Doubao response: " + e.getMessage());
        }
    }

    private String buildMockResponse(String modelId, String userPrompt) {
        return "【模拟分析 - 豆包 " + modelId + "】\n\n" +
                "基于提供的市场数据，豆包AI分析如下：\n\n" +
                "技术面分析：\n" +
                "从技术指标来看，股价在均线系统附近运行，MACD指标显示动能减弱趋势。" +
                "成交量配合价格走势，市场存在分歧。\n\n" +
                "操作建议：\n" +
                "短期建议观望，等待市场方向明朗后再行操作。中长期投资者可适当关注。\n\n" +
                "建议: 持有\n" +
                "置信度: 0.60\n\n" +
                "注：这是模拟响应，请配置真实的API密钥以获取实际AI分析。";
    }
}
