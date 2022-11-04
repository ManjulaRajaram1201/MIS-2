package com.mis.common;

public class MIS_Company {

	String Cid;
	String name;
	String address;
	String city;
	String state;
	String zip;
	String country;
	String phone;
	String contact;
	
	public MIS_Company(String CompanyID,String Name,String Address,String City,String State,String ZIP,String Country,String Phone,String Contact)
	{
		this.Cid=CompanyID;
		this.name=Name;
		this.address=Address;
		this.city=City;
		this.state=State;
		this.zip=ZIP;
		this.country=Country;
		this.phone=Phone;
		this.contact=Contact;
	}

	public String getCid() {
		return Cid;
	}

	public void setCid(String cid) {
		Cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}
}
