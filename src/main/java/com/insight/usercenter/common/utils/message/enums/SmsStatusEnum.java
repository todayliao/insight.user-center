package com.insight.usercenter.common.utils.message.enums;

/**
 * @author duxl
 * @date 2017/9/13
 * @remark
 */
public enum SmsStatusEnum {
    /**
     * 待发送
     */
    Waiting,
    /**
     * 已提交到短信网关
     */
    Sent,
    /**
     * 提交网关发送失败
     * x为对接的短信服务商数量
     * 验证码类在1分钟之内连续重试3*x次，3*x次cancle
     * 通知类消息在10分钟内连续重试3*x次，3*x次cancle
     * 群发消息在100分钟内连续重试3*x次，3*x次cancle
     */
    Delayed,
    /**
     * 发送成功
     */
    Success,
    /**
     * 网关返回失败信息
     * 记录失败原因
     */
    Failed,
    /**
     * 该条发送任务取消
     */
    Cancled
}