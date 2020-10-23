package me.younglin.springex.util;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/20 11:08 下午
 */
public class BeanUtils {

    private BeanUtils() {
    }

    public static boolean isContainAnnotation(Object target, Class<? extends Annotation> clazz) {
        return !Objects.isNull(target.getClass().getAnnotation(clazz));
    }
}
