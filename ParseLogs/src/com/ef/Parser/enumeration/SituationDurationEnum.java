package com.ef.Parser.enumeration;

/**
 * 
 * @author Silvio
 *
 */
public enum SituationDurationEnum {

	HOURLY(1, "hourly"),
	DAILY(2, "daily");
	
	private Integer id;

	private String description;

	SituationDurationEnum(Integer id, String description) {
		this.id = id;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
	
}
