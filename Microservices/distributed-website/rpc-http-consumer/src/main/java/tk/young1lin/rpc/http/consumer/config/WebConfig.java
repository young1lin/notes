package tk.young1lin.rpc.http.consumer.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 4:49 下午
 */
@Configuration
public class WebConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder,MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
/*        final List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(mappingJackson2HttpMessageConverter);
        RestTemplate restTemplate = builder.defaultHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("application",MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .messageConverters(messageConverters)
                .build();*/
        //return restTemplate;
        return builder.build();
    }
}
