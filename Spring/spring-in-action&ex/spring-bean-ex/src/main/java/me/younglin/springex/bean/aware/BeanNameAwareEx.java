package me.younglin.springex.bean.aware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/20 10:50 下午
 */
@Component
@Slf4j
public class BeanNameAwareEx implements BeanNameAware {

    @Override
    public void setBeanName(String s) {
        log.info("Class {} s BeanName is {} \n ",BeanNameAwareEx.class.getName(),s);
    }
}
