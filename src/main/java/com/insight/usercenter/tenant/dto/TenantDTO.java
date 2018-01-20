package com.insight.usercenter.tenant.dto;


import com.insight.usercenter.common.dto.PageConfigDTO;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 租户查询对象实体
 */
public class TenantDTO extends PageConfigDTO {

    /**
     * 公司名称
     */
    private String name;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 状态
     */
    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
