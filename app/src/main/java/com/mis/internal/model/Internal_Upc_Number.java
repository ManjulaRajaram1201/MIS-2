package com.mis.internal.model;

import java.io.Serializable;

public class Internal_Upc_Number implements Serializable {

	private String itemno;
	private String upcCode;
	public String getItemno() {
		return itemno;
	}
	public void setItemno(String itemno) {
		this.itemno = itemno;
	}
	public String getUpcCode() {
		return upcCode;
	}
	public void setUpcCode(String upcCode) {
		this.upcCode = upcCode;
	}
	

	

}
