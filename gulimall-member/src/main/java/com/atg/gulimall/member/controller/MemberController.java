package com.atg.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.atg.common.exception.BizCodeEnume;
import com.atg.common.to.UserRegistTo;
import com.atg.gulimall.member.vo.MemberUserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atg.gulimall.member.entity.MemberEntity;
import com.atg.gulimall.member.service.MemberService;
import com.atg.common.utils.PageUtils;
import com.atg.common.utils.R;



/**
 * 会员
 *
 * @author atg
 * @email 2601259226@qq.com
 * @date 2022-08-12 16:06:49
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/regist")
    public R regist(@RequestBody UserRegistTo userRegistTo){
        BizCodeEnume regist = memberService.regist(userRegistTo);
        if(regist!=null){
            if(regist.getCode()==BizCodeEnume.USER_EXISTED_EXCEPTION.getCode()){
                return R.error(BizCodeEnume.USER_EXISTED_EXCEPTION.getCode(),BizCodeEnume.USER_EXISTED_EXCEPTION.getMsg());
            }
            if(regist.getCode()==BizCodeEnume.PHONE_EXISTED_EXCEPTION.getCode()){
                return R.error(BizCodeEnume.PHONE_EXISTED_EXCEPTION.getCode(),BizCodeEnume.PHONE_EXISTED_EXCEPTION.getMsg());
            }
        }
        return R.ok();
    }

    @PostMapping(value = "/login")
    public R login(@RequestBody MemberUserLoginVo vo) {

        MemberEntity memberEntity = memberService.login(vo);

        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getCode(),BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getMsg());
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
