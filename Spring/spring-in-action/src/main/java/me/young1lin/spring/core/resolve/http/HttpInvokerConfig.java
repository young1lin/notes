package me.young1lin.spring.core.resolve.http;

import me.young1lin.spring.core.resolve.rmi.client.RmiClientConfig;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/27 7:54 上午
 */
@Configuration
public class HttpInvokerConfig {

	/**
	 *
	 * @param httpInvokerTest j111
	 * @return Http
	 * @author yyl
	 * @see HttpInvokerConfig
	 */
	@Bean
	public HttpInvokerServiceExporter hit(HttpInvokerTest httpInvokerTest) {
		HttpInvokerServiceExporter ex = new HttpInvokerServiceExporter();
		ex.setService(httpInvokerTest);
		ex.setServiceInterface(HttpInvokerTest.class);
		return ex;
	}


	@Bean
	public ServletWebServerFactory servletWebServerFactory() {
		return new TomcatServletWebServerFactory(8080);
	}

	public static void main(String[] args) {
		String className = RmiClientConfig.class.getName();
		String currentPackage = className.substring(0, className.lastIndexOf("."));
		ServletWebServerApplicationContext ctx = new AnnotationConfigServletWebServerApplicationContext(currentPackage);
		//ctx.setServletConfig(new TestDispatcherServlet());
		//ctx.setParent();
	}

}
