package com.mis.common;

public class MIS_User {

	String userid;
	String pwd;
	String deviceid;
	String cid;
	int afq;
	String uname;
	
	public MIS_User(String UserID,String PWD,String DeviceID,String CompanyID,int AllowFractionalQty,String UserName)
	{
		this.userid=UserID;
		this.pwd=PWD;
		this.deviceid=DeviceID;
		this.cid=CompanyID;
		this.afq=AllowFractionalQty;
		this.uname=UserName;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public int getAfq() {
		return afq;
	}

	public void setAfq(int afq) {
		this.afq = afq;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}
}
