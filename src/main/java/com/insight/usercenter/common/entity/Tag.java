package com.insight.usercenter.common.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author luwenbao
 * @date 2018/1/4.
 * @remark 用户标签实体
 */
public class Tag implements Serializable {
    /**
     * 标签ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户标签
     */
    private String tag;

    /**
     * 创建人ID
     */
    private String creatorUserId;

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
