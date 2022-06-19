package com.imooc.ecommerce.advice;


import com.imooc.ecommerce.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常捕获处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {


    /**
     * 捕获所有异常处理
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {Exception.class})
    public CommonResponse<String> handlerCommerceException(HttpServletRequest request, Exception ex) {

        CommonResponse<String> response = new CommonResponse<>(500, "business error");
        response.setData(ex.getMessage());

        log.error("commerce service has error : [{}]", ex.getMessage(), ex);

        return response;

    }


}
