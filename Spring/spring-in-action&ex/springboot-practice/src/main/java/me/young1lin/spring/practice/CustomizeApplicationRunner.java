package me.young1lin.spring.practice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/23 3:38 下午
 */
@Slf4j
@Component
public class CustomizeApplicationRunner implements ApplicationRunner {

    /**
     * 可以在这里设置一些东西，例如 把常用数据，初始化到 Redis 缓存中
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("我会在 Spring boot 应用程序启动后执行代码");
    }
}
