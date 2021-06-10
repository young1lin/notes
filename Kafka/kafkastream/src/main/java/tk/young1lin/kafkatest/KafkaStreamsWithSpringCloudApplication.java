package tk.young1lin.kafkatest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/16 9:36 下午
 */
@EnableBinding(Binder.class)
@SpringBootApplication
public class KafkaStreamsWithSpringCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamsWithSpringCloudApplication.class, args);
    }

}
