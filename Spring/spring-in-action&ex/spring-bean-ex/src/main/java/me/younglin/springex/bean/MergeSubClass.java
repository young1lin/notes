package me.younglin.springex.bean;

import lombok.*;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.HashMap;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/6 1:56 下午
 */

@EqualsAndHashCode(callSuper = true)
@Component("mergeSubClass")
@Primary
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class MergeSubClass extends MergeClass{

    //如果依赖 循环 创建的对象，会启动就报错
   /* @Autowired
    CircleBeanOne circleBeanOne;*/

    private String address = "杭州";

    public static void main(String[] args) {
        JButton jButton = new JButton();
        jButton.addActionListener((e)->{

        });
        HashMap<String, Object> map = new HashMap<>(16);
        new JmsTemplate();
    }
}
