package me.young1lin.spring.in.action.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/9/24 7:50 上午
 */
public class CglibDemo {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(CglibDemo.class);
        enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> {
            System.err.println("Before invoke " + method);
            Object result = methodProxy.invokeSuper(o, objects);
            System.err.println("After invoke" + method);
            return result;
        });

        CglibDemo cglibDemo = (CglibDemo) enhancer.create();
        cglibDemo.test();
        //System.out.println(cglibDemo.toString());
    }

    public void test() {
        System.out.println("I'm test method");
    }
}
