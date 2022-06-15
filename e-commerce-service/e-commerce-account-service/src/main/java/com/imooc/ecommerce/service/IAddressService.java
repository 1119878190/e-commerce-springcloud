package com.imooc.ecommerce.service;

import com.imooc.ecommerce.account.AddressInfo;
import com.imooc.ecommerce.common.TableId;

/**
 * <h1> 用户地址相关服务接口定义</h1>
 */
public interface IAddressService {


    /**
     * <h2>创建用户地址信息</h2>
     *
     * @param addressInfo
     * @return
     */
    TableId createAddressInfo(AddressInfo addressInfo);


    /**
     * <h2>获取当前登录的用户地址信息</h2>
     *
     * @return
     */
    AddressInfo getCurrentAddressInfo();

    /**
     * 通过 id 获取用户地址信息
     *
     * @param id
     * @return
     */
    AddressInfo getAddressInfoById(Long id);


    /**
     * 通过 ids 获取地址
     *
     * @param tableId
     * @return
     */
    AddressInfo getAddressInfoByTableId(TableId tableId);

}
