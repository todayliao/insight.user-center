package com.insight.usercenter.common.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2017/9/13
 * @remark 角色实体类
 */
public class Role implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 角色ID
     */
    private String id;

    /**
     * 所属租户ID
     */
    private String tenantId;

    /**
     * 所属应用ID
     */
    private String applicationId;

    /**
     * 租户类型：0、未定义；1、旅行社；2、供应商；3、旅行社和供应商
     */
    private Integer tenantType;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否内置角色
     */
    private Boolean isBuiltin;

    /**
     * 创建人ID
     */
    private String creatorUserId;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 授权功能集合
     */
    private List<Function> functions;

    /**
     * 成员集合
     */
    private List<Member> members;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getTenantType() {
        return tenantType;
    }

    public void setTenantType(Integer accountType) {
        this.tenantType = accountType;
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

    public Boolean getBuiltin() {
        return isBuiltin;
    }

    public void setBuiltin(Boolean builtin) {
        isBuiltin = builtin;
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

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
