package com.lianghua.adapter.web;

import com.alibaba.cola.exception.BizException;
import com.alibaba.cola.dto.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleBizException(BizException e) {
        log.warn("Business exception: {}", e.getMessage());
        Response response = new Response();
        response.setSuccess(false);
        response.setErrCode("BIZ_ERROR");
        response.setErrMessage(e.getMessage());
        return response;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        Response response = new Response();
        response.setSuccess(false);
        response.setErrCode("ILLEGAL_ARGUMENT");
        response.setErrMessage(e.getMessage());
        return response;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleException(Exception e) {
        log.error("Unexpected error", e);
        Response response = new Response();
        response.setSuccess(false);
        response.setErrCode("SYSTEM_ERROR");
        response.setErrMessage("Internal server error: " + e.getMessage());
        return response;
    }
}
