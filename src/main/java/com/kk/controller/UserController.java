package com.kk.controller;

import com.kk.domain.dto.PageDTO;
import com.kk.domain.po.R;
import com.kk.domain.po.User;
import com.kk.domain.query.UserQuery;
import com.kk.domain.vo.UserVO;
import com.kk.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/send/registerPhone")
    public R<Void> sendRegisterPhone(@RequestParam String phone) {
        userService.sendRegisterPhone(phone);
        return R.ok();
    }

    @PostMapping("/send/loginPhone")
    public R<Void> sendLoginPhone(@RequestParam String phone) {
        userService.sendLoginPhone(phone);
        return R.ok();
    }

    @PostMapping("/send/email")
    public R<Void> sendEmail(@RequestParam String email) {
        userService.sendEmail(email);
        return R.ok();
    }

    /**
     * 添加用户
     *
     * @param user 用户
     * @return Void
     */
    @PostMapping("/addUser")
    public R<Void> addUser(@RequestBody User user) {
        userService.save(user);
        return R.ok();
    }

    /**
     * 删除用户
     *
     * @param userId 用户id
     * @return Void
     */
    @DeleteMapping("/deleteUser/{userId}")
    public R<Void> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return R.ok();
    }

    /**
     * 修改用户
     *
     * @param user  用户
     * @return Void
     */
    @PutMapping("/updateUser")
    public R<Void> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return R.ok();
    }

    /**
     * 根据id查询用户
     *
     * @param userId 用户id
     * @return User
     */
    @GetMapping("/getUserById/{userId}")
    public R<User> getUserById(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        return R.ok(user);
    }

    /**
     * 获取用户列表
     *
     * @param userQuery 查询参数
     * @return List<UserVO>
     */
    @GetMapping("/list/getUsers")
    public R<List<UserVO>> getUsersList(UserQuery userQuery) {
        List<UserVO> usersList = userService.getUsersList(userQuery);
        return R.ok(usersList);
    }

    /**
     * 获取用户分页列表
     *
     * @param userQuery 查询参数
     * @return PageDTO<UserVO>
     */
    @GetMapping("/page/getUsers")
    public R<PageDTO<UserVO>> getUsersPage(UserQuery userQuery) {
        PageDTO<UserVO> pageDTO = userService.getUsersPage(userQuery);
        return R.ok(pageDTO);
    }
}
