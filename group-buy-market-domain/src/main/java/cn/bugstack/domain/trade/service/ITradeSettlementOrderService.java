package cn.bugstack.domain.trade.service;

import cn.bugstack.domain.trade.model.entity.TradePaySettlementEntity;
import cn.bugstack.domain.trade.model.entity.TradePaySuccessEntity;

public interface ITradeSettlementOrderService {

    TradePaySettlementEntity settlementMarketPayOrder(TradePaySuccessEntity tradePaySuccessEntity);

}
