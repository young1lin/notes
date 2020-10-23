package me.young1lin.spring.practice.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/24 12:28 下午
 */
@Slf4j
@Component
public class Scheduler {

    @Scheduled(cron = "0 * 9 *  * ?")
    public void cronJobSch(){
        //每分钟执行一次
        log.info("演示如何在每天上午9:00开始到每天上午9:59结束执行任务。");
    }

    /**
     * 每秒执行的任务
     * 毫秒为单位
     *//*
    @Scheduled(fixedRate = 1000)
    public void fixedRateSch() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Date now = new Date();
        String strDate = sdf.format(now);
        log.info("Fixed Rate scheduler:: {}",strDate);
    }
*/
   /* *//**
     * initialDelay 从应用程序启动完成 xx 毫秒之后，每秒执行一次任务
     *//*
    @Scheduled(fixedDelay = 1000, initialDelay = 3000)
    public void fixedDelaySch() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println("Fixed Delay scheduler:: " + strDate);
    }*/
}
