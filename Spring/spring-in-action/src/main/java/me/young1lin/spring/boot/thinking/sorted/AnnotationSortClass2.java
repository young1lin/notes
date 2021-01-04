package me.young1lin.spring.boot.thinking.sorted;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 上午8:38
 * @version 1.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class AnnotationSortClass2 extends PrintRunnerAdapter{
}
