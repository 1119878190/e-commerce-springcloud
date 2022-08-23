package com.imooc.ecommerce.order;

import com.imooc.ecommerce.goods.DeductGoodsInventory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1> 订单信息 </h1>
 *
 * @Author: lafe
 * @DateTime: 2022/8/23 22:09
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "用户发起购买订单")
public class OrderInfo {


    @ApiModelProperty(value = "用户地址表主键 id")
    private Long userAddress;

    @ApiModelProperty(value = "订单商品信息")
    private List<OrderItem> orderItems;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(description = "订单中的单项商品信息")
    public static class OrderItem {

        @ApiModelProperty(value = "商品表主键 id")
        private Long goodsId;

        @ApiModelProperty(value = "购买商品个数")
        private Integer count;


        public DeductGoodsInventory toDeductGoodsInventory() {
            return new DeductGoodsInventory(this.goodsId, this.count);
        }

    }

}
