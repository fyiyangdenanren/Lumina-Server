package com.kk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kk.domain.dto.PageDTO;
import com.kk.domain.po.User;
import com.kk.domain.query.UserQuery;
import com.kk.domain.vo.UserVO;

import java.util.List;

public interface IUserService extends IService<User> {

    void deleteUser(Integer userId);

    void updateUser(User user);

    User getUserById(Integer userId);

    List<UserVO> getUsersList(UserQuery userQuery);

    PageDTO<UserVO> getUsersPage(UserQuery userQuery);

    void sendEmail(String email);

    void sendLoginPhone(String phone);

    void sendRegisterPhone(String phone);
}
