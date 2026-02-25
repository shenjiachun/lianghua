package com.lianghua.infrastructure.ai.gateway;

import com.lianghua.domain.analysis.gateway.AIModelGateway;
import com.lianghua.domain.analysis.model.valobj.AIModel;
import com.lianghua.infrastructure.ai.client.AliyunQwenClient;
import com.lianghua.infrastructure.ai.client.DoubaoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AIModelGatewayImpl implements AIModelGateway {

    private final AliyunQwenClient qwenClient;
    private final DoubaoClient doubaoClient;

    @Override
    public String analyze(AIModel model, String systemPrompt, String userPrompt) {
        return switch (model) {
            case QWEN, QWEN_TURBO -> qwenClient.chat(model.getModelId(), systemPrompt, userPrompt);
            case DOUBAO, DOUBAO_LITE -> doubaoClient.chat(model.getModelId(), systemPrompt, userPrompt);
        };
    }

    @Override
    public boolean supports(AIModel model) {
        return true;
    }
}
