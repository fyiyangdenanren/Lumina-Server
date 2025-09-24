package com.kk.controller;

import com.kk.domain.dto.LoginDTO;
import com.kk.domain.po.R;
import com.kk.service.IRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {
    private final IRegisterService registerService;

    /**
     * 注册
     *
     * @return Void
     */
    @PostMapping("/byPhone")
    public R<Void> registerByPhone(@RequestBody LoginDTO loginDTO) {
        registerService.registerByPhone(loginDTO);
        return R.ok();
    }

    /**
     * 谷歌邮箱注册
     *
     * @return Void
     */

}
