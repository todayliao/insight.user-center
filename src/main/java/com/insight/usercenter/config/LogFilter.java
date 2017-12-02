package com.insight.usercenter.config;

import com.insight.usercenter.common.dto.BodyReaderRequestWrapper;
import com.insight.usercenter.common.dto.Log;
import com.insight.usercenter.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2017/10/06
 * @remark 调试信息过滤器
 */
@Component
@WebFilter(urlPatterns = {"/*"})
public class LogFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private FilterConfig filterConfig;

    /**
     * 初始化方法,传入过滤器配置
     *
     * @param filterConfig FilterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        if (this.filterConfig == null) {
            logger.debug("FilterConfig is NULL!");
        }
    }

    /**
     * 拦截请求，通过DEBUG级别日志输出请求内容
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Log log = new Log();
        log.setTime(new Date());
        log.setLevel("DEBUG");

        // 读取客户端IP地址、请求方法和调用的接口URL
        String ip = getIp(request);
        String method = request.getMethod();
        String path = request.getRequestURI();
        log.setSource(ip);
        log.setMethod(method);
        log.setUrl(path);
        logger.info("来源地址:" + ip + " -> 目标接口:[" + method + "]" + path);

        // 读取并解析访问令牌
        String token = request.getHeader("authorization");
        if (token != null && !token.isEmpty()) {
            try {
                log.setToken(JsonUtils.toAccessToken(token));
            } catch (Exception ex) {
                log.setException(ex.getMessage());
            }
        }

        // 读取请求头
        Map<String, String> headers = new HashMap<>(16);
        Enumeration<String> headerList = request.getHeaderNames();
        while (headerList.hasMoreElements()) {
            String headerName = headerList.nextElement();
            String header = request.getHeader(headerName);
            headers.put(headerName, header);
        }
        log.setHeaders(headers);

        // 读取请求参数
        Map<String, String> params = new HashMap<>(16);
        Map<String, String[]> map = request.getParameterMap();
        map.forEach((k, v) -> params.put(k, v[0]));
        log.setParams(params.isEmpty() ? null : params);

        // 如请求方法为GET,则打印日志后结束
        if ("GET".equals(method)) {
            logger.debug(JsonUtils.toJson(log));
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 非GET请求,读取BODY,打印日志
        BodyReaderRequestWrapper requestWrapper = new BodyReaderRequestWrapper(request);
        BufferedReader reader = requestWrapper.getReader();

        String inputLine;
        StringBuilder body = new StringBuilder();
        while ((inputLine = reader.readLine()) != null) {
            body.append(inputLine);
        }
        reader.close();

        String bodyStr = body.toString();
        if (bodyStr != null)
            log.setBody(JsonUtils.toMap(bodyStr));

        logger.debug(JsonUtils.toJson(log));

        filterChain.doFilter(requestWrapper, servletResponse);
    }

    /**
     * 释放过滤器
     */
    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * 获取客户端IP
     *
     * @param request 请求对象
     * @return 客户端IP
     */
    private String getIp(HttpServletRequest request) {
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
     * IP是否为空
     *
     * @param str IP字符串
     * @return 是否为空
     */
    private Boolean isEmpty(String str) {
        return str == null || str.isEmpty() || "unknown".equalsIgnoreCase(str);
    }
}
