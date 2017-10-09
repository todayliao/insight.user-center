package com.apin.usercenter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2017/10/06
 * @remark 调试信息过滤器
 */
@Component
@WebFilter(urlPatterns = {"/*"})
public class LogFilter implements Filter {
    private FilterConfig filterConfig;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 初始化方法,传入过滤器配置
     *
     * @param filterConfig FilterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        if (this.filterConfig == null) logger.debug("FilterConfig is NULL!");
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

        // 打印URL
        String method = request.getMethod();
        String path = request.getRequestURI();
        logger.debug("[" + method + "]" + path);

        // 打印请求头信息
        logger.debug("<-- Request Headers -->");
        Enumeration<String> headerList = request.getHeaderNames();
        while (headerList.hasMoreElements()) {
            String headerName = headerList.nextElement();
            String header = request.getHeader(headerName);
            logger.debug(headerName + ":" + header);
        }

        // 打印请求参数
        Map<String, String[]> map = request.getParameterMap();
        if (!map.isEmpty()) {
            logger.debug("<-- Request Parameters -->");
            map.forEach((k, v) -> logger.debug(k + ":" + v[0]));
        }

        if (!"POST,PUT".contains(method)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // POST或PUT请求,打印请求体
        BodyReaderRequestWrapper requestWrapper = new BodyReaderRequestWrapper(request);
        BufferedReader reader = requestWrapper.getReader();

        String inputLine;
        StringBuilder body = new StringBuilder();
        while ((inputLine = reader.readLine()) != null) {
            body.append(inputLine);
        }

        reader.close();
        if (map.isEmpty()) logger.debug("<-- Request Parameters -->");

        logger.debug("BODY:" + body);

        filterChain.doFilter(requestWrapper, servletResponse);
    }

    /**
     * 释放过滤器
     */
    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}
