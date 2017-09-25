package com.apin.usercenter.auth.dto;

import java.io.Serializable;

/**
 * 刷新令牌，客户端调用刷新接口延长令牌使用时间所需提供的凭证
 */
public class RefreshToken implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 令牌ID，唯一
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 安全码，用于验证令牌合法性
     */
    private String secret;

    public RefreshToken() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

}
