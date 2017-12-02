package com.insight.usercenter.config;

import java.io.Serializable;

/**
 * @author duxl
 * @date 2017/11/22
 * @remark BusinessException
 */
public class BusinessException extends RuntimeException implements Serializable {

    public BusinessException(String message) {
        super(message);
    }

}