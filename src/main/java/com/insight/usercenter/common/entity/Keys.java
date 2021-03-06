package com.insight.usercenter.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.insight.util.Generator;

import java.util.Date;

/**
 * @author 宣炳刚
 * @date 2018/1/4
 * @remark 令牌关键数据集
 */
public class Keys {

    /**
     * Token允许的超时毫秒数(300秒)
     */
    private static final int TIME_OUT = 1000 * 300;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 令牌生命周期(秒)
     */
    private Integer tokenLife;

    /**
     * Token验证密钥
     */
    private String secretKey;

    /**
     * Token刷新密钥
     */
    private String refreshKey;

    /**
     * 微信OpenID
     */
    private String weChatOpenId;

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
    public Keys() {
    }

    /**
     * 构造方法
     *
     * @param appId 应用ID
     * @param hours 令牌有效小时数
     */
    public Keys(String appId, int hours) {
        this.appId = appId;

        tokenLife = 3600 * hours;
        secretKey = Generator.uuid();
        refreshKey = Generator.uuid();
        expiryTime = new Date(1000 * tokenLife.longValue() / 12 + System.currentTimeMillis() + TIME_OUT);
        failureTime = new Date(1000 * tokenLife.longValue() + System.currentTimeMillis() + TIME_OUT);
    }

    /**
     * 验证密钥
     *
     * @param key  密钥
     * @param type 验证类型(1:验证AccessToken、2:验证RefreshToken)
     * @return 是否通过验证
     */
    public Boolean verifyKey(String key, int type) {
        if (type < 1 || type > 2) {
            return false;
        }

        String secret = type == 1 ? secretKey : refreshKey;
        return secret.equals(key);
    }

    /**
     * 刷新令牌关键数据
     **/
    public void refresh() {
        expiryTime = new Date(1000 * tokenLife.longValue() / 12 + System.currentTimeMillis() + TIME_OUT);
        if (appId == null) {
            failureTime = new Date(1000 * tokenLife.longValue() + System.currentTimeMillis() + TIME_OUT);
        } else {
            secretKey = Generator.uuid();
        }
    }

    /**
     * Token是否过期
     *
     * @param isReal 是否实际过期时间
     * @return Token是否过期
     */
    @JsonIgnore
    public Boolean isExpiry(Boolean isReal) {
        Date now = new Date();
        Date expiry = isReal ? expiryTime : new Date(expiryTime.getTime() - TIME_OUT);
        return now.after(expiry);
    }

    /**
     * Token是否失效
     *
     * @return Token是否失效
     */
    public Boolean isFailure() {
        Date now = new Date();
        return now.after(failureTime);
    }

    public String getAppId() {
        return appId;
    }

    public Integer getTokenLife() {
        return tokenLife;
    }

    public void setTokenLife(Integer tokenLife) {
        this.tokenLife = tokenLife;
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

    public String getWeChatOpenId() {
        return weChatOpenId;
    }

    public void setWeChatOpenId(String weChatOpenId) {
        this.weChatOpenId = weChatOpenId;
    }

    public Date getExpiryTime() {
        return new Date(expiryTime.getTime() - TIME_OUT);
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public Date getFailureTime() {
        return new Date(failureTime.getTime() - TIME_OUT);
    }

    public void setFailureTime(Date failureTime) {
        this.failureTime = failureTime;
    }
}
