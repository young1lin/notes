package me.young1lin.spring.in.action.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.MessageChannel;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/21 4:34 下午
 * @version 1.0
 */
@Configuration
public class FileWriterIntegrationConfig {

	@Bean
	@Transformer(inputChannel = "textChannel", outputChannel = "fileWriterChannel")
	public GenericTransformer<String, String> upperCaseTransformer() {
		return String::toUpperCase;
	}

	@Bean
	@ServiceActivator(inputChannel = "fileWriterChannel")
	public FileWritingMessageHandler fileWriter() {
		FileWritingMessageHandler handler = new FileWritingMessageHandler(new File("/tmp/sia5/files"));
		handler.setExpectReply(false);
		handler.setFileExistsMode(FileExistsMode.APPEND);
		handler.setAppendNewLine(true);
		return handler;
	}

	@Bean
	public MessageChannel textChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel fileWriterChannel() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow fileWriteFlow() {
		return IntegrationFlows.from(MessageChannels.direct("textChannel"))
				.<String, String>transform(String::toUpperCase)
				.handle(Files.outboundAdapter(new File("/tmp/sia5/files"))
						.fileExistsMode(FileExistsMode.APPEND)
						.appendNewLine(true))
						.get();
	}

}
