package com.young1lin.spring.core.resolve.rmi.service;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/22 8:01 上午
 */
public interface HelloRmiService {
    /**
     * 获取消息
     *
     * @param name 返回消息中带的名字
     * @return 类似 Hello + name 的消息
     */
    String getMessage(String name);
}
