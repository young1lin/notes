package me.young1lin.spring.in.action.gateway;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/17 4:02 下午A
 * @version 1.0
 */
@MessagingGateway(defaultRequestChannel = "textInChannel")
public interface FileWriteGateway {

	/**
	 * write to file
	 * @param fileName the name of file
	 * @param data the data of file
	 */
	void writeToFile(@Header(FileHeaders.FILENAME) String fileName, String data);

}
