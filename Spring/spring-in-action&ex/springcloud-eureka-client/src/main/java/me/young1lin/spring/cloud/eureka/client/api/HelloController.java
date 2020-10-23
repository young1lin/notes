package me.young1lin.spring.cloud.eureka.client.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/25 3:51 下午
 */
@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Eureka Client application";
    }

    @GetMapping("/user")
    public String getUser(){
        Random random = new Random();
        int id = random.nextInt();
        return "name：张三，id："+id+"age：24";
    }
}
