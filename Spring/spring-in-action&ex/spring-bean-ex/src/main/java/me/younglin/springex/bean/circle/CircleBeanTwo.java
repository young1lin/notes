package me.younglin.springex.bean.circle;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/20 11:50 下午
 */
@Slf4j
@Component("circleBeanTwo")
public class CircleBeanTwo {

    @Autowired
    private CircleBeanOne circleBeanOne;

    public CircleBeanTwo(){
        log.info(String.valueOf(circleBeanOne));
        log.info("Bean CircleBean  Two is initializing\n");
    }

    @Override
    public String toString() {
        return "CircleBeanTwo{" +
                "circleBeanOne=" + circleBeanOne +
                '}';
    }

}
