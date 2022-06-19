package com.imooc.ecommerce.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 品牌分类
 */
@Getter
@AllArgsConstructor
public enum BrandCategory {


    BRAND_A("20001", "品牌A"),
    BRAND_B("20002", "品牌B"),
    BRAND_C("20003", "品牌C"),
    BRAND_D("20004", "品牌D"),
    BRAND_E("20005", "品牌E"),

    ;

    /**
     * 品牌分类编码
     */
    private final String code;

    /**
     * 品牌分类描述信息
     */
    private final String description;


    /**
     * 根据 code  获取 BrandCategory
     *
     * @param code
     * @return
     */
    public static BrandCategory of(String code) {
        Objects.requireNonNull(code);

        return Stream.of(BrandCategory.values())
                .filter(item -> item.getCode().equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + "not exist")
                );
    }

}
