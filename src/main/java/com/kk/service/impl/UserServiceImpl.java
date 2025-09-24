package com.kk.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kk.constants.HttpStatus;
import com.kk.domain.dto.PageDTO;
import com.kk.domain.po.User;
import com.kk.domain.query.UserQuery;
import com.kk.domain.vo.UserVO;
import com.kk.exception.CustomException;
import com.kk.exception.ServerException;
import com.kk.mapper.UserMapper;
import com.kk.service.IUserService;
import com.kk.utils.MailUtil;
import com.kk.utils.RegexUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.kk.constants.RedisConstant.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private final StringRedisTemplate stringRedisTemplate;
    private final MailUtil mailUtil;

    @Override
    public void sendEmail(final String email) {
        // 1.校验邮箱格式
        if (RegexUtil.isEmailInvalid(email)) {
            throw new CustomException("邮箱格式错误", HttpStatus.BAD_REQUEST);
        }
        // 2.验证码不能频繁发送
        if (stringRedisTemplate.hasKey(LOGIN_USER_CODE + email)) {
            throw new CustomException("请勿频繁发送验证码！", HttpStatus.BAD_REQUEST);
        }
        // 3.随机验证码
        String code = RandomUtil.randomString(6);
        // 4.保存验证码
        stringRedisTemplate.opsForValue().set(LOGIN_USER_CODE + email, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 3.发送验证码
        mailUtil.sendMail(email, email, "邮箱验证码", "验证码：" + code);
    }

    @Override
    public void sendLoginPhone(final String phone) {
        // 1.校验手机号格式
        if (RegexUtil.isPhoneInvalid(phone)) {
            throw new CustomException("手机号格式错误", HttpStatus.BAD_REQUEST);
        }
        // 2.验证码不能频繁发送
        if (stringRedisTemplate.hasKey(LOGIN_USER_CODE + phone)) {
            throw new CustomException("请勿频繁发送验证码！", HttpStatus.BAD_REQUEST);
        }
        // 3.随机验证码
        String code = RandomUtil.randomString(6);
        // 4.保存验证码
        stringRedisTemplate.opsForValue().set(LOGIN_USER_CODE + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // TODO 3.发送验证码
    }

    @Override
    public void sendRegisterPhone(final String tel) {
        // 1.校验手机号格式
        if (RegexUtil.isPhoneInvalid(tel)) {
            throw new CustomException("手机号格式错误", HttpStatus.BAD_REQUEST);
        }
        // 2.验证码不能频繁发送
        if (stringRedisTemplate.hasKey(REGISTER_USER_CODE + tel)) {
            throw new CustomException("请勿频繁发送验证码！", HttpStatus.BAD_REQUEST);
        }
        // 3.随机验证码
        String code = RandomUtil.randomString(6);
        // 4.保存验证码
        stringRedisTemplate.opsForValue().set(REGISTER_USER_CODE + tel, code, REGISTER_CODE_TTL, TimeUnit.MINUTES);
        // TODO 3.发送验证码
    }

    @Override
    public void deleteUser(final Integer userId) {
        // 1.根据userId查询user
        User user = this.getById(userId);
        if (user == null) {
            throw new ServerException("无法删除用户", HttpStatus.BAD_REQUEST);
        }
        // 2.逻辑删除
        user.setIsDeleted(1);
    }

    @Override
    public void updateUser(final User user) {
        // 1.根据userId查询user
        if (user.getUserId() == null) {
            throw new ServerException("无法修改用户", HttpStatus.BAD_REQUEST);
        }
        // 2.修改用户
        updateById(user);
    }

    @Override
    public User getUserById(final Integer userId) {
        // 1.根据userId查询user
        if (userId == null) {
            throw new ServerException("无法查询用户", HttpStatus.BAD_REQUEST);
        }
        // 2.查询用户
        return this.getById(userId);
    }

    @Override
    public List<UserVO> getUsersList(final UserQuery userQuery) {
        // 1.构造查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getIsDeleted, userQuery.getIsDelete());
        List<User> users = list(queryWrapper);
        // 2.判断是否空
        if (users == null) {
            return Collections.emptyList();
        }
        // 3.转换集合类型
        return BeanUtil.copyToList(users, UserVO.class);
    }

    @Override
    public PageDTO<UserVO> getUsersPage(final UserQuery userQuery) {
        // 1.构造分页条件
        Page<User> page = userQuery.toMpPageDefaultSortByCreateTime();
        // 2.分页查询
        Page<User> p = lambdaQuery()
                .eq(userQuery.getIsDelete() != null, User::getIsDeleted, userQuery.getIsDelete())
                .page(page);
        // 3.封装VO,返回
        return PageDTO.of(p, UserVO.class);
    }

}
