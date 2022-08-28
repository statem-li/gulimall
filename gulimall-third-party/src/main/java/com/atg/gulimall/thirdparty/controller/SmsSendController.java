package com.atg.gulimall.thirdparty.controller;

import com.atg.common.to.SmsTo;
import com.atg.common.utils.R;
import com.atg.gulimall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    SmsComponent smsComponent;

    @PostMapping("/sendCode")
    public R sendCode(@RequestBody SmsTo smsTo) {
        smsComponent.sendSmsCode(smsTo.getPhone(),smsTo.getCode(),smsTo.getMinute());
        return R.ok();
    }
}
