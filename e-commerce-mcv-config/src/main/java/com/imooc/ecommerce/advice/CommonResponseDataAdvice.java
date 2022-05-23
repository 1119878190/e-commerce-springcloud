package com.imooc.ecommerce.advice;


import com.imooc.ecommerce.annotation.IgnoreResponseAdvice;
import com.imooc.ecommerce.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * <h1>实现统一响应</h1>
 */
@RestControllerAdvice(value = "com.imooc.ecommerce")
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 判断是否需要对响应进行处理
     *
     * @param methodParameter
     * @param aClass
     * @return
     */
    @Override
    @SuppressWarnings("all") // 屏蔽警告信息
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {

        // 如果返回结果的类或者方法上有@IgnoreResponseAdvice,则不处理(不走我们的统一返回对象,CommonResponse)
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class))
            return false;
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class))
            return false;

        return true;
    }

    /**
     * 对响应的数据进行处理
     *
     * @param o                  要返回的数据,controller的响应
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // 定义最终的返回对象
        CommonResponse<Object> response = new CommonResponse<>(200, "");

        if (o == null) {
            // 如果要返回的数据为null,则直接返回
            return response;
        } else if (o instanceof CommonResponse) {
            // 如果已经是CommonResponse了,强转返回
            response = (CommonResponse<Object>) o;
        } else {
            response.setData(o);
        }
        return response;
    }
}
