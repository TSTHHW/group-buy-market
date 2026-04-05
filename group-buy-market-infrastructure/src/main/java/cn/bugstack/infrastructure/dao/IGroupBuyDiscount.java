package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.GroupBuyDiscount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IGroupBuyDiscount {
    List<GroupBuyDiscount> queryGroupBuyDiscountList();
}
