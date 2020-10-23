package me.younglin.springex.annotation;


import java.lang.annotation.*;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/20 10:26 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAnnotation {

    String mood() default "";

    Class<?> getMyClass();
}
