package com.apin.usercenter.component;

import com.apin.util.Generator;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 用户身份验证令牌类
 */
public class Token implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 用户所属账户ID
     */
    private String accountId;

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
     * 用户是否失效
     */
    private Boolean isInvalid;

    /**
     * Token验证密钥
     */
    private String secretKey;

    /**
     * Token刷新密钥
     */
    private String refreshKey;

    /**
     * 上次连接时间
     */
    private Date lastConnectTime;

    /**
     * 连续验证失败次数
     */
    private Integer failureCount;

    /**
     * Token过期时间
     */
    private Date expiryTime;

    /**
     * Token失效时间
     */
    private Date failureTime;

    /**
     * 构造函数
     */
    public Token() {
        Date now = new Date();
        secretKey = Generator.uuid();
        refreshKey = Generator.uuid();
        failureCount = 0;
        lastConnectTime = now;
        expiryTime = now;
        failureTime = now;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public Boolean getInvalid() {
        return isInvalid;
    }

    public void setInvalid(Boolean invalid) {
        isInvalid = invalid;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRefreshKey() {
        return refreshKey;
    }

    public void setRefreshKey(String refreshKey) {
        this.refreshKey = refreshKey;
    }

    public Date getLastConnectTime() {
        return lastConnectTime;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount += failureCount;
        lastConnectTime = new Date();
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public Date getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(Date failureTime) {
        this.failureTime = failureTime;
    }
}
