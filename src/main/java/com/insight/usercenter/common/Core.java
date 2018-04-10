package com.insight.usercenter.common;

import com.insight.usercenter.common.dto.UserDTO;
import com.insight.usercenter.common.entity.Function;
import com.insight.usercenter.common.entity.User;
import com.insight.usercenter.common.mapper.AuthMapper;
import com.insight.usercenter.common.mapper.UserMapper;
import com.insight.util.Generator;
import com.insight.util.Json;
import com.insight.util.Util;
import com.insight.utils.message.Message;
import com.insight.utils.wechat.WeChatHelper;
import com.insight.utils.wechat.WeChatUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 用户身份验证核心类(组件类)
 * @since 3、管理签名-Code对应关系缓存
 */
@Component
public class Core {
    private final Logger logger;
    private final StringRedisTemplate redis;
    private final ThreadPool pool;
    private final AuthMapper authMapper;
    private final UserMapper userMapper;
    private final WeChatHelper weChatHelper;

    /**
     * 构造方法
     *
     * @param redis        自动注入的StringRedisTemplate
     * @param pool         自动注入的ThreadPool
     * @param authMapper   自动注入的AuthMapper
     * @param userMapper   自动注入的UserMapper
     * @param weChatHelper 自动注入的WeChatHelper
     */
    @Autowired
    public Core(StringRedisTemplate redis, ThreadPool pool, AuthMapper authMapper, UserMapper userMapper, WeChatHelper weChatHelper) {
        this.redis = redis;
        this.pool = pool;
        this.authMapper = authMapper;
        this.userMapper = userMapper;
        this.weChatHelper = weChatHelper;

        logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 根据用户登录账号获取Account缓存中的用户ID
     *
     * @param account 登录账号(账号、手机号、E-mail、openId)
     * @return 用户ID
     */
    public String getUserId(String account) {
        String key = "ID:" + account;
        String userId = getFromRedis(key);
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }

        synchronized ( this ) {
            userId = getFromRedis(account);
            if (userId != null && !userId.isEmpty()) {
                return userId;
            }

            User user = userMapper.getUser(account);
            if (user == null) {
                return null;
            }

            // 缓存用户ID到Redis
            userId = user.getId();
            key = "ID:" + user.getAccount();
            setToRedis(key, userId);

            String mobile = user.getMobile();
            if (mobile != null && !mobile.isEmpty()) {
                key = "ID:" + mobile;
                setToRedis(key, userId);
            }

            String openId = user.getOpenId();
            if (openId != null && !openId.isEmpty()) {
                key = "ID:" + openId;
                setToRedis(key, userId);
            }

            String mail = user.getEmail();
            if (mail != null && !mail.isEmpty()) {
                key = "ID:" + mail;
                setToRedis(key, userId);
            }

            Token token = new Token(user);
            setTokenCache(token);

            return userId;
        }
    }

    /**
     * 生成Code,缓存后返回
     *
     * @param token   Token
     * @param account 登录账号
     * @param type    登录类型(0:密码登录、1:验证码登录)
     * @return Code
     */
    public String generateCode(Token token, String account, int type) {
        String key;
        int seconds = 3;
        switch (type) {
            case 0:
                key = Util.md5(account + token.getPassword());
                break;
            case 1:
                // 生成短信验证码(5分钟内有效)并发送
                String mobile = token.getMobile();
                if (mobile == null || mobile.isEmpty()) {
                    return null;
                }

                seconds = 60 * 5;
                Map<String, String> map = new HashMap<>(16);

                String smsCode = generateSmsCode(4, mobile, 5, 4);
                key = Util.md5(mobile + Util.md5(smsCode));
                map.put("code", smsCode);

                sendMessage("SMS_102865037", mobile, map);
                break;
            default:
                // Invalid type! You guess, you guess, you guess. (≧∇≦)
                key = Util.md5(Generator.uuid());
                break;
        }

        String code = Generator.uuid();
        String signature = Util.md5(key + code);

        // 缓存签名-Code,以及Code-用户ID.
        setToRedis(signature, code, seconds, TimeUnit.SECONDS);
        setToRedis(code, token.getUserId());

        return code;
    }

