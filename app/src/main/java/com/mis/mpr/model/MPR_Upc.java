package com.mis.mpr.model;


public class MPR_Upc {
	String item_number;
	String bar_code;

	/*public MPR_Upc(String it_no, String bar_code) {
		this.item_number = it_no;
		this.bar_code = bar_code;
	}
*/
	public String getItem_number() {
		return item_number;
	}

	public void setItem_number(String item_number) {
		this.item_number = item_number;
	}

	public String getBar_code() {
		return bar_code;
	}

	public void setBar_code(String bar_code) {
		this.bar_code = bar_code;
	}

}
