package com.lianghua.infrastructure.ai;

import com.alibaba.cola.exception.SysException;
import com.lianghua.domain.analysis.gateway.AIAnalysisGateway;
import com.lianghua.domain.analysis.model.vo.AIProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI分析网关路由器（组合模式）
 * 根据AIProvider路由到具体的AI实现
 */
@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class AIAnalysisGatewayRouter implements AIAnalysisGateway {

    private final List<AIAnalysisGateway> gateways;

    @Override
    public String generateAnalysis(AIProvider provider, String prompt) {
        return gateways.stream()
                .filter(g -> !(g instanceof AIAnalysisGatewayRouter))
                .filter(g -> g.supports(provider))
                .findFirst()
                .map(g -> g.generateAnalysis(provider, prompt))
                .orElseThrow(() -> new SysException("不支持的AI提供商: " + provider.getCode()));
    }

    @Override
    public boolean supports(AIProvider provider) {
        return gateways.stream()
                .filter(g -> !(g instanceof AIAnalysisGatewayRouter))
                .anyMatch(g -> g.supports(provider));
    }
}
