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
public class AliyunQwenClient {

    private static final Logger log = LoggerFactory.getLogger(AliyunQwenClient.class);
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;

    @Value("${ai.aliyun.api-key:}")
    private String apiKey;

    @Value("${ai.aliyun.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    public AliyunQwenClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public String chat(String modelId, String systemPrompt, String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Aliyun API key not configured, returning mock response");
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
                throw new BizException("Aliyun Qwen API call failed: " + response.code() + " - " + errorBody);
            }
            String responseBody = response.body() != null ? response.body().string() : "";
            return parseContent(responseBody);
        } catch (IOException e) {
            throw new BizException("Aliyun Qwen API network error: " + e.getMessage());
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
            throw new BizException("Invalid response format from Aliyun Qwen: " + responseBody);
        } catch (Exception e) {
            if (e instanceof BizException) throw e;
            throw new BizException("Failed to parse Aliyun Qwen response: " + e.getMessage());
        }
    }

    private String buildMockResponse(String modelId, String userPrompt) {
        return "【模拟分析 - " + modelId + "】\n\n" +
                "基于提供的市场数据，本次分析如下：\n\n" +
                "市场走势分析：\n" +
                "当前市场处于震荡整理阶段，短期方向不明朗。技术指标显示价格在关键支撑位附近运行。\n\n" +
                "风险提示：\n" +
                "市场存在一定的不确定性，建议投资者谨慎操作，做好仓位管理。\n\n" +
                "建议: 持有\n" +
                "置信度: 0.65\n\n" +
                "注：这是模拟响应，请配置真实的API密钥以获取实际AI分析。";
    }
}
