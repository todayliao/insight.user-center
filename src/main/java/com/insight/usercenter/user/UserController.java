package com.insight.usercenter.user;

import com.insight.usercenter.common.Verify;
import com.insight.usercenter.common.dto.UserDTO;
import com.insight.usercenter.user.dto.QueryUserDTO;
import com.insight.util.pojo.Reply;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 宣炳刚
 * @date 2017/9/17
 * @remark 用户服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/userapi")
public class UserController {
    private final UserServices services;

    @Autowired
    public UserController(UserServices services) {
        this.services = services;
    }

    /**
     * 获取符合条件用户
     *
     * @param token 访问令牌
     * @param user  用户查询对象实体
     * @return Reply
     */
    @GetMapping("/v1.1/users")
    public Reply getUsers(@RequestHeader("Authorization") String token, QueryUserDTO user) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("getUsers");
        if (!result.getSuccess()) {
            return result;
        }

        return services.getUsers(user);
    }

    /**
     * 获取单个用户信息
     *
     * @param id 用户ID
     * @return Reply
     */
    @GetMapping("/v1.1/users/{id}")
    public Reply getUser(@RequestHeader("Authorization") String token, @PathVariable("id") String id) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("getUsers");
        if (!result.getSuccess()) {
            return result;
        }

        return services.getUser(verify.getBasis(), id);
    }

    /**
     * 获取访问者的用户信息
     *
     * @param token 访问令牌
     * @return Reply
     */
    @GetMapping("/v1.1/users/myself")
    public Reply getMyself(@RequestHeader("Authorization") String token, @RequestParam(value = "appid", required = false) String appId) {
        Verify verify = new Verify(token);
        Reply result = verify.compare();
        if (!result.getSuccess()) {
            return result;
        }

        String userId = verify.getBasis().getUserId();
        return appId == null ? services.getUser(verify.getBasis(), userId) : services.getUser(verify.getBasis(), userId, appId);
    }

    /**
     * 检验用户信息是否存在
     *
     * @param user User实体
     * @return Reply
     */
    @GetMapping("/v1.1/users/account")
    public Reply exist(UserDTO user) {
        return services.exist(user);
    }

    /**
     * 新增用户
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PostMapping("/v1.1/users")
    public Reply addUser(@RequestHeader("Authorization") String token, @RequestBody UserDTO user) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("addUser");
        if (!result.getSuccess()) {
            return result;
        }

        return services.addUser(verify.getBasis(), user);
    }

    /**
     * 注册用户
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    @PostMapping("/v1.1/users/signup")
    public Reply signUp(@RequestBody UserDTO user) {
        return services.signUp(user);
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return Reply
     */
    @DeleteMapping("/v1.1/users/{id}")
    public Reply deleteUser(@RequestHeader("Authorization") String token, @PathVariable("id") String userId) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("deleteUser");
        if (!result.getSuccess()) {
            return result;
        }

        return services.deleteUser(userId);
    }

    /**
     * 更新用户信息(名称及备注)
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/info")
    public Reply updateUserInfo(@RequestHeader("Authorization") String token, @RequestBody UserDTO user) {
        Verify verify = new Verify(token);
        String function = verify.getFunction(user.getId(), "updateUser");
        Reply result = verify.compare(function);
        if (!result.getSuccess()) {
            return result;
        }
        //如果用户id为空，则从token中取
        if (StringUtils.isBlank(user.getId())) {
            user.setId(verify.getBasis().getUserId());
        }
        return services.updateUserInfo(user);
    }

    /**
     * 更新用户类型
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/type")
    public Reply updateUserType(@RequestHeader("Authorization") String token, @RequestBody UserDTO user) {
        Verify verify = new Verify(token);
        Reply result = verify.compare("updateUser");
        if (!result.getSuccess()) {
            return result;
        }

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
    public Reply updateUserMobile(@RequestHeader("Authorization") String token, @RequestBody UserDTO user) {
        Verify verify = new Verify(token);
        String function = verify.getFunction(user.getId(), "updateUser");
        Reply result = verify.compare(function);
        if (!result.getSuccess()) {
            return result;
        }

        return services.updateUserMobile(user);
    }

    /**
     * 更新用户绑定邮箱
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/email")
    public Reply updateUserEmail(@RequestHeader("Authorization") String token, @RequestBody UserDTO user) {
        Verify verify = new Verify(token);
        String function = verify.getFunction(user.getId(), "updateUser");
        Reply result = verify.compare(function);
        if (!result.getSuccess()) {
            return result;
        }

        return services.updateUserEmail(user);
    }

    /**
     * 更新登录密码
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/sign")
    public Reply updatePassword(@RequestHeader("Authorization") String token, @RequestBody UserDTO user) {
        Verify verify = new Verify(token);
        String function = verify.getFunction(user.getId(), "resetPassword");
        Reply result = verify.compare(function);
        if (!result.getSuccess()) {
            return result;
        }

        return services.updatePassword(verify.getBasis(), user);
    }

    /**
     * 重置登录密码
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/sign")
    public Reply resetPassword(@RequestBody UserDTO user) {
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
    public Reply updatePayPassword(@RequestHeader("Authorization") String token, @RequestBody UserDTO user) {
        Verify verify = new Verify(token);
        Reply result = verify.compare();
        if (!result.getSuccess()) {
            return result;
        }

        return services.updatePayPassword(verify.getBasis(), user);
    }

    /**
     * 更新用户状态(禁用/启用)
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    @PutMapping("/v1.1/users/{id}/status")
    public Reply updateUserStatus(@RequestHeader("Authorization") String token, @RequestBody UserDTO user) {
        Verify verify = new Verify(token);
        String function = user.getInvalid() ? "disableUser" : "enableUser";
        Reply result = verify.compare(function);
        if (!result.getSuccess()) {
            return result;
        }

        return services.updateUserStatus(user);
    }
}
