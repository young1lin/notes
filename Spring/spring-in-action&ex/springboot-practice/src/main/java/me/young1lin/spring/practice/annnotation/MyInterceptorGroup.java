package me.young1lin.spring.practice.annnotation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author young1lin
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier
@Component
public @interface MyInterceptorGroup {
    @AliasFor(
            annotation = Component.class
    )
    String value() default "";

    @AliasFor("value")
    String name() default "";

}
