package me.younglin.springex.bean;

import lombok.extern.slf4j.Slf4j;
import me.younglin.springex.annotation.MyAnnotation;
import me.younglin.springex.inter.MyBeanInterface;
import org.springframework.stereotype.Component;


/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/11 8:52 下午
 */
@Slf4j
@MyAnnotation(mood = MyBean2.mood,getMyClass = MyBean2.class)
@Component("myBean2")
public class MyBean2 extends MyBeanInterface {

    public static final String mood = "happy";

    public MyBean2(){
    }

    @Override
    public  String getMood(){
        return mood;
    }
}
