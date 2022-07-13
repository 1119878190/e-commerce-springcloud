package com.imooc.ecommerce.stream.controller;

import com.imooc.ecommerce.stream.DefaultSendService;
import com.imooc.ecommerce.stream.custom.CustomSendService;
import com.imooc.ecommerce.vo.TestMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <h1></h1>
 *
 * @Author: lafe
 * @DateTime: 2022/7/13 22:34
 **/
@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private DefaultSendService defaultSendService;

    @Resource
    private CustomSendService customSendService;

    /**
     * 默认信道
     */
    @GetMapping("/default")
    public void defaultSend() {
        defaultSendService.sendMessage(TestMessage.defaultMessage());
    }

    /**
     * 自定义信道
     */
    @GetMapping("/custom")
    public void customSend() {
        customSendService.sendMessage(TestMessage.defaultMessage());
    }
}