    /**
     * 生成短信验证码
     *
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码)
     * @param mobile  手机号
     * @param minutes 验证码有效时长(分钟)
     * @param length  验证码长度
     * @return 短信验证码
     */
    public String generateSmsCode(int type, String mobile, int minutes, int length) {
        String code = randomString(length);
        logger.info("为手机号【" + mobile + "】生成了类型为" + type + "的验证码:" + code + ",有效时间:" + minutes + "分钟.");
        String key = Util.md5(type + mobile + code);
        if (type == 4) {
            return code;
        }

        setToRedis(key, code, minutes, TimeUnit.MINUTES);
        return code;
    }

    /**
     * 发送短信
     *
     * @param templateId 短信模板ID
     * @param mobile     手机号
     * @param map        模板参数Map
     */
    public void sendMessage(String templateId, String mobile, Map<String, String> map) {
        Message message = new Message();
        message.setTemplate(templateId);
        message.setParams(map);
        message.setReceiverString(mobile);
        pool.sendSms(message);
    }

    /**
     * 验证短信验证码
     *
     * @param type   验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码)
     * @param mobile 手机号
     * @param code   验证码
     * @return 是否通过验证
     */
    public Boolean verifySmsCode(int type, String mobile, String code) {
        return verifySmsCode(type, mobile, code, false);
    }

    /**
     * 验证短信验证码
     *
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码)
     * @param mobile  手机号
     * @param code    验证码
     * @param isCheck 是否检验模式(true:检验模式,验证后验证码不失效;false:验证模式,验证后验证码失效)
     * @return 是否通过验证
     */
    public Boolean verifySmsCode(int type, String mobile, String code, Boolean isCheck) {
        String key = Util.md5(type + mobile + code);
        Boolean isExisted = redisHasKey(key);
        if (!isExisted || isCheck) {
            return isExisted;
        }

        deleteFromRedis(key);
        return true;
    }

    /**
     * 通过签名获取Code
     *
     * @param sign 签名
     * @return 签名对应的Code
     */
    public String getCode(String sign) {
        String code = getFromRedis(sign);
        if (code == null || code.isEmpty()) {
            return null;
        }

        deleteFromRedis(sign);
        return code;
    }

    /**
     * 根据用户ID获取Token缓存中的Token
     *
     * @param userId 用户ID
     * @return Token(可能为null)
     */
    public Token getToken(String userId) {
        String key = "Token:" + userId;
        String json = getFromRedis(key);
        if (json == null || json.isEmpty()) {
            return null;
        }

        return Json.toBean(json, Token.class);
    }

    /**
     * 查询指定ID的应用的令牌生命周期小时数
     *
     * @param appId 应用ID
     * @return 应用的令牌生命周期小时数
     */
    public Integer getTokenLife(String appId) {
        if (appId == null || appId.isEmpty()) {
            return 24;
        }

        // 从缓存读取应用的令牌生命周期
        String fiele = "TokenLife";
        Object val = getFromRedis(appId, fiele);
        if (val != null) {
            return Integer.valueOf(val.toString());
        }

        // 从数据库读取应用的令牌生命周期
        Integer hours = authMapper.getTokenLife(appId);
        if (hours == null) {
            hours = 24;
        }

        setToRedis(appId, fiele, hours.toString());

        return hours;
    }

    /**
     * 自动绑定租户ID和登录部门ID
     *
     * @param token Token
     */
    public void setTenantIdAndDeptId(Token token) {
        pool.setTenantIdAndDeptId(token);
    }

    /**
     * 指定的功能是否授权给用户
     *
     * @param token    Token
     * @param function 功能ID或功能对应接口URL
     * @return 功能是否授权给用户
     */
    public Boolean isPermit(Token token, String function) {
        List<Function> functions = authMapper.getAllFunctions(token.getTenantId(), token.getUserId(), token.getDeptId());
        return functions.stream().anyMatch(i -> function.equals(i.getId()) || function.equals(i.getAlias()) || i.getInterfaces().contains(function));
    }

