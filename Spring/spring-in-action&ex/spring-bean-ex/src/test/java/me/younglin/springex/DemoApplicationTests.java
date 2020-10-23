package me.younglin.springex;

import me.younglin.springex.service.ScopeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/22 11:45 下午
 */


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApplicationTests.class})
public class DemoApplicationTests implements BeanFactoryAware{

    //获取spring工厂
    private BeanFactory factory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.factory = beanFactory;
    }

    @Test
    public void contextLoads() throws InterruptedException {
        //线程1
        Thread task1 = new Thread(){
            @Override
            public void run() {
                ScopeService service = factory.getBean(ScopeService.class);
                ScopeService service1 = factory.getBean(ScopeService.class);
                ScopeService service2 = factory.getBean(ScopeService.class);
                service.getMessage();
                service1.getMessage();
                service2.getMessage();
            }
        };
        task1.start();
        //线程2
        Thread task2 = new Thread(){
            @Override
            public void run() {
                ScopeService service = factory.getBean("scopeService", ScopeService.class);
                ScopeService service1 = factory.getBean("scopeService", ScopeService.class);
                ScopeService service2 = factory.getBean("scopeService", ScopeService.class);
                service.getMessage();
                service1.getMessage();
                service2.getMessage();
            }
        };
        task2.start();
        //线程3
        Thread task3 = new Thread(){
            @Override
            public void run() {
                ScopeService service = factory.getBean("scopeService", ScopeService.class);
                ScopeService service1 = factory.getBean("scopeService", ScopeService.class);
                ScopeService service2 = factory.getBean("scopeService", ScopeService.class);
                service.getMessage();
                service1.getMessage();
                service2.getMessage();
            }
        };
        task3.start();
        //让主线程睡10s
        Thread.sleep(10000);
    }

}