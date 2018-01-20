package com.insight.usercenter.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.insight.usercenter.auth.dto.RefreshToken;
import com.insight.usercenter.auth.dto.TokenPackage;
import com.insight.usercenter.common.dto.AccessToken;
import com.insight.usercenter.common.entity.Keys;
import com.insight.usercenter.common.entity.User;
import com.insight.usercenter.common.utils.Json;
import com.insight.usercenter.common.utils.Util;
import com.insight.usercenter.common.utils.encrypt.Encryptor;

import java.io.Serializable;
import java.util.*;

/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 用户身份验证令牌类
 */
public class Token implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * RSA私钥
     */
    private final String privateKey = "";

    /**
     * 当前令牌对应的关键数据集
     */
    @JsonIgnore
    private Keys currentKeys;

    /**
     * Token属性是否已改变
     */
    @JsonIgnore
    private boolean isChanged;

    /**
     * 用户当前使用的租户ID
     */
    private String tenantId;

    /**
     * 登录部门ID
     */
    private String deptId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 登录账号
     */
    private String account;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 微信UnionID
     */
    private String unionId;

    /**
     * 用户E-mail
     */
    private String email;

    /**
     * 用户签名
     */
    private String password;

    /**
     * 用户支付密码
     */
    private String payPassword;

    /**
     * 用户是否内置
     */
    private Boolean isBuiltIn;

    /**
     * 用户是否失效
     */
    private Boolean isInvalid;

    /**
     * 上次验证失败时间
     */
    private Date lastFailureTime;

    /**
     * 连续验证失败次数
     */
    private Integer failureCount;

    /**
     * 使用中的Code
     */
    private Map<String, Keys> keyMap;

    /**
     * 角色集合
     */
    private List<String> roleList;

    /**
     * 构造函数
     */
    public Token() {
    }

    /**
     * 构造函数
     *
     * @param user User实体数据
     */
    public Token(User user) {
        Date now = new Date();
        String pwStr = user.getPassword();

        userId = user.getId();
        userType = user.getUserType();
        userName = user.getName();
        account = user.getAccount();
        mobile = user.getMobile();
        unionId = user.getOpenId();
        email = user.getEmail();
        password = pwStr.length() > 32 ? Encryptor.rsaDecrypt(pwStr, privateKey) : pwStr;
        payPassword = user.getPaypw();
        isBuiltIn = user.getBuiltin();
        isInvalid = user.getInvalid();
        failureCount = 0;
        lastFailureTime = now;
        keyMap = new HashMap<>(16);
        roleList = new ArrayList<>();
        isChanged = true;
    }

    /**
     * 选择当前令牌对应的关键数据集
     *
     * @param tokenId 令牌ID
     */
    public void selectKeys(String tokenId) {
        currentKeys = keyMap.get(tokenId);
    }

    /**
     * 生成令牌数据包
     *
     * @param code  Code
     * @param appId 应用ID
     * @param hours 令牌有效小时数
     * @return 令牌数据包
     */
    @JsonIgnore
    public TokenPackage creatorKey(String code, String appId, Integer hours) {
        return creatorKey(code, appId, hours, null);
    }

    /**
     * 生成令牌数据包
     *
     * @param code   Code
     * @param appId  应用ID
     * @param hours  令牌有效小时数
     * @param openId 微信OpenID
     * @return 令牌数据包
     */
    @JsonIgnore
    public TokenPackage creatorKey(String code, String appId, Integer hours, String openId) {
        for (String key : keyMap.keySet()) {
            Keys keys = keyMap.get(key);

            // 如应用ID不为空,且应用ID有对应的Key则从Map中删除该应用对应的Key.否则使用无应用ID的公共Key.
            if (appId != null && !appId.isEmpty()) {
                if (appId.equals(keys.getAppId())) {
                    if (openId == null) {
                        openId = keys.getWeChatOpenId();
                    }

                    keyMap.remove(key);
                    currentKeys = null;
                    break;
                }
            } else {
                if (keys.getAppId() == null) {
                    keys.refresh();

                    currentKeys = keys;
                    code = key;
                    break;
                }
            }
        }

        // 生成新的Key加入Map
        if (currentKeys == null) {
            currentKeys = new Keys(appId, hours);
            keyMap.put(code, currentKeys);
        }

        if (failureCount > 0) {
            failureCount = 0;
        }

        currentKeys.setWeChatOpenId(openId);
        isChanged = true;

        return initPackage(code);
    }

    /**
     * 刷新Secret过期时间
     *
     * @param tokenId 令牌ID
     * @return 令牌数据包
     */
    @JsonIgnore
    public TokenPackage refreshToken(String tokenId) {
        currentKeys.refresh();
        isChanged = true;

        return initPackage(tokenId);
    }

    /**
     * 初始化令牌数据包
     *
     * @param code Code
     * @return 令牌数据包
     */
    private TokenPackage initPackage(String code) {
        AccessToken accessToken = new AccessToken();
        accessToken.setId(code);
        accessToken.setUserId(userId);
        accessToken.setUserName(userName);
        accessToken.setSecret(currentKeys.getSecretKey());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(code);
        refreshToken.setUserId(userId);
        refreshToken.setSecret(currentKeys.getRefreshKey());

        TokenPackage tokenPackage = new TokenPackage();
        tokenPackage.setAccessToken(Json.toBase64(accessToken));
        tokenPackage.setRefreshToken(Json.toBase64(refreshToken));
        tokenPackage.setExpireTime(currentKeys.getExpiryTime());
        tokenPackage.setFailureTime(currentKeys.getFailureTime());

        return tokenPackage;
    }

    /**
     * 验证Token是否合法
     *
     * @param key  密钥
     * @param type 验证类型(1:验证AccessToken、2:验证RefreshToken)
     * @return Token是否合法
     */
    @JsonIgnore
    public Boolean verifyToken(String key, int type) {
        if (currentKeys != null) {
            switch (type) {
                case 1:
                    if (currentKeys.getSecretKey().equals(key)) {
                        return true;
                    } else {
                        break;
                    }

                case 2:
                    if (currentKeys.getRefreshKey().equals(key)) {
                        return true;
                    } else {
                        break;
                    }

                default:
                    break;
            }
        }

        addFailureCount();
        return false;
    }

    /**
     * 验证PayPassword
     *
     * @param key PayPassword
     * @return PayPassword是否正确
     */
    @JsonIgnore
    public Boolean verifyPayPassword(String key) {
        if (payPassword == null) {
            return null;
        }

        String pw = Util.md5(userId + key);
        return payPassword.equals(pw);
    }

    /**
     * Token是否过期
     *
     * @return Token是否过期
     */
    @JsonIgnore
    public Boolean isExpiry() {
        return currentKeys == null || currentKeys.isExpiry(true);
    }

    /**
     * Token是否失效
     *
     * @return Token是否失效
     */
    public Boolean isFailure() {
        return currentKeys == null || currentKeys.isFailure();
    }

    /**
     * 用户是否失效状态
     *
     * @return 用户是否失效状态
     */
    @JsonIgnore
    public Boolean userIsInvalid() {
        Date now = new Date();
        Date resetTime = new Date(lastFailureTime.getTime() + 1000 * 600);
        if (failureCount > 0 && now.after(resetTime)) {
            failureCount = 0;
            isChanged = true;
        }

        return failureCount > 5 || isInvalid;
    }

    /**
     * 累计失败次数(有效时)
     */
    @JsonIgnore
    public void addFailureCount() {
        if (userIsInvalid()) {
            return;
        }

        failureCount++;
        lastFailureTime = new Date();
        isChanged = true;
    }

    /**
     * 使用户离线
     */
    @JsonIgnore
    public void deleteKeys(String tokenId) {
        if (keyMap.containsKey(tokenId)) {
            keyMap.remove(tokenId);
            isChanged = true;
        }
    }

    /**
     * 获取当前令牌关键数据集中的微信OpenID
     *
     * @return 微信OpenID
     */
    @JsonIgnore
    public String getWeChatOpenId() {
        return currentKeys == null ? null : currentKeys.getWeChatOpenId();
    }

    /**
     * 获取RSA私钥
     *
     * @return RSA私钥
     */
    @JsonIgnore
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Token数据是否发生变化
     *
     * @return 是否发生变化
     */
    @JsonIgnore
    public boolean isChanged() {
        return isChanged;
    }

    /**
     * 设置修改标志位为真值
     */
    @JsonIgnore
    public void setChanged() {
        isChanged = true;
    }


    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }

    public Boolean getBuiltIn() {
        return isBuiltIn;
    }

    public void setBuiltIn(Boolean builtIn) {
        isBuiltIn = builtIn;
    }

    public Boolean getInvalid() {
        return isInvalid;
    }

    public void setInvalid(Boolean invalid) {
        isInvalid = invalid;
    }

    public Date getLastFailureTime() {
        return lastFailureTime;
    }

    public void setLastFailureTime(Date lastFailureTime) {
        this.lastFailureTime = lastFailureTime;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public Map<String, Keys> getKeyMap() {
        return keyMap;
    }

    public void setKeyMap(Map<String, Keys> keyMap) {
        this.keyMap = keyMap;
    }

    public List<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }
}
