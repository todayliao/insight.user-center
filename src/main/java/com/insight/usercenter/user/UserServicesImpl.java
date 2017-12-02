package com.insight.usercenter.user;

import com.insight.usercenter.common.Core;
import com.insight.usercenter.common.dto.AccessToken;
import com.insight.usercenter.common.dto.Reply;
import com.insight.usercenter.common.dto.TokenPackage;
import com.insight.usercenter.common.dto.User;
import com.insight.usercenter.common.entity.Token;
import com.insight.usercenter.common.mapper.UserMapper;
import com.insight.usercenter.common.utils.Generator;
import com.insight.usercenter.common.utils.ReplyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/17
 * @remark 用户服务实现
 */
@Service
public class UserServicesImpl implements UserServices {
    private final Core core;
    private final UserMapper userMapper;

    /**
     * 构造函数
     *
     * @param core       自动注入的Core
     * @param userMapper 自动注入的UserMapper
     */
    @Autowired
    public UserServicesImpl(Core core, UserMapper userMapper) {
        this.core = core;
        this.userMapper = userMapper;
    }

    /**
     * 获取全部用户
     *
     * @param token 访问令牌
     * @param page  分页页码,默认1
     * @param size  每页行数,默认20
     * @return Reply
     */
    @Override
    public Reply getUsers(AccessToken token, int page, int size) {
        if (page < 1) {
            page = 1;
        }

        Integer total = userMapper.getUsersCountByApp(token.getAppId(), token.getAccountId());
        int offset = (page - 1) * size;
        List<User> users = userMapper.getUsersByApp(token.getAppId(), token.getAccountId(), offset, size);

        return ReplyHelper.success(users, total);
    }

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
    @Override
    public Reply getUserList(AccessToken token, int page, int size, String account, String name, String mobile,
                             Boolean status, String startDate, String endDate) {
        String applicationId = token.getAppId();
        int offset = (page - 1) * size;
        Integer total = userMapper.queryUsersCount(applicationId, account, name, mobile, status, startDate, endDate);
        List<User> users = userMapper.queryUsers(applicationId, offset, size, account, name, mobile, status, startDate, endDate);
        return ReplyHelper.success(users, total);
    }

    /**
     * 根据ID查询用户
     *
     * @param token 访问令牌
     * @param id    用户ID
     * @return 用户实体数据
     */
    @Override
    public Reply getUser(AccessToken token, String id) {
        User user = userMapper.getUserById(token.getAppId(), id);
        return ReplyHelper.success(user);
    }

    /**
     * 检验用户信息是否存在
     *
     * @param user User实体
     * @return Reply
     * @Author:郑昊
     */
    @Override
    public Reply ifExist(User user) {
        return ReplyHelper.success(core.isExisted(user));
    }

