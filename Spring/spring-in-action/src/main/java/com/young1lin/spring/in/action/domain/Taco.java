package com.young1lin.spring.in.action.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/27 7:53 下午
 */
@Data
@Entity
public class Taco {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date createAt;

    @NotNull
    @Size(min=5,message = "Name must be at least 5 characters long")
    private String name;
    //@ManyToMany(targetEntity = Ingredient.class)
    //@Size(min = 1,message = "You must choose at least 1 ingredient")
    //private List<Ingredient> ingredients;

    @PrePersist
    void createdAt(){
        this.createAt = new Date();
    }
}
