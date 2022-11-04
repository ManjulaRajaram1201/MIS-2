package com.mis.mpr.model;

public class MPR_OrderDetails {
	String po_number;
	String vd_code;
	String item_no;
	String desc;
	String uom;
	Integer ordered_qty;
	Integer received_qty;
	String comments;
	String loc_code;
	String line_no;

	/*public MPR_OrderDetails(String po_no, String vdcode, String it_no,
			String desc, String uom, Integer ord_qty, Integer rec_qty,
			String comments, String loc_code, String line_no) {
		// TODO Auto-generated constructor stub

		this.po_number = po_no;
		this.vd_code = vdcode;
		this.item_no = it_no;
		this.desc = desc;
		this.uom = uom;
		this.ordered_qty = ord_qty;
		this.received_qty = rec_qty;
		this.comments = comments;
		this.loc_code = loc_code;
		this.line_no = line_no;

	}*/

	public String getPo_number() {
		return po_number;
	}

	public void setPo_number(String po_number) {
		this.po_number = po_number;
	}

	public String getVd_code() {
		return vd_code;
	}

	public void setVd_code(String vd_code) {
		this.vd_code = vd_code;
	}

	public String getItem_no() {
		return item_no;
	}

	public void setItem_no(String item_no) {
		this.item_no = item_no;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Integer getOrdered_qty() {
		return ordered_qty;
	}

	public void setOrdered_qty(Integer ordered_qty) {
		this.ordered_qty = ordered_qty;
	}

	public Integer getReceived_qty() {
		return received_qty;
	}

	public void setReceived_qty(Integer received_qty) {
		this.received_qty = received_qty;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getLoc_code() {
		return loc_code;
	}

	public void setLoc_code(String loc_code) {
		this.loc_code = loc_code;
	}

	public String getLine_no() {
		return line_no;
	}

	public void setLine_no(String line_no) {
		this.line_no = line_no;
	}

}