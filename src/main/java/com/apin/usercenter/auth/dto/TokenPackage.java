package com.apin.usercenter.auth.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 令牌包，向客户端返回令牌数据用
 */
public class TokenPackage implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 访问令牌(Base64编码)
     */
    private String accessToken;

    /**
     * 刷新令牌(Base64编码)
     */
    private String refreshToken;

    /**
     * 令牌过期时间
     */
    private Date expireTime;

    /**
     * 令牌失效时间
     */
    private Date failureTime;

    public TokenPackage() {
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(Date failureTime) {
        this.failureTime = failureTime;
    }
}
