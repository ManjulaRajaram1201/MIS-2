package com.mis.mlt.model;

public class MLT_UOMIntenal 
{
	
	String location;
	String itemno;
	Double qc;
	String uom;
	
	public MLT_UOMIntenal(String Location,String ItemNumber,Double QtyCounted,String UOM)
	{
		this.location=Location;
		this.itemno=ItemNumber;
		this.qc=QtyCounted;
		this.uom=UOM;
	}

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

	
	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Double getQc() {
		return qc;
	}

	public void setQc(Double qc) {
		this.qc = qc;
	}
}
