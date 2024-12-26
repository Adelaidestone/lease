package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.admin.mapper.SystemUserMapper;
import com.atguigu.lease.web.admin.service.LoginService;
import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.atguigu.lease.web.admin.vo.login.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wf.captcha.SpecCaptcha;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.swing.text.Utilities;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SystemUserMapper systemUserMapper;

    @Override
    public CaptchaVo getCaptcha() {
        //图形界面的对象
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        //获取图形验证码中的 文本内容
        String verCode = specCaptcha.text().toLowerCase();
        //图片 (1.图片的样式2.图片中的内容)
        String image = specCaptcha.toBase64();


        //再redis中保存验证码的 key
        String key = RedisConstant.ADMIN_LOGIN_PREFIX+ UUID.randomUUID();
        // 存入redis并设置过期时间为1分钟
        redisTemplate.opsForValue().set(key,verCode,RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);

        CaptchaVo captchaVo=new CaptchaVo(image,key);

        return captchaVo;
    }


    @Override
    public String login(LoginVo loginVo) {

        //账号
        String username = loginVo.getUsername();
        //密码
        String password = loginVo.getPassword();
        //验证码
        String captchaCode = loginVo.getCaptchaCode();
        //验证码在redis中的key
        String captchaKey = loginVo.getCaptchaKey();


        //验证码不能为空
        if(!StringUtils.hasText(captchaCode)){
            throw  new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_NOT_FOUND);
        }

        //获取redis中的验证码
        String redisCode = (String)  redisTemplate.opsForValue().get(captchaKey);
        //如果redis中的验证码过期了,自动删除验证码
        if(redisCode==null){
            throw  new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_EXPIRED);
        }

        //比较客户端输入的验证码和redis中的验证码，如果不一致响应错误消息
        if(!captchaCode.equals(redisCode)){
            throw  new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_ERROR);
        }


        //根据账号username 到数据库中查询用户的信息
        SystemUser systemUser = systemUserMapper.selectOne(new LambdaQueryWrapper<SystemUser>().eq(SystemUser::getUsername, username));

        //判断账号是否存在
        if(systemUser==null){
            throw  new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }

        //如果用户被禁用
        if(systemUser.getStatus()== BaseStatus.DISABLE){
            throw  new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);
        }



        //比较登录的密码是否一致
        if(DigestUtils.md5DigestAsHex(password.getBytes()).equals(systemUser.getPassword())){
            throw  new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }


        //给登录成功的用户创建token信息
        String token = JwtUtil.createToken(systemUser.getId(), systemUser.getUsername());

        return token;
    }
    }


