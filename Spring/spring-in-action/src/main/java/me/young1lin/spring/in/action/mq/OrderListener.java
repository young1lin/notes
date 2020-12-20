package me.young1lin.spring.in.action.mq;

import me.young1lin.spring.in.action.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/17 3:19 下午
 * @version 1.0
 */
@Component
public class OrderListener {

	private static final Logger log = LoggerFactory.getLogger(OrderListener.class);

	@KafkaListener(topics = "TacoCloud.orders.topic",groupId = "yyl_test")
	public void handle(Order order) {
		log.info("new order [{}]", order.toString());
		//, ConsumerRecord<String, Order> record
		//log.info("key is [{}],value is [{}]", record.key(), record.value());
		log.info("============================================");
	}
}
