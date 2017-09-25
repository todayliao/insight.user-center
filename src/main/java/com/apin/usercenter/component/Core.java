package com.apin.usercenter.component;

import com.apin.usercenter.auth.dto.RefreshToken;
import com.apin.usercenter.auth.dto.TokenPackage;
import com.apin.usercenter.common.entity.Function;
import com.apin.usercenter.common.mapper.AuthMapper;
import com.apin.usercenter.common.mapper.UserMapper;
import com.apin.util.Encryptor;
import com.apin.util.Generator;
import com.apin.util.JsonUtils;
import com.apin.util.message.Message;
import com.apin.util.message.SmsUtils;
import com.apin.util.pojo.AccessToken;
import com.apin.util.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 用户身份验证核心类(组件类)
 * @since 1、管理令牌类的缓存
 * @since 2、管理账号-用户ID对应关系缓存
 * @since 3、管理签名-Code对应关系缓存
 */
@Component()
public class Core {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private UserMapper userMapper;

    // RSA公钥
    private final String publicKey = "";

    // RSA私钥
    private final String privateKey = "";

    // Token允许的超时毫秒数(300秒)
    private final Integer timeOut = 1000 * 300;

    /**
     * 根据用户登录账号获取Account缓存中的用户ID
     *
     * @param appId   应用ID
     * @param account 登录账号(账号、手机号、E-mail)
     * @return 用户ID
     */
    public String getUserId(String appId, String account) {
        String key = Generator.md5((appId == null ? "" : appId) + account);
        String userId = redis.opsForValue().get(key);
        if (userId != null && !userId.isEmpty()) return userId;

        synchronized ( this ) {
            userId = redis.opsForValue().get(key);
            if (userId != null && !userId.isEmpty()) return userId;

            User user = userMapper.getUserByAccount(appId, account);
            if (user == null) return null;

            Token token = initToken(user);
            setUserIdCache(user);
            setTokenCache(token);

            return user.getId();
        }
    }

    /**
     * 生成Code,缓存后返回
     *
     * @param token   Token
     * @param type    登录类型(0:密码登录、1:验证码登录)
     * @param appName 应用名称
     * @return Code
     */
    public String generateCode(Token token, int type, String appName) {
        String key;
        int seconds = 3;
        switch (type) {
            case 0:
                key = Generator.md5(token.getAccount() + token.getPassword());
                break;
            case 1:
                // 生成短信验证码(5分钟内有效)并发送
                String mobile = token.getMobile();
                String smsCode = generateSmsCode(4, mobile, 5, 4);
                key = Generator.md5(token.getMobile() + Generator.md5(smsCode));
                seconds = 300;

                Message message = initVerifyMessage(appName, mobile, smsCode);
                SmsUtils.sendSms(message);
                break;
            default:
                // Invalid type! You guess, you guess, you guess. (≧∇≦)
                key = Generator.md5(Generator.uuid());
                break;
        }

        String code = Generator.uuid();
        String signature = Generator.md5(key + code);

        // 缓存Code,签名作为key
        redis.opsForValue().set(signature, code, seconds, TimeUnit.SECONDS);

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
        String key = Generator.md5(type + mobile + code);
        if (type == 4) return code;

        redis.opsForValue().set(key, code, minutes, TimeUnit.MINUTES);
        return code;
    }

    /**
     * 通过签名获取Code
     *
     * @param sign 签名
     * @return 签名对应的Code
     */
    public String getCode(String sign) {
        String code = redis.opsForValue().get(sign);
        if (code == null || code.isEmpty()) return null;

        redis.delete(sign);
        return code;
    }

    /**
     * 根据用户ID获取Token缓存中的Token
     *
     * @param userId 用户ID
     * @return Token(可能为null)
     */
    public Token getToken(String userId) {
        String json = redis.opsForValue().get(userId);
        if (json == null || json.isEmpty()) return null;

        return JsonUtils.toBean(json, Token.class);
    }

    /**
     * 验证Token是否合法
     *
     * @param token Token
     * @param key   密钥
     * @param type  验证类型(1:验证AccessToken、2:验证RefreshToken)
     * @return Token是否合法
     */
    public Boolean verifyToken(Token token, String key, int type) {
        switch (type) {
            case 1:
                if (token.getSecretKey().equals(key)) return true;

            case 2:
                if (token.getRefreshKey().equals(key)) return true;

            default:
                break;
        }

        addFailureCount(token);
        setTokenCache(token);
        return false;
    }

