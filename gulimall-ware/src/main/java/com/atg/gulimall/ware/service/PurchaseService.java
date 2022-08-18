package com.atg.gulimall.ware.service;

import com.atg.gulimall.ware.vo.MergeVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atg.common.utils.PageUtils;
import com.atg.gulimall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author atg
 * @email 2601259226@qq.com
 * @date 2022-08-12 15:05:15
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceiveList(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> ids);
}

