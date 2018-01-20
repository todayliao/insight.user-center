package com.insight.usercenter.common.entity;


import java.io.Serializable;
import java.util.Date;

/**
 * @author 宣炳刚
 * @date 2018/1/19
 * @remark 用户绑定微信OpenID记录类
 */
public class UserOpenId implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键(微信OpenID)
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 微信AppID
     */
    private String appId;

    /**
     * 创建时间
     */
    private Date createdTime;

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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
