package com.mis.mlt.model;

public class MLT_Inventory
{
	/**
	 * 
	 */
	String itemno;
	String desc;

	


	public MLT_Inventory(String ItemNumber,String Description)
	{
		this.itemno=ItemNumber;
		this.desc=Description;
	}
	public MLT_Inventory()
	{
	}
	
	public String getItemno() {
		return itemno;
	}
	public void setItemno(String itemno) {
		this.itemno = itemno;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}



}
