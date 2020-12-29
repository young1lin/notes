package me.young1lin.spring.boot.thinking.listener.generics;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/29 上午8:22
 * @version 1.0
 */
public class User {

	private final String name;

	public User(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User{" +
				"name='" + name + '\'' +
				'}';
	}

}
