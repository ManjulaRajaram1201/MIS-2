package com.mis.mse.model;

public class MSE_Trans {
	String ordNumber;
	String custNumber;
	String lineNumber;
	String itemNumber;
	String itemDescription;
	String Uom;
	Integer qtyOrdered;
	Integer qtyShipped;

	String shipVia;
	String comments;
	String shipNumber;
	String shipDay;
	String shipMonth;
	String shipYear;
	Integer status;

	/*
	 * public MSE_Trans(String ordno, String custno, String lineno, String
	 * itemno, String itemdesc, String uom, Integer qtyord, Integer qtyship,
	 * String shipvia, String comments, String shipno, String shipday, String
	 * shipmon, String shipyear, Integer status) { this.ordNumber = ordno;
	 * this.custNumber = custno; this.lineNumber = lineno; this.itemNumber =
	 * itemno; this.itemDescription = itemdesc; this.Uom = uom; this.qtyOrdered
	 * = qtyord; this.qtyShipped=qtyship; this.shipVia=shipvia;
	 * this.comments=comments; this.shipNumber=shipno; this.shipDay=shipday;
	 * this.shipMonth=shipmon; this.shipYear=shipyear; this.status=status; }
	 */

	public String getOrdNumber() {
		return ordNumber;
	}

	public void setOrdNumber(String ordNumber) {
		this.ordNumber = ordNumber;
	}

	public String getCustNumber() {
		return custNumber;
	}

	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
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

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getUom() {
		return Uom;
	}

	public void setUom(String uom) {
		Uom = uom;
	}

	public Integer getQtyOrdered() {
		return qtyOrdered;
	}

	public void setQtyOrdered(Integer qtyOrdered) {
		this.qtyOrdered = qtyOrdered;
	}

	public Integer getQtyShipped() {
		return qtyShipped;
	}

	/*
	 * public void setQtyShipped(Integer qtyShipped) { this.qtyShipped =
	 * qtyShipped; }
	 */
	public String getShipVia() {
		return shipVia;
	}

	public void setShipVia(String shipVia) {
		this.shipVia = shipVia;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getShipNumber() {
		return shipNumber;
	}

	public void setShipNumber(String shipNumber) {
		this.shipNumber = shipNumber;
	}

	public void setQtyShipped(Integer qtyShipped) {
		this.qtyShipped = qtyShipped;
	}

	public String getShipDay() {
		return shipDay;
	}

	public void setShipDay(String shipDay) {
		this.shipDay = shipDay;
	}

	public String getShipMonth() {
		return shipMonth;
	}

	public void setShipMonth(String shipMonth) {
		this.shipMonth = shipMonth;
	}

	public String getShipYear() {
		return shipYear;
	}

	public void setShipYear(String shipYear) {
		this.shipYear = shipYear;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
