package com.insight.usercenter.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author duxl
 * @date 2017年9月14日
 * @remark 基础帮助类
 */
public final class Util {

    /**
     * 分割list
     *
     * @param source 原list
     * @param n      每个list size
     * @param <T>
     * @return List<List>
     */
    public static <T> List<List<T>> splitList(List<T> source, Integer n) {
        List<List<T>> result = new ArrayList<>();
        int remaider = source.size() % n;
        int number = source.size() / n;
        int offset = 0;
        for (int i = 0; i < n; i++) {
            if (remaider > 0) {
                remaider--;
                offset++;
            }

            result.add(source.subList(i * number + offset, (i + 1) * number + offset));
        }

        return result;
    }

    /**
     * 将一个fix字符串转换成map
     *
     * @param content 字符串，比如x1=a&x2=b
     * @param s1      词组分隔符
     * @return map
     */
    public static Map<String, String> fix(String content, String s1) {
        Map<String, String> map = new HashMap<>(16);
        String[] arr = content.split(s1);
        for (String a : arr) {
            String[] s = a.split("=");
            if (s.length < 2) {
                continue;
            }
            map.put(s[0], s[1]);
        }

        return map;
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
     * 获取客户端IP
     *
     * @param request 请求对象
     * @return 客户端IP
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (isEmpty(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }

        if (isEmpty(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (isEmpty(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (isEmpty(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /**
     * 获取客户端指纹
     * (限流)
     *
     * @param request 请求对象
     * @return 客户端指纹
     */
    public static String getFingerprint(HttpServletRequest request){
        String info = getIp(request) + request.getHeader("user-agent");
        return md5(info);
    }

    /**
     * IP是否为空
     *
     * @param str IP字符串
     * @return 是否为空
     */
    private static Boolean isEmpty(String str) {
        return str == null || str.isEmpty() || "unknown".equalsIgnoreCase(str);
    }
}
