package com.apin.usercenter.auth;

import com.apin.usercenter.auth.dto.RefreshToken;
import com.apin.usercenter.auth.dto.TokenPackage;
import com.apin.usercenter.common.Verify;
import com.apin.usercenter.common.entity.Function;
import com.apin.usercenter.common.entity.Navigator;
import com.apin.usercenter.common.mapper.AuthMapper;
import com.apin.usercenter.component.Core;
import com.apin.usercenter.common.entity.Token;
import com.apin.util.Generator;
import com.apin.util.JsonUtils;
import com.apin.util.ReplyHelper;
import com.apin.util.common.CallManage;
import com.apin.util.encrypt.Base64Encryptor;
import com.apin.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 用户鉴权服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final Core core;
    private final StringRedisTemplate redis;
    private final AuthMapper authMapper;
    private final CallManage callManage;
    private final Logger logger;

    /**
     * 构造函数
     *
     * @param core       自动注入的Core
     * @param redis      自动注入的StringRedisTemplate
     * @param authMapper 自动注入的AuthMapper
     */
    @Autowired
    public AuthServiceImpl(Core core, StringRedisTemplate redis, AuthMapper authMapper) {
        this.core = core;
        this.redis = redis;
        this.authMapper = authMapper;

        callManage = CallManage.getInstance(redis);
        logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 获取Code
     *
     * @param appId   应用ID
     * @param account 用户登录账号
     * @param type    登录类型(0:密码登录、1:验证码登录)
     * @param appName 应用名称
     * @return Reply
     */
    @Override
    public Reply getCode(String appId, String account, int type, String appName) {
        Date now = new Date();
        String userId = core.getUserId(appId, account);

        if (userId == null) return ReplyHelper.notExist();

        Token token = core.getToken(userId);
        if (token == null) {
            redis.delete(account);
            return ReplyHelper.notExist();
        }

        // 限流,非公共账号每用户每分钟可访问一次
        if (token.getUserType() > 0) {
            String key = Generator.md5("getCode" + userId);
            Integer surplus = callManage.getSurplus(key, 60);
            if (surplus > 0) return ReplyHelper.tooOften(surplus);

        }

        String code = core.generateCode(token, type, appName);

        long time = new Date().getTime() - now.getTime();
        logger.info("getCode耗时:" + time + "毫秒...");

        return ReplyHelper.success(code);
    }

    /**
     * 获取Token数据
     *
     * @param appId     应用ID
     * @param account   登录账号
     * @param signature 签名
     * @param deptId    登录部门ID
     * @return Reply
     */
    @Override
    public Reply getToken(String appId, String account, String signature, String deptId) {
        Date now = new Date();
        String code = core.getCode(signature);
        if (code == null) return ReplyHelper.invalidPassword();

        String userId = core.getUserId(appId, account);
        if (userId == null) return ReplyHelper.error();

        Token token = core.getToken(userId);
        if (token == null) return ReplyHelper.error();

        if (core.isInvalid(token)) return ReplyHelper.fail("用户被禁止登录");

        core.initAccessToken(token);
        TokenPackage tokens = core.creatorKey(token, code, deptId);

        long time = new Date().getTime() - now.getTime();
        logger.info("getToken耗时:" + time + "毫秒...");

        return ReplyHelper.success(tokens);
    }

    /**
     * 刷新访问令牌过期时间
     *
     * @param token 刷新令牌字符串
     * @return Reply
     */
    @Override
    public Reply refreshToken(String token) {
        Date now = new Date();
        RefreshToken refreshToken = JsonUtils.toBean(Base64Encryptor.decodeToString(token), RefreshToken.class);
        if (refreshToken == null) return ReplyHelper.invalidToken();

        // 限流,每客户端每5分钟可访问一次
        String key = Generator.md5("refreshToken" + refreshToken.getId());
        Integer surplus = callManage.getSurplus(key, 300);
        if (surplus > 0) return ReplyHelper.tooOften(surplus);

        // 验证令牌
        key = refreshToken.getSecret();
        Token basis = core.getToken(refreshToken.getUserId());
        if (basis == null || !core.verifyToken(basis, key, 2)) return ReplyHelper.invalidToken();

        Date expiryTime = core.refreshToken(basis);

        long time = new Date().getTime() - now.getTime();
        logger.info("refreshToken耗时:" + time + "毫秒...");

        return ReplyHelper.success(expiryTime);
    }

    /**
     * 用户身份验证及鉴权(需要传入function)
     *
     * @param token    访问令牌字符串
     * @param function 功能ID或URL
     * @return Reply
     */
    @Override
    public Reply verifyToken(String token, String function) {
        Date now = new Date();

        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare(function);

        long time = new Date().getTime() - now.getTime();
        logger.info("verifyToken耗时:" + time + "毫秒...");

        return reply;
    }

    /**
     * 验证支付密码
     *
     * @param token       访问令牌
     * @param payPassword 支付密码(MD5)
     * @return Reply
     */
    @Override
    public Reply verifyPayPassword(String token, String payPassword) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) return reply;

        Boolean success = core.verifyPayPassword(verify.getBasis(), payPassword);
        if (success == null) return ReplyHelper.fail("未设置支付密码,请先设置支付密码!");

        long time = new Date().getTime() - now.getTime();
        logger.info("verifyPayPassword耗时:" + time + "毫秒...");

        return success ? ReplyHelper.success() : ReplyHelper.invalidParam("密码错误！");
    }

    /**
     * 获取用户导航栏
     *
     * @param token 访问令牌
     * @return Reply
     */
    @Override
    public Reply getNavigators(String token) {
        Date now = new Date();

        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) return reply;

        List<Navigator> navigators = authMapper.getNavigators(verify.getAppId(), verify.getUserId(), verify.getDeptId());
        long time = new Date().getTime() - now.getTime();
        logger.info("getNavigators耗时:" + time + "毫秒...");

        return ReplyHelper.success(navigators);
    }

    /**
     * 获取业务模块的功能(及对用户的授权情况)
     *
     * @param token    访问令牌
     * @param moduleId 业务模块ID
     * @return Reply
     */
    @Override
    public Reply getModuleFunctions(String token, String moduleId) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) return reply;

        List<Function> functions = authMapper.getModuleFunctions(moduleId, verify.getUserId(), verify.getDeptId());
        long time = new Date().getTime() - now.getTime();
        logger.info("getModuleFunctions耗时:" + time + "毫秒...");

        return ReplyHelper.success(functions);
    }

    /**
     * 生成短信验证码
     *
     * @param token   访问令牌
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码;5:修改手机号)
     * @param mobile  手机号
     * @param minutes 验证码有效时长(分钟)
     * @param length  验证码长度
     * @return Reply
     */
    @Override
    public Reply getSmsCode(String token, int type, String mobile, int minutes, int length) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) return reply;

        // 限流,每客户端每分钟可访问一次
        String key = Generator.md5("getSmsCode" + verify.getTokenId());
        Integer surplus = callManage.getSurplus(key, 60);
        if (surplus > 0) return ReplyHelper.tooOften(surplus);

        String code = core.generateSmsCode(type, mobile, minutes, length);
        long time = new Date().getTime() - now.getTime();
        logger.info("getSmsCode耗时:" + time + "毫秒...");

        return ReplyHelper.success(code);
    }

    /**
     * 验证短信验证码
     *
     * @param token   访问令牌
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码;5:修改手机号)
     * @param mobile  手机号
     * @param code    验证码
     * @param isCheck 是否检验模式(true:检验模式,验证后验证码不失效;false:验证模式,验证后验证码失效)
     * @return Reply
     */
    @Override
    public Reply verifySmsCode(String token, int type, String mobile, String code, Boolean isCheck) {
        Date now = new Date();

        // 验证令牌
        Verify verify = new Verify(core, redis, token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) return reply;

        Boolean success = core.verifySmsCode(type, mobile, code, isCheck);
        long time = new Date().getTime() - now.getTime();
        logger.info("verifySmsCode耗时:" + time + "毫秒...");

        return success ? ReplyHelper.success() : ReplyHelper.invalidParam("验证码错误！");
    }
}
