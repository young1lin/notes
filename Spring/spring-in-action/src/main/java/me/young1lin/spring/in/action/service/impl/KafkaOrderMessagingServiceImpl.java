package me.young1lin.spring.in.action.service.impl;

import me.young1lin.spring.in.action.domain.Order;
import me.young1lin.spring.in.action.service.OrderMessagingService;

import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/17 1:46 下午
 * @version 1.0
 */
@Service("kafkaOrderMessagingServiceImpl")
@Primary
public class KafkaOrderMessagingServiceImpl implements OrderMessagingService {

	private final KafkaTemplate<String,Order> kafkaTemplate;

	/**
	 * 这里其实不用 @AutoWired Spring 会根据构造方法去 newInstance 自动注入 Bean
	 * @param kafkaTemplate kafkaTemplate to send message
	 */
	public KafkaOrderMessagingServiceImpl(KafkaTemplate<String,Order> kafkaTemplate){
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void sendOrder(Order order) {
		kafkaTemplate.send("TacoCloud.orders.topic",order);
	}

}
