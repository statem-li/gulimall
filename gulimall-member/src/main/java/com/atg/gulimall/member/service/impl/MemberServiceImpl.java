package com.atg.gulimall.member.service.impl;

import com.atg.common.exception.BizCodeEnume;
import com.atg.common.to.UserRegistTo;
import com.atg.common.utils.PageUtils;
import com.atg.common.utils.Query;
import com.atg.gulimall.member.dao.MemberDao;
import com.atg.gulimall.member.dao.MemberLevelDao;
import com.atg.gulimall.member.entity.MemberEntity;
import com.atg.gulimall.member.entity.MemberLevelEntity;
import com.atg.gulimall.member.service.MemberService;
import com.atg.gulimall.member.vo.MemberUserLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public BizCodeEnume regist(UserRegistTo userRegistTo) {
        MemberEntity member = new MemberEntity();
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        member.setLevelId(levelEntity.getId());
        member.setMobile(userRegistTo.getPhone());
        member.setUsername(userRegistTo.getUserName());
        long mobileCount = count(new QueryWrapper<MemberEntity>().eq("mobile", userRegistTo.getPhone()));
        if (mobileCount < 0) {
            return BizCodeEnume.PHONE_EXISTED_EXCEPTION;
        }
        long usernameCount = count(new QueryWrapper<MemberEntity>().eq("username", userRegistTo.getUserName()));
        if (usernameCount < 0) {
            return BizCodeEnume.USER_EXISTED_EXCEPTION;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passwordEncode = passwordEncoder.encode(userRegistTo.getPassword());
        member.setPassword(passwordEncode);
        baseMapper.insert(member);
        return null;
    }

    @Override
    public MemberEntity login(MemberUserLoginVo vo) {

        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();

        //1、去数据库查询 SELECT * FROM ums_member WHERE username = ? OR mobile = ?
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginacct).or().eq("mobile", loginacct));

        if (memberEntity == null) {
            //登录失败
            return null;
        } else {
            //获取到数据库里的password
            String password1 = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //进行密码匹配
            boolean matches = passwordEncoder.matches(password, password1);
            if (matches) {
                //登录成功
                return memberEntity;
            }
        }

        return null;
    }
}
