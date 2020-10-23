package me.young1lin.spring.data.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/11 4:27 下午
 */
@Data
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

}
