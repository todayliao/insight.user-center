package com.apin.usercenter.user;

import com.apin.usercenter.auth.dto.TokenPackage;
import com.apin.usercenter.common.Verify;
import com.apin.usercenter.common.mapper.UserMapper;
import com.apin.usercenter.component.Core;
import com.apin.usercenter.component.Token;
import com.apin.util.ReplyHelper;
import com.apin.util.pojo.Reply;
import com.apin.util.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate redis;
    private final UserMapper userMapper;
    private final Logger logger;

    /**
     * 构造函数
     *
     * @param core       自动注入的Core
     * @param redis      自动注入的StringRedisTemplate
     * @param userMapper 自动注入的UserMapper
     */
    @Autowired
    public UserServicesImpl(Core core, StringRedisTemplate redis, UserMapper userMapper) {
        this.core = core;
        this.redis = redis;
        this.userMapper = userMapper;

        logger = LoggerFactory.getLogger(this.getClass());
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
    public Reply getUsers(String token, int page, int size) {
        Date now = new Date();
        if (size < 10 || size > 100) return ReplyHelper.invalidParam("每页数量只能是10-100之间的数字！");

        if (page < 1) page = 1;

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("ListUsers");
        if (!reply.getSuccess()) return reply;

        Integer total = userMapper.getUsersCountByApp(verify.getAppId());
        int offset = (page - 1) * size;
        List<User> users = userMapper.getUsersByApp(verify.getAppId(), offset, size);

        long time = new Date().getTime() - now.getTime();
        logger.info("getUsers耗时:" + time + "毫秒...");

        return ReplyHelper.success(users, total);
    }

    /**
     * 新增用户
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply addUser(String token, User user) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("AddUser");
        if (!reply.getSuccess()) return reply;

        if (core.isExisted(user)) return ReplyHelper.accountExist();

        user.setApplicationId(verify.getAppId());
        user.setAccountId(verify.getAccountId());
        user.setBuiltin(false);
        user.setInvalid(false);
        user.setCreatedTime(new Date());
        Integer count = userMapper.addUser(user);

        long time = new Date().getTime() - now.getTime();
        logger.info("addUser耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 注册用户
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply signUp(String token, User user) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) return reply;

        Boolean success = core.verifySmsCode(1, user.getMobile(), user.getOption(), false);
        if (!success) return ReplyHelper.invalidCode();

        user.setApplicationId(verify.getAppId());
        user.setBuiltin(false);
        user.setInvalid(false);
        user.setCreatedTime(new Date());
        Integer count = userMapper.addUser(user);

        String userId = core.getUserId(verify.getAppId(), user.getAccount());
        if (userId == null) return ReplyHelper.notExist();

        Token accessToken = core.getToken(user.getId());
        String code = core.generateCode(accessToken, 0, null);
        core.initAccessToken(accessToken);
        TokenPackage tokens = core.creatorKey(accessToken, code);

        long time = new Date().getTime() - now.getTime();
        logger.info("addUser耗时:" + time + "毫秒...");

        return count > 0 ? ReplyHelper.success(tokens) : ReplyHelper.error();
    }

    /**
     * 删除用户
     *
     * @param token  访问令牌
     * @param userId 用户ID
     * @return Reply
     */
    @Override
    public Reply deleteUser(String token, String userId) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("DeleteUser");
        if (!reply.getSuccess()) return reply;

        // 获取Token缓存中被删除用户的Token
        Token accessToken = core.getToken(userId);
        Boolean success = core.deleteUser(accessToken, userId);

        long time = new Date().getTime() - now.getTime();
        logger.info("deleteUser耗时:" + time + "毫秒...");

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 更新用户信息(名称及备注)
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply updateUserInfo(String token, User user) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        String key = "EditUser";
        if (verify.getUserId().equals(user.getId())) key = null;

        Reply reply = verify.compare(key);
        if (!reply.getSuccess()) return reply;

        // 如更新的是自己的用户信息，则使用自己的Token，否则使用Token缓存中用户的Token
        Token accessToken = key == null ? verify.getBasis() : core.getToken(user.getId());
        Boolean success = core.setUserInfo(accessToken, user.getId(), user.getName(), user.getRemark());

        long time = new Date().getTime() - now.getTime();
        logger.info("updateUserInfo耗时:" + time + "毫秒...");

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 更新用户类型
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply updateUserType(String token, User user) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare("EditUser");
        if (!reply.getSuccess()) return reply;

        // 获取Token缓存中被更新用户的Token
        Token accessToken = core.getToken(user.getId());
        Boolean success = core.setUserType(accessToken, user.getId(), user.getUserType());

        long time = new Date().getTime() - now.getTime();
        logger.info("updateUserType耗时:" + time + "毫秒...");

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
    public Reply updateUserMobile(String token, User user) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) return reply;

        if (!verify.getUserId().equals(user.getId())) return ReplyHelper.invalidParam();

        // 使用用户自己的Token
        Boolean success = core.setMobile(verify.getBasis(), user.getMobile());
        long time = new Date().getTime() - now.getTime();
        logger.info("updateUserMobile耗时:" + time + "毫秒...");

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
    public Reply updateUserEmail(String token, User user) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) return reply;

        if (!verify.getUserId().equals(user.getId())) return ReplyHelper.invalidParam();

        // 使用用户自己的Token
        Boolean success = core.setEmail(verify.getBasis(), user.getEmail());
        long time = new Date().getTime() - now.getTime();
        logger.info("updateUserEmail耗时:" + time + "毫秒...");

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
    public Reply updatePassword(String token, User user) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        String key = "ResetPassword";
        if (verify.getUserId().equals(user.getId())) key = null;

        Reply reply = verify.compare(key);
        if (!reply.getSuccess()) return reply;

        // 如更新的是自己的密码，则使用自己的Token，否则使用Token缓存中用户的Token
        Token accessToken = key == null ? verify.getBasis() : core.getToken(user.getId());
        Boolean success = core.setPassword(accessToken, user.getId(), user.getPassword());

        long time = new Date().getTime() - now.getTime();
        logger.info("updatePassword耗时:" + time + "毫秒...");

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 重置登录密码
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply resetPassword(String token, User user) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) return reply;

        Boolean success = core.verifySmsCode(2, user.getMobile(), user.getOption(), false);
        if (!success) return ReplyHelper.invalidCode();

        String userId = core.getUserId(verify.getAppId(), user.getAccount());
        if (userId == null) return ReplyHelper.notExist();

        Token accessToken = core.getToken(userId);
        String code = core.generateCode(accessToken, 0, null);
        core.initAccessToken(accessToken);
        TokenPackage tokens = core.creatorKey(accessToken, code);

        long time = new Date().getTime() - now.getTime();
        logger.info("addUser耗时:" + time + "毫秒...");

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
    public Reply updatePayPassword(String token, User user) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) return reply;

        if (!verify.getUserId().equals(user.getId())) return ReplyHelper.invalidParam();

        Boolean success = core.verifySmsCode(3, user.getMobile(), user.getOption(), false);
        if (!success) return ReplyHelper.invalidCode();

        // 使用用户自己的Token
        success = core.setPayPassword(verify.getBasis(), user.getPaypw());
        long time = new Date().getTime() - now.getTime();
        logger.info("updatePayPassword耗时:" + time + "毫秒...");

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 更新用户状态(禁用/启用)
     *
     * @param token 访问令牌
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply updateUserStatus(String token, User user) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        String key = user.getInvalid() ? "DisableUser" : "EnableUser";
        Reply reply = verify.compare(key);
        if (!reply.getSuccess()) return reply;

        // 获取Token缓存中被更新用户的Token
        Token accessToken = core.getToken(user.getId());
        Boolean success = core.setInvalidStatus(accessToken, user.getId(), user.getInvalid());
        long time = new Date().getTime() - now.getTime();
        logger.info("updateUserStatus耗时:" + time + "毫秒...");

        return success ? ReplyHelper.success() : ReplyHelper.error();
    }
}
