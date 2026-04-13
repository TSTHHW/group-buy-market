package cn.bugstack.domain.activity.service.discount.impl;

import cn.bugstack.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import cn.bugstack.domain.activity.service.discount.AbstractDiscountCalculateService;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 满减优惠计算
 * @create 2024-12-22 12:12
 */
@Slf4j
@Service("MJ")
public class MJCalculateService extends AbstractDiscountCalculateService {

    @Override
    public BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) {
        log.info("优惠策略折扣计算:{}", groupBuyDiscount.getDiscountType().getCode());

        String marketExpr = groupBuyDiscount.getMarketExpr();
        String[] split = marketExpr.split(Constants.SPLIT);
        BigDecimal marketPrice = new BigDecimal(split[0].trim());
        BigDecimal marketRate = new BigDecimal(split[1].trim());

        if(originalPrice.compareTo(marketPrice) < 0) return originalPrice;

        BigDecimal deductionPrice = originalPrice.subtract(marketRate);

        if(deductionPrice.compareTo(BigDecimal.ZERO) <= 0) return new BigDecimal("0.01");
        return deductionPrice;
    }
}
