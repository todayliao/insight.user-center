package com.insight.usercenter.user.dto;


import com.insight.usercenter.common.dto.PageConfigDTO;

/**
 * @author 宣炳刚
 * @date 2017/12/18
 * @remark 用户查询对象实体
 */
public class QueryUserDTO extends PageConfigDTO {

    /**
     * 用户编码
     */
    private String code;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户账号,登录名
     */
    private String account;

    /**
     * 用户绑定手机号,可作登录名
     */
    private String mobile;

    /**
     * 用户绑定E-mail,可作登录名
     */
    private String email;

    /**
     * 用户状态(是否失效)
     */
    private Boolean status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