    /**
     * 验证PayPassword
     *
     * @param token Token
     * @param key   PayPassword
     * @return PayPassword是否正确
     */
    public Boolean verifyPayPassword(Token token, String key) {
        if (token.getPayPassword() == null) return null;

        String pw = Generator.md5(token.getUserId() + key);
        return token.getPayPassword().equals(pw);
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
        String key = Generator.md5(type + mobile + code);
        Boolean isExisted = redis.hasKey(key);
        if (!isExisted || isCheck) return isExisted;

        redis.delete(key);
        return true;
    }

    /**
     * 生成Token数据
     *
     * @param token Token
     * @param id    TokenID
     * @return Token数据
     */
    public TokenPackage creatorKey(Token token, String id) {
        return creatorKey(token, id, null);
    }

    /**
     * 生成Token数据
     *
     * @param token  Token
     * @param id     TokenID
     * @param deptId 部门ID，可为空
     * @return Token数据
     */
    public TokenPackage creatorKey(Token token, String id, String deptId) {
        token.setFailureCount(0);

        AccessToken accessToken = new AccessToken();
        accessToken.setId(id);
        accessToken.setAppId(token.getAppId());
        accessToken.setAccountId(token.getAccountId());
        accessToken.setUserId(token.getUserId());
        accessToken.setDeptId(deptId);
        accessToken.setUserName(token.getUserName());
        accessToken.setSecret(token.getSecretKey());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(id);
        refreshToken.setUserId(token.getUserId());
        refreshToken.setSecret(token.getRefreshKey());

        TokenPackage tokenPackage = new TokenPackage();
        tokenPackage.setAccessToken(JsonUtils.toBase64FromBean(accessToken));
        tokenPackage.setRefreshToken(JsonUtils.toBase64FromBean(refreshToken));
        tokenPackage.setExpireTime(new Date(token.getExpiryTime().getTime() - timeOut));
        tokenPackage.setFailureTime(new Date(token.getFailureTime().getTime() - timeOut));

        setTokenCache(token);
        return tokenPackage;
    }

    /**
     * 初始化Secret及失效、过期时间
     */
    public void initAccessToken(Token token) {
        initAccessToken(token, false);
    }

    /**
     * 初始化Secret及失效、过期时间
     *
     * @param token Token
     * @param force 是否强制初始化(false:不强制、true:强制)
     */
    public void initAccessToken(Token token, Boolean force) {
        Date now = new Date();
        if (isFailure(token) || force) {
            synchronized ( this ) {
                if (isFailure(token) || force) {

                    String secretKey = Generator.md5(Generator.uuid() + token.getPassword() + now);
                    String refreshKey = Generator.md5(Generator.uuid() + secretKey);
                    Date expiryTime = new Date(now.getTime() + 1000 * 3600 * 2 + timeOut);
                    Date failureTime = new Date(now.getTime() + 1000 * 3600 * 24 + timeOut);

                    token.setSecretKey(secretKey);
                    token.setRefreshKey(refreshKey);
                    token.setExpiryTime(expiryTime);
                    token.setFailureTime(failureTime);
                } else if (isExpiry(token)) {
                    refreshToken(token);
                }
            }
        } else if (isExpiry(token)) {
            refreshToken(token);
        }
        setTokenCache(token);
    }

    /**
     * 刷新Secret过期时间
     *
     * @param token Token
     */
    public Date refreshToken(Token token) {
        Date now = new Date();
        if (now.before(token.getExpiryTime())) return new Date(token.getExpiryTime().getTime() - timeOut);

        Date expiryTime = new Date(now.getTime() + 1000 * 3600 * 2 + timeOut);
        token.setExpiryTime(expiryTime);
        setTokenCache(token);

        return new Date(expiryTime.getTime() - timeOut);
    }

    /**
     * 用户是否失效状态
     *
     * @param token Token
     * @return 用户是否失效状态(false:有效;true:失效)
     */
    public Boolean isInvalid(Token token) {
        Date now = new Date();
        Date resetTime = new Date(token.getLastConnectTime().getTime() + 1000 * 600);
        if (token.getFailureCount() > 0 && now.after(resetTime)) {
            token.setFailureCount(0);
            setTokenCache(token);
        }

        return token.getFailureCount() > 5 || token.getInvalid();
    }

