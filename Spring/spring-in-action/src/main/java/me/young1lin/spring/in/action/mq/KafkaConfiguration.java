package me.young1lin.spring.in.action.mq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.young1lin.spring.in.action.domain.Order;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/17 11:00 上午
 * @version 1.0
 */
@Configuration
@Deprecated
public class KafkaConfiguration {
//
//	@Value("${spring.kafka.bootstrap-servers}")
//	private String bootstrapServers;
//
//	@Bean
//	public Map<String, Object> producerConfigs() {
//		Map<String, Object> props = new HashMap<>(3);
//		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//		return props;
//	}
//
//	@Bean
//	public ProducerFactory<String, Order> producerFactory() {
//		return new DefaultKafkaProducerFactory<>(producerConfigs());
//	}
//
//	@Bean
//	public KafkaTemplate<String, Order> kafkaTemplate() {
////		KafkaTemplate<String, Order> kafkaTemplate = new KafkaTemplate<>(producerFactory());
////		kafkaTemplate.setMessageConverter(new JsonMessageConverter());
////		return kafkaTemplate;
//		return new KafkaTemplate<>(producerFactory());
//	}
}
