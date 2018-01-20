package com.insight.usercenter.role.dto;


import com.insight.usercenter.common.dto.PageConfigDTO;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 角色查询对象实体
 */
public class RoleDTO extends PageConfigDTO {

    /**
     * 所属租户ID
     */
    private String tenantId;

    /**
     * 所属应用ID
     */
    private String appId;

    /**
     * 角色名称
     */
    private String name;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
