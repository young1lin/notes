package me.young1lin.spring.practice.config;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author young1Lin
 * @github www.github.com/young1lin
 */
@Slf4j
@WebFilter(urlPatterns = {"/**"})
public class ApiFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext sx = filterConfig.getServletContext();

        /*String contextName = filterConfig.getServletContext().getContextPath();
        log.info("上下文环境为：{}",contextName);
        Enumeration<String> names = filterConfig.getServletContext().getAttributeNames();
        while(names.hasMoreElements()){
            String name = names.nextElement();
            log.info(name);
            Object o  = filterConfig.getServletContext().getAttribute(name);
            System.out.println(o);
        }*/
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        filterChain.doFilter(servletRequest,servletResponse);
        long endTime = System.currentTimeMillis();
        log.info("--------------costTime : [{}] ms",endTime-startTime);
    }

    @Override
    public void destroy() {
        log.info("APIFilter destroyed");
    }
}
