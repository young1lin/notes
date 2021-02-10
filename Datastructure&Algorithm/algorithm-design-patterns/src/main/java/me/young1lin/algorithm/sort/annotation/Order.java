package me.young1lin.algorithm.sort.annotation;

import java.lang.annotation.*;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/11/2 3:08 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Order {

	int value() default 0;

}
