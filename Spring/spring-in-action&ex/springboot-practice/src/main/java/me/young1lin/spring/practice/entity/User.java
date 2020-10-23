package me.young1lin.spring.practice.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/23 4:30 下午
 */
@Data
@ToString
public class User {
    private String name;
    private String id;
    private Integer age;
}
