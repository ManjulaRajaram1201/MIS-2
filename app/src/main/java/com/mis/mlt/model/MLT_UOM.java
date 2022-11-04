package com.mis.mlt.model;

public class MLT_UOM 
{
	String itemno;
	String uom;
	public MLT_UOM(String ItemNo,String UOM)
	{
		this.uom=UOM;
		this.itemno=ItemNo;
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

}
