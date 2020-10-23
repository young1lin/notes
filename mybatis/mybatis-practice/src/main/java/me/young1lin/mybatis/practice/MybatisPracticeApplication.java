package me.young1lin.mybatis.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableConfigurationProperties
@EnableTransactionManagement
public class MybatisPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPracticeApplication.class, args);
    }

}
