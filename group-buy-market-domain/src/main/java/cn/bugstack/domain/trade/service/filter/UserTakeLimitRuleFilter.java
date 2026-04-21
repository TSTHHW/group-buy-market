package cn.bugstack.domain.trade.service.filter;

import cn.bugstack.domain.trade.adapter.repository.ITradeRepository;
import cn.bugstack.domain.trade.model.entity.GroupBuyActivityEntity;
import cn.bugstack.domain.trade.model.entity.TradeRuleCommandEntity;
import cn.bugstack.domain.trade.model.entity.TradeRuleFilterBackEntity;
import cn.bugstack.domain.trade.service.factory.TradeRuleFilterFactory;
import cn.bugstack.types.design.framework.link.model2.handler.ILogicHandler;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserTakeLimitRuleFilter implements ILogicHandler<TradeRuleCommandEntity, TradeRuleFilterFactory.DynamicContext, TradeRuleFilterBackEntity> {

    @Resource
    private ITradeRepository repository;

    @Override
    public TradeRuleFilterBackEntity apply(TradeRuleCommandEntity requestParameter, TradeRuleFilterFactory.DynamicContext dynamicContext) {
        log.info("交易规则过滤-用户参与次数限制校验{} activityId:{}", requestParameter.getUserId(), requestParameter.getActivityId());

        GroupBuyActivityEntity groupBuyActivityEntity = dynamicContext.getGroupBuyActivity();
        Integer count = repository.queryOrderCountByActivityId(groupBuyActivityEntity.getActivityId(), requestParameter.getUserId());
        if (null != groupBuyActivityEntity.getTakeLimitCount()&&groupBuyActivityEntity.getTakeLimitCount() <= count) {
            log.info("用户参与次数限制校验，已达参与次数上限 activityId:{}", requestParameter.getActivityId());
            throw new AppException(ResponseCode.E0103);
        }
        return TradeRuleFilterBackEntity.builder()
                .userTakeOrderCount(count)
                .build();
    }

}
