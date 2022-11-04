package com.mis.mic.model;

public class MIC_Conversionfactor {

	String itemno;

	String uom;
	
	public MIC_Conversionfactor(String ItemNumber,Double ConvFactor,String UOM)
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

	public Double getCf() {
		return cf;
	}

	public void setCf(Double cf) {
		this.cf = cf;
	}

	Double cf;

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}
}
