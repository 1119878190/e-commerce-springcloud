package com.imooc.ecommerce.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <h1>通用响应对象定义</h1>
 * {
 * <p>
 * "code":0,
 * "message":"",
 * "data":{}
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> implements Serializable {


    /**
     * 响应码
     */
    private Integer code;
    /**
     * 消息
     */
    private String message;
    /**
     * 泛型响应数据
     */
    private T data;


    public CommonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
