package me.younglin.springex.bean.aware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.Arrays;
import java.util.Map;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/21 3:22 下午
 */
@Slf4j
@Component
public class EnvironmentAwareEx implements EnvironmentAware {

    @Override
    public void setEnvironment(Environment environment) {
        // 实际是 org.springframework.web.context.support.StandardServletEnvironment 类
        log.info(environment.getClass().getName());
       // StandardServletEnvironment s = (StandardServletEnvironment)environment;
/*        Map<String,Object> map = s.getSystemEnvironment();
        map.forEach((k,v) -> {
            log.info("key : {}   value: {}",k,v);
        });*/
        //String[] strArray = s.getDefaultProfiles();
        //log.info(Arrays.toString(strArray)+"\n");
    }
}
