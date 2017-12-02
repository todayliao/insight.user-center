package com.insight.usercenter.user;

import com.insight.usercenter.common.dto.AccessToken;
import com.insight.usercenter.common.dto.Reply;
import com.insight.usercenter.common.dto.User;

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
    Reply getUsers(AccessToken token, int page, int size);

    /**
     * 根据条件查询用户集合(分页)
     *
     * @param token     访问令牌
     * @param page      页码
     * @param size      每页行数
     * @param account   账号名
     * @param name      用户名
     * @param mobile    手机号
     * @param status    用户状态
     * @param startDate 开始日期
     * @param endDate   截止日期
     * @return 用户集合
     */
    Reply getUserList(AccessToken token, int page, int size, String account, String name, String mobile, Boolean status, String startDate, String endDate);

    /**
     * 根据ID查询用户
     *
     * @param token 访问令牌
     * @param id    用户ID
     * @return 用户实体数据
     */
    Reply getUser(AccessToken token, String id);

    /**
     * 新增用户
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    Reply addUser(AccessToken token, User user);

    /**
     * 检验用户信息是否存在
     *
     * @param user User实体
     * @return Reply
     * @Author:郑昊
     */
    Reply ifExist(User user);

    /**
     * 注册用户
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    Reply signUp(User user);

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
    Reply updateUserInfo(User user);

    /**
     * 更新用户类型
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    Reply updateUserType(User user);

    /**
     * 更新用户绑定手机号
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updateUserMobile(AccessToken token, User user);

    /**
     * 更新用户绑定邮箱
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updateUserEmail(AccessToken token, User user);

    /**
     * 更新登录密码
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updatePassword(AccessToken token, User user);

    /**
     * 重置登录密码
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    Reply resetPassword(User user);

    /**
     * 更新支付密码
     *
     * @param token 访问令牌
     * @param user  User实体,来自Body
     * @return Reply
     */
    Reply updatePayPassword(AccessToken token, User user);

    /**
     * 更新用户状态(禁用/启用)
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    Reply updateUserStatus(User user);

}
