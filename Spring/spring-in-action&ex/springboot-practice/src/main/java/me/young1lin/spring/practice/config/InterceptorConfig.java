package me.young1lin.spring.practice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加拦截器
 * @author young1Lin
 * @GitHub www.github.com/young1lin
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    /**
     * 用于国际化
     */
    @Resource
    private LocaleChangeInterceptor localeChangeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePathPatterns = new ArrayList<>(2);
        excludePathPatterns.add("/js/*.js");
        excludePathPatterns.add("/css/*.css");
        registry.addInterceptor(new AccessLogInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludePathPatterns)
                .order(-1);
        registry.addInterceptor(localeChangeInterceptor);
    }
}
