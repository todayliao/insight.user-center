package com.insight.usercenter.user;

import com.insight.usercenter.common.dto.AccessToken;
import com.insight.usercenter.common.dto.Reply;
import com.insight.usercenter.common.dto.User;
import com.insight.usercenter.common.utils.JsonUtils;
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
                          @RequestParam(defaultValue = "20") int size) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return services.getUsers(accessToken, page, size);
    }

    /**
     * 获取符合条件用户
     *
     * @param token 访问令牌
     * @param token 访问令牌
     * @param token 访问令牌
     * @param page  分页页码,默认1
     * @param size  每页行数,默认20
     * @return Reply
     */
    @GetMapping("/v1.1/userList")
    public Reply getUserList(@RequestHeader("Authorization") String token, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size,
                             String account, String name, String mobile, Boolean status, String startDate, String endDate) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return services.getUserList(accessToken, page, size, account, name, mobile, status, startDate, endDate);
    }

    /**
     * 获取单个用户信息
     *
     * @param token 访问令牌
     * @param id    用户ID
     * @return Reply
     * @Author:郑昊
     */
    @GetMapping("/v1.1/users/{id}")
    public Reply getUser(@RequestHeader("Authorization") String token, @PathVariable("id") String id) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return services.getUser(accessToken, id);
    }

    /**
     * 获取访问者的用户信息
     *
     * @param token 访问令牌
     * @return Reply
     */
    @GetMapping("/v1.1/users/myself")
    public Reply getMyself(@RequestHeader("Authorization") String token) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return services.getUser(accessToken, accessToken.getUserId());
    }

    /**
     * 检验用户信息是否存在
     *
     * @param user User实体
     * @return Reply
     * @Author:郑昊
     */
    @PostMapping("/v1.1/users/ifexist")
    public Reply ifExist(@RequestBody User user) {
        return services.ifExist(user);
    }

    /**
     * 新增用户
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PostMapping("/v1.1/users")
    public Reply addUser(@RequestHeader("Authorization") String token, @RequestBody User user) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return services.addUser(accessToken, user);
    }

    /**
     * 注册用户
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    @PostMapping("/v1.1/users/signup")
    public Reply signUp(@RequestBody User user) {
        return services.signUp(user);
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/users/{id}")
    public Reply deleteUser(@PathVariable("id") String userId) {
        return services.deleteUser(userId);
    }

    /**
     * 更新用户信息(名称及备注)
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/info")
    public Reply updateUserInfo(@RequestBody User user) {
        return services.updateUserInfo(user);
    }

    /**
     * 更新用户类型
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/type")
    public Reply updateUserType(@RequestBody User user) {
        return services.updateUserType(user);
    }

    /**
     * 更新用户绑定手机号
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/mobile")
    public Reply updateUserMobile(@RequestHeader("Authorization") String token, @RequestBody User user) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return services.updateUserMobile(accessToken, user);
    }

    /**
     * 更新用户绑定邮箱
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/email")
    public Reply updateUserEmail(@RequestHeader("Authorization") String token, @RequestBody User user) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return services.updateUserEmail(accessToken, user);
    }

    /**
     * 更新登录密码
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/sign")
    public Reply updatePassword(@RequestHeader("Authorization") String token, @RequestBody User user) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return services.updatePassword(accessToken, user);
    }

    /**
     * 重置登录密码
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    @PostMapping("/v1.1/users/{id}/sign")
    public Reply resetPassword(@RequestBody User user) {
        return services.resetPassword(user);
    }

    /**
     * 更新支付密码
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/paypw")
    public Reply updatePayPassword(@RequestHeader("Authorization") String token, @RequestBody User user) {
        AccessToken accessToken = JsonUtils.toAccessToken(token);
        return services.updatePayPassword(accessToken, user);
    }

    /**
     * 更新用户状态(禁用/启用)
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/status")
    public Reply updateUserStatus(@RequestBody User user) {
        return services.updateUserStatus(user);
    }
}
