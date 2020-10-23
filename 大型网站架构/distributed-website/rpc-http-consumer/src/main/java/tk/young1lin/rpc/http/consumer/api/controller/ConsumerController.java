package tk.young1lin.rpc.http.consumer.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import tk.young1lin.rpc.rest.http.JsonResult;
import tk.young1lin.rpc.rest.http.entity.ServiceEntity;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 4:41 下午
 */
@RestController
public class ConsumerController {

    private final RestTemplate restTemplate;

    @Autowired
    public ConsumerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/hello")
    public JsonResult hello(@RequestParam(value = "name", required = false, defaultValue = "张三") String name) throws URISyntaxException {
        System.out.println(name);
        String service = "tk.young1lin.rpc.http.provider.service.sayHelloServiceImpl";
        String format = "json";
        String arg1 = "hello";
        Map<String, Object> requestMap = new HashMap<>(5);
        requestMap.put("service", service);
        requestMap.put("arg1", arg1);
        requestMap.put("format", format);
        String url = "http://127.0.0.1:8080/p";
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("service", service);
        map.add("arg1", arg1);
        //String gUrl = "https://www.googleapis.com/customsearch/v1?key=<YOUR API KEY>&cx=<your google custom search engine>&q=java&as_filetype=png";
        JsonResult jsonResult = restTemplate.postForObject(url, map, JsonResult.class);
        //restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        //String resultUrl = restTemplate.getForObject(gUrl, String.class, "Android");
        //JsonResult jsonResult = JsonResultBuilder.create().resultCode(200).result(resultUrl).message("success").build();
        return jsonResult;
    }

    /**
     *  实际测试用这个才好用
     * @return JsonResult
     */
    @GetMapping("/hello2")
    public JsonResult hello2() {
        String service = "tk.young1lin.rpc.http.provider.service.sayHelloServiceImpl";
        String format = "json";
        String arg1 = "hello";
        ServiceEntity serviceEntity = new ServiceEntity(service, arg1);
        String url = "http://127.0.0.1:8080/p";
        return restTemplate.getForObject(url, JsonResult.class, serviceEntity);
    }

    private HttpHeaders getHttpHeaders() {
        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
