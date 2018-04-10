package com.insight.usercenter.auth;

import com.insight.usercenter.common.Core;
import com.insight.usercenter.common.PicCode;
import com.insight.usercenter.common.Token;
import com.insight.usercenter.common.dto.RefreshToken;
import com.insight.usercenter.common.dto.TokenPackage;
import com.insight.usercenter.common.dto.UserDTO;
import com.insight.usercenter.common.dto.UserInfo;
import com.insight.usercenter.common.entity.Device;
import com.insight.usercenter.common.entity.Function;
import com.insight.usercenter.common.entity.Navigator;
import com.insight.usercenter.common.mapper.AuthMapper;
import com.insight.util.Generator;
import com.insight.util.Json;
import com.insight.util.ReplyHelper;
import com.insight.util.Util;
import com.insight.util.pojo.Reply;
import com.insight.utils.redis.CallManage;
import com.insight.utils.wechat.WeChatUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 用户鉴权服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final HttpServletRequest request;
    private final Core core;
    private final CallManage callManage;
    private final PicCode picCode;
    private final AuthMapper authMapper;
    private final Logger logger;

    /**
     * 构造函数
     *
     * @param request    自动注入的HttpServletRequest
     * @param core       自动注入的Core
     * @param callManage 自动注入的CallManage
     * @param picCode    自动注入的PicCode
     * @param authMapper 自动注入的AuthMapper
     */
    @Autowired
    public AuthServiceImpl(HttpServletRequest request, Core core, CallManage callManage, PicCode picCode, AuthMapper authMapper) {
        this.request = request;
        this.core = core;
        this.callManage = callManage;
        this.authMapper = authMapper;
        this.picCode = picCode;

        logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 获取Code
     *
     * @param account 用户登录账号
     * @param type    登录类型(0:密码登录、1:验证码登录)
     * @return Reply
     */
    @Override
    public Reply getCode(String account, int type) {
        String userId = core.getUserId(account);

        if (userId == null) {
            return ReplyHelper.notExist();
        }

        Token token = core.getToken(userId);
        if (token == null) {
            core.deleteFromRedis(account);
            return ReplyHelper.notExist();
        }

        // 限流,每用户每分钟可访问一次
        String key = Util.md5("getCode" + userId + type);
        Integer surplus = callManage.getSurplus(key, type == 0 ? 5 : 5);
        if (surplus > 0) {
            return ReplyHelper.tooOften();
        }

        // 生成Code
        Object code = core.generateCode(token, account, type);

        return ReplyHelper.success(code);
    }

    /**
     * 获取Token数据
     *
     * @param appId       应用ID
     * @param account     登录账号
     * @param signature   签名
     * @param deviceId    设备ID
     * @param deviceModel 设备型号
     * @return Reply
     */
    @Override
    public Reply getToken(String appId, String account, String signature, String deviceId, String deviceModel) {
        String code = core.getCode(signature);
        if (code == null) {
            String userId = core.getUserId(account);
            Token token = core.getToken(userId);
            if (token != null) {
                token.addFailureCount();
                core.setTokenCache(token);
                logger.warn("账号[" + account + "]正在尝试使用错误的签名请求令牌!");
            }

            return ReplyHelper.invalidPassword("验证码或密码错误！");
        }

        String userId = core.getFromRedis(code);
        if (userId == null || userId.isEmpty()) {
            return ReplyHelper.fail("缓存异常");
        }

        core.deleteFromRedis(code);
        Token token = core.getToken(userId);
        if (token == null) {
            core.deleteFromRedis(account);
            return ReplyHelper.fail("缓存异常");
        }

        if (token.userIsInvalid()) {
            core.setTokenCache(token);
            return ReplyHelper.fail("用户被禁止登录");
        }

        // 绑定设备ID到用户,并更新设备激活信息,设置租户ID和部门ID
        core.bindDeviceToUser(userId, deviceId, deviceModel);
        core.setTenantIdAndDeptId(token);

        // 创建令牌数据并返回
        TokenPackage tokens = token.creatorKey(code, appId, core.getTokenLife(appId));
        core.setTokenCache(token);

        return ReplyHelper.success(tokens);
    }

    /**
     * 通过微信授权码获取访问令牌
     *
     * @param code        微信授权码
     * @param weChatAppId 微信appId
     * @param appId       应用ID
     * @param deviceId    设备ID
     * @param deviceModel 设备型号
     * @return Reply
     */
    @Override
    public Reply getTokenWithWeChat(String code, String weChatAppId, String appId, String deviceId, String deviceModel) {
        WeChatUser weChatUser = core.getWeChatInfo(code, weChatAppId);
        if (weChatUser == null) {
            return ReplyHelper.invalidParam("微信授权失败");
        }

        String unionId = weChatUser.getUnionid();
        if (unionId == null || unionId.isEmpty()) {
            return ReplyHelper.fail("未取得微信用户的UnionID");
        }

        // 使用微信UnionID读取缓存,如用户不存在,则返回微信用户信息
        String userId = core.getUserId(unionId);
        if (userId == null || userId.isEmpty()) {
            String key = "we" + unionId;
            core.setToRedis(key, Json.toJson(weChatUser));
            return ReplyHelper.success(weChatUser, false);
        }

        Token token = core.getToken(userId);
        if (token == null) {
            core.deleteFromRedis(unionId);
            return ReplyHelper.fail("缓存异常");
        }

        if (token.userIsInvalid()) {
            core.setTokenCache(token);
            return ReplyHelper.fail("用户被禁止登录");
        }

        if (!unionId.equals(token.getUnionId())) {
            String key = "we" + unionId;
            core.setToRedis(key, Json.toJson(weChatUser));
            return ReplyHelper.success(weChatUser, false);
        }

        // 绑定设备ID和OpenID到用户,并更新设备激活信息
        core.bindDeviceToUser(userId, deviceId, deviceModel);
        core.bindOpenId(userId, weChatUser.getOpenid(), weChatAppId);

        // 创建令牌数据并返回
        TokenPackage tokens = token.creatorKey(Util.md5(code), appId, core.getTokenLife(appId), weChatUser.getOpenid());
        core.setTokenCache(token);

        return ReplyHelper.success(tokens);
    }

    /**
     * 通过微信用户信息获取访问令牌
     *
     * @param info 用户信息对象实体
     * @return Reply
     */
    @Override
    public Reply getTokenWithUserInfo(UserInfo info) {
        String mobile = info.getMobile();
        if (!core.verifySmsCode(0, mobile, info.getCode(), !info.getReplace())) {
            return ReplyHelper.invalidCode();
        }

        // 从缓存读取微信用户信息
        String unionId = info.getWeChatUser().getUnionid();
        String key = "we" + unionId;
        WeChatUser weChatUser = Json.toBean(core.getFromRedis(key), WeChatUser.class);
        if (weChatUser == null) {
            return ReplyHelper.invalidParam();
        }

        // 使用手机号查询用户,如用户不存在,则新增用户
        UserDTO user = new UserDTO();
        user.setMobile(mobile);
        if (!core.isExisted(user)) {
            user.setId(Generator.uuid());
            user.setUserType(3);
            user.setName(weChatUser.getNickname());
            user.setAccount(Generator.uuid());
            user.setOpenId(unionId);
            user.setPassword(Generator.uuid());
            user.setHeadImg(weChatUser.getHeadimgurl());
            core.addUser(user);
        }

        // 使用手机号读取缓存,如用户不存在,则返回错误信息
        String userId = core.getUserId(mobile);
        Token token = core.getToken(userId);
        if (token == null) {
            core.deleteFromRedis(mobile);
            return ReplyHelper.fail("缓存异常");
        }

        if (token.userIsInvalid()) {
            core.setTokenCache(token);
            return ReplyHelper.fail("用户被禁止登录");
        }

        // 检查Token中的UnionID是否与当前的相同,如不同则返回UnionID不同的错误信息
        if (!unionId.equals(token.getUnionId())) {
            if (token.getUnionId() != null && !info.getReplace()) {
                return ReplyHelper.success(core.getUser(userId, info.getWeChatAppId()), false);
            }

            // 更新用户信息
            user.setId(userId);
            user.setName(weChatUser.getNickname());
            user.setOpenId(unionId);
            user.setHeadImg(weChatUser.getHeadimgurl());
            core.updateUser(user);

            // 更新用户ID缓存
            core.setToRedis(unionId, userId);
            if (info.getReplace()) {
                core.deleteFromRedis(token.getUnionId());
            }

            // 更新Token数据
            token.setUnionId(unionId);
            token.setChanged();
        }

        // 绑定设备ID和OpenID到用户,并更新设备激活信息
        core.deleteFromRedis(key);
        core.bindDeviceToUser(userId, info.getDeviceId(), info.getDeviceModel());
        core.bindOpenId(userId, weChatUser.getOpenid(), info.getWeChatAppId());

        // 创建令牌数据并返回
        String appId = info.getAppId();
        TokenPackage tokens = token.creatorKey(Util.md5(Generator.uuid()), appId, core.getTokenLife(appId), weChatUser.getOpenid());
        core.setTokenCache(token);

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

        // 限流,每客户端每24小时可访问60次
        String key = Util.md5("refreshToken" + token.getId());
        Boolean limited = callManage.isLimited(key, 3600 * 24, 60);
        if (limited) {
            return ReplyHelper.fail("刷新次数已用完,请合理刷新");
        }

        // 验证令牌
        Token basis = core.getToken(token.getUserId());
        if (basis == null) {
            return ReplyHelper.invalidToken();
        }

        basis.selectKeys(token.getId());
        if (basis.isFailure() || basis.userIsInvalid() || !basis.verifyToken(token.getSecret(), 2)) {
            return ReplyHelper.invalidToken();
        }

        // 刷新令牌
        TokenPackage tokens = basis.refreshToken(token.getId());
        core.setTokenCache(basis);

        return ReplyHelper.success(tokens);
    }

    /**
     * 用户账号离线
     *
     * @param token   Token
     * @param tokenId 令牌ID
     * @return Reply
     */
    @Override
    public Reply deleteToken(Token token, String tokenId) {
        token.deleteKeys(tokenId);
        core.logOffUser(token.getUserId());
        core.setTokenCache(token);

        return ReplyHelper.success();
    }

    /**
     * 为当前用户绑定当前使用的设备信息
     *
     * @param userId 用户ID
     * @param device 设备信息
     * @return Reply
     */
    @Override
    public Reply setDevice(String userId, Device device) {
        core.bindDeviceToUser(userId, device.getId(), device.getDeviceModel());
        return ReplyHelper.success();
    }

    /**
     * 为当前Token设置租户ID及登录部门ID
     *
     * @param token    Token
     * @param tenantId 租户ID
     * @param deptId   登录部门ID
     * @return Reply
     */
    @Override
    public Reply setTenantId(Token token, String tenantId, String deptId) {
        List<String> tenantIds = core.getTenantIds(token.getUserId());
        if (!tenantIds.contains(tenantId)) {
            return ReplyHelper.invalidParam("用户未与指定的租户绑定");
        }

        // 缓存租户ID及登录部门ID
        if (!tenantId.equals(token.getTenantId())) {
            token.setTenantId(tenantId);
            token.setDeptId(deptId);
            token.setRoleList(core.getRoleList(token.getUserId(), tenantId, deptId));
            token.setChanged();
        } else {
            if (token.getDeptId() == null && deptId == null) {
                return ReplyHelper.success();
            }

            if (token.getDeptId() == null || !token.getDeptId().equals(deptId)) {
                token.setDeptId(deptId);
                token.setRoleList(core.getRoleList(token.getUserId(), tenantId, deptId));
                token.setChanged();
            }
        }

        core.setTokenCache(token);

        return ReplyHelper.success();
    }

    /**
     * 验证支付密码
     *
     * @param token       Token
     * @param payPassword 支付密码(MD5)
     * @return Reply
     */
    @Override
    public Reply verifyPayPassword(Token token, String payPassword) {
        Boolean success = token.verifyPayPassword(payPassword);
        if (success == null) {
            return ReplyHelper.fail("未设置支付密码,请先设置支付密码!");
        }

        return success ? ReplyHelper.success() : ReplyHelper.invalidParam("密码错误！");
    }

    /**
     * 获取用户导航栏
     *
     * @param token Token
     * @param appId 应用ID
     * @return Reply
     */
    @Override
    public Reply getNavigators(Token token, String appId) {
        if (!core.containsApp(token.getTenantId(), appId)) {
            return ReplyHelper.invalidParam();
        }

        List<Navigator> navigators = authMapper.getNavigators(token.getTenantId(), appId, token.getUserId(), token.getDeptId());

        return ReplyHelper.success(navigators);
    }

    /**
     * 获取业务模块的功能(及对用户的授权情况)
     *
     * @param token       Token
     * @param navigatorId 导航ID
     * @return Reply
     */
    @Override
    public Reply getModuleFunctions(Token token, String navigatorId) {
        List<Function> functions = authMapper.getModuleFunctions(token.getTenantId(), navigatorId, token.getUserId(), token.getDeptId());

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
        String fingerprint = Util.getFingerprint(request);

        // 限流,每客户端每分钟可访问一次
        String limitKey = Util.md5("getSmsCode" + fingerprint + type);
        Integer surplus = callManage.getSurplus(limitKey, 60);
        if (surplus > 0) {
            return ReplyHelper.tooOften();
        }

        // 限流,每客户端每种类型验证码无限制额度为每天5次
        limitKey = Util.md5(fingerprint + type + "5/Day");
        Boolean isLimited = callManage.isLimited(limitKey, 3600 * 24, 5);
        if (isLimited) {
            mobile = core.getFromRedis(key);
            if ((mobile == null || mobile.isEmpty())) {
                return ReplyHelper.fail("验证码次数超过限制！");
            }

            core.deleteFromRedis(key);
        }

        // 生成短信验证码,如类型为0-5,则直接发送短信.否则返回验证码
        Map<String, String> map = new HashMap<>(16);
        Object code = core.generateSmsCode(type, mobile, minutes, length);
        switch (type) {
            case 0:
                map.put("mobileCode", code.toString());
                core.sendMessage("SMS_120410150", mobile, map);
                break;
            case 1:
                map.put("code", code.toString());
                map.put("product", "爱思格");
                core.sendMessage("SMS_70105327", mobile, map);
                break;
            case 2:
                map.put("code", code.toString());
                map.put("product", "爱思格用户");
                core.sendMessage("SMS_70150487", mobile, map);
                break;
            case 3:
            case 4:
                return ReplyHelper.invalidParam();
            case 5:
                map.put("code", code.toString());
                map.put("product", "爱思格用户");
                core.sendMessage("SMS_34890236", mobile, map);
                break;
            default:
                return ReplyHelper.success(code);
        }

        return ReplyHelper.success();
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
     * @param mobile 手机号
     * @return Reply
     * @throws IOException IO异常
     */
    @Override
    public Reply getVerifyPic(String mobile) throws IOException {
        String fingerprint = Util.md5(Util.getIp(request) + request.getHeader("user-agent"));

        Object pic = picCode.generateCode(fingerprint, mobile);
        return ReplyHelper.success(pic);
    }

    /**
     * 验证图形验证答案
     *
     * @param key 手机号+验证答案的Hash值
     * @return Reply
     */
    @Override
    public Reply verifyPicCode(String key) {
        return core.redisHasKey(key) ? ReplyHelper.success() : ReplyHelper.invalidParam();
    }
}
