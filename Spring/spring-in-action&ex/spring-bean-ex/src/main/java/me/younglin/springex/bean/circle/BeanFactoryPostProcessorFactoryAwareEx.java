package me.younglin.springex.bean.circle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 实现了{@link BeanPostProcessor}{@link BeanFactoryAware}  两个接口的类，会先初始化，这时候Bean还没开始创建，
 * 所以这里获得的 CircleBeanTwo 会是空的
 * 具体代码在
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/7/20 11:51 下午
 */
@Component
@Slf4j
@Configurable
public class BeanFactoryPostProcessorFactoryAwareEx implements BeanPostProcessor, BeanFactoryAware {

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        /*if(beanFactory != null){
            throw new NullPointerException();
        }*/
        // 这个 BeanFactory 默认是 DefaultListableBeanFactory
        // 该类中 内含了一个 BeanFactory
        // 向下转型为解决 Bean 的循环依赖的类
        DefaultSingletonBeanRegistry d = (DefaultSingletonBeanRegistry) beanFactory;
        CircleBeanTwo circleBeanTwo = (CircleBeanTwo) d.getSingleton("circleBeanTwo");
        if (circleBeanTwo == null) {
            log.info("I' am empty");
        } else {
            log.info(circleBeanTwo.toString());
        }
        Map<String, Object> map = (Map<String, Object>) d.getSingletonMutex();
       /* map.forEach((k, v) -> {
            log.info("this singleton Object" + k, v);
        });*/

    }
}
