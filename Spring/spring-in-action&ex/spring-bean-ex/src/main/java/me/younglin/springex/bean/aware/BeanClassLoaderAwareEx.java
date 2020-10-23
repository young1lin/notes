package me.younglin.springex.bean.aware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.stereotype.Component;

/**
 * {@link BeanClassLoaderAware }获取当前加载 Bean Class 的 ClassLoader 对象
 * @author young1lin
 * @version 1.0
 * @date 2020/7/21 3:36 下午
 */
@Slf4j
@Component
public class BeanClassLoaderAwareEx implements BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        log.info("BeanClassLoaderAwareEx classLoader is {} \n",classLoader.getParent());
    }
}
