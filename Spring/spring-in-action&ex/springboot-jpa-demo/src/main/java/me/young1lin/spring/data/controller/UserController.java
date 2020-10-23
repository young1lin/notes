package me.young1lin.spring.data.controller;

import me.young1lin.spring.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import me.young1lin.spring.data.entity.UserEntity;

import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/11 4:34 下午
 */
@RestController
public class UserController {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/save")
    public UserEntity save(){
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("123456@gmail.com");
        userEntity.setName("王五");
        return userRepository.save(userEntity);
    }

    @GetMapping("/all")
    public List<UserEntity> all(){

        return (List<UserEntity>) userRepository.findAll();
    }
}
