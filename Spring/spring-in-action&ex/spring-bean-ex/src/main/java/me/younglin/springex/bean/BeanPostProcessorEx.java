package me.younglin.springex.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import me.younglin.springex.annotation.MyAnnotation;
import me.younglin.springex.util.BeanUtils;

import javax.annotation.Resource;

/**
 * {@link AutowiredAnnotationBeanPostProcessor}
 * {@link BeanPostProcessor}
 * {@link BeanFactoryAware}
 * 对 bean 的初始化做前后处理
 * 可以对特定 Bean 拦截，然后进行处理，Spring data jpa 就是这么干的
 * 进行 @AutoWired 解析，也是这么干的
 * 如果只实现了其中一个接口，那么会慢于
 * @author young1lin
 * @version 1.0
 * @date 2020/7/11 8:27 下午
 */
@Component
@Slf4j
public class BeanPostProcessorEx implements BeanPostProcessor, BeanFactoryAware {

    @Resource
    Environment environment;

    public BeanPostProcessorEx() {

    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (BeanUtils.isContainAnnotation(bean, MyAnnotation.class)) {
            log.info("BeforeInitialization==============");
            log.info(getAnnotationInfo(bean, beanName));
        }
       /* if (Objects.isNull(environment)){
            log.info("Environment is empty");
        }else {
            log.info("env is {}",environment);
        }*/
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (BeanUtils.isContainAnnotation(bean, MyAnnotation.class)) {
            log.info("AfterInitialization==============");
            log.info(getAnnotationInfo(bean, beanName));
        }
        return bean;
    }

    @Override
    public String toString() {

        return "this is FactoryBeanEx";
    }


    private String getAnnotationInfo(Object o, String beanName) {
        MyAnnotation myAnnotation = o.getClass().getAnnotation(MyAnnotation.class);
        return " I am  " + myAnnotation.mood() + " and myBeanName is" + beanName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        log.info(beanFactory.toString());
    }
}
