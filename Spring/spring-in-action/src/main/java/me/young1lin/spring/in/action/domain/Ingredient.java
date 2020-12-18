package me.young1lin.spring.in.action.domain;


import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/27 7:38 下午
 */
@Entity
public class Ingredient {
	@Id
	private String id;
	private String name;
	private Type type;

	public static enum Type {
		WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE;

		public static Type value(String s) {
			Type[] types = Type.values();
			for (Type type : types) {
				if (s.toUpperCase().equals(type.toString())) {
					return type;
				}
			}
			return null;
		}
	}

	public Ingredient() {
	}

	public Ingredient(String id, String name, Type type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
