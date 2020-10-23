package me.young1lin.spring.practice.config;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/27 2:54 下午
 */
public interface MyInterceptor extends HandlerInterceptor {
    /**
     * 1
     * @param name
     * @param classLoader
     * @param clazz
     */
    default void postProcess(String name,ClassLoader classLoader,Class<? extends Object> clazz){
    }
}
