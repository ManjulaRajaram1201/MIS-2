package com.mis.mlt.model;

public class MLT_UPC
{
	String itemno;
	String upc;
	public MLT_UPC(String ItemNo,String UPC)
	{
		this.itemno=ItemNo;
		this.upc=UPC;
	}
	public MLT_UPC()
	{
		
	}
	public String getItemno() {
		return itemno;
	}
	public void setItemno(String itemno) {
		this.itemno = itemno;
	}
	public String getUpc() {
		return upc;
	}
	public void setUpc(String upc) {
		this.upc = upc;
	}
}
