package com.atg.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atg.common.utils.PageUtils;
import com.atg.gulimall.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author atg
 * @email 2601259226@qq.com
 * @date 2022-08-12 16:06:49
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

