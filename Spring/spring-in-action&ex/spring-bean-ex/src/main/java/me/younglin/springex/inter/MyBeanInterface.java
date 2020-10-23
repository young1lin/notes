package me.younglin.springex.inter;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/20 11:21 下午
 */
public abstract class MyBeanInterface {

    protected String mood;

    /**
     * 获得心情
     * @return String
     */
    protected abstract String getMood();
}
