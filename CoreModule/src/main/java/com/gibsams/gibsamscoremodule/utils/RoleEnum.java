package com.gibsams.gibsamscoremodule.utils;

/**
 * Enum class for the different user roles available
 * 
 * @author jackl
 *
 */
public enum RoleEnum {

	USER(1, "USER"), ADMIN(2, "ADMIN");

	private int id;

	private String name;

	RoleEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
