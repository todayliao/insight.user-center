package com.insight.usercenter.user;


import com.insight.usercenter.common.Token;
import com.insight.usercenter.common.dto.UserDTO;
import com.insight.usercenter.user.dto.QueryUserDTO;
import com.insight.util.pojo.Reply;

/**
 * @author 宣炳刚
 * @date 2017/9/17
 * @remark 用户服务接口
 */
public interface UserServices {

    /**
     * 根据条件查询用户集合(分页)
     *
     * @param user 用户查询对象实体
     * @return 用户集合
     */
    Reply getUsers(QueryUserDTO user);

    /**
     * 根据ID查询用户
     *
     * @param token Token
     * @param id    用户ID
     * @return Reply
     */
    Reply getUser(Token token, String id);

    /**
     * 根据ID查询用户
     *
     * @param token Token
     * @param id    用户ID
     * @param appId 微信AppID
     * @return Reply
     */
    Reply getUser(Token token, String id, String appId);

    /**
     * 新增用户
     *
     * @param token Token
     * @param user  User实体
     * @return Reply
     */
    Reply addUser(Token token, UserDTO user);

    /**
     * 检验用户信息是否存在
     *
     * @param user User实体
     * @return Reply
     */
    Reply exist(UserDTO user);

    /**
     * 注册用户
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    Reply signUp(UserDTO user);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return Reply
     */
    Reply deleteUser(String userId);

    /**
     * 更新用户信息(名称及备注)
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    Reply updateUserInfo(UserDTO user);

    /**
     * 更新用户类型
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    Reply updateUserType(UserDTO user);

    /**
     * 更新用户绑定手机号
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    Reply updateUserMobile(UserDTO user);

    /**
     * 更新用户绑定邮箱
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    Reply updateUserEmail(UserDTO user);

    /**
     * 更新登录密码
     *
     * @param token Token
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updatePassword(Token token, UserDTO user);

    /**
     * 重置登录密码
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    Reply resetPassword(UserDTO user);

    /**
     * 更新支付密码
     *
     * @param token Token
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updatePayPassword(Token token, UserDTO user);

    /**
     * 更新用户状态(禁用/启用)
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    Reply updateUserStatus(UserDTO user);
}
