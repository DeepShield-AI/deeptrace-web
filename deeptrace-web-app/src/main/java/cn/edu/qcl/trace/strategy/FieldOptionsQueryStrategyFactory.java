package cn.edu.qcl.trace.strategy;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Field Options Query Strategy Factory
 * Manages strategy registration and lookup using Strategy Pattern
 * 
 * Eliminates if-else logic by using a Map for strategy dispatch
 */
@Slf4j
@Component
public class FieldOptionsQueryStrategyFactory {

    private final List<FieldOptionsQueryStrategy> strategies;
    private Map<String, FieldOptionsQueryStrategy> strategyMap;

    public FieldOptionsQueryStrategyFactory(List<FieldOptionsQueryStrategy> strategies) {
        this.strategies = strategies;
    }

    @PostConstruct
    public void init() {
        strategyMap = new HashMap<>();
        for (FieldOptionsQueryStrategy strategy : strategies) {
            strategyMap.put(strategy.getStrategyName(), strategy);
            log.info("Registered strategy: {} -> {}", strategy.getStrategyName(), strategy.getClass().getSimpleName());
        }
    }

    /**
     * Get strategy by name
     * 
     * @param strategyName the strategy name (e.g., "query", "sql")
     * @return the corresponding strategy
     * @throws IllegalArgumentException if strategy not found
     */
    public FieldOptionsQueryStrategy getStrategy(String strategyName) {
        FieldOptionsQueryStrategy strategy = strategyMap.get(strategyName);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown query method: " + strategyName + 
                    ". Available methods: " + strategyMap.keySet());
        }
        return strategy;
    }

    /**
     * Check if a strategy exists for the given name
     * 
     * @param strategyName the strategy name
     * @return true if strategy exists
     */
    public boolean hasStrategy(String strategyName) {
        return strategyMap.containsKey(strategyName);
    }

    /**
     * Get default strategy (used when method is null or empty)
     * 
     * @return the default strategy (query method)
     */
    public FieldOptionsQueryStrategy getDefaultStrategy() {
        return getStrategy("query");
    }
}