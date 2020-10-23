package me.young1lin.spring.practice.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import me.young1lin.spring.practice.entity.User;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/23 5:10 下午
 */
@RequestMapping("/rest")
@RestController
public class RestTemplateRestController {

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/users")
    public List<User> getUsers() {
        return restTemplate.getForObject("http://localhost:8080/hello/users", List.class);
    }

    @GetMapping(value = "/user")
    public String getUserList() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        return restTemplate.exchange(
                "http://localhost:8080/user", HttpMethod.GET, entity, String.class).getBody();
    }

    @PostMapping(value = "/user")
    public String createUsers(@RequestBody User product) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<User> entity = new HttpEntity<User>(product, headers);

        return restTemplate.exchange(
                "http://localhost:8080/user", HttpMethod.POST, entity, String.class).getBody();
    }

    @PutMapping(value = "/user/{id}")
    public String updateUser(@PathVariable("id") String id, @RequestBody User product) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<User> entity = new HttpEntity<User>(product, headers);

        return restTemplate.exchange(
                "http://localhost:8080/user/" + id, HttpMethod.PUT, entity, String.class).getBody();
    }

    @DeleteMapping(value = "/user/{id}")
    public String deleteUser(@PathVariable("id") String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<User> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                "http://localhost:8080/user/" + id, HttpMethod.DELETE, entity, String.class).getBody();
    }

}
