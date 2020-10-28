/*
package com.young1lin.springinaction.service.impl;

import com.young1lin.springinaction.domain.SysUser;
import com.young1lin.springinaction.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

*/
/**
 * @author young1lin
 * @version 1.0
 * @date 2020/9/7 5:19 下午
 *//*

@Service
public class UserDetailServiceImpl implements UserDetailsService {


    private final SysUserRepository userRepository;

    @Autowired
    public UserDetailServiceImpl(SysUserRepository userRepository){
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        SysUser user = userRepository.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("User +"+username+" not found");
        }
        return user;
    }
}
*/
