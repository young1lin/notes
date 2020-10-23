package com.young1lin.spring.in.action.controller;

import com.young1lin.spring.in.action.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/27 8:26 下午
 */
@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {
    @GetMapping("/current")
    public String orderForm(Model model){
        model.addAttribute("order",new Order());
        return "orderForm";
    }
    @PostMapping
    public String processOrder(@Valid Order order, Errors errors){
        if(errors.hasErrors()){
            return "orderForm";
        }
        log.info("Order submitted:[{}]",order);
        return "redirect:/";
    }
}
