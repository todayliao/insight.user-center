package com.insight.usercenter.common.entity;

import java.io.Serializable;

/**
 * @author 宣炳刚
 * @date 2017/9/21
 * @remark 角色/用户组成员实体类
 */
public class Member implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 成员关系ID
     */
    private String id;

    /**
     * 成员类型
     */
    private Integer type;

    /**
     * 上级ID/角色ID
     */
    private String parentId;

    /**
     * 成员ID(根据类型不同)
     */
    private String memberId;

    /**
     * 成员名称(根据类型不同)
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
