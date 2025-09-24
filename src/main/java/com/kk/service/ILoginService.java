package com.kk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kk.domain.dto.LoginDTO;
import com.kk.domain.po.User;

import java.util.Map;

public interface ILoginService extends IService<User> {

    String loginByPassword(LoginDTO loginDTO);

    String loginByPhone(LoginDTO loginDTO);

    String loginByGoogleMail(Map<String, String> body);

    String loginByEmail(String email, String code);

    void logout(String userId);

}
