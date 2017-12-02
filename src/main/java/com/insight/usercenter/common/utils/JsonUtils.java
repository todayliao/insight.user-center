package com.insight.usercenter.common.utils;


import com.insight.usercenter.common.dto.AccessToken;
import com.insight.usercenter.common.utils.encrypt.Base64Encryptor;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author duxl
 * @date 2017年8月22日
 * @remark Json工具类
 */
public final class JsonUtils {

    /**
     * 设置JSON时间格式
     */
    static SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * jackson map
     */
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setDateFormat(myDateFormat);
    }

    private JsonUtils() {
    }

    /**
     * 将bean转换成json
     *
     * @param obj bean对象
     * @return json
     */
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 先把bean转换为json字符串，再把json字符串进行base64编码
     *
     * @param obj bean
     * @return
     */
    public static String toBase64(Object obj) {
        String json = toJson(obj);

        return Base64Encryptor.encode(json);
    }

    /**
     * 把json字符串转换为相应的JavaBean对象
     *
     * @param json json数据
     * @param type bean 类型
     * @param <T>  泛型
     * @return bean
     */
    public static <T> T toBean(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 把json字符串转换为相应的JavaBean对象，支持泛型Collection
     * 如List<Bean>的json，则先调用getCollectionType(List.class, Bean.class)获取java type
     * 再调用该方法获取到List<bean>
     *
     * @param json json字符串
     * @param type javatype
     * @param <T>  泛型
     * @return List<Bean>
     */
    public static <T> T toBean(String json, JavaType type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 把list linkedhashmap object转换为相应的List<Bean>对象
     * 如List<LinkedHashMap> object，则调用方式为(object, Bean.class)
     *
     * @param object List<LinkedHashMap>对象
     * @param clazz  如Bean.class
     * @return List<Bean>
     */
    public static <T> List<T> toBean(Object object, Class<T> clazz) {
        try {
            return ((List<LinkedHashMap>) object).stream().map(e -> toBean(toJson(e), clazz)).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 将json字符串转换为HashMap
     *
     * @param json json
     * @return hashmap
     */
    public static Map toMap(String json) {
        try {
            return mapper.readValue(json, HashMap.class);
        } catch (Exception e) {
            LogFactory.getLog(JsonUtils.class).error("json转换失败：" + json);
        }

        return null;
    }


    /**
     * 把base64编码过的json字符串转换为相应的JavaBean对象
     * 先转换为json字符串，再转换为bean
     *
     * @param base64Str base64编码过的json字符串
     * @param type      bean类型
     * @param <T>       泛型
     * @return bean
     */
    public static <T> T toBeanFromBase64(String base64Str, Class<T> type) {
        return toBean(Base64Encryptor.decodeToString(base64Str), type);
    }

    /**
     * 从base64码解析AccessToken
     *
     * @param base64Str accesstoken str
     * @return AccessToken
     */
    public static AccessToken toAccessToken(String base64Str) {
        return toBeanFromBase64(base64Str, AccessToken.class);
    }

    /**
     * 获取json中的某个字段值
     *
     * @param json json字符串
     * @return 字段值
     */
    public static Object getValue(String json, String name) {
        try {
            HashMap map = mapper.readValue(json, HashMap.class);

            return map.get(name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取泛型Collection JavaType
     *
     * @param collectionClass 泛型的Collection
     * @param elementClasses  元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    public static JavaType getJavaType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
