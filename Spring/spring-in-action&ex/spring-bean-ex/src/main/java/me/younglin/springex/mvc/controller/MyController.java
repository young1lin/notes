package me.younglin.springex.mvc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/21 9:41 上午
 */
@Slf4j
@RestController
@RequestMapping("/my")
public class MyController {


    @GetMapping("/getMapping")
    public String getStr(){
        return "Hello World";
    }

}
