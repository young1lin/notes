package com.young1lin.spring.in.action.controller;

import com.young1lin.spring.in.action.repository.SysUserRepository;
import com.young1lin.spring.in.action.form.RegistrationForm;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/9/9 3:09 下午
 */
@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    @Autowired
    private SysUserRepository sysUserRepository;

    //private PasswordEncoder passwordEncoder;

    @GetMapping
    public String registerForm(){
        return "registration";
    }

    @PostMapping
    public String processRegistration(RegistrationForm form){
        //sysUserRepository.save(form.toUser(passwordEncoder));
        sysUserRepository.save(form.toUser());
        return "redirect:/login";
    }
}
