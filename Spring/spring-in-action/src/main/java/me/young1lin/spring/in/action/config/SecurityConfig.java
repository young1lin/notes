/*
package com.young1lin.springinaction.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import javax.sql.DataSource;

*/
/**
 * @author young1lin
 * @version 1.0
 * @date 2020/9/7 10:34 上午
 *//*

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private DataSource dataSource;

    private UserDetailsService userDetailService;

// 基于 SQL 配置 Spring Security
*/
/*    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery("SELECT username,password,enabled FROM Users where username=?")
            .authoritiesByUsernameQuery("SELECT username,authority FROM UserAuthorities where username=?")
        .passwordEncoder(new StandardPasswordEncoder("53cr3t"));
    }*//*

// 基于内存
*/
/*    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("buzz")
                .password("infinity")
                .authorities("ROLE_USE")
                .and()
                .withUser("woody")
                .password("bullseye")
                .authorities("ROLE_USER");
    }*//*


    */
/*    *//*
*/
/**
     * 使用 LDAP 作为后端的用户存储
     *
     * @param auth
     * @throws Exception
     *//*
*/
/*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
                .userSearchBase("ou=people")
                .userSearchFilter("(uid={0})")
                .groupSearchBase("ou=groups")
                .groupSearchFilter("member={0}")
                .passwordCompare()
        .passwordEncoder(new BCryptPasswordEncoder())
        .passwordAttribute("passcode");
    }*//*


    */
/**
     * @param auth
     * @throws Exception
     *//*

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService)
        .passwordEncoder(encoder());
    }

    private PasswordEncoder encoder() {
        return new StandardPasswordEncoder();
    }

}*/
