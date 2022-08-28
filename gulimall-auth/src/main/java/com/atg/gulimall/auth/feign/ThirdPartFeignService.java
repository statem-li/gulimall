package com.atg.gulimall.auth.feign;

import com.atg.common.to.SmsTo;
import com.atg.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-third-party")
public interface ThirdPartFeignService {

    @PostMapping("/sms/sendCode")
    public R sendCode(@RequestBody SmsTo smsTo);

}
