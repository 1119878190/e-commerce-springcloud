package com.imooc.ecommerce.controller;


import com.imooc.ecommerce.service.SleuthTraceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 打印跟踪信息
 */
@Slf4j
@RestController
@RequestMapping("/sleuth")
public class SleuthTraceInfoController {


    private final SleuthTraceInfoService sleuthTraceInfoService;

    public SleuthTraceInfoController(SleuthTraceInfoService sleuthTraceInfoService) {
        this.sleuthTraceInfoService = sleuthTraceInfoService;
    }

    /**
     * 打印日志跟踪信息
     */
    @GetMapping("/trace-info")
    public void logCurrentTraceInfo() {
        sleuthTraceInfoService.logCurrentTraceInfo();
    }


}
