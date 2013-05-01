package com.eightkmiles.parse;

public class ChartObject {
	private String name;
	private double value = 0;

	public ChartObject(String name, double value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public double getValue() {
		return value;
	}
}
