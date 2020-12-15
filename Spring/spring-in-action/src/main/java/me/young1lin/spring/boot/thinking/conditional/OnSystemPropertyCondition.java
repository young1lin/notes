package me.young1lin.spring.boot.thinking.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/12/15 7:46 上午
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OnSystemPropertyCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // get all of ConditionalOnSystemProperty method attribute value
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(ConditionalOnSystemProperty.class.getName());
        // get all of ConditionalOnSystemProperty#name value
        String propertyName = (String) attributes.getFirst("name");
        // get all of ConditionalOnSystemProperty#value value
        String propertyValue = (String) attributes.getFirst("value");
        // get SystemProperty value
        String systemPropertyValue = System.getProperty(propertyName);
        // match SystemPropertyValue and ConditionalOnSystemProperty#value is equals
        if (Objects.equals(systemPropertyValue, propertyValue)) {
            System.out.printf("SystemProperty [name: %s] find match [value: %s\n]", propertyName, propertyValue);
            return true;
        }
        return false;
    }

}
