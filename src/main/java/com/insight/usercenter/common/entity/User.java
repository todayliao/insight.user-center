package com.insight.usercenter.common.entity;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 用户类
 */
public class User implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 用户ID(UUID,唯一)
     */
    private String id;

    /**
     * 用户类型(0:公共用户、1:企业内部用户、2:合作方用户、3:外部个人用户)
     */
    private Integer userType;

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
     * 微信openId
     */
    private String openId;

    /**
     * 用户绑定E-mail,可作登录名
     */
    private String email;

    /**
     * 用户密码:原始密码的MD5值使用RSA算法加密后的字符串,或原始密码的MD5值(不推荐)
     */
    @JsonIgnore
    private String password;

    /**
     * 支付密码:(用户ID+原始密码)的MD5值
     */
    @JsonIgnore
    private String paypw;

    /**
     * 用户头像
     */
    private String headImg;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否内置用户
     */
    private Boolean builtin;

    /**
     * 是否失效
     */
    private Boolean invalid;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 用户角色
     */
    private String roles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

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

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPaypw() {
        return paypw;
    }

    public void setPaypw(String paypw) {
        this.paypw = paypw;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getBuiltin() {
        return builtin;
    }

    public void setBuiltin(Boolean builtin) {
        this.builtin = builtin;
    }

    public Boolean getInvalid() {
        return invalid;
    }

    public void setInvalid(Boolean invalid) {
        this.invalid = invalid;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
