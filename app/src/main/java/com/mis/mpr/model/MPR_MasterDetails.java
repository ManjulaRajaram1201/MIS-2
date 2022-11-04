package com.mis.mpr.model;

public class MPR_MasterDetails {
	
	String po_number;
	Integer date;
	String vd_code;

	/*public MPR_MasterDetails(String po_no, Integer date, String vd_code) {
		this.po_number = po_no;
		this.date = date;
		this.vd_code = vd_code;
	}*/

	public String getPo_number() {
		return po_number;
	}

	public void setPo_number(String po_number) {
		this.po_number = po_number;
	}

	public Integer getDate() {
		return date;
	}

	public void setDate(Integer date) {
		this.date = date;
	}

	public String getVd_code() {
		return vd_code;
	}

	public void setVd_code(String vd_code) {
		this.vd_code = vd_code;
	}
	
}
