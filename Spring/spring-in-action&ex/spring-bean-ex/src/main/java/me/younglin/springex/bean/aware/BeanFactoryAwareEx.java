package me.younglin.springex.bean.aware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/21 8:45 上午
 */
@Slf4j
@Component("beanFactoryAwareEx")
public class BeanFactoryAwareEx implements BeanFactoryAware {


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // 实际这里的 beanFactory 是 DefaultListableBeanFactory
        DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) beanFactory;
        // 正常情况下，这里 get 到是 null
        // 但是在 mvc 框架下，他能 get 到
        log.info("null======{} \n" , dbf.getParentBeanFactory());
    }

}
