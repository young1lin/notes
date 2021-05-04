package me.young1lin.springsecurity;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/4 下午11:32
 * @version 1.0
 */
public class PasswordEncoderTest {

	/**
	 * The BCryptPasswordEncoder implementation uses the widely supported bcrypt algorithm
	 * to hash the passwords. In order to make it more resistent to password cracking,
	 * bcrypt is deliberately slow. Like other adaptive one-way functions,
	 * it should be tuned to take about 1 second to verify a password on your system.
	 * The default implementation of BCryptPasswordEncoder uses strength 10 as mentioned
	 * in the Javadoc of BCryptPasswordEncoder. You are encouraged to tune and test the
	 * strength parameter on your own system so that it takes roughly 1 second to verify a password
	 */
	@Test
	public void bCryptTest() {
		// Create an encoder with strength 16
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
		String result = encoder.encode("myPassword");
		assertTrue(encoder.matches("myPassword", result));
		System.out.println(result);
	}

	/**
	 * The Argon2PasswordEncoder implementation uses the Argon2 algorithm to hash the passwords.
	 * Argon2 is the winner of the Password Hashing Competition. In order to defeat password
	 * cracking on custom hardware, Argon2 is a deliberately slow algorithm that requires
	 * large amounts of memory. Like other adaptive one-way functions,
	 * it should be tuned to take about 1 second to verify a password on your system.
	 * The current implementation if the Argon2PasswordEncoder requires BouncyCastle.
	 */
	@Test
	public void argon2Test() {
		// Create an encoder with all the defaults
		Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
		String result = encoder.encode("myPassword");
		assertTrue(encoder.matches("myPassword", result));
		System.out.println(result);
	}

	/**
	 * The Pbkdf2PasswordEncoder implementation uses the PBKDF2 algorithm to hash the passwords.
	 * In order to defeat password cracking PBKDF2 is a deliberately slow algorithm.
	 * Like other adaptive one-way functions, it should be tuned to take about 1 second
	 * to verify a password on your system. This algorithm is a good choice when FIPS certification is required.
	 */
	@Test
	public void pbkdf2Test() {
		// Create an encoder with all the defaults
		Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder();
		String result = encoder.encode("myPassword");
		assertTrue(encoder.matches("myPassword", result));
		System.out.println(result);
	}

	/**
	 * The SCryptPasswordEncoder implementation uses scrypt algorithm to hash the passwords.
	 * In order to defeat password cracking on custom hardware scrypt is a deliberately
	 * slow algorithm that requires large amounts of memory.
	 * Like other adaptive one-way functions, it should be tuned to
	 * take about 1 second to verify a password on your system.
	 */
	@Test
	public void sCryptTest(){
		// Create an encoder with all the defaults
		SCryptPasswordEncoder encoder = new SCryptPasswordEncoder();
		String result = encoder.encode("myPassword");
		assertTrue(encoder.matches("myPassword", result));
		System.out.println(result);
	}

}
