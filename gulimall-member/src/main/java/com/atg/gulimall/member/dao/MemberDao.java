package com.atg.gulimall.member.dao;

import com.atg.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author atg
 * @email 2601259226@qq.com
 * @date 2022-08-12 16:06:49
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
