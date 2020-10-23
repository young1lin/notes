package me.younglin.springex.bean.circle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/20 11:50 下午
 */
@Slf4j
@Component("circleBeanOne")
public class CircleBeanOne {

    @Resource
    private CircleBeanTwo circleBeanTwo;

    public CircleBeanOne(){
        log.info("Current circleBeanTwo is {}",circleBeanTwo);
        log.info("Bean circleBean  One is initializing \n");
    }

    @Override
    public String toString() {
        return "CircleBeanOne{" +
                "circleBeanTwo=" + circleBeanTwo +
                '}';
    }
}