    /**
     * 新增用户
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply addUser(AccessToken token, User user) {

        user.setApplicationId(token.getAppId());
        if (user.getAccountId() == null || user.getAccountId().isEmpty()) {
            user.setAccountId(token.getAccountId());
        }

        // 验证用户是否存在
        if (core.isExisted(user)) {
            return ReplyHelper.accountExist();
        }

        // 持久化用户对象
        Integer count = userMapper.addUser(user);

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 注册用户
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    @Override
    public Reply signUp(User user) {

        // 验证短信验证码
        Boolean success = core.verifySmsCode(1, user.getMobile(), user.getOption(), false);
        if (!success) {
            return ReplyHelper.invalidCode();
        }

        // 验证用户是否存在
        if (core.isExisted(user)) {
            return ReplyHelper.accountExist();
        }

        // 初始化并持久化用户对象
        user.setBuiltin(false);
        user.setInvalid(false);
        user.setCreatedTime(new Date());
        Integer count = userMapper.addUser(user);

        String userId = core.getUserId(user.getApplicationId(), user.getAccount());
        if (userId == null) {
            return ReplyHelper.error();
        }

        // 初始化令牌数据并返回，实现自动登录功能
        Token accessToken = core.getToken(user.getId());
        core.initAccessToken(accessToken);
        TokenPackage tokens = core.creatorKey(accessToken, Generator.uuid());

        return count > 0 ? ReplyHelper.success(tokens) : ReplyHelper.error();
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return Reply
     */
    @Override
    public Reply deleteUser(String userId) {

        // 获取Token缓存中被删除用户的Token
        Token accessToken = core.getToken(userId);
        Boolean success = core.deleteUser(accessToken, userId);

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 更新用户信息(名称及备注)
     *
     * @param user User实体
     * @return Reply
     */
    @Override
    public Reply updateUserInfo(User user) {
        Token accessToken = core.getToken(user.getId());
        Boolean success = core.setUserInfo(accessToken, user.getId(), user.getName(), user.getRemark());

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 更新用户类型
     *
     * @param user User实体
     * @return Reply
     */
    @Override
    public Reply updateUserType(User user) {

        // 获取Token缓存中被更新用户的Token
        Token accessToken = core.getToken(user.getId());
        Boolean success = core.setUserType(accessToken, user.getId(), user.getUserType());

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 更新用户绑定手机号
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply updateUserMobile(AccessToken token, User user) {
        if (!token.getUserId().equals(user.getId())) {
            return ReplyHelper.invalidParam();
        }

        // 使用用户自己的Token
        Token accessToken = core.getToken(user.getId());
        Boolean success = core.setMobile(accessToken, user.getMobile());

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 更新用户绑定邮箱
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply updateUserEmail(AccessToken token, User user) {
        if (!token.getUserId().equals(user.getId())) {
            return ReplyHelper.invalidParam();
        }

        // 使用用户自己的Token
        Token accessToken = core.getToken(user.getId());
        Boolean success = core.setEmail(accessToken, user.getEmail());

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 更新登录密码
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply updatePassword(AccessToken token, User user) {
        Token accessToken = core.getToken(user.getId());
        if (token.getUserId().equals(user.getId()) && !accessToken.getPassword().equals(user.getOption())) {
            return ReplyHelper.invalidParam("错误的原密码!");
        }

        Boolean success = core.setPassword(accessToken, user.getId(), user.getPassword());

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 重置登录密码
     *
     * @param user User实体
     * @return Reply
     */
    @Override
    public Reply resetPassword(User user) {
        Boolean success = core.verifySmsCode(2, user.getMobile(), user.getOption(), false);
        if (!success) {
            return ReplyHelper.invalidCode();
        }

        String userId = core.getUserId(user.getApplicationId(), user.getAccount());
        if (userId == null) {
            return ReplyHelper.notExist();
        }

        // 更新密码
        Token accessToken = core.getToken(userId);
        success = core.setPassword(accessToken, userId, user.getPassword());
        if (!success) {
            return ReplyHelper.error();
        }

        // 生成令牌数据
        core.initAccessToken(accessToken);
        TokenPackage tokens = core.creatorKey(accessToken, Generator.uuid());

        return ReplyHelper.success(tokens);
    }

    /**
     * 更新支付密码
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply updatePayPassword(AccessToken token, User user) {
        if (!token.getUserId().equals(user.getId())) {
            return ReplyHelper.invalidParam();
        }

        Boolean success = core.verifySmsCode(3, user.getMobile(), user.getOption(), false);
        if (!success) {
            return ReplyHelper.invalidCode();
        }

        // 使用用户自己的Token
        Token accessToken = core.getToken(token.getUserId());
        success = core.setPayPassword(accessToken, user.getPaypw());

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 更新用户状态(禁用/启用)
     *
     * @param user User实体
     * @return Reply
     */
    @Override
    public Reply updateUserStatus(User user) {

        // 获取Token缓存中被更新用户的Token
        Token accessToken = core.getToken(user.getId());
        Boolean success = core.setInvalidStatus(accessToken, user.getId(), user.getInvalid());

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }
}
