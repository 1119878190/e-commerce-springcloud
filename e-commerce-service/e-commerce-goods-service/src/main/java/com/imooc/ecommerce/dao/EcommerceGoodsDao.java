package com.imooc.ecommerce.dao;

import com.imooc.ecommerce.constant.BrandCategory;
import com.imooc.ecommerce.constant.GoodsCategory;
import com.imooc.ecommerce.entity.EcommerceGoods;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * EcommerceGoods Dao 接口定义
 * <p>
 * 继承 PagingAndSortingRepository 而不是 JpaRepository 是为了分页查询
 */
public interface EcommerceGoodsDao extends PagingAndSortingRepository<EcommerceGoods, Long> {


    /**
     * 根据查询调教查询商品表，并限制返回结果
     * select * from t_ecommerce_goods where goods_category = ? and brand_category = ?
     * and goods_name = ? limit 1;
     *
     * @return
     */
    Optional<EcommerceGoods> findFirst1ByGoodsCategoryAndBrandCategoryAndGoodsName(GoodsCategory goodsCategory, BrandCategory brandCategory, String goodsName);
}
