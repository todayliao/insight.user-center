package com.insight.usercenter.tenant.dto;


import com.insight.usercenter.common.dto.PageConfigDTO;
import com.insight.util.Json;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 租户查询对象实体
 */
public class TenantDTO extends PageConfigDTO {
    private static final long serialVersionUID = -1L;

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
     * 查询关键词
     */
    private String key;

    /**
     * 状态
     */
    private Integer status;

    @Override
    public String toString() {
        return Json.toJson(this);
    }

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
