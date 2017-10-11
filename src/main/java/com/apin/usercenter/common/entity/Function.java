package com.apin.usercenter.common.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 宣炳刚
 * @date 2017/9/13
 * @remark 模块功能实体类
 */
public class Function implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 节点(功能/模块/模块组)ID
     */
    private String id;

    /**
     * 上级节点ID
     */
    private String parentId;

    /**
     * 索引,排序用
     */
    private Integer index;

    /**
     * 节点类型
     */
    private Integer type;

    /**
     * 功能/模块/模块组名称
     */
    private String name;

    /**
     * 功能别名(用于鉴权)
     */
    private String alias;

    /**
     * 功能/模块/模块组图标(URL)
     */
    private String icon;

    /**
     * 功能/模块URL(用于鉴权)
     */
    private String url;

    /**
     * 是否授权(true:已授权,false:已拒绝,null:未授权)
     */
    private Boolean action;

    /**
     * 授予权限描述(允许/拒绝/无)
     */
    private String desc;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否可见
     */
    private Boolean invisible;

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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getAction() {
        return action;
    }

    public void setAction(Boolean action) {
        this.action = action;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getInvisible() {
        return invisible;
    }

    public void setInvisible(Boolean invisible) {
        this.invisible = invisible;
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
