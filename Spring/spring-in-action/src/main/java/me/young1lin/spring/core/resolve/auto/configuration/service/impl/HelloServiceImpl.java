package me.young1lin.spring.core.resolve.auto.configuration.service.impl;

import me.young1lin.spring.core.resolve.auto.configuration.service.HelloService;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/28 7:49 上午
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello() {
        return "Hello World";
    }

}
