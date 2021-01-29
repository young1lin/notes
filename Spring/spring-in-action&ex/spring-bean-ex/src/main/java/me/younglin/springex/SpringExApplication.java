package me.younglin.springex;

import me.younglin.springex.bean.UserMergeClass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Yasir_Lin
 */
@SpringBootApplication(scanBasePackages={"me.younglin.springex.bean"})
public class SpringExApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext context =  SpringApplication.run(SpringExApplication.class, args);
        //  UserMergeClass userMergeClass = context.getBean(UserMergeClass.class);
    }


}
