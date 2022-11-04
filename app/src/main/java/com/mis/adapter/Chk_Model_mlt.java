package com.mis.adapter;

public class Chk_Model_mlt {

	private String name;

	private boolean selected;

	public Chk_Model_mlt(String name, boolean selected) {

		this.name = name;
		this.selected = selected;
	}

	public String getName() {
		return name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}



}