package com.mis.mse.model;

public class MSE_OrderDetails {
	String ordNumber;
	String custNumber;
	String itemNumber;
	String itemDescription;
	String uom;

	Integer qtyOrdred;
	Integer qtyShiped;
	String location;
	String shipViaCode;
	String shipviaDescription;
	String comments;


	String lineNumber;
	String pickingSequence;
	
	public MSE_OrderDetails(){}

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

	public String getShipViaCode() {
		return shipViaCode;
	}

	public void setShipViaCode(String shipViaCode) {
		this.shipViaCode = shipViaCode;
	}

	public String getShipviaDescription() {
		return shipviaDescription;
	}

	public void setShipviaDescription(String shipviaDescription) {
		this.shipviaDescription = shipviaDescription;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getPickingSequence() {
		return pickingSequence;
	}

	public void setPickingSequence(String pickingSequence) {
		this.pickingSequence = pickingSequence;
	}
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	
	/*public MSE_OrderDetails(String ordNo, String custNo, String itemNo,
			String itemDesc, String uom, int qtyOrdrd, int qtyShipd, String loc,
			String viaCode, String viaDesc, String comments, String lineNo,
			String pickingSeq) {
		// TODO Auto-generated constructor stub
		this.ordNumber=ordNo;
		this.custNumber=custNo;
		this.itemNumber=itemNo;
		this.itemDescription=itemDesc;
		this.uom=uom;
		this.qtyOrdred=qtyOrdrd;
		this.qtyShiped=qtyShipd;
		this.location=loc;
		this.shipViaCode=viaCode;
		this.shipviaDescription=viaDesc;
		this.comments=comments;
		this.lineNumber=lineNo;
		this.pickingSequence=pickingSeq;
	}
*/

}
