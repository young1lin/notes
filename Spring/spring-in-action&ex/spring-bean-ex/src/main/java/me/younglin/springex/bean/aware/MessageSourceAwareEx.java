package me.younglin.springex.bean.aware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * {@link MessageSourceAware} 获取 MessageSource 对象，用于国际化
 * @author young1lin
 * @version 1.0
 * @date 2020/7/21 3:41 下午
 */
@Slf4j
@Component
public class MessageSourceAwareEx implements MessageSourceAware {

    @Override
    public void setMessageSource(@NonNull MessageSource messageSource) {
        log.info("MessageSourceAwareEx is {} \n",messageSource);
    }
}
