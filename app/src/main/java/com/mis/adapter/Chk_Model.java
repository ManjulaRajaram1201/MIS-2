package com.mis.adapter;

public class Chk_Model {

	private String name;
	private String name1;
	private boolean selected;

	public Chk_Model(String name, boolean selected) {

		this.name = name;
		this.selected = selected;
	}
	public Chk_Model(String name,String name1, boolean selected) {
		this.name1=name1;
		this.name = name;
		this.selected = selected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getName1() {
		return name1;
	}
	public void setName1(String name1) {
		this.name1 = name1;
	}

}