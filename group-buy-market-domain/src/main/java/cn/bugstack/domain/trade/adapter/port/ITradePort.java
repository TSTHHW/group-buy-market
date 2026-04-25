package cn.bugstack.domain.trade.adapter.port;

import cn.bugstack.domain.trade.model.entity.NotifyTaskEntity;

//交易服务接口
public interface ITradePort {

    String groupBuyNotify(NotifyTaskEntity notifyTask) throws Exception;

}
