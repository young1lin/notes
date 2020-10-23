package me.younglin.springex.bean.aware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/21 10:14 上午
 */
@Slf4j
@Component
public class ApplicationContextAwareEx implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("ApplicationContextAwareEx this real class name is :===== {}", applicationContext.getClass().getName());
        //AnnotationConfigServletWebServerApplicationContext acs = (AnnotationConfigServletWebServerApplicationContext) applicationContext;
        log.info("this parentBeanFactory is ========{} \n", applicationContext);
    }
}
