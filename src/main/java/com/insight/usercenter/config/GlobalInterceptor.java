package com.insight.usercenter.config;

import com.insight.usercenter.common.dto.AccessToken;
import com.insight.usercenter.common.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GlobalInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(GlobalInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String tokenStr = request.getHeader("Authorization");

        if (StringUtils.isEmpty(tokenStr)) {
            return true;
        }

        try {
            AccessToken accessToken = JsonUtils.toAccessToken(tokenStr);
            request.setAttribute("accessToken", accessToken);
            request.setAttribute("userId", accessToken.getUserId());
            request.setAttribute("userName", accessToken.getUserName());
        } catch (Exception e) {
            logger.error(">>>GlobalInterceptor>>>>>>>accssToken error");
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}