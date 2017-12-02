package com.insight.usercenter.common;


import com.insight.usercenter.common.utils.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author 宣炳刚
 * @date 2017/9/9
 * @remark 接口调用记录管理类
 */
@Component
public class CallManage {
    @Autowired
    private StringRedisTemplate redis;

    /**
     * 获取限流计时周期剩余秒数
     *
     * @param key     键值
     * @param seconds 限流计时周期秒数
     * @return 剩余秒数
     */
    public Integer getSurplus(String key, Integer seconds) {
        if (key == null || key.isEmpty() || seconds == null || seconds.equals(0)) {
            return 0;
        }

        String val = redis.opsForValue().get(key);
        if (val == null || val.isEmpty()) {
            redis.opsForValue().set(key, DateHelper.getDateTime(), seconds, TimeUnit.SECONDS);

            return 0;
        }

        Date time = DateHelper.parseDateTime(val);
        Long bypast = System.currentTimeMillis() - time.getTime();
        if (bypast > 1000) {
            return seconds - bypast.intValue() / 1000;
        }

        // 调用时间间隔低于1秒时,重置调用时间为当前时间作为惩罚
        redis.opsForValue().set(key, DateHelper.getDateTime(), seconds, TimeUnit.SECONDS);

        return seconds;
    }

    /**
     * 是否被限流(超过限流计时周期最大访问次数)
     *
     * @param key     键值
     * @param seconds 限流计时周期秒数
     * @param max     调用限制次数
     * @return 是否限制访问
     */
    public Boolean isLimited(String key, Integer seconds, Integer max) {
        if (key == null || key.isEmpty() || seconds == null || seconds.equals(0)) {
            return false;
        }

        // 如记录不存在,则记录访问次数为1
        String val = redis.opsForValue().get(key);
        if (val == null || val.isEmpty()) {
            redis.opsForValue().set(key, "1", seconds, TimeUnit.SECONDS);

            return false;
        }

        // 读取访问次数,如次数超过限制,返回true,否则访问次数增加1次
        Integer count = Integer.valueOf(val);
        Long expire = redis.getExpire(key, TimeUnit.SECONDS);
        if (count > max) {
            return true;
        }

        count++;
        redis.opsForValue().set(key, count.toString(), expire, TimeUnit.SECONDS);
        return false;
    }
}
