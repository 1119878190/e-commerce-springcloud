package com.imooc.ecommerce.service;

import com.imooc.ecommerce.common.TableId;
import com.imooc.ecommerce.order.OrderInfo;
import com.imooc.ecommerce.vo.PageSimpleOrderDetail;

/**
 * <h1> 订单线管服务接口定义 </h1>
 *
 * @Author: lafe
 * @DateTime: 2022/8/23 22:29
 **/
public interface IOrderService {


    /***
     *  下单(分布式事务) : 创建订单 -> 扣减库存 -> 扣减余额 -> 创建物流信息 （Stream + kafka）
     * @param orderInfo
     * @return
     */
    TableId createOrder(OrderInfo orderInfo);


    PageSimpleOrderDetail getSimpleOrderDetailByPage(int page);

}
