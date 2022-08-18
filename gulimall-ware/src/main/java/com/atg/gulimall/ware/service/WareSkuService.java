package com.atg.gulimall.ware.service;

import com.atg.common.to.SkuHasStockTo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atg.common.utils.PageUtils;
import com.atg.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author atg
 * @email 2601259226@qq.com
 * @date 2022-08-12 15:05:15
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockTo> getSkuHasStock(List<Long> skuIds);
}

