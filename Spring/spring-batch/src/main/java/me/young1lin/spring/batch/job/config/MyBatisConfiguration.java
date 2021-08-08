package me.young1lin.spring.batch.job.config;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.context.annotation.Configuration;

/**
 * use default datasource config.
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/8/7 上午9:25
 * @version 1.0
 */
@Configuration
@MapperScan("me.young1lin.spring.*.mapper")
public class MyBatisConfiguration {

}
