package com.apin.usercenter.config;

import com.apin.util.ReplyHelper;
import com.apin.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 宣炳刚
 * @date 2017/9/25
 * @remark 统一异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Reply errorHandler(Exception e) throws Exception {
        e.printStackTrace();
        logger.error(e.getMessage());

        return ReplyHelper.error();
    }
}
