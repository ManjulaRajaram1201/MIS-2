package com.mis.mpr.model;

public class MPR_SerialNo {
	String orderNumber;
	String lineNumber;
	String itemNumber;
	String serialNumber;
	
/*public MSE_SerialNo(String ordno,String lineno,String itemno,String serialno)
{
	this.orderNumber=ordno;
	this.lineNumber=lineno;
	this.itemNumber=itemno;
	this.serialNumber=serialno;
}
*/
public String getOrderNumber() {
	return orderNumber;
}

public void setOrderNumber(String orderNumber) {
	this.orderNumber = orderNumber;
}

public String getLineNumber() {
	return lineNumber;
}

public void setLineNumber(String lineNumber) {
	this.lineNumber = lineNumber;
}

public String getItemNumber() {
	return itemNumber;
}

public void setItemNumber(String itemNumber) {
	this.itemNumber = itemNumber;
}

public String getSerialNumber() {
	return serialNumber;
}

public void setSerialNumber(String serialNumber) {
	this.serialNumber = serialNumber;
}
}
