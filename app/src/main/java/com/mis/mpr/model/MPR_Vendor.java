package com.mis.mpr.model;

public class MPR_Vendor {

	String vendor_id;
	String vendor_name;
	String street1;
	String street2;
	String namecity;
	String code_state;
	String code_country;
	String code_pstl;
	String phone;
	
	/*
	public MPR_Vendor(String ven_id, String ven_name, String st1,
			String st2, String city, String state, String country,
			String pstl, String ph) {
		// TODO Auto-generated constructor stub
		this.vendor_id=ven_id;
		this.vendor_name=ven_name;
		this.street1=st1;
		this.street2=st2;
		this.namecity=city;
		this.code_state=state;
		this.code_country=country;
		this.code_pstl=pstl;
		this.phone=ph;
	}
*/
	public String getVendor_id() {
		return vendor_id;
	}
	public void setVendor_id(String vendor_id) {
		this.vendor_id = vendor_id;
	}
	public String getVendor_name() {
		return vendor_name;
	}
	public void setVendor_name(String vendor_name) {
		this.vendor_name = vendor_name;
	}
	public String getStreet1() {
		return street1;
	}
	public void setStreet1(String street1) {
		this.street1 = street1;
	}
	public String getStreet2() {
		return street2;
	}
	public void setStreet2(String street2) {
		this.street2 = street2;
	}
	public String getNamecity() {
		return namecity;
	}
	public void setNamecity(String namecity) {
		this.namecity = namecity;
	}
	public String getCode_state() {
		return code_state;
	}
	public void setCode_state(String code_state) {
		this.code_state = code_state;
	}
	public String getCode_country() {
		return code_country;
	}
	public void setCode_country(String code_country) {
		this.code_country = code_country;
	}
	public String getCode_pstl() {
		return code_pstl;
	}
	public void setCode_pstl(String code_pstl) {
		this.code_pstl = code_pstl;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}
