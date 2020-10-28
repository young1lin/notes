package me.young1lin.spring.core.resolve.rmi.server;

import me.young1lin.spring.core.resolve.rmi.service.HelloRmiService;
import org.springframework.stereotype.Component;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/22 8:01 上午
 */
@Component
public class HelloRmiServiceImpl implements HelloRmiService {

    @Override
    public String getMessage(String name) {
        return "Hello" + name;
    }
}
