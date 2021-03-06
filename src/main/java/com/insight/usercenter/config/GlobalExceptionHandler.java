package com.insight.usercenter.config;

import com.insight.util.ReplyHelper;
import com.insight.util.pojo.Reply;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.UnexpectedTypeException;
import java.util.List;

/**
 * @author duxl
 * @date 2017/10/25
 * @remark 异常Advice
 */
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnexpectedTypeException.class)
    public Reply unexpected(UnexpectedTypeException ex) {
        ex.printStackTrace();

        return ReplyHelper.fail("非预期的类型。");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Reply notreadavble(HttpMessageNotReadableException ex) {
        ex.printStackTrace();

        return ReplyHelper.fail("无法处理的请求。");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Reply notsupport(HttpRequestMethodNotSupportedException ex) {
        ex.printStackTrace();

        return ReplyHelper.fail("不支持的请求方式。");
    }

    @ExceptionHandler(BindException.class)
    public Reply ex(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        return ReplyHelper.fail(getError(bindingResult));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Reply invalid(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        return ReplyHelper.fail(getError(bindingResult));
    }

    /**
     * 获取错误信息
     *
     * @param result 错误list
     * @return error
     */
    private String getError(BindingResult result) {
        List<FieldError> list = result.getFieldErrors();

        return "输入参数{" + list.get(0).getField() + "}的值不合法！" + list.get(0).getDefaultMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Reply illegal(IllegalArgumentException ex) {
        ex.printStackTrace();

        return ReplyHelper.invalidParam(ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Reply missing(MissingServletRequestParameterException ex) {
        ex.printStackTrace();

        return ReplyHelper.invalidParam(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Reply runtime(RuntimeException ex) {
        ex.printStackTrace();

        return ReplyHelper.fail("处理异常。");
    }

    @ExceptionHandler(Exception.class)
    public Reply error(Exception ex) {
        ex.printStackTrace();

        return ReplyHelper.error();
    }
}
