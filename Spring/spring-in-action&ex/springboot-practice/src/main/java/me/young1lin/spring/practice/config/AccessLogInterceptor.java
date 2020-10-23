package me.young1lin.spring.practice.config;

import lombok.extern.slf4j.Slf4j;
import me.young1lin.spring.practice.util.DateUtil;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

/**
 * @author young1Lin
 */
@Slf4j
public class AccessLogInterceptor implements HandlerInterceptor {

    private ThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<>("ThreadLocal StartTime");

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler) {
        startTimeThreadLocal.set(System.currentTimeMillis());
        if(handler instanceof HandlerMethod){
            String date = DateUtil.getCurrentDateTime();
            HandlerMethod method = (HandlerMethod)handler;
            log.info("<====================="+date+"=====================>");
            log.info("Controller: "+method.getBean().getClass().getName());
            log.info("Method    : "+method.getMethod().getName());
            log.info("Params    : "+getParamString(request.getParameterMap()));
            log.info("URI       : "+request.getRequestURI());
        }
        return true;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull Object handler, ModelAndView modelAndView) {
        Long endTime = System.currentTimeMillis();
        if(startTimeThreadLocal == null){
            log.error("startTimeThreadLocal has be collection");
            return;
        }
        Long startTime = startTimeThreadLocal.get();
        long costTime = endTime - startTime;
        if(handler instanceof HandlerMethod){
            String s = "<==========================="+ costTime +  "ms=============================>";
            log.info(s);
        }

    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        // 打印JVM信息。
        //得到线程绑定的局部变量（开始时间）
        long beginTime = startTimeThreadLocal.get();
        //2、结束时间
        long endTime = System.currentTimeMillis();
        // 如果controller报错，则记录异常错误
        if(ex != null){
            log.debug("Controller异常: " + getStackTraceAsString(ex));
        }

        log.info("计时结束：" + " 耗时：" + (endTime - beginTime) + " URI:" +
                request.getRequestURI()+ " 最大内存: " +Runtime.getRuntime().maxMemory()/1024/1024+ "m  已分配内存: " +Runtime.getRuntime().totalMemory()/1024/1024+ "m  已分配内存中的剩余空间: " +Runtime.getRuntime().freeMemory()/1024/1024+ "m  最大可用内存: " +
                (Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory()+Runtime.getRuntime().freeMemory())/1024/1024 + "m");
        startTimeThreadLocal.remove();
    }
    /**
     * 将栈异常信息打印出来
     * @param e Throwable
     * @return java.lang.String
    */
    private String getStackTraceAsString(Throwable e) {
        if (e == null){
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * 将请求参数转换为String类型
     * @param map requestMap
     * @return java.lang.String
    */
    private String getParamString(Map<String, String[]> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach((k,v)->{
            sb.append(k)
              .append("=");
            if(v != null && v.length==1){
                sb.append(v[0])
                        .append("\t");
            }else{
                sb.append(Arrays.toString(v))
                        .append("\t");
            }
        });
        return sb.toString();
    }
}
