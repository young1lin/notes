package me.younglin.springex.annotation;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/13 7:51 下午
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ComAnnotation {
}
