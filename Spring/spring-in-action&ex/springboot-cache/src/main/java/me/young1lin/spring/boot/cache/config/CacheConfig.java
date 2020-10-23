package me.young1lin.spring.boot.cache.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/17 7:23 下午
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 这里使用 ConcurrentHashMap 进行存储缓存
     * @return
     */
    @Bean
    public CacheManager cacheManager(){
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Collections.singletonList(new ConcurrentMapCache("accountCache")));
        return cacheManager;
    }
}
