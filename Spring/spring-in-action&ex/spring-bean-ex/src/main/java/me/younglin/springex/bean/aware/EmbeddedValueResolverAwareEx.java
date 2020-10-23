package me.younglin.springex.bean.aware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * {@link EmbeddedValueResolverAware} 获取 StringValueResolver  对象，用于对占位符的处理
 * @author young1lin
 * @version 1.0
 * @date 2020/7/21 3:45 下午
 */
@Slf4j
@Component
public class EmbeddedValueResolverAwareEx implements EmbeddedValueResolverAware {
    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        log.info("EmbeddedValueResolverAwareEx is {} \n",stringValueResolver);
    }
}
