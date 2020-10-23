package me.young1lin.spring.practice.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import me.young1lin.spring.practice.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/23 3:05 下午
 */
@RequestMapping("/hello")
@RestController
@CrossOrigin(origins = {"http://localhost:9090"})
public class HelloController {


    @RequestMapping("/hello-spring-boot")
    public String hello() {
        return "Hello Spring Boot";
    }

    @GetMapping("/{id}")
    public String id(@PathVariable("id") String id, @RequestParam(value = "name", required = false, defaultValue = "张三") String name, @RequestBody @RequestParam(value = "user", required = false) User user) {
        return "current Id is" + id + " name is" + name;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User user = getRandomUser();
            users.add(user);
        }
        return users;
    }

    @GetMapping("/user")
    public User getUser(){
        return getRandomUser();
    }

    @PostMapping("/user")
    private ResponseEntity<Object> addUser(User user) {
        System.out.println(user);
        // add User operation
        return new ResponseEntity<>("add user success", HttpStatus.OK);
    }

    @PutMapping("/user")
    public ResponseEntity<Object> updateUser(User user){
        System.out.println(user);
        // update user operation
        return new ResponseEntity<>("update user success", HttpStatus.OK);
    }

    @DeleteMapping("/user")
    public ResponseEntity<Object> deleteUser(User user){
        System.out.println(user);
        String id = user.getId();
        // delete user operation by id
        return new ResponseEntity<>("delete user success", HttpStatus.OK);
    }

    @GetMapping("/exception")
    public ResponseEntity<Object> exception() throws Exception {
        throw new Exception();
    }

    private User getRandomUser() {
        User user = new User();
        Random random = new Random();
        int age = random.nextInt();
        int id = random.nextInt();
        user.setAge(age);
        user.setName("李四");
        user.setId(String.valueOf(id));
        return user;
    }

}