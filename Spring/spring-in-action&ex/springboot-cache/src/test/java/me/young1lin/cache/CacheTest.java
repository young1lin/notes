package me.young1lin.cache;

import me.young1lin.spring.boot.cache.AccountService;
import me.young1lin.spring.boot.cache.CacheApplication;
import me.young1lin.spring.boot.cache.domain.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/17 7:29 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CacheApplication.class})
public class CacheTest {

    @Autowired
    private AccountService accountService;

    @Test
    public void test(){
        accountService.save("张三");
        accountService.getAccountByName("张三");
    }

}
