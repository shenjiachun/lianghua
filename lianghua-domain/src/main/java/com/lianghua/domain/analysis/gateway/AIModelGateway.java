package com.lianghua.domain.analysis.gateway;

import com.lianghua.domain.analysis.model.valobj.AIModel;

public interface AIModelGateway {

    String analyze(AIModel model, String systemPrompt, String userPrompt);

    boolean supports(AIModel model);
}
