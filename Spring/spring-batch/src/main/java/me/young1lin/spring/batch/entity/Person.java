package me.young1lin.spring.batch.entity;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/8/7 上午9:30
 * @version 1.0
 */
public class Person {

	private Long id;

	private String name;

	private Integer age;

	private String email;

	private	BigDecimal balance;


	public Person(){}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Person person = (Person) o;
		return Objects.equals(id, person.id) &&
				Objects.equals(name, person.name) &&
				Objects.equals(age, person.age) &&
				Objects.equals(email, person.email) &&
				Objects.equals(balance, person.balance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, age, email, balance);
	}

	@Override
	public String toString() {
		return "Person{" +
				"id=" + id +
				", name='" + name + '\'' +
				", age=" + age +
				", email='" + email + '\'' +
				", balance=" + balance +
				'}';
	}

}