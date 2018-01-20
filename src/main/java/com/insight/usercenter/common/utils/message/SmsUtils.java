package com.insight.usercenter.common.utils.message;


import com.insight.usercenter.common.utils.Json;
import com.insight.usercenter.common.utils.common.ApplicationContextHolder;

/**
 * @author duxl
 * @date 2017年9月13日
 * @remark 短信消息工具类
 */
public final class SmsUtils {

    /**
     * 获取kafka实例
     */
    private static KafkaProducer kafkaProducer = ApplicationContextHolder.getContext().getBean(KafkaProducer.class);

    /**
     * 发送消息
     *
     * @param message 消息
     */
    public static void sendSms(Message message) {
        String str = Json.toJson(message);
        kafkaProducer.sendSms(str);
    }

    /**
     * 发送消息
     *
     * @param message 消息
     */
    public static void sendEmail(Message message) {
        String str = Json.toJson(message);
        kafkaProducer.sendEmail(str);
    }
}
