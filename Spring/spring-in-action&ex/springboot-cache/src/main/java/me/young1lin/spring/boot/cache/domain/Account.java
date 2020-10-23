package me.young1lin.spring.boot.cache.domain;

import lombok.Data;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/17 7:12 下午
 */
@Data
public class Account {
    private String id;
    private String name;

    public Account(String name) {
        this.name = name;
    }
}
