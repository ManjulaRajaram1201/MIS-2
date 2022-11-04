package com.mis.mpr.model;


public class MPR_ConversionFactor {
	String itemno;
	Double cf;
	String uom;
	
	public MPR_ConversionFactor(String ItemNumber,Double ConvFactor,String UOM)
	{
		this.itemno=ItemNumber;
		this.cf=ConvFactor;
		this.uom=UOM;
	}
	public String getItemno() {
		return itemno;
	}
	public void setItemno(String itemno) {
		this.itemno = itemno;
	}
	
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public Double getCf() {
		return cf;
	}
	public void setCf(Double cf) {
		this.cf = cf;
	}
}
