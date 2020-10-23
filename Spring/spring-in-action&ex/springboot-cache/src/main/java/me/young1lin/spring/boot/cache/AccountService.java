package me.young1lin.spring.boot.cache;

import lombok.extern.slf4j.Slf4j;
import me.young1lin.spring.boot.cache.domain.Account;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/17 7:13 下午
 */
@Service
@Slf4j
public class AccountService {

    @Cacheable(value = "accountCache")
    public Account getAccountByName(String name){
        log.info("name is [{}]",name);
        Account account = getFromDB(name);
        Objects.requireNonNull(account,"返回数据为空");
        return account;
    }

    private Account getFromDB(String name){
        log.info("从数据库中返回========");
        return new Account(name);
    }

    @CachePut(value = "accountCache",key = "#name")
    public void save(String name){
        log.info("缓存数据=======[{}]",name);
    }
}
