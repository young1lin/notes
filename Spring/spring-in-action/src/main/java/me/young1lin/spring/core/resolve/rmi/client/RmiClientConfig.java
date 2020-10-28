package me.young1lin.spring.core.resolve.rmi.client;

import me.young1lin.spring.core.resolve.rmi.service.HelloRmiService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/22 8:15 上午
 */
@Configuration
public class RmiClientConfig {
    @Bean
    public RmiProxyFactoryBean myClient(){
        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
        rmiProxyFactoryBean.setServiceUrl("rmi://127.0.0.1:7777/helloRMI");
        rmiProxyFactoryBean.setServiceInterface(HelloRmiService.class);
        return rmiProxyFactoryBean;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        String className = RmiClientConfig.class.getName();
        ctx.scan(className.substring(0,className.lastIndexOf(".")));
        ctx.refresh();
        HelloRmiService helloService = ctx.getBean("myClient",HelloRmiService.class);
        System.out.println(helloService.getMessage("\tWord!"));
    }
}
