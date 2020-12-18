package me.young1lin.spring.in.action.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/27 7:15 下午
 */
@Controller
public class HomeController extends BaseController {

    @GetMapping("/")
    public String home(){
        return "home";
    }
}
