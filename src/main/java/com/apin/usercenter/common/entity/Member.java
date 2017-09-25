package com.apin.usercenter.common.entity;

import java.io.Serializable;

/**
 * @author 宣炳刚
 * @date 2017/9/21
 * @remark 角色/用户组成员实体类
 */
public class Member implements Serializable {
    private static final long serialVersionUID = -1L;

    private String id;
    private Integer type;
    private String parentId;
    private String memberId;
    private String name;
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
