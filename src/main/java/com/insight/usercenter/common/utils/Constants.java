package com.insight.usercenter.common.utils;

/**
 * @author duxl
 * @date 2017/10/20
 * @remark 常量
 */
public final class Constants {

    /**
     * kafka消息短信topic
     */
    public static final String KAFKA_TOPIC_SMS = "${apin.message.topic.sms:apinsms}";
    /**
     * kafka消息邮件topic
     */
    public static final String KAFKA_TOPIC_EMAIL = "${apin.message.topic.email:apinemail}";


}
