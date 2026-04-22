package cn.bugstack.domain.trade.model.aggregate;

import cn.bugstack.domain.trade.model.entity.GroupBuyTeamEntity;
import cn.bugstack.domain.trade.model.entity.TradePaySuccessEntity;
import cn.bugstack.domain.trade.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyTeamSettlementAggregate {

    private UserEntity userEntity;

    private GroupBuyTeamEntity groupBuyTeamEntity;

    private TradePaySuccessEntity tradePaySuccessEntity;
}
