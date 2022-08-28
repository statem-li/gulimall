package com.atg.gulimall.member.service;

import com.atg.common.exception.BizCodeEnume;
import com.atg.common.to.UserRegistTo;
import com.atg.gulimall.member.vo.MemberUserLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atg.common.utils.PageUtils;
import com.atg.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author atg
 * @email 2601259226@qq.com
 * @date 2022-08-12 16:06:49
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    BizCodeEnume regist(UserRegistTo userRegistTo);

    MemberEntity login(MemberUserLoginVo vo);
}

