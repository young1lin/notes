package me.young1lin.spring.boot.thinking.conditional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/12/15 7:58 上午
 */
public class ConditionOnSystemPropertyBootstrap {

    public static void main(String[] args) {
        System.setProperty("language", "Chinese");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ConditionalMessageConfiguration.class);
        context.refresh();
        String message = context.getBean("message", String.class);
        System.out.printf("message bean object is  %s", message);
    }

}
