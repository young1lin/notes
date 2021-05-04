package com.young1lin;

import me.young1lin.spring.boot.thinking.ThinkingInSpringBootApplication;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/3/29 下午11:33
 * @version 1.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Te.class)
@TestPropertySource(properties = "spring.config.location = 7777", locations = "application.properties")
public class Te {

	Object o = new Object();

	public static void main(String[] args) {
		Te te = new Te();
		Runnable r = () -> {
			te.test();
			System.out.println(Thread.currentThread().getName() + "222222");
		};
		Thread t1 = new Thread(r);
		Thread t2 = new Thread(r);
		Thread t3 = new Thread(r);
		Thread t4 = new Thread(r);
		t3.start();
		t1.start();
		t2.start();
		t4.start();

	}

	public void test() {
		synchronized (o) {
			System.out.println(Thread.currentThread().getName() + "111");
			o = null;
		}
	}

}
