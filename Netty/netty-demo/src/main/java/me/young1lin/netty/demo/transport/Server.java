package me.young1lin.netty.demo.transport;

import java.io.IOException;

/**
 * 策略模式的体现，中间加个 AbstractServer 类，定义好启动的方式。留给子类一个抽象方法，然后
 * 让策略类继承 AbstractServer，即是模板模式，多用于已经规定好一系列流程，但是实现各有不同
 * 的类中。
 * 模版模式常用于网络编程中，之前接触的仰邦科技的调用 LED 的 jar 中就是类似这么定义的。=
 * 基于接口而非实现编程，是写出松耦合代码的方式之一
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/10/20 1:15 上午
 */
public interface Server {
    /**
     * 根据端口号创建对应的服务端
     *
     * @param port 端口号
     * @throws IOException IO 异常，绑定端口后，同步方法失败等
     */
    void server(int port) throws IOException;
}
