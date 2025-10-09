package com.kk.controller;

import com.kk.domain.dto.LoginDTO;
import com.kk.domain.po.R;
import com.kk.service.ILoginService;
import com.kk.utils.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final ILoginService loginService;

    /**
     * 密码登录
     *
     * @param loginDTO 登录参数
     * @return String
     */
    @PostMapping("/byPassword")
    public R<String> loginByPassword(@RequestBody LoginDTO loginDTO) {
        String token = loginService.loginByPassword(loginDTO);
        return R.ok(token);
    }

    /**
     * 手机号登录
     *
     * @param loginDTO 登录参数
     * @return String
     */
    @PostMapping("/byPhone")
    public R<String> loginByPhone(@RequestBody LoginDTO loginDTO) {
        String token = loginService.loginByPhone(loginDTO);
        return R.ok(token);
    }

    /**
     * 谷歌邮箱方式登录
     *
     * @param body 谷歌邮箱登录参数
     * @return String
     */
    @PostMapping("/byGoogleMail")
    public R<String> loginByGoogleMail(@RequestBody Map<String, String> body) {
        String token = loginService.loginByGoogleMail(body);
        return R.ok(token);
    }

    /**
     * 邮箱方式登录
     *
     * @param email,code 邮箱验证码
     * @return String
     */
    @PostMapping("/byEmail")
    public R<String> loginByEmail(String email, String code) {
        String token = loginService.loginByEmail(email, code);
        return R.ok(token);
    }

    /**
     * 退出登录
     *
     * @return void
     */
    @DeleteMapping("/logout")
    public R<Void> logout() {
        String userId = UserContextHolder.getUserId();
        loginService.logout(userId);
        return R.ok();
    }
}
