package com.atg.gulimall.auth.feign;

import com.atg.common.to.UserRegistTo;
import com.atg.common.utils.R;
import com.atg.gulimall.auth.vo.UserLoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistTo userRegistTo);

    @PostMapping(value = "/member/member/login")
    R login(@RequestBody UserLoginVo vo);

//    @PostMapping(value = "/member/member/oauth2/login")
//    R oauthLogin(@RequestBody SocialUser socialUser) throws Exception;

    @PostMapping(value = "/member/member/weixin/login")
    R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo);
}