package me.young1lin.spring.core.resolve.rmi.server;

import me.young1lin.spring.core.resolve.rmi.service.HelloRmiService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/22 8:03 上午
 */
@Configuration
public class RmiServiceConfig {

    /**
     * 如果 helloRmiService 报红线，不用管
     * 这里用的是 AnnotationConfigApplicationContext 来启动，而不是 ClassPathXmlApplication 来做
     *
     *
     * @param helloRmiService 服务
     * @return rmiServiceExporter
     */
    @Bean
    public RmiServiceExporter helloService(HelloRmiService helloRmiService){
        RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
        // 这里需要和 client 端的 rmi://host:7777/hellRMI 对应起来
        rmiServiceExporter.setServiceName("helloRMI");
        rmiServiceExporter.setService(helloRmiService);
        rmiServiceExporter.setServiceInterface(HelloRmiService.class);
        rmiServiceExporter.setRegistryPort(7777);
        return rmiServiceExporter;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        String className = RmiServiceConfig.class.getName();
        ctx.scan(className.substring(0,className.lastIndexOf(".")));
        ctx.refresh();
    }
    
}
