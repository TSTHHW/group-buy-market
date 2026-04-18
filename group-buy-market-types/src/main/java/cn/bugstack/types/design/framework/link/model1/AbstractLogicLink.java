package cn.bugstack.types.design.framework.link.model1;

public abstract class AbstractLogicLink<T, D, R> implements ILogicLink<T, D, R>{

    private ILogicLink<T, D, R> next;

    //获取下一个节点
    @Override
    public ILogicLink<T, D, R> next() {
        return next;
    }

    //将当前节点的next指针指向下一个节点，并返回下一个节点
    @Override
    public ILogicLink<T, D, R> appendNext(ILogicLink<T, D, R> next) {
        this.next = next;
        return next;
    }

    //将请求参数和动态上下文传递给下一个节点
    protected R next(T requestParameter, D dynamicContext) throws Exception {
        return next.apply(requestParameter, dynamicContext);
    }

}
