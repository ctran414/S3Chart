package com.eightkmiles.parse;

import java.util.ArrayList;

public class ChartCategory {
	private String name;
	private ArrayList<ChartObject> data = new ArrayList<ChartObject>();
	
	public ChartCategory(String name) {
		this.setName(name);
	}
	
	public void add(ChartObject object) {
		data.add(object);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<ChartObject> getData() {
		return data;
	}
}
