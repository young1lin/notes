package me.younglin.springex.bean.aware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * {@link ApplicationEventPublisherAware} 获取 ApplicationEventPublisher 用于 Spring 事件
 * @author young1lin
 * @version 1.0
 * @date 2020/7/21 3:42 下午
 */
@Slf4j
@Component
public class ApplicationEventPublisherAwareEx implements ApplicationEventPublisherAware {

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        //AnnotationConfigServletWebServerApplicationContext ac = (AnnotationConfigServletWebServerApplicationContext) applicationEventPublisher;
        log.info("ApplicationEventPublisherAwareEx parentBeanFactory is {} \n",applicationEventPublisher);
    }
}
