package com.insight.usercenter.common.utils.message;

import com.insight.usercenter.common.utils.message.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author duxl
 * @date 2017年9月12日
 * @remark kafka producer
 */
@Component
public class KafkaProducer {

    /**
     * 短信topic
     */
    @Value(Constants.KAFKA_TOPIC_SMS)
    private String topicSms;

    /**
     * 邮件topic
     */
    @Value(Constants.KAFKA_TOPIC_EMAIL)
    private String topicEmail;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送短信
     *
     * @param content 内容
     */
    public void sendSms(String content) {
        kafkaTemplate.send(topicSms, content);
    }

    /**
     * 发送邮件
     *
     * @param content 消息
     */
    public void sendEmail(String content) {
        kafkaTemplate.send(topicEmail, content);
    }

    /**
     * 发布消息
     *
     * @param topic   主题
     * @param content 内容
     */
    public void publish(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }
}
