package com.mis.mic.model;

public class MIC_Inventory {

	String location;
	String itemno;
	String itemdescription;
	String pickingseq;
	String qoh;
	String qc;
	String stockunit;
	int status;
	
	

	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getItemno() {
		return itemno;
	}
	public void setItemno(String itemno) {
		this.itemno = itemno;
	}
	public String getItemdescription() {
		return itemdescription;
	}
	public void setItemdescription(String itemdescription) {
		this.itemdescription = itemdescription;
	}
	public String getPickingseq() {
		return pickingseq;
	}
	public void setPickingseq(String pickingseq) {
		this.pickingseq = pickingseq;
	}

	public String getStockunit() {
		return stockunit;
	}
	public void setStockunit(String stockunit) {
		this.stockunit = stockunit;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getQoh() {
		return qoh;
	}
	public String getQc() {
		return qc;
	}
	public void setQoh(String qoh) {
		this.qoh = qoh;
	}
	public void setQc(String qc) {
		this.qc = qc;
	}
	
	
}
