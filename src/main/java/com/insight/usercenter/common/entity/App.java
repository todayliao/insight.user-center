package com.insight.usercenter.common.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 宣炳刚
 * @date 2017/9/30
 * @remark 应用实体类
 */
public class App implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 应用ID
     */
    private String id;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用简称
     */
    private String alias;

    /**
     * 应用图标
     */
    private String icon;

    /**
     * 应用域名
     */
    private String host;

    /**
     * 令牌生命周期(小时)
     */
    private Integer tokenLife;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getTokenLife() {
        return tokenLife;
    }

    public void setTokenLife(Integer tokenLife) {
        this.tokenLife = tokenLife;
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
