package com.apin.usercenter.user;

import com.apin.util.pojo.Reply;
import com.apin.util.pojo.User;

/**
 * @author 宣炳刚
 * @date 2017/9/17
 * @remark 用户服务接口
 */
public interface UserServices {

    /**
     * 获取全部用户
     *
     * @param token 访问令牌
     * @param page  分页页码,默认1
     * @param size  每页行数,默认20
     * @return Reply
     */
    Reply getUsers(String token, int page, int size);

    /**
     * 新增用户
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    Reply addUser(String token, User user);

    /**
     * 注册用户
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    Reply signUp(String token, User user);

    /**
     * 删除用户
     *
     * @param token  访问令牌
     * @param userId 用户ID
     * @return Reply
     */
    Reply deleteUser(String token, String userId);

    /**
     * 更新用户信息(名称及备注)
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updateUserInfo(String token, User user);

    /**
     * 更新用户类型
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updateUserType(String token, User user);

    /**
     * 更新用户绑定手机号
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updateUserMobile(String token, User user);

    /**
     * 更新用户绑定邮箱
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updateUserEmail(String token, User user);

    /**
     * 更新登录密码
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updatePassword(String token, User user);

    /**
     * 重置登录密码
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply resetPassword(String token, User user);

    /**
     * 更新支付密码
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updatePayPassword(String token, User user);

    /**
     * 更新用户状态(禁用/启用)
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updateUserStatus(String token, User user);

}
