package com.kk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kk.constants.HttpStatus;
import com.kk.domain.dto.LoginDTO;
import com.kk.domain.po.User;
import com.kk.enums.MethodType;
import com.kk.enums.Verified;
import com.kk.exception.CustomException;
import com.kk.mapper.UserMapper;
import com.kk.service.IRegisterService;
import com.kk.utils.BCryptUtil;
import com.kk.utils.RegexUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.kk.constants.RedisConstant.REGISTER_USER_CODE;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl extends ServiceImpl<UserMapper, User> implements IRegisterService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void registerByPhone(@NotNull final LoginDTO loginDTO) {
        // 手机号
        String tel = loginDTO.getTelephone();
        // 验证码
        String code = loginDTO.getCode();
        // 姓名
        String username = loginDTO.getUsername();
        // 密码
        String password = loginDTO.getPassword();
        // 1.验证手机号格式
        if (RegexUtil.isPhoneInvalid(tel)) {
            throw new CustomException("手机号格式有误", HttpStatus.BAD_REQUEST);
        }
        // 2.查询该手机号是否已注册
        if (this.getOneOpt(new QueryWrapper<User>().eq("telephone", tel)).isPresent()) {
            throw new CustomException("该手机号已注册", HttpStatus.BAD_REQUEST);
        }
        // 3.获取验证码
        if (RegexUtil.isCodeInvalid(code)) {
            throw new CustomException("验证码格式错误", HttpStatus.BAD_REQUEST);
        }
        String redisCode = stringRedisTemplate.opsForValue().get(REGISTER_USER_CODE + tel);
        if (redisCode == null || !redisCode.equals(code)) {
            throw new CustomException("验证码无效", HttpStatus.BAD_REQUEST);
        }
        // 4.保存用户
        User user = new User();
        user.setUsername(username);
        user.setMethodType(MethodType.PHONE);
        user.setIdentifier(tel);
        user.setCredential(BCryptUtil.hashPassword(password));
        user.setVerified(Verified.VERIFIED);
        this.save(user);
    }
}
