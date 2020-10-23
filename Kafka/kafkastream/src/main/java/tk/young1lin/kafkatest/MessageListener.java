package tk.young1lin.kafkatest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/16 9:34 下午
 */
@Component
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class MessageListener {
    /**
     * {@link Resource} 由JSR 规定，Spring的实现比 Autowired 更先加载
     */
    @Resource
    @Qualifier(Binder.OUTPUT_TOPIC)
    private MessageChannel finishedOrdersMessageChannel;


    @Value("${order.barista-prefix}${random.uuid}")
    private String barista;

    @StreamListener(Binder.INPUT_TOPIC)
    @SendTo(Binder.OUTPUT_TOPIC)
    public String processNewOrder(String id) {
        log.info(barista);
        log.info("这是一条消息，传过来的ID：{}",id);
        id = id + "2";
        return "这是一条消息处理过后的数字："+id+"<<EOF";
    }
}

