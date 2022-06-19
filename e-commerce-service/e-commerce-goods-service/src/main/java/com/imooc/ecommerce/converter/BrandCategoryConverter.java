package com.imooc.ecommerce.converter;

import com.imooc.ecommerce.constant.BrandCategory;

import javax.persistence.AttributeConverter;

/**
 * 品牌分类枚举属性转换器
 */
public class BrandCategoryConverter implements AttributeConverter<BrandCategory, String> {
    @Override
    public String convertToDatabaseColumn(BrandCategory brandCategory) {
        return brandCategory.getCode();
    }

    @Override
    public BrandCategory convertToEntityAttribute(String code) {
        return BrandCategory.of(code);
    }
}
