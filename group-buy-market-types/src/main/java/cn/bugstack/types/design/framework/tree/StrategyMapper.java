package cn.bugstack.types.design.framework.tree;
/**
 * 策略映射
 */
public interface StrategyMapper<T,D,R> {
    StrategyHandler<T,D,R> getStrategHandler(T requestParam, D dynamicContext) throws Exception;
}
