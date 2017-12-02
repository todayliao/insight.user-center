package com.insight.usercenter.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

/**
 * @author duxl
 * @date 2017年8月22日
 * @remark 常用Generator
 */
public final class Generator {

    private Generator() {
    }

    /**
     * 生成uuid
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * md5 加密
     *
     * @param key 字符串
     * @return 密文
     */
    public static String md5(String key) {
        return DigestUtils.md5Hex(key);
    }

    /**
     * sha 散列
     *
     * @param key 字符串
     * @return hash散列
     */
    public static String sha(String key) {
        return DigestUtils.sha256Hex(key);
    }

    /**
     * 生成指定长度的随机数字字符串
     *
     * @param length 长度
     * @return 数字字符串
     */
    public static String randomInt(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String randomStr(int length) {
        return RandomStringUtils.random(length);
    }
}
