package com.atg.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atg.common.utils.PageUtils;
import com.atg.gulimall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author atg
 * @email 2601259226@qq.com
 * @date 2022-08-12 15:05:15
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

