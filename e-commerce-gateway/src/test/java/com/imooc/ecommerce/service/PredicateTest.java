package com.imooc.ecommerce.service;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * java8 predicate 使用方法与思想
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class PredicateTest {


    public static List<String> MICRO_SERVICE = Arrays.asList("nacos", "authority", "gateway", "ribbon", "feign", "hystrix", "e-commerce");


    /**
     * test 方法主要用于参数符不符合规则，返回值是boolean
     */
    @Test
    public void testPredicateTest() {
        Predicate<String> letterLengthLimit = s -> s.length() > 5;
        MICRO_SERVICE.stream().filter(letterLengthLimit).forEach(System.out::println);
    }

    /**
     * and方法等同于我们的逻辑与 &&， 存在短路特性，需要所有的条件都满足
     *
     * negate
     *
     * isEqual
     *
     * or
     *
     */
    @Test
    public void testPredicateAnd() {
        Predicate<String> letterLengthLimit = s -> s.length() > 5;
        Predicate<String> letterStartWith = s -> s.startsWith("gate");
        MICRO_SERVICE.stream().filter(letterLengthLimit.and(letterStartWith)).forEach(System.out::println);
    }


}
