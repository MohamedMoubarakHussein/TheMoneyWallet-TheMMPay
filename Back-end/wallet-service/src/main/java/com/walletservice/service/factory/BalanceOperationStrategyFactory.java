package com.walletservice.service.factory;

import com.walletservice.service.strategy.BalanceOperationStrategy;
import com.themoneywallet.sharedUtilities.patterns.factory.ServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory for balance operation strategies
 * Follows Factory Pattern
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceOperationStrategyFactory implements ServiceFactory<BalanceOperationStrategy, String> {
    
    private final Map<String, BalanceOperationStrategy> strategyMap;
    
    public BalanceOperationStrategyFactory(List<BalanceOperationStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        BalanceOperationStrategy::getOperationType,
                        Function.identity()
                ));
        
        log.info("Initialized BalanceOperationStrategyFactory with {} strategies: {}", 
                strategyMap.size(), strategyMap.keySet());
    }
    
    @Override
    public BalanceOperationStrategy create(String operationType) {
        BalanceOperationStrategy strategy = strategyMap.get(operationType.toUpperCase());
        
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for operation type: " + operationType);
        }
        
        log.debug("Created strategy for operation type: {}", operationType);
        return strategy;
    }
    
    @Override
    public boolean supports(String operationType) {
        return strategyMap.containsKey(operationType.toUpperCase());
    }
    
    @Override
    public String getFactoryName() {
        return "BalanceOperationStrategyFactory";
    }
}
