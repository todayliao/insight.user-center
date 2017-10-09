package com.apin.usercenter.user;

import com.apin.util.pojo.Reply;
import com.apin.util.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 宣炳刚
 * @date 2017/9/17
 * @remark 用户服务控制器
 */
@RestController
@RequestMapping("/userapi")
public class UserController {
    @Autowired
    private UserServices services;

    /**
     * 获取全部用户
     *
     * @param token 访问令牌
     * @param page  分页页码,默认1
     * @param size  每页行数,默认20
     * @return Reply
     */
    @GetMapping("/v1.1/users")
    public Reply getUsers(@RequestHeader("Authorization") String token, @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "20") int size) throws Exception {
        return services.getUsers(token, page, size);
    }

    /**
     * 新增用户
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PostMapping("/v1.1/users")
    public Reply addUser(@RequestHeader("Authorization") String token, @RequestBody User user) throws Exception {
        return services.addUser(token, user);
    }

    /**
     * 注册用户
     *
     * @param token    访问令牌
     * @param user     User实体,来自Body
     * @param initRole 是否初始化角色
     * @return Reply
     */
    @PostMapping("/v1.1/users/signup")
    public Reply signUp(@RequestHeader("Authorization") String token, @RequestBody User user,
                        @RequestParam(value = "init", defaultValue = "false") Boolean initRole) throws Exception {
        return services.signUp(token, user, initRole);
    }

    /**
     * 删除用户
     *
     * @param token  访问令牌
     * @param userId 用户ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/users/{id}")
    public Reply deleteUser(@RequestHeader("Authorization") String token, @PathVariable("id") String userId) throws Exception {
        return services.deleteUser(token, userId);
    }

    /**
     * 更新用户信息(名称及备注)
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/info")
    public Reply updateUserInfo(@RequestHeader("Authorization") String token, @RequestBody User user) throws Exception {
        return services.updateUserInfo(token, user);
    }

    /**
     * 更新用户类型
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/type")
    public Reply updateUserType(@RequestHeader("Authorization") String token, @RequestBody User user) throws Exception {
        return services.updateUserType(token, user);
    }

    /**
     * 更新用户绑定手机号
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/mobile")
    public Reply updateUserMobile(@RequestHeader("Authorization") String token, @RequestBody User user) throws Exception {
        return services.updateUserMobile(token, user);
    }

    /**
     * 更新用户绑定邮箱
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/email")
    public Reply updateUserEmail(@RequestHeader("Authorization") String token, @RequestBody User user) throws Exception {
        return services.updateUserEmail(token, user);
    }

    /**
     * 更新登录密码
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/sign")
    public Reply updatePassword(@RequestHeader("Authorization") String token, @RequestBody User user) throws Exception {
        return services.updatePassword(token, user);
    }

    /**
     * 重置登录密码
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PostMapping("/v1.1/users/{id}/sign")
    public Reply resetPassword(@RequestHeader("Authorization") String token, @RequestBody User user) throws Exception {
        return services.resetPassword(token, user);
    }

    /**
     * 更新支付密码
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/paypw")
    public Reply updatePayPassword(@RequestHeader("Authorization") String token, @RequestBody User user) throws Exception {
        return services.updatePayPassword(token, user);
    }

    /**
     * 更新用户状态(禁用/启用)
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/status")
    public Reply updateUserStatus(@RequestHeader("Authorization") String token, @RequestBody User user) throws Exception {
        return services.updateUserStatus(token, user);
    }

}
