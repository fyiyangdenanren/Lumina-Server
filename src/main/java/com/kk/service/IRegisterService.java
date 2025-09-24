package com.kk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kk.domain.dto.LoginDTO;
import com.kk.domain.po.User;

public interface IRegisterService extends IService<User> {
    void registerByPhone(LoginDTO loginDTO);
}
