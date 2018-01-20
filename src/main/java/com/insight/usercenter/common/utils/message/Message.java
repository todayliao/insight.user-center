package com.insight.usercenter.common.utils.message;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author duxl
 * @date 2017/9/13
 * @remark 消息实体
 */
public final class Message implements Serializable {
    /**
     * 消息模板id
     */
    private String template;

    /**
     * 接收人列表
     * 短信：手机号码列表
     * 邮件：email地址列表
     */
    private List<String> receivers;

    /**
     * 消息内容参数
     */

    private Map<String, String> params;


    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    /**
     * 字符串分隔的接收人转换成list
     *
     * @param receivers "139...,138..."
     */
    public void setReceiverString(String receivers) {
        String[] rs = receivers.split(",");
        this.receivers = Arrays.asList(rs);
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
