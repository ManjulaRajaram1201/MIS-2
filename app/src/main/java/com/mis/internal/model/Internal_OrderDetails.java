package com.mis.internal.model;

public class Internal_OrderDetails {


	String intNumber;
	
	String costCeneter;
	String itemNumber;
	String itemDescription;
	String uom;

	Integer qtyOrdred;
	Integer qtyShiped;
	String location;

	String iidate;
	String lineNumber;

	public Internal_OrderDetails() {
	}
	public String getIntNumber() {
		return intNumber;
	}

	public void setIntNumber(String intNumber) {
		this.intNumber = intNumber;
	}
	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Integer getQtyOrdred() {
		return qtyOrdred;
	}

	public void setQtyOrdred(Integer qtyOrdred) {
		this.qtyOrdred = qtyOrdred;
	}

	public Integer getQtyShiped() {
		return qtyShiped;
	}

	public void setQtyShiped(Integer qtyShiped) {
		this.qtyShiped = qtyShiped;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public String getCostCeneter() {
		return costCeneter;
	}
	public void setCostCeneter(String costCeneter) {
		this.costCeneter = costCeneter;
	}
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}
	public String getIidate() {
		return iidate;
	}
	public void setIidate(String iidate) {
		this.iidate = iidate;
	}

}
