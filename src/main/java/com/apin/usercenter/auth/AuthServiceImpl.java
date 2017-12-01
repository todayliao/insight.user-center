package com.apin.usercenter.auth;

import com.apin.usercenter.auth.dto.RefreshToken;
import com.apin.usercenter.auth.dto.TokenPackage;
import com.apin.usercenter.common.Core;
import com.apin.usercenter.common.PicCode;
import com.apin.usercenter.common.Verify;
import com.apin.usercenter.common.entity.Function;
import com.apin.usercenter.common.entity.Navigator;
import com.apin.usercenter.common.entity.Token;
import com.apin.usercenter.common.mapper.AuthMapper;
import com.apin.util.Generator;
import com.apin.util.ReplyHelper;
import com.apin.util.common.CallManage;
import com.apin.util.pojo.AccessToken;
import com.apin.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 用户鉴权服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final HttpServletRequest request;
    private final Core core;
    private final StringRedisTemplate redis;
    private final AuthMapper authMapper;
    private final CallManage callManage;
    private final PicCode picCode;
    private final Logger logger;

    /**
     * 构造函数
     *
     * @param request
     * @param core       自动注入的Core
     * @param redis      自动注入的StringRedisTemplate
     * @param callManage 自动注入的CallManage
     * @param picCode    自动注入的PicCode
     * @param authMapper 自动注入的AuthMapper
     */
    @Autowired
    public AuthServiceImpl(HttpServletRequest request, Core core, StringRedisTemplate redis, CallManage callManage, PicCode picCode, AuthMapper authMapper) {
        this.request = request;
        this.core = core;
        this.redis = redis;
        this.callManage = callManage;
        this.authMapper = authMapper;
        this.picCode = picCode;

        logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 获取Code
     *
     * @param appId   应用ID
     * @param account 用户登录账号
     * @param type    登录类型(0:密码登录、1:验证码登录)
     * @return Reply
     */
    @Override
    public Reply getCode(String appId, String account, int type) {
        String userId = core.getUserId(appId, account);

        if (userId == null) {
            return ReplyHelper.notExist();
        }

        Token token = core.getToken(userId);
        if (token == null) {
            redis.delete(account);
            return ReplyHelper.notExist();
        }

        // 限流,非公共账号每用户每分钟可访问一次
        if (token.getUserType() > 0) {
            String key = Generator.md5("getCode" + userId + type);
            Integer surplus = callManage.getSurplus(key, type == 0 ? 5 : 60);
            if (surplus > 0) {
                return ReplyHelper.tooOften(surplus);
            }
        }

        // 生成Code
        Object code = core.generateCode(token, account, type);

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
        String code = core.getCode(signature);
        if (code == null) {
            String key = Generator.md5(appId + account);
            String id = redis.opsForValue().get(key);
            if (id == null || id.isEmpty()) {
                return ReplyHelper.invalidPassword();
            }

            Token token = core.getToken(id);
            if (token != null) {
                core.addFailureCount(token);
                logger.warn("账号[" + account + "]正在尝试使用错误的签名请求令牌!");
            }

            return ReplyHelper.invalidPassword();
        }

        String userId = redis.opsForValue().get(code);
        if (userId == null) {
            return ReplyHelper.error();
        }

        redis.delete(code);
        Token token = core.getToken(userId);
        if (token == null) {
            return ReplyHelper.error();
        }

        if (core.isInvalid(token)) {
            return ReplyHelper.fail("用户被禁止登录");
        }

        // 创建令牌数据并返回
        core.initAccessToken(token);
        TokenPackage tokens = core.creatorKey(token, code, deptId);

        return ReplyHelper.success(tokens);
    }

    /**
     * 获取Token
     *
     * @param appId  应用ID
     * @param openId 微信openId
     * @return Reply
     * 正常：返回接口调用成功,通过data返回Token数据
     */
    @Override
    public Reply getTokenByOpenId(String appId, String openId) {
        String userId = core.getUserId(appId, openId);
        if (userId == null) {
            return ReplyHelper.invalidParam();
        }

        Token token = core.getToken(userId);
        if (token == null) {
            return ReplyHelper.error();
        }

        if (core.isInvalid(token)) {
            return ReplyHelper.fail("用户被禁止登录");
        }

        // 创建令牌数据并返回
        core.initAccessToken(token);
        TokenPackage tokens = core.creatorKey(token, Generator.uuid(), null);

        return ReplyHelper.success(tokens);
    }

    /**
     * 刷新访问令牌过期时间
     *
     * @param token 刷新令牌
     * @return Reply
     */
    @Override
    public Reply refreshToken(RefreshToken token) {
        if (token == null) {
            return ReplyHelper.invalidToken();
        }

        // 限流,每客户端每5分钟可访问一次
        String key = Generator.md5("refreshToken" + token.getId());
        Integer surplus = callManage.getSurplus(key, 300);
        if (surplus > 0) {
            return ReplyHelper.tooOften(surplus);
        }

        // 验证令牌
        key = token.getSecret();
        Token basis = core.getToken(token.getUserId());
        if (basis == null || !core.verifyToken(basis, key, 2)) {
            return ReplyHelper.invalidToken();
        }

        // 刷新令牌
        Date expiryTime = core.refreshToken(basis);

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
        Verify verify = new Verify(token);

        return verify.compare(function);
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

        // 验证令牌
        Verify verify = new Verify(token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) {
            return reply;
        }

        Boolean success = core.verifyPayPassword(verify.getBasis(), payPassword);
        if (success == null) {
            return ReplyHelper.fail("未设置支付密码,请先设置支付密码!");
        }

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

        // 验证令牌
        Verify verify = new Verify(token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) {
            return reply;
        }

        List<Navigator> navigators = authMapper.getNavigators(verify.getAppId(), verify.getUserId(), verify.getDeptId());

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

        // 验证令牌
        Verify verify = new Verify(token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) {
            return reply;
        }

        List<Function> functions = authMapper.getModuleFunctions(moduleId, verify.getUserId(), verify.getDeptId());

        return ReplyHelper.success(functions);
    }

    /**
     * 生成短信验证码
     *
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码;5:修改手机号)
     * @param key     手机号或手机号+验证答案的Hash值
     * @param minutes 验证码有效时长(分钟)
     * @param length  验证码长度
     * @return Reply
     */
    @Override
    public Reply getSmsCode(int type, String key, int minutes, int length) {
        String mobile = key;
        if (request.getAttribute("client") != null) {
            String clientKey = request.getAttribute("client").toString();
            String limitKey = Generator.md5(clientKey + type + "5/Day");

            // 限流,每客户端每种类型验证码无限制额度为每天5次
            Boolean isLimited = callManage.isLimited(limitKey, 24 * 60 * 60, 5);
            if (isLimited) {
                mobile = redis.opsForValue().get(key);
                if ((mobile == null || mobile.isEmpty())) {
                    return ReplyHelper.fail("验证码次数超过限制！");
                }

                redis.delete(key);
            }

            // 限流,每客户端每分钟可访问一次
            limitKey = Generator.md5("getSmsCode" + clientKey + type);
            Integer surplus = callManage.getSurplus(limitKey, 60);
            if (surplus > 0) {
                return ReplyHelper.tooOften(surplus);
            }
        }

        // 生成短信验证码
        Object code = core.generateSmsCode(type, mobile, minutes, length);

        return ReplyHelper.success(code);
    }

    /**
     * 验证短信验证码
     *
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码;5:修改手机号)
     * @param mobile  手机号
     * @param code    验证码
     * @param isCheck 是否检验模式(true:检验模式,验证后验证码不失效;false:验证模式,验证后验证码失效)
     * @return Reply
     */
    @Override
    public Reply verifySmsCode(int type, String mobile, String code, Boolean isCheck) {
        Boolean success = core.verifySmsCode(type, mobile, code, isCheck);

        return success ? ReplyHelper.success() : ReplyHelper.invalidParam("验证码错误！");
    }

    /**
     * 获取图形验证图片
     *
     * @param token  访问令牌
     * @param mobile 手机号
     * @return Reply
     */
    @Override
    public Reply getVerifyPic(AccessToken token, String mobile) throws IOException {
        return ReplyHelper.success(picCode.generateCode(token.getAppId(), mobile));
    }

    /**
     * 验证图形验证答案
     *
     * @param key 手机号+验证答案的Hash值
     * @return Reply
     */
    @Override
    public Reply verifyPicCode(String key) {
        return redis.hasKey(key) ? ReplyHelper.success() : ReplyHelper.invalidParam();
    }
}
