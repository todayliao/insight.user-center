package com.insight.usercenter.common;

import com.insight.util.Json;
import com.insight.util.ReplyHelper;
import com.insight.util.common.ApplicationContextHolder;
import com.insight.util.pojo.AccessToken;
import com.insight.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 宣炳刚
 * @date 2017/9/9
 * @remark 令牌校验帮助类
 */
public class Verify {
    private final Core core;
    private final AccessToken accessToken;
    private final Logger logger;
    private final Token basis;

    /**
     * 令牌ID
     */
    private String tokenId;

    /**
     * 构造函数
     *
     * @param token 访问令牌
     */
    public Verify(String token) {
        core = ApplicationContextHolder.getContext().getBean(Core.class);
        logger = LoggerFactory.getLogger(this.getClass());

        // 初始化参数
        accessToken = Json.toAccessToken(token);
        if (accessToken == null){
            basis = null;
            logger.error("提取验证信息失败。Token is:" + token);
            return;
        }

        tokenId = accessToken.getId();
        basis = core.getToken(accessToken.getUserId());
    }

    /**
     * 获取功能的Key(如用户ID为当前用户,则返回空)
     *
     * @param userId   用户ID
     * @param function 功能的Key
     * @return 功能的Key
     */
    public String getFunction(String userId, String function) {
        return basis != null && basis.getUserId().equals(userId) ? null : function;
    }

    /**
     * 验证Token合法性
     *
     * @return Reply Token验证结果
     */
    public Reply compare() {
        return compare(null);
    }

    /**
     * 验证Token合法性
     *
     * @param function 功能ID或URL
     * @return Reply Token验证结果
     */
    public Reply compare(String function) {
        if (basis == null) {
            return ReplyHelper.invalidToken();
        }

        // 验证令牌
        basis.selectKeys(tokenId);
        if (basis.isExpiry()) {
            return ReplyHelper.expiredToken();
        }

        if (basis.isFailure()) {
            return ReplyHelper.invalidToken();
        }

        if (basis.userIsInvalid()) {
            core.setTokenCache(basis);
            return ReplyHelper.fail("用户被禁止登录");
        }

        Boolean isPermit = basis.verifyToken(accessToken.getSecret(), 1);
        if (!isPermit) {
            core.setTokenCache(basis);
            return ReplyHelper.invalidToken();
        }
        // 无需鉴权,返回成功
        if (function == null || function.isEmpty()) {
            return ReplyHelper.success();
        }
        // 进行鉴权,返回鉴权结果
        isPermit = core.isPermit(basis, function);
        if (isPermit) {
            return ReplyHelper.success();
        }

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
     * 获取缓存中的令牌
     *
     * @return Token
     */
    public Token getBasis() {
        return basis;
    }
}
