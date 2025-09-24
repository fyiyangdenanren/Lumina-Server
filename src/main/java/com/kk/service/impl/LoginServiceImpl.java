package com.kk.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drew.lang.annotations.NotNull;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.kk.constants.HttpStatus;
import com.kk.domain.dto.LoginDTO;
import com.kk.domain.po.User;
import com.kk.enums.MethodType;
import com.kk.enums.Verified;
import com.kk.exception.CustomException;
import com.kk.mapper.UserMapper;
import com.kk.properties.GoogleProperties;
import com.kk.properties.JwtProperties;
import com.kk.service.ILoginService;
import com.kk.utils.BCryptUtil;
import com.kk.utils.JwtUtil;
import com.kk.utils.RegexUtil;
import com.kk.utils.UserContextHolder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.kk.constants.RedisConstant.LOGIN_USER_CODE;
import static com.kk.constants.RedisConstant.LOGIN_USER_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl extends ServiceImpl<UserMapper, User> implements ILoginService {

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtProperties jwtProperties;
    private final GoogleProperties googleProperties;

    // 生成token
    @NotNull
    private String generateToken(final String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        String token = JwtUtil.createJWT(jwtProperties.getSecretKey(), jwtProperties.getTtlDays(), claims);
        String tokenKey = LOGIN_USER_TOKEN + userId;
        stringRedisTemplate.opsForValue().set(tokenKey, token, jwtProperties.getTtlDays(), TimeUnit.DAYS);
        return token;
    }

    // 验证码验证
    private String verifyCode(final String code, final String tel) {
        if (RegexUtil.isCodeInvalid(code)) {
            throw new CustomException("验证码格式错误", HttpStatus.BAD_REQUEST);
        }
        String redisCode = stringRedisTemplate.opsForValue().get(LOGIN_USER_CODE + tel);
        if (!code.equals(redisCode)) {
            throw new CustomException("验证码错误", HttpStatus.BAD_REQUEST);
        }
        // 3.生成token
        String userId = UserContextHolder.getUserId();
        return generateToken(userId);
    }

    @Override
    public String loginByPassword(@NonNull final LoginDTO loginDTO) {
        String telephone = loginDTO.getTelephone();
        String password = loginDTO.getPassword();
        // 1.获取手机号、密码
        if (StrUtil.isBlank(telephone) || StrUtil.isBlank(password)) {
            throw new CustomException("手机号或密码不能为空", HttpStatus.BAD_REQUEST);
        }
        // 2.查询用户
        // 2.1.校验手机号
        if (RegexUtil.isPhoneInvalid(telephone)) {
            throw new CustomException("手机号格式错误", HttpStatus.BAD_REQUEST);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getIdentifier, telephone);
        if (!this.exists(queryWrapper)) {
            throw new CustomException("手机号不存在，请先注册", HttpStatus.BAD_REQUEST);
        }
        // 2.2.校验密码
        User user = this.getOne(queryWrapper);
        if (!BCryptUtil.checkPassword(password, user.getCredential())) {
            throw new CustomException("密码错误", HttpStatus.BAD_REQUEST);
        }
        // 3.下达jwt令牌
        return generateToken(user.getUserId().toString());
    }

    @Override
    public String loginByPhone(@NonNull final LoginDTO loginDTO) {
        String tel = loginDTO.getTelephone();
        String code = loginDTO.getCode();
        // 1.校验手机号
        if (RegexUtil.isPhoneInvalid(tel)) {
            throw new CustomException("手机号格式错误", HttpStatus.BAD_REQUEST);
        }
        if (!this.exists(new LambdaQueryWrapper<User>().eq(User::getIdentifier, tel))) {
            throw new CustomException("手机号不存在", HttpStatus.NOT_FOUND);
        }
        // 2.匹配验证码
        return verifyCode(code, tel);
    }

    @SneakyThrows
    @Override
    public String loginByGoogleMail(@NonNull final Map<String, String> body) {
        // 1.交换 code 为 access_token
        String idToken = body.get("idToken");
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(googleProperties.getClientId()))
                .build();
        // 2.验证 id_token
        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            throw new CustomException("Failed to verify id token", HttpStatus.BAD_REQUEST);
        }
        // 3.获取用户信息
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        if (payload == null) {
            throw new CustomException("Failed to get user info", HttpStatus.BAD_REQUEST);
        }
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");
        String sub = payload.getSubject();
        // 3.处理用户数据（例如，提取 email, name）,保存用户
        if (this.exists(new LambdaQueryWrapper<User>().eq(User::getIdentifier, payload.getEmail()))) {
            Long userId = this.getOne(new LambdaQueryWrapper<User>().eq(User::getIdentifier, payload.getEmail())).getUserId();
            return generateToken(Long.toString(userId));
        }
        String s = RandomUtil.randomNumbers(19);
        Long userId = Long.valueOf(s);
        User user = new User();
        user.setUserId(userId);
        user.setUsername(name);
        user.setMethodType(MethodType.GOOGLE);
        user.setIdentifier(email);
        user.setCredential(sub);
        user.setVerified(Verified.VERIFIED);
        user.setAvatarUrl(picture);
        this.save(user);
        return generateToken(userId.toString());
    }

    @Override
    public String loginByEmail(String email, final String code) {
        // 1.校验邮箱
        if (RegexUtil.isEmailInvalid(email)) {
            throw new CustomException("邮箱格式错误", HttpStatus.BAD_REQUEST);
        }
        if (!this.exists(new QueryWrapper<User>().eq("email", email))) {
            throw new CustomException("邮箱不存在", HttpStatus.NOT_FOUND);
        }
        // 2.匹配验证码
        return verifyCode(code, email);
    }

    @Override
    public void logout(final String userId) {
        // 1.删除token
        stringRedisTemplate.delete(LOGIN_USER_TOKEN + userId);
        // 2.删除用户
        UserContextHolder.clear();
    }

}
