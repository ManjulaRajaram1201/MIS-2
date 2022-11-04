package com.mis.mpr.model;

import java.io.Serializable;

public class Manf_Number01_mpr implements Serializable {

	String mn;
	String itemno;

/*	public Manf_Number01_mpr(String ManuNumber, String ItemNumber) {
		this.itemno = ItemNumber;
		this.mn = ManuNumber;
	}
*/
	public String getMn() {
		return mn;
	}

	public void setMn(String mn) {
		this.mn = mn;
	}

	public String getItemno() {
		return itemno;
	}

	public void setItemno(String itemno) {
		this.itemno = itemno;
	}

}