    /**
     * Token是否过期
     *
     * @param token Token
     * @return Token是否过期
     */
    public Boolean isExpiry(Token token) {
        Date now = new Date();
        return now.after(token.getExpiryTime());
    }

    /**
     * Token是否失效
     *
     * @param token Token
     * @return Token是否失效
     */
    public Boolean isFailure(Token token) {
        Date now = new Date();
        return now.after(token.getFailureTime());
    }

    /**
     * 指定的功能是否授权给用户
     *
     * @param appId    应用ID
     * @param userId   用户ID
     * @param deptId   登录部门ID
     * @param function 功能ID或功能对应接口URL
     * @return 功能是否授权给用户
     */
    public Boolean isPermit(String appId, String userId, String deptId, String function) {
        List<Function> functions = authMapper.getAllFunctions(appId, userId, deptId);

        return functions.stream().filter(i -> function.equals(i.getId()) || function.equals(i.getAlias()) || function.equals(i.getUrl())).count() > 0;
    }

    /**
     * 用户是否存在
     *
     * @param user User数据
     * @return 用户是否存在
     */
    public Boolean isExisted(User user) {
        return userMapper.getExistedUserByApp("", user.getAccount(), user.getMobile(), user.getEmail()) > 0;
    }

    /**
     * 更新用户信息
     *
     * @param token    Token
     * @param userId   用户ID
     * @param userName 用户名
     * @param remark   备注
     * @return 是否更新成功
     */
    public Boolean setUserInfo(Token token, String userId, String userName, String remark) {
        Integer count = userMapper.updateUserInfo(userId, userName, remark);
        if (count <= 0) return false;

        if (token != null) {
            token.setUserName(userName);
            setTokenCache(token);
        }

        return true;
    }

    /**
     * 更新用户类型
     *
     * @param token    Token
     * @param userId   用户ID
     * @param userType 用户类型(0:公共用户、1:企业内部用户、2:合作方用户、3:外部个人用户)
     * @return 是否更新成功
     */
    public Boolean setUserType(Token token, String userId, Integer userType) {
        if (token != null && token.getUserType().equals(userType)) return true;

        // 保存用户类型到数据库
        Integer count = userMapper.updateUserType(userId, userType);
        if (count <= 0) return false;

        if (token != null) {
            token.setUserType(userType);
            setTokenCache(token);
        }

        return true;
    }

    /**
     * 更新用户手机号
     *
     * @param token  Token
     * @param mobile 用户手机号
     * @return 是否更新成功
     */
    public Boolean setMobile(Token token, String mobile) {
        String key = token.getMobile();
        if (key != null && key.equals(mobile)) return true;

        // 保存数据到数据库
        Integer count = userMapper.updateAccount(token.getUserId(), mobile, token.getEmail());
        if (count <= 0) return false;

        if (key != null && !key.isEmpty()) redis.delete(key);

        token.setMobile(mobile);
        setTokenCache(token);
        if (mobile != null && !mobile.isEmpty()) redis.opsForValue().set(mobile, token.getUserId());

        return true;
    }

    /**
     * 更新E-mail
     *
     * @param token Token
     * @param email E-mail
     * @return 是否更新成功
     */
    public Boolean setEmail(Token token, String email) {
        String key = token.getEmail();
        if (key != null && key.equals(email)) return true;

        // 保存数据到数据库
        Integer count = userMapper.updateAccount(token.getUserId(), token.getMobile(), email);
        if (count <= 0) return false;

        if (key != null && !key.isEmpty()) redis.delete(key);

        token.setEmail(email);
        setTokenCache(token);
        if (email != null && !email.isEmpty()) redis.opsForValue().set(email, token.getUserId());

        return true;
    }

    /**
     * 更新用户密码
     *
     * @param userId   用户ID
     * @param token    Token
     * @param password 用户登录密码
     * @return 是否更新成功
     */
    public Boolean setPassword(Token token, String userId, String password) {
        String key = password.length() > 32 ? Encryptor.rsaDecrypt(password, privateKey) : password;
        if (token != null && token.getPassword() != null && token.getPassword().equals(key)) return true;

        // 保存密码到数据库
        Integer count = userMapper.updatePassword(userId, password);
        if (count <= 0) return false;

        if (token != null) {
            token.setPassword(key);
            setTokenCache(token);
        }

        return true;
    }

