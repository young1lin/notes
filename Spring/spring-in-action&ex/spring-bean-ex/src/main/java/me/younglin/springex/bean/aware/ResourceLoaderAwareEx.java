package me.younglin.springex.bean.aware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * {@link ResourceLoaderAware} 获取资源加载器对象，ResourceLoader
 * @author young1lin
 * @version 1.0
 * @date 2020/7/21 3:39 下午
 */
@Slf4j
@Component
public class ResourceLoaderAwareEx implements ResourceLoaderAware {
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {

        log.info("ResourceLoaderAwareEx is {} \n",resourceLoader.toString());
    }
}
