package com.imooc.ecommerce.controller;

import com.imooc.ecommerce.account.AddressInfo;
import com.imooc.ecommerce.common.TableId;
import com.imooc.ecommerce.service.IAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户地址服务 controller
 */
@Api(tags = "用户地址服务")
@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController {

    @Resource
    private IAddressService addressService;


    // VALUE 是简述  ，notes是详细的描述信息
    @ApiOperation(value = "创建", notes = "创建用户地址信息", httpMethod = "POST")
    @PostMapping("/createAddress")
    public TableId createAddressInfo(@RequestBody AddressInfo addressInfo) {
        TableId tableId = addressService.createAddressInfo(addressInfo);
        return tableId;
    }

    @ApiOperation(value = "当前用户", notes = "获取当前登录用户地址信息", httpMethod = "GET")
    @GetMapping("/currentAddress")
    public AddressInfo getCurrentAddressInfo() {
        return addressService.getCurrentAddressInfo();
    }

    @ApiOperation(value = "获取用户地址信息"
            , notes = "通过id 获取用户地址信息，id至 EcommerceAddress 表的主键",
            httpMethod = "GET")
    @GetMapping("/addressInfoById")
    public AddressInfo getAddressInfoById(@RequestParam(value = "id") Long id) {
        return addressService.getAddressInfoById(id);
    }

    @ApiOperation(value = "获取用户地址信息", notes = "通过 TableId 获取用户信息", httpMethod = "POST")
    @PostMapping("/addresssInfoByTableId")
    public AddressInfo getAddressInfoByTableId(@RequestBody TableId tableId) {
        return addressService.getAddressInfoByTableId(tableId);
    }
}
