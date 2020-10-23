package com.young1lin.spring.in.action.form;

import com.young1lin.spring.in.action.domain.SysUser;
import lombok.Data;
//import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/9/9 3:15 下午
 */
@Data
public class RegistrationForm {
    private final String username;
    private final String password;
    private final String fullname;
    private final String street;
    private final String city;
    private final String state;
    private final String zip;
    private final String phone;

/*    public SysUser toUser(PasswordEncoder passwordEncoder){
        SysUser sysUser = new SysUser(username,
                password,fullname,street,
                city,state,zip,phone);
        return sysUser;
    }*/
    public SysUser toUser(){
        SysUser sysUser = new SysUser(username,
                password,fullname,street,
                city,state,zip,phone);
        return sysUser;
    }
}
