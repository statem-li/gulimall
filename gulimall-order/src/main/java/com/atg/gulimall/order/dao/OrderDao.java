package com.atg.gulimall.order.dao;

import com.atg.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author atg
 * @email 2601259226@qq.com
 * @date 2022-08-12 15:07:59
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
