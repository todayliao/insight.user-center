package com.insight.usercenter.user;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.usercenter.common.Core;
import com.insight.usercenter.common.Token;
import com.insight.usercenter.common.dto.TokenPackage;
import com.insight.usercenter.common.dto.UserDTO;
import com.insight.usercenter.common.entity.User;
import com.insight.usercenter.common.mapper.TenantMapper;
import com.insight.usercenter.common.mapper.UserMapper;
import com.insight.usercenter.user.dto.QueryUserDTO;
import com.insight.util.Generator;
import com.insight.util.ReplyHelper;
import com.insight.util.Util;
import com.insight.util.encrypt.Encryptor;
import com.insight.util.pojo.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private final TenantMapper tenantMapper;

    /**
     * 构造函数
     *
     * @param core         自动注入的Core
     * @param userMapper   自动注入的UserMapper
     * @param tenantMapper 自动注入的TenantMapper
     */
    @Autowired
    public UserServicesImpl(Core core, UserMapper userMapper, TenantMapper tenantMapper) {
        this.core = core;
        this.userMapper = userMapper;
        this.tenantMapper = tenantMapper;
    }

    /**
     * 根据条件查询用户集合(分页)
     *
     * @param user 用户查询对象实体
     * @return 用户集合
     */
    @Override
    public Reply getUsers(QueryUserDTO user) {
        PageHelper.startPage(user.getPage(), user.getPageSize());
        List<User> users = userMapper.getUsers(user);
        PageInfo<User> pageInfo = new PageInfo<>(users);

        return ReplyHelper.success(users, pageInfo.getTotal());
    }

    /**
     * 根据ID查询用户
     *
     * @param token Token
     * @param id    用户ID
     * @return 用户实体数据
     */
    @Override
    public Reply getUser(Token token, String id) {
        User user = userMapper.getUserById(id);
        if (user == null) {
            return ReplyHelper.notExist();
        }

        return ReplyHelper.success(user);
    }

    /**
     * 根据ID查询用户
     *
     * @param token Token
     * @param id    用户ID
     * @param appId 微信AppID
     * @return Reply
     */
    @Override
    public Reply getUser(Token token, String id, String appId) {
        User user = userMapper.getUserWithAppId(id, appId);
        if (user == null) {
            return ReplyHelper.notExist();
        }

        return ReplyHelper.success(user);
    }

    /**
     * 检验用户信息是否存在
     *
     * @param user User实体
     * @return Reply
     */
    @Override
    public Reply exist(UserDTO user) {
        return ReplyHelper.success(core.isExisted(user));
    }

    /**
     * 新增用户
     *
     * @param token Token
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply addUser(Token token, UserDTO user) {

        // 验证用户是否存在
        if (core.isExisted(user)) {
            return ReplyHelper.accountExist();
        }

        // 持久化用户对象
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(Generator.uuid());
        }

        Integer count = userMapper.addUser(user);
        count += tenantMapper.addUserToTenant(token.getTenantId(), user.getId());

        return count > 0 ? ReplyHelper.success() : ReplyHelper.error();
    }

    /**
     * 注册用户
     *
     * @param user User实体,来自Body
     * @return Reply
     */
    @Override
    public Reply signUp(UserDTO user) {
        String appId = user.getAppId();

        // 验证短信验证码
        if (!core.verifySmsCode(1, user.getMobile(), user.getOption())) {
            return ReplyHelper.invalidCode();
        }

        // 验证用户是否存在
        if (core.isExisted(user)) {
            return ReplyHelper.accountExist();
        }

        // 持久化用户对象
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(Generator.uuid());
        }

        userMapper.addUser(user);
        String userId = core.getUserId(user.getAccount());
        if (userId == null) {
            return ReplyHelper.error();
        }

        // 初始化令牌数据并返回，实现自动登录功能
        Token token = core.getToken(user.getId());
        if (token == null) {
            return ReplyHelper.error();
        }

        // 绑定设备到用户,并更新设备激活信息
        core.setTenantIdAndDeptId(token);

        TokenPackage tokens = token.creatorKey(Generator.uuid(), appId, core.getTokenLife(appId));
        core.setTokenCache(token);

        return ReplyHelper.success(tokens);
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return Reply
     */
    @Override
    public Reply deleteUser(String userId) {
        Integer count = userMapper.deleteUserById(userId);
        if (count <= 0) {
            return ReplyHelper.error();
        }

        // 获取Token缓存中被删除用户的Token
        Token token = core.getToken(userId);
        if (token != null) {
            core.deleteFromRedis(userId);
            core.deleteFromRedis(token.getAccount());

            String mobile = token.getMobile();
            if (mobile != null && !mobile.isEmpty()) {
                core.deleteFromRedis(mobile);
            }

            String openId = token.getUnionId();
            if (openId != null && !openId.isEmpty()) {
                core.deleteFromRedis(openId);
            }

            String email = token.getEmail();
            if (email != null && !email.isEmpty()) {
                core.deleteFromRedis(email);
            }
        }

        return ReplyHelper.success();
    }

    /**
     * 更新用户信息(名称及备注)
     *
     * @param user User实体
     * @return Reply
     */
    @Override
    public Reply updateUserInfo(UserDTO user) {
        String userId = user.getId();
        if (userId == null || userId.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        String userName = user.getName();
        if (userName == null || userName.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        Integer count = userMapper.updateUserInfo(userId, user.getCode(), userName, user.getRemark());
        if (count <= 0) {
            return ReplyHelper.error();
        }

        // 更新Token缓存
        Token token = core.getToken(userId);
        if (token != null && !token.getUserName().equals(userName)) {
            token.setUserName(userName);
            token.setChanged();
            core.setTokenCache(token);
        }

        return ReplyHelper.success();
    }

    /**
     * 更新用户类型
     *
     * @param user User实体
     * @return Reply
     */
    @Override
    public Reply updateUserType(UserDTO user) {
        String userId = user.getId();
        if (userId == null || userId.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        Integer userType = user.getUserType();
        if (userType == null) {
            return ReplyHelper.invalidParam();
        }

        Token token = core.getToken(userId);
        if (token != null && token.getUserType().equals(userType)) {
            return ReplyHelper.success();
        }

        // 保存用户类型到数据库
        Integer count = userMapper.updateUserType(userId, userType);
        if (count <= 0) {
            return ReplyHelper.error();
        }

        // 更新Token缓存
        if (token != null) {
            token.setUserType(userType);
            token.setChanged();
            core.setTokenCache(token);
        }

        return ReplyHelper.success();
    }

    /**
     * 更新用户绑定手机号
     *
     * @param user User实体
     * @return Reply
     */
    @Override
    public Reply updateUserMobile(UserDTO user) {
        String userId = user.getId();
        if (userId == null || userId.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        String mobile = user.getMobile();
        if (mobile != null && !mobile.matches("^1[3-9]\\d{9}")) {
            return ReplyHelper.invalidParam("错误的手机号码");
        }

        // 验证用户是否存在
        if (mobile != null && core.isExisted(user)) {
            return ReplyHelper.accountExist();
        }

        Token token = core.getToken(userId);
        if (token != null && !token.getBuiltIn() && token.getUserId().equals(user.getId())) {
            String old = token.getMobile();

            // 验证旧手机号
            if (old != null && !old.isEmpty() && !core.verifySmsCode(5, old, user.getCode())) {
                return ReplyHelper.invalidCode();
            }

            // 验证新手机号
            if (!core.verifySmsCode(0, mobile, user.getOption())) {
                return ReplyHelper.invalidCode();
            }
        }

        if (token != null && token.getMobile() != null && token.getMobile().equals(mobile)) {
            return ReplyHelper.success();
        }

        // 保存数据到数据库
        Integer count = userMapper.updateMobile(userId, mobile);
        if (count <= 0) {
            return ReplyHelper.error();
        }

        // 更新用户ID缓存
        if (token != null) {
            String old = token.getMobile();
            if (mobile != null) {
                core.setToRedis(mobile, userId);
            }

            if (old != null) {
                core.deleteFromRedis(old);
            }

            // 更新Token缓存
            token.setMobile(mobile);
            token.setChanged();
            core.setTokenCache(token);
        }

        return ReplyHelper.success();
    }

    /**
     * 更新用户绑定邮箱
     *
     * @param user User实体
     * @return Reply
     */
    @Override
    public Reply updateUserEmail(UserDTO user) {
        String userId = user.getId();
        if (userId == null || userId.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        String email = user.getEmail();
        if (email != null && !email.matches("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")) {
            return ReplyHelper.invalidParam("错误的E-Mail格式");
        }

        // 验证用户是否存在
        if (core.isExisted(user)) {
            return ReplyHelper.accountExist();
        }

        Token token = core.getToken(userId);
        if (token != null && token.getEmail() != null && token.getEmail().equals(email)) {
            return ReplyHelper.success();
        }

        // 保存数据到数据库
        Integer count = userMapper.updateEmail(userId, email);
        if (count <= 0) {
            return ReplyHelper.error();
        }

        // 更新用户ID缓存
        if (token != null) {
            String old = token.getEmail();
            if (old != null) {
                core.deleteFromRedis(old);
            }

            if (email != null) {
                core.setToRedis(email, userId);
            }

            // 更新Token缓存
            token.setEmail(email);
            token.setChanged();
            core.setTokenCache(token);
        }

        return ReplyHelper.success();
    }

    /**
     * 更新登录密码
     *
     * @param token Token
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply updatePassword(Token token, UserDTO user) {
        String userId = user.getId();
        if (userId == null || userId.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            return ReplyHelper.invalidParam("密码不能为空");
        }

        String key = password.length() > 32 ? Encryptor.rsaDecrypt(password, token.getPrivateKey()) : password;
        Token basis = core.getToken(user.getId());
        if (token.getUserId().equals(userId)) {
            String option = user.getOption();
            if (option == null || option.isEmpty()) {
                return ReplyHelper.invalidParam();
            }

            String old = option.length() > 32 ? Encryptor.rsaDecrypt(option, token.getPrivateKey()) : option;
            if (!basis.getPassword().equals(old)) {
                return ReplyHelper.invalidParam("错误的原密码!");
            }

            if (basis.getPassword().equals(key)) {
                return ReplyHelper.invalidParam("新密码不能与原密码相同");
            }
        }

        // 保存密码到数据库
        Integer count = userMapper.updatePassword(userId, password);
        if (count <= 0) {
            return ReplyHelper.error();
        }

        // 更新Token缓存
        if (basis != null) {
            basis.setPassword(key);
            basis.setChanged();
            core.setTokenCache(basis);
        }

        return ReplyHelper.success();
    }

    /**
     * 重置登录密码
     *
     * @param user User实体
     * @return Reply
     */
    @Override
    public Reply resetPassword(UserDTO user) {
        String appId = user.getAppId();

        String mobile = user.getMobile();
        if (mobile == null || !mobile.matches("^1[3-5|8]\\d{9}")) {
            return ReplyHelper.invalidParam("错误的手机号码");
        }

        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            return ReplyHelper.invalidParam("密码不能为空");
        }

        // 验证短信验证码
        if (!core.verifySmsCode(2, mobile, user.getOption())) {
            return ReplyHelper.invalidCode();
        }

        String userId = core.getUserId(mobile);
        if (userId == null) {
            return ReplyHelper.notExist();
        }

        Token token = core.getToken(userId);
        if (token == null) {
            return ReplyHelper.error();
        }

        if (token.userIsInvalid()) {
            core.setTokenCache(token);
            return ReplyHelper.fail("用户被禁止登录");
        }

        String key = password.length() > 32 ? Encryptor.rsaDecrypt(password, token.getPrivateKey()) : password;
        if (token.getPassword().equals(key)) {
            return ReplyHelper.invalidParam("新密码不能与原密码相同");
        }

        // 保存密码到数据库
        Integer count = userMapper.updatePassword(userId, key);
        if (count <= 0) {
            return ReplyHelper.error();
        }

        // 绑定设备到用户,并更新设备激活信息
        core.setTenantIdAndDeptId(token);

        // 更新Token缓存
        token.setPassword(key);
        token.setChanged();

        // 生成令牌数据
        TokenPackage tokens = token.creatorKey(Generator.uuid(), appId, core.getTokenLife(appId));
        core.setTokenCache(token);

        return ReplyHelper.success(tokens);
    }

    /**
     * 更新支付密码
     *
     * @param token Token
     * @param user  User实体
     * @return Reply
     */
    @Override
    public Reply updatePayPassword(Token token, UserDTO user) {
        String userId = token.getUserId();
        if (!userId.equals(user.getId())) {
            return ReplyHelper.invalidParam();
        }

        String password = user.getPaypw();
        if (password == null || password.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        // 验证短信验证码
        if (!core.verifySmsCode(3, token.getMobile(), user.getOption(), false)) {
            return ReplyHelper.invalidCode();
        }

        String key = Util.md5(token.getUserId() + password);
        if (token.getPayPassword() != null && token.getPayPassword().equals(key)) {
            return ReplyHelper.success();
        }

        // 保存支付密码到数据库
        Integer count = userMapper.updatePayPassword(token.getUserId(), key);
        if (count <= 0) {
            return ReplyHelper.error();
        }

        // 更新Token缓存
        token.setPayPassword(key);
        token.setChanged();
        core.setTokenCache(token);

        return ReplyHelper.success();
    }

    /**
     * 更新用户状态(禁用/启用)
     *
     * @param user User实体
     * @return Reply
     */
    @Override
    public Reply updateUserStatus(UserDTO user) {
        String userId = user.getId();
        if (userId == null || userId.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        Boolean isInvalid = user.getInvalid();
        if (isInvalid == null) {
            return ReplyHelper.invalidParam();
        }

        User data = userMapper.getUserById(user.getId());
        if (data == null) {
            return ReplyHelper.notExist();
        }

        if (data.getBuiltin()) {
            return ReplyHelper.fail("不能禁用内置用户");
        }

        Token token = core.getToken(userId);
        if (token != null && token.getInvalid().equals(isInvalid)) {
            return ReplyHelper.success();
        }

        // 保存用户失效状态到数据库
        Integer count = userMapper.updateStatus(userId, isInvalid);
        if (count <= 0) {
            return ReplyHelper.error();
        }

        // 更新Token缓存
        if (token != null) {
            token.setInvalid(isInvalid);
            token.setChanged();
            core.setTokenCache(token);
        }

        return ReplyHelper.success();
    }
}
