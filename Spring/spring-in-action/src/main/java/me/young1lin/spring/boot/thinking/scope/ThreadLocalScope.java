package me.young1lin.spring.boot.thinking.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 如果要自定义 Scope 要实现这个接口，并且 CustomScopeConfigurer 在里面设置 SCOPE_NAME 和实例。
 * 详情见 {@link me.young1lin.spring.boot.thinking.scope.ScopeConfig}
 * @author young1lin
 * @version 1.0
 * @date 2020/7/22 11:25 下午
 */
public class ThreadLocalScope implements Scope {

	public static final String SCOPE_NAME = "thread-local";

	private final NamedThreadLocal<Map<String, Object>> threadLocal = new NamedThreadLocal<Map<String, Object>>("thread-local-scope") {

		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<>();
		}
	};

	@Override
	public Object get(@NonNull String name,@NonNull ObjectFactory<?> objectFactory) {
		Map<String, Object> context = getContext();
		Object o = context.get(name);
		if (o == null) {
			o = objectFactory.getObject();
			context.put(name, o);
		}
		return null;
	}

	private Map<String, Object> getContext() {
		return threadLocal.get();
	}

	@Override
	public Object remove(String name) {
		Map<String, Object> context = getContext();
		return context.remove(name);
	}

	@Override
	public void registerDestructionCallback(String s, Runnable runnable) {

	}

	@Override
	public Object resolveContextualObject(String name) {
		Map<String, Object> context = getContext();
		return context.get(name);
	}

	@Override
	public String getConversationId() {
		Thread thread = Thread.currentThread();
		return String.valueOf(thread.getId());
	}

}
