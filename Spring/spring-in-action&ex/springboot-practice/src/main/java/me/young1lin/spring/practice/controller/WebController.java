package me.young1lin.spring.practice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/24 11:25 上午
 */
@Slf4j
@Controller
public class WebController {

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping("/locale")
    public String locale(){
        return "locale";
    }
}
