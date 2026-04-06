package cn.bugstack.types.design.framework.tree;
/**
 * 策略处理器
 */
public interface StrategyHandler<T,D,R> {

    StrategyHandler DEFAULT = (T, D) -> null;

    R apply(T requestParam,D dynamicContext) throws Exception;
}
