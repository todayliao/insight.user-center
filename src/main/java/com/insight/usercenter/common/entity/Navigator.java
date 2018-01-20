package com.insight.usercenter.common.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 宣炳刚
 * @date 2017/9/13
 * @remark 导航实体类
 */
public class Navigator implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 导航ID
     */
    private String id;

    /**
     * 应用ID
     */
    private String applicationId;

    /**
     * 父级导航ID
     */
    private String parentId;

    /**
     * 级别
     */
    private Integer type;

    /**
     * 索引,排序用
     */
    private Integer index;

    /**
     * 导航名称
     */
    private String name;

    /**
     * 导航图标(URL)
     */
    private String icon;

    /**
     * 导航路由URL(模块组该值为空)
     */
    private String url;

    /**
     * 备注
     */
    private String remark;

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

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
