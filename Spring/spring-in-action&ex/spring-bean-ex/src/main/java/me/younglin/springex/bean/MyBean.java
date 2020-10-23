package me.younglin.springex.bean;

import me.younglin.springex.annotation.MyAnnotation;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import me.younglin.springex.inter.MyBeanInterface;


/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/11 8:52 下午
 */
@MyAnnotation(mood = MyBean.mood,getMyClass = MyBean.class)
@Component("myBean")
@Primary
public class MyBean extends MyBeanInterface {

    public static final String mood = "sad";

    public MyBean(){

    }

    @Override
    protected String getMood() {
        return mood;
    }
}