    /**
     * 用户是否存在
     *
     * @param user User数据
     * @return 用户是否存在
     */
    public Boolean isExisted(UserDTO user) {
        return userMapper.getExistedUserCount(user.getAccount(), user.getMobile(), user.getEmail(), user.getOpenId()) > 0;
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @param appId  微信AppID
     * @return 用户对象实体
     */
    public User getUser(String userId, String appId) {
        return userMapper.getUserWithAppId(userId, appId);
    }

    /**
     * 新增用户
     *
     * @param user 用户对象实体
     */
    public void addUser(UserDTO user) {
        pool.addUser(user);
    }

    /**
     * 更新用户微信用户信息
     *
     * @param user 用户对象实体
     */
    public void updateUser(UserDTO user) {
        pool.updateUser(user);
    }

    /**
     * 记录用户绑定的微信OpenID
     *
     * @param userId 用户ID
     * @param openId 微信OpenID
     * @param appId  微信AppID
     * @return 是否绑定成功
     */
    public void bindOpenId(String userId, String openId, String appId) {
        pool.bindOpenId(userId, openId, appId);
    }

    /**
     * 绑定设备到用户
     *
     * @param userId      用户ID
     * @param deviceId    设备ID
     * @param deviceModel 设备型号
     */
    public void bindDeviceToUser(String userId, String deviceId, String deviceModel) {
        pool.bindDeviceToUser(userId, deviceId, deviceModel);
    }

    /**
     * 使在线用户绑定设备失效
     *
     * @param userId 用户ID
     */
    public void logOffUser(String userId) {
        userMapper.logOffUser(userId);
    }

    /**
     * 根据用户id,租户id,部门id 查询拥有角色集合
     *
     * @param userId   用户id
     * @param tenantId 租户id
     * @param deptId   部门id
     * @return 角色集合
     */
    public List<String> getRoleList(String userId, String tenantId, String deptId) {
        return userMapper.getRoleIds(userId, tenantId, deptId);
    }

    /**
     * 获取用户关联的全部租户ID
     *
     * @param userId 用户ID
     * @return 租户ID集合
     */
    public List<String> getTenantIds(String userId) {
        return userMapper.getTenantIds(userId);
    }

    /**
     * 根据授权码获取用户的微信OpenID
     *
     * @param code 授权码
     * @return 微信OpenID
     */
    public WeChatUser getWeChatInfo(String code, String weChatAppId) {
        Object secret = redis.opsForHash().get(weChatAppId, "secret");

        return weChatHelper.getUserInfo(code, weChatAppId, secret.toString());
    }

    /**
     * 是否绑定了指定的应用
     *
     * @param tenantId 租户ID
     * @param appId    应用ID
     * @return 是否绑定了指定的应用
     */
    public Boolean containsApp(String tenantId, String appId) {
        return authMapper.containsApp(tenantId, appId) > 0;
    }

    /**
     * 保存Token数据到缓存
     *
     * @param token Token
     */
    public void setTokenCache(Token token) {
        if (!token.isChanged()) {
            return;
        }

        String key = "Token:" + token.getUserId();
        String json = Json.toJson(token);
        setToRedis(key, json);
    }

    /**
     * 以键值对方式保存数据到Redis
     *
     * @param key   键
     * @param value 值
     */
    public void setToRedis(String key, String value) {
        redis.opsForValue().set(key, value);
    }

    /**
     * 以键值对方式保存数据到Redis
     *
     * @param key   键
     * @param value 值
     * @param time  时间长度
     * @param unit  时间单位
     */
    private void setToRedis(String key, String value, long time, TimeUnit unit) {
        redis.opsForValue().set(key, value, time, unit);
    }

    /**
     * 以Hash方式保存数据到Redis
     *
     * @param key   键
     * @param field 字段名称
     * @param value 值
     */
    public void setToRedis(String key, String field, String value) {
        redis.opsForHash().put(key, field, value);
    }

    /**
     * 从Redis读取指定键的值
     *
     * @param key 键
     * @return Value
     */
    public String getFromRedis(String key) {
        return redis.opsForValue().get(key);
    }

    /**
     * 从Redis读取指定键下的字段名称的值
     *
     * @param key   键
     * @param field 字段名称
     * @return Value
     */
    public Object getFromRedis(String key, String field) {
        return redis.opsForHash().get(key, field);
    }

    /**
     * 从Redis删除指定键
     *
     * @param key 键
     */
    public void deleteFromRedis(String key) {
        redis.delete(key);
    }

    /**
     * Redis中是否存在指定键
     *
     * @param key 键
     * @return 是否存在指定键
     */
    public Boolean redisHasKey(String key) {
        return redis.hasKey(key);
    }

    /**
     * 生成一个指定长度的纯数字组成的随机字符串
     *
     * @param length 生成字符串长度(1-8)
     * @return 随机字符串
     */
    private String randomString(Integer length) {
        Random random = new Random();
        String base = "00000000";

        Double max = Math.pow(Double.valueOf("10"), length.doubleValue());
        String r = String.valueOf(random.nextInt(max.intValue()));

        int len = r.length();
        return length.equals(len) ? r : base.substring(0, length - len) + r;
    }
}
