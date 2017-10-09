package com.apin.usercenter.common;

import com.apin.usercenter.component.Core;
import com.apin.usercenter.common.entity.Token;
import com.apin.util.Generator;
import com.apin.util.JsonUtils;
import com.apin.util.ReplyHelper;
import com.apin.util.common.CallManage;
import com.apin.util.pojo.AccessToken;
import com.apin.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author 宣炳刚
 * @date 2017/9/9
 * @remark 令牌校验帮助类
 */
public class Verify {
    private final Core core;
    private final CallManage callManage;
    private final AccessToken accessToken;
    private final Logger logger;

    private Token basis;

    // 令牌ID
    private String tokenId;

    // 应用ID
    private String appId;

    // 账户ID
    private String accountId;

    // 用户ID
    private String userId;

    // 用户名
    private String userName;

    // 登录部门ID
    private String deptId;

    /**
     * 构造函数
     *
     * @param core  Core
     * @param redis StringRedisTemplate
     * @param token 访问令牌
     */
    public Verify(Core core, StringRedisTemplate redis, String token) {
        this.core = core;

        logger = LoggerFactory.getLogger(this.getClass());
        callManage = CallManage.getInstance(redis);

        // 初始化参数
        accessToken = JsonUtils.toAccessToken(token);
        tokenId = accessToken.getId();
        userId = accessToken.getUserId();
        userName = accessToken.getUserName();
        deptId = accessToken.getDeptId();
    }

    /**
     * 验证Token合法性
     *
     * @return Reply Token验证结果
     */
    public Reply compare() {
        return compare(0);
    }

    /**
     * 验证Token合法性
     *
     * @param function 功能ID或URL
     * @return Reply Token验证结果
     */
    public Reply compare(String function) {
        return compare(function, 0);
    }

    /**
     * 验证Token合法性
     *
     * @param limit 接口调用时间间隔秒数
     * @return Reply Token验证结果
     */
    public Reply compare(int limit) {
        return compare(null, limit);
    }

    /**
     * 验证Token合法性
     *
     * @param function 功能ID或URL
     * @param limit    接口调用时间间隔秒数
     * @return Reply Token验证结果
     */
    public Reply compare(String function, int limit) {
        if (accessToken == null) return ReplyHelper.invalidToken();

        // 验证令牌
        basis = core.getToken(userId);
        if (basis == null || core.isFailure(basis) || core.isInvalid(basis)) return ReplyHelper.invalidToken();

        if (core.isExpiry(basis)) return ReplyHelper.expiredToken();

        if (limit > 0) {
            String key = Generator.md5(tokenId + function);
            Integer surplus = callManage.getSurplus(key, limit);
            if (surplus > 0) return ReplyHelper.overload();
        }

        Boolean isPermit = core.verifyToken(basis, accessToken.getSecret(), 1);
        if (!isPermit) return ReplyHelper.invalidToken();

        appId = basis.getAppId();
        accountId = basis.getAccountId();

        // 无需鉴权,返回成功
        if (function == null || function.isEmpty()) return ReplyHelper.success();

        // 进行鉴权,返回鉴权结果
        isPermit = core.isPermit(appId, userId, deptId, function);
        if (isPermit) return ReplyHelper.success();

        logger.warn("用户『" + basis.getAccount() + "』试图使用未授权的功能:" + function);
        return ReplyHelper.noAuth();
    }

    /**
     * 获取令牌ID
     *
     * @return 令牌ID
     */
    public String getTokenId() {
        return tokenId;
    }

    /**
     * 获取应用ID
     *
     * @return
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 获取所属账户ID
     *
     * @return 所属账户ID
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 获取当前登录部门ID
     *
     * @return 当前登录部门ID
     */
    public String getDeptId() {
        return deptId;
    }

    /**
     * 获取缓存中的令牌
     *
     * @return Token
     */
    public Token getBasis() {
        return basis;
    }

}
