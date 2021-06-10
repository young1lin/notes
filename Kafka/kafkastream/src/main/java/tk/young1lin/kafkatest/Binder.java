package tk.young1lin.kafkatest;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * {@link SubscribableChannel}
 * {@link MessageChannel}
 * @author young1lin
 * @version 1.0
 * @date 2020/7/16 9:30 下午
 */

public interface Binder {

    String INPUT_TOPIC = "input";

    String OUTPUT_TOPIC = "output";


    /**
     * {@link SubscribableChannel}
     * 声明输入流
     * @return SubscribableChannel
     */
    @Input(INPUT_TOPIC)
    SubscribableChannel newMessage();

    /**
     * 在这声明输出流
     * @return MessageChannel
     */
    @Output(OUTPUT_TOPIC)
    MessageChannel outputMessage();

}
