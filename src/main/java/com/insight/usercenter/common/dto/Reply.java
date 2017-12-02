package com.insight.usercenter.common.dto;

import java.io.Serializable;

/**
 * @author 作者
 * @date 2017年9月5日
 * @remark Reply封装
 */
public final class Reply implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 数据
     */
    private Object data;

    /**
     * 可选数据
     */
    private Object option;

    public Reply() {
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getOption() {
        return option;
    }

    public void setOption(Object option) {
        this.option = option;
    }
}
