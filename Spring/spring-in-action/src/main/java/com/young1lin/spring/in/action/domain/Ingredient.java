package com.young1lin.spring.in.action.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/27 7:38 下午
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE,force = true)
@Entity
public class Ingredient {
    @Id
    private final String id;
    private final String name;
    private final Type type;

    public static enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE;

        public static Type value(String s){
            Type[] types = Type.values();
            for (Type type : types){
                if(s.toUpperCase().equals(type.toString())){
                    return type;
                }
            }
            return null;
        }
    }
}