    /**
     * 更新支付密码
     *
     * @param token       Token
     * @param payPassword 支付密码
     * @return 是否更新成功
     */
    public Boolean setPayPassword(Token token, String payPassword) {
        String key = Generator.md5(token.getUserId() + payPassword);
        String pw = token.getPayPassword();
        if (pw != null && pw.equals(key)) return true;

        // 保存支付密码到数据库
        Integer count = userMapper.updatePayPassword(token.getUserId(), key);
        if (count <= 0) return false;

        token.setPayPassword(key);
        setTokenCache(token);
        return true;
    }

    /**
     * 更新用户失效状态
     *
     * @param token     Token
     * @param userId    用户ID
     * @param isInvalid 用户失效状态(false:有效、true:失效)
     * @return 是否更新成功
     */
    public Boolean setInvalidStatus(Token token, String userId, Boolean isInvalid) {
        if (token != null && token.getInvalid().equals(isInvalid)) return true;

        // 保存用户失效状态到数据库
        Integer count = userMapper.updateStatus(userId, isInvalid);
        if (count <= 0) return false;

        if (token != null) {
            token.setInvalid(isInvalid);
            setTokenCache(token);
        }

        return true;
    }

    /**
     * 使用户离线
     *
     * @param token Token
     */
    public void setOffline(Token token) {
        Date now = new Date();
        token.setExpiryTime(now);
        token.setFailureTime(now);
        token.setSecretKey(Generator.uuid());
        token.setRefreshKey(Generator.uuid());
        setTokenCache(token);
    }

    /**
     * 删除用户
     *
     * @param token  Token
     * @param userId 用户ID
     * @return 是否删除成功
     */
    public Boolean deleteUser(Token token, String userId) {
        Integer count = userMapper.deleteUserById(userId);
        if (count <= 0) return false;

        if (token != null) {
            String mobile = token.getMobile();
            if (mobile != null && !mobile.isEmpty()) redis.delete(mobile);

            String email = token.getEmail();
            if (email != null && !email.isEmpty()) redis.delete(email);

            redis.delete(token.getAccount());
            redis.delete(userId);
        }

        return true;
    }

    /**
     * 使用传入的User数据构造并初始化Token对象
     *
     * @param user User实体数据
     * @return Token
     */
    private Token initToken(User user) {
        String pwStr = user.getPassword();
        String password = pwStr.length() > 32 ? Encryptor.rsaDecrypt(pwStr, privateKey) : pwStr;

        Token token = new Token();
        token.setAppId(user.getApplicationId());
        token.setAccountId(user.getAccountId());
        token.setUserId(user.getId());
        token.setUserType(user.getUserType());
        token.setUserName(user.getName());
        token.setAccount(user.getAccount());
        token.setMobile(user.getMobile());
        token.setEmail(user.getEmail());
        token.setPassword(password);
        token.setPayPassword(user.getPaypw());
        token.setInvalid(user.getInvalid());

        return token;
    }

    /**
     * 累计失败次数
     *
     * @param token Token
     */
    private void addFailureCount(Token token) {
        if (isInvalid(token)) return;

        token.setFailureCount(1);
    }

    /**
     * 缓存用户ID到Redis
     *
     * @param user User
     */
    private void setUserIdCache(User user) {
        String userId = user.getId();
        String mobile = user.getMobile();
        String appId = user.getApplicationId();
        String key;
        if (mobile != null && !mobile.isEmpty()) {
            key = Generator.md5(appId + mobile);
            redis.opsForValue().set(key, userId);
        }

        String mail = user.getEmail();
        if (mail != null && !mail.isEmpty()) {
            key = Generator.md5(appId + mail);
            redis.opsForValue().set(key, userId);
        }

        key = Generator.md5(appId + user.getAccount());
        redis.opsForValue().set(key, userId);
    }

    /**
     * 缓存Token到Redis
     *
     * @param token Token
     */
    private void setTokenCache(Token token) {
        String json = JsonUtils.toJson(token);
        redis.opsForValue().set(token.getUserId(), json);
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

    /**
     * 初始化登录验证码消息实体
     *
     * @param appName 应用名称
     * @param mobile  手机号
     * @param code    验证码
     * @return Message消息实体
     */
    private Message initVerifyMessage(String appName, String mobile, String code) {
        Map<String, String> map = new HashMap<>();
        map.put("product", appName);
        map.put("code", code);

        Message message = new Message();
        message.setTemplate("SMS_97945028");
        message.setParams(map);
        message.setReceivers(mobile);

        return message;
    }

}
