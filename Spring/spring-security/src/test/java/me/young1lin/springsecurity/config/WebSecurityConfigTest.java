package me.young1lin.springsecurity.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
public class WebSecurityConfigTest {

	/**
	 * If you are putting together a demo or a sample,
	 * it is a bit cumbersome to take time to hash the passwords of your users.
	 * There are convenience mechanisms to make this easier,
	 * but this is still not intended for production.
	 *
	 * Example 23. withDefaultPasswordEncoder Example
	 */
	@Test
	void defaultPasswordEncoder() {
		// 不推荐生产上使用，只是为了测试样🌰编写
		UserDetails user = User.withDefaultPasswordEncoder()
				.username("user")
				.password("password")
				.roles("user")
				.build();
		System.out.println(user.getPassword());
	}

	/**
	 * If you are creating multiple users, you can also reuse the builder.
	 */
	void setMultipleRolesInDefaultUsers() {
		User.UserBuilder users = User.withDefaultPasswordEncoder();
		UserDetails user = users
				.username("user")
				.password("password")
				.roles("USER")
				.build();
		UserDetails admin = users
				.username("admin")
				.password("password")
				.roles("USER", "ADMIN")
				.build();
	}

}