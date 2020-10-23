package me.young1lin.spring.practice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import me.young1lin.spring.practice.service.UserService;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/23 5:33 下午
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Override
    public void createUser() {
        log.info("我正在被创建");
    }

    @Override
    public void updateUser() {
        log.info("我正在被更新");
    }

    @Override
    public void deleteUser() {
        log.info("我正在被删除");
    }
}
