package com.atg.gulimall.auth.web;

import com.alibaba.fastjson.TypeReference;
import com.atg.common.constant.AuthServerConstant;
import com.atg.common.exception.BizCodeEnume;
import com.atg.common.to.SmsTo;
import com.atg.common.to.UserRegistTo;
import com.atg.common.utils.R;
import com.atg.common.vo.MemberResponseVo;
import com.atg.gulimall.auth.feign.MemberFeignService;
import com.atg.gulimall.auth.feign.ThirdPartFeignService;
import com.atg.gulimall.auth.vo.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.atg.common.constant.AuthServerConstant.LOGIN_USER;

@Controller
public class indexController {

    @Autowired
    ThirdPartFeignService thirdPartFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping({"/","login.html"})
    public String loginPage(){
        return "login";
    }

    @GetMapping("reg.html")
    public String regPage(){
        return "reg";
    }

    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone")String phone){
        //判断该手机号验证码是否存在
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if(StringUtils.hasLength(redisCode)){
            long l=Long.parseLong(redisCode.split("_")[1]);
            if(System.currentTimeMillis()-l<60000){
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(),BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        String code = UUID.randomUUID().toString().substring(0, 5);
        String codeTime = code+"_"+System.currentTimeMillis();
        String Minute = "15";
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,codeTime, Long.parseLong(Minute), TimeUnit.MINUTES);
        SmsTo smsTo = new SmsTo();
        smsTo.setPhone(phone);
        smsTo.setCode(code);
        smsTo.setMinute(Minute);
        return thirdPartFeignService.sendCode(smsTo);
    }

    @PostMapping("/regist")
    public String regist(@Valid UserRegistTo userRegistTo, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            List<FieldError> list = result.getFieldErrors();
            Map<String, String> map = result.getFieldErrors().stream().collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (entity1, entity2) -> entity1
            ));
            redirectAttributes.addFlashAttribute("errors",map);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        String code = userRegistTo.getCode();
        String cacheCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegistTo.getPhone());
        Map<String, String> codeErrors = new HashMap<>();
        if(StringUtils.hasLength(cacheCode)){
            if(code.equals(cacheCode.split("_")[0])){
                //验证码验证成功直接删除验证码
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegistTo.getPhone());
                R r = memberFeignService.regist(userRegistTo);
                if (r.getCode() == 0) {
                    return "redirect:http://auth.gulimall.com/login.html";
                }else{
                    codeErrors.put("msg",r.getData(new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors",codeErrors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }

            }else{
                codeErrors.put("code","验证码错误");
                redirectAttributes.addFlashAttribute("errors",codeErrors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        }else{
            codeErrors.put("code","验证码不存在");
            redirectAttributes.addFlashAttribute("errors",codeErrors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }


    @PostMapping(value = "/login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session) {

        //远程登录
        R login = memberFeignService.login(vo);

        if (login.getCode() == 0) {
            MemberResponseVo data = login.getData("data", new TypeReference<MemberResponseVo>() {});
            session.setAttribute(LOGIN_USER,data);
            return "redirect:http://gulimall.com";
        } else {
            Map<String,String> errors = new HashMap<>();
            errors.put("msg",login.getData("msg",new TypeReference<String>(){}));
            attributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
