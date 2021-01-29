package me.younglin.springex.bean;

import lombok.*;
import me.younglin.springex.bean.circle.CircleBeanOne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.HashMap;

/**
 * 合并类的时候，如果存在循环依赖，则会使得栈溢出
 *
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
public class MergeSubClass extends MergeClass {

	//如果依赖 循环 创建的对象，会启动就报错
	@Autowired
	CircleBeanOne circleBeanOne;

	private String address = "杭州";

}
