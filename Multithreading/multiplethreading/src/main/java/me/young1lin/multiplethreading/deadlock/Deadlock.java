package me.young1lin.multiplethreading.deadlock;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/12/15 11:42 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Deadlock {

    /**
     * @return just tag it
     */
    boolean value() default true;

}
