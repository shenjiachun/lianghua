package com.lianghua.infrastructure.ai.aliyun;

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
 * 阿里云通义千问AI网关实现
 * 文档：https://help.aliyun.com/zh/dashscope/developer-reference/api-details
 */
@Slf4j
@Component
public class AliyunAIGatewayImpl implements AIAnalysisGateway {

    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    @Value("${lianghua.ai.aliyun.api-key:}")
    private String apiKey;

    @Value("${lianghua.ai.aliyun.model:qwen-turbo}")
    private String model;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public String generateAnalysis(AIProvider provider, String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("阿里云API Key未配置，返回模拟数据");
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
                    log.error("阿里云API调用失败，状态码：{}", response.code());
                    return buildMockResponse(prompt);
                }
                String responseBody = response.body() != null ? response.body().string() : "";
                return parseResponse(responseBody);
            }
        } catch (IOException e) {
            log.error("调用阿里云通义千问API异常", e);
            return buildMockResponse(prompt);
        }
    }

    @Override
    public boolean supports(AIProvider provider) {
        return AIProvider.ALIYUN == provider;
    }

    private JSONObject buildRequestBody(String prompt) {
        JSONObject body = new JSONObject();
        body.put("model", model);
        JSONObject input = new JSONObject();
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        input.put("messages", messages);
        body.put("input", input);
        JSONObject parameters = new JSONObject();
        parameters.put("result_format", "message");
        parameters.put("temperature", 0.7);
        parameters.put("max_tokens", 2000);
        body.put("parameters", parameters);
        return body;
    }

    private String parseResponse(String responseBody) {
        try {
            JSONObject response = JSON.parseObject(responseBody);
            JSONObject output = response.getJSONObject("output");
            if (output != null) {
                JSONArray choices = output.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject messageObj = choice.getJSONObject("message");
                    if (messageObj != null) {
                        return messageObj.getString("content");
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析阿里云API响应失败", e);
        }
        return responseBody;
    }

    private String buildMockResponse(String prompt) {
        return "{\n"
                + "  \"overallScore\": 65,\n"
                + "  \"recommendation\": \"HOLD\",\n"
                + "  \"targetPrice\": 11.50,\n"
                + "  \"technicalSummary\": \"[阿里云通义千问模拟分析] 股票当前处于盘整阶段，均线系统多头排列，MACD金叉，短期看好\",\n"
                + "  \"fundamentalSummary\": \"公司基本面稳健，市盈率处于合理区间，值得关注\",\n"
                + "  \"riskWarnings\": [\"市场整体风险需关注\", \"注意止损位设置\"],\n"
                + "  \"fullAnalysis\": \"[阿里云通义千问] 这是模拟分析内容，实际使用需配置有效的API Key。\"\n"
                + "}";
    }
}
