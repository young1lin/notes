package me.young1lin.spring.core.resolve.rmi.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/26 8:38 上午
 */
public class ProxyDemo{

    public static void main(String[] args) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addInterface(ProxyClassInterface.class);
        proxyFactory.addAdvice(new ProxyInvocationInterceptor());
        proxyFactory.setTarget(new ProxyClass());
        proxyFactory.setOpaque(false);
        // 这里要在里面打断点并且 debug 运行，不然会报错，来不及创建
         //ProxyClass proxy = (ProxyClass)proxyFactory.getProxy(ProxyDemo.class.getClassLoader());
        // proxy.println();
        // proxy.println();
        Object proxy1 = proxyFactory.getProxy(ProxyDemo.class.getClassLoader());
        // 这里如果执行的话，正常应该会输出三次，直接正常运行是输出一次，在 JdkDynamicAopProxy 50 行打断点一步步执行，才可以
        System.out.println(proxy1.toString());
    }
}

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/26 8:40 上午
 */
interface ProxyClassInterface {
    /**
     * 输出一段话
     */
    void println();
}

class ProxyClass implements ProxyClassInterface{

    @Override
    public void println() {
        System.out.println("输出一段话");
    }
}

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/26 8:43 上午
 */
class ProxyInvocationInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Method method = methodInvocation.getMethod();
        String methodName = ClassUtils.getQualifiedMethodName(method);
        System.out.println(methodName+"方法执行前");
        Object proxyObj = methodInvocation.proceed();
        System.out.println(methodName+"方法执行后");
        return proxyObj;
    }
}