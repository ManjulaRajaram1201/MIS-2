package com.mis.database;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.mis.common.MIS_Company;
import com.mis.common.MIS_Setting;
import com.mis.common.MIS_User;
import com.mis.common.LogfileCreator;
import com.mis.internal.model.Internal_Manf_Number01;
import com.mis.internal.model.Internal_OrderDetails;
import com.mis.internal.model.Internal_SerialNo;
import com.mis.internal.model.Internal_Trans;
import com.mis.internal.model.Internal_Upc;
import com.mis.internal.model.Internal_Upc_Number;
import com.mis.mic.model.MIC_Conversionfactor;
import com.mis.mic.model.MIC_Inventory;
import com.mis.mic.model.MIC_Manufacturenumber;
import com.mis.mic.model.MIC_OrderDetails;
import com.mis.mic.model.MIC_UOMInternal;
import com.mis.mic.model.MIC_UPC;
import com.mis.mlt.model.MLT_Details;
import com.mis.mlt.model.MLT_Inventory;
import com.mis.mlt.model.MLT_LOCATION;
import com.mis.mlt.model.MLT_UOM;
import com.mis.mlt.model.MLT_UOMIntenal;
import com.mis.mlt.model.MLT_UPC;
import com.mis.mlt.model.MltHeader;
import com.mis.mlt.model.MltTrans;
import com.mis.mpr.model.MPR_MasterDetails;
import com.mis.mpr.model.MPR_OrderDetails;
import com.mis.mpr.model.MPR_Trans;
import com.mis.mpr.model.MPR_Upc;
import com.mis.mpr.model.MPR_Vendor;
import com.mis.mpr.model.Manf_Number01_mpr;
import com.mis.mse.model.MSE_Customer;

import com.mis.mse.model.MSE_MasterDetails;
import com.mis.mse.model.MSE_OrderDetails;
import com.mis.mse.model.MSE_SerialNo;
import com.mis.mse.model.MSE_Trans;
import com.mis.mse.model.MSE_Upc;
import com.mis.mse.model.Manf_Number01;
import com.mis.mse.model.Upc_Number;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class DatabaseHandler {
	public int doc_val = 1;
	private File fpath = null;
	private File DATABASE_FILE_PATH = null;
	private SQLiteDatabase db;
	private String errCode;
	private String msg;
	private String errMsg;
	public static int doc = 0;

	public static final int DATABASE_VERSION = 1;

	public static final String DATABASE_NAME = "mis.db";

	public static final String TABLE_SETTINGS = "MIS_SETTING";

	public static final String TABLE_MIC1 = "MIC_CONVERSIONFACTOR";

	public static final String TABLE_MIC2 = "MIC_INVENTORY";

	public static final String TABLE_MIC3 = "MIC_MANUFACTURENUMBER";

	public static final String TABLE_MIC4 = "MIC_UOMINTERNAL";

	public static final String TABLE_MIC5 = "MIC_UPC";

	public static final String TABLE_MIC6 = "MIC_LOC";

	public static final String TABLE_MIS1 = "MIS_COMPANY";

	public static final String TABLE_MIS2 = "MIS_USER";

	public static final String TABLE_MPR1 = "MPR_CONVERSION_FACTOR";

	public static final String TABLE_MPR2 = "MPR_INACTIVE_LOCATIONS";

	public static final String TABLE_MPR3 = "MPR_MANUFACTURER_NUMBER";

	public static final String TABLE_MPR4 = "MPR_ORDER_DETAIL";

	public static final String TABLE_MPR5 = "MPR_ORDER_MASTER";

	public static final String TABLE_MPR6 = "MPR_TRANS";

	public static final String TABLE_MPR7 = "MPR_UPC";

	public static final String TABLE_MPR8 = "MPR_VENDOR";

	public static final String TABLE_MPR9 = "MPR_TEMP_PO";
	// MSE tables
	public static final String TABLE_MSE1 = "MSE_CUSTOMER";

	public static final String TABLE_MSE2 = "MSE_ORDERDETAIL";

	public static final String TABLE_MSE3 = "MSE_ORDERMASTER";

	public static final String TABLE_MSE4 = "MSE_SERIALNO";

	public static final String TABLE_MSE5 = "MSE_TRANS";

	public static final String TABLE_MSE6 = "MSE_UPC";

	public static final String TABLE_MSE7 = "MSE_MANF_NO";

	public static final String TABLE_MSE8 = "MSE_TEMP_ORD";
	
	//Internal Issue Table
	public static final String TABLE_INT1 = "INT_COST";

	public static final String TABLE_INT2 = "INT_ORDERDETAIL";

	public static final String TABLE_INT3 = "INT_ORDERMASTER";

	public static final String TABLE_INT4 = "INT_SERIALNO";

	public static final String TABLE_INT5 = "INT_TRANS";

	public static final String TABLE_INT6 = "INT_UPC";

	public static final String TABLE_INT7 = "INT_MANF_NO";

	public static final String TABLE_INT8 = "INT_TEMP_ORD";

	public static final String KEY_IP = "IpAddress";

	public static final String KEY_DEVICE_ID = "DeviceId";

	public static final String KEY_ID = "_id";

	public static final String KEY_CONVFACTOR = "ConvFactor";

	public static final String KEY_UOM = "UOM";

	public static final String KEY_MANUNUMBER = "ManuNumber";

	public static final String KEY_UPCCODE = "UPCcode";

	public static final String KEY_COMPANYID = "CompanyID";

	public static final String KEY_NAME = "Name";

	public static final String KEY_ADDRESS = "Address";

	public static final String KEY_CITY = "City";

	public static final String KEY_STATE = "State";

	public static final String KEY_STATE_CODE = "StateCode";

	public static final String KEY_ZIP = "ZIP";

	public static final String KEY_COUNTRY = "Country";

	public static final String KEY_PHONE = "Phone";

	public static final String KEY_CONTACT = "Contact";

	public static final String KEY_LOCATION = "Location";

	public static final String KEY_ITEMNUMBER = "ItemNumber";

	public static final String KEY_DESCRIPTION = "Description";

	public static final String KEY_PICKINGSEQUENCE = "Pickingsequence";

	public static final String KEY_QTYONHAND = "QuantityOnHand";

	public static final String KEY_QTYCOUNTED = "QuantityCounted";

	public static final String KEY_STOCKUNIT = "Stockunit";

	public static final String KEY_STATUS = "Status";

	public static final String KEY_USERID = "UserID";

	public static final String KEY_PWD = "PWD";

	public static final String KEY_AFQ = "AllowFractionalQty";

	public static final String KEY_UNAME = "UserName";

	// MPR COLUMNS NAME
	public static final String KEY_BAR_CODE = "BarCodeField";

	public static final String KEY_LOCATION_CODE = "LocationCode";

	public static final String KEY_PONUMBER = "PONumber";

	public static final String KEY_VENDORNUMBER = "VendorId";

	public static final String KEY_VENDORNAME = "ShortName";

	public static final String KEY_QUANTITY_ORDER = "QtyOrdered";

	public static final String KEY_QUANTITY_RECEIVED = "QtyReceived";

	public static final String KEY_QUANTITY_BO = "QtyBo";

	public static final String KEY_COMMENTS = "Comments";

	public static final String KEY_LINENUMBER = "LineNumber";

	public static final String KEY_TYPE = "Type";

	public static final String KEY_ORDERDATE = "OrderDate";

	public static final String KEY_RECEIPT_NUMBER = "ReceiptNumber";

	public static final String KEY_RECEIPT_DAY = "ReceiptDay";

	public static final String KEY_NAMECITY = "NameCity";

	public static final String KEY_RECEIPT_MONTH = "ReceiptMonth";

	public static final String KEY_RECEIPT_YEAR = "ReceiptYear";

	public static final String KEY_ADDRESS1 = "Street1";

	public static final String KEY_ADDRESS2 = "Street2";

	// MSE Columns
	public static final String KEY_CUSTOMER_NUMBER = "CustomerNo";

	public static final String KEY_CUSTOMER_NAME = "CustomerName";

	public static final String KEY_ORDER_NUMBER = "OrderNo";

	public static final String KEY_ITEM_DESCRIPTION = "ItemDesc";

	public static final String KEY_QUANTITY_SHIPPED = "QtyShipped";

	public static final String KEY_SHIP_VIA_CODE = "ShipViaCode";

	public static final String KEY_SHIP_VIA_DESC = "ShipViaDesc";

	public static final String KEY_ORDER_DATE = "OrderDate";

	public static final String KEY_SHIP_TO_LOCATION_CODE = "ShipToLocCode";

	public static final String KEY_BILL_TO_NAME = "BillToName";

	public static final String KEY_BILL_TO_ADDR1 = "BillToAddr1";

	public static final String KEY_BILL_TO_ADDR2 = "BillToAddr2";

	public static final String KEY_BILL_TO_CITY = "BillToCity";

	public static final String KEY_BILL_TO_STATE = "BillToState";

	public static final String KEY_BILL_TO_ZIP = "BillToZip";

	public static final String KEY_BILL_TO_COUNTRY = "BillToCountry";

	public static final String KEY_BILL_TO_PHONE = "BillToPhone";

	public static final String KEY_BILL_TO_FAX = "BillToFax";

	public static final String KEY_SHIP_TO_NAME = "ShipToName";

	public static final String KEY_SHIP_TO_ADDR1 = "ShipToAddr1";

	public static final String KEY_SHIP_TO_ADDR2 = "ShipToAddr2";

	public static final String KEY_SHIP_TO_CITY = "ShipToCity";

	public static final String KEY_SHIP_TO_STATE = "ShipToState";

	public static final String KEY_SHIP_TO_ZIP = "ShipToZip";

	public static final String KEY_SHIP_TO_COUNTRY = "ShipToCountry";

	public static final String KEY_SHIP_TO_PHONE = "ShipToPhone";

	public static final String KEY_SHIP_TO_FAX = "ShipToFax";

	public static final String KEY_SERIAL_NUMBER = "SerialNumber";

	public static final String KEY_SHIP_VIA = "ShipVia";

	public static final String KEY_SHIPMENT_NUMBER = "ShipmentNumber";

	public static final String KEY_SHIP_DAY = "ShipDay";

	public static final String KEY_SHIP_MONTH = "ShipMonth";

	public static final String KEY_SHIP_YEAR = "ShipYear";

	public static final String KEY_UPC_NUMBER = "UpcNumber";

	public static final String KEY_LOTNUM = "LotNum";

	public static final String KEY_TRFNUMBER = "TrfNumber";

	public static final String KEY_EXPDATE = "ExpDate";
	
	
	
	public static final String KEY_INT_NUMBER = "InternalNumber";

	public static final String KEY_II_NUMBER = "IntIssueNumber";

	public static final String KEY_COST_CENTER = "Costcenter";

	public static final String KEY_INT_DATE = "Internalissuedate";
	
	// MLT Tables
	// MLT Tables

	public static final String TABLE_MLT1 = "MLT_DOCDETAIL";
	public static final String TABLE_MLT2 = "MLT_HEADER";
	public static final String TABLE_MLT3 = "MLT_INVENTORY";
	public static final String TABLE_MLT4 = "MLT_INVUOM";
	public static final String TABLE_MLT5 = "MLT_LOCATION";
	public static final String TABLE_MLT6 = "MLT_UPC";
	public static final String TABLE_MLT7 = "TEMP_MLT";

	public static final String KEY_DOCNUMBER = "DocNumber";
	// public static final String KEY_ITEMNO="ItemNo";
	public static final String KEY_FROMLOCATION = "FromLoc";
	public static final String KEY_TOLOCATION = "ToLoc";
	public static final String KEY_QUANTITY = "Qty";
	// public static final String KEY_STATUS="Status";
	// public static final String KEY_UOM="UOM";
	public static final String KEY_EXPECTEDARRDAY = "ExpArrDay";
	public static final String KEY_EXPECTEDARRMONTH = "ExpArrMonth";
	public static final String KEY_EXPECTEDARRYEAR = "ExpArrYear";
	public static final String KEY_ADDCOST = "AddCost";

	String CREATE_MIS_SETTING = "CREATE TABLE " + TABLE_SETTINGS + "("
			+ KEY_DEVICE_ID + " TEXT," + KEY_IP + " TEXT" + ")";

	String CREATE_MIC_TABLE1 = "CREATE TABLE " + TABLE_MIC1 + "("
			+ KEY_ITEMNUMBER + " TEXT," + KEY_CONVFACTOR + " REAL,"
			+ KEY_UOM + " TEXT" + ")";

	String CREATE_MIC_TABLE2 = "CREATE TABLE " + TABLE_MIC2 + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LOCATION + " TEXT,"
			+ KEY_ITEMNUMBER + " TEXT," + KEY_DESCRIPTION + " TEXT,"
			+ KEY_PICKINGSEQUENCE + " TEXT," + KEY_QTYONHAND + " REAL,"
			+ KEY_QTYCOUNTED + " REAL," + KEY_STOCKUNIT + " TEXT,"
			+ KEY_STATUS + " INTEGER" + ")";

	String CREATE_MIC_TABLE3 = "CREATE TABLE " + TABLE_MIC3 + "("
			+ KEY_MANUNUMBER + " TEXT ," + KEY_ITEMNUMBER + " TEXT" + ")";

	String CREATE_MIC_TABLE4 = "CREATE TABLE " + TABLE_MIC4 + "("
			+ KEY_LOCATION + " TEXT," + KEY_ITEMNUMBER + " TEXT,"
			+ KEY_QTYCOUNTED + " REAL," + KEY_UOM + " TEXT" + ")";

	String CREATE_MIC_TABLE5 = "CREATE TABLE " + TABLE_MIC5 + "("
			+ KEY_ITEMNUMBER + " TEXT ," + KEY_UPCCODE + " TEXT" + ")";

	String CREATE_MIC_TABLE6 = "CREATE TABLE " + TABLE_MIS1 + "("
			+ KEY_COMPANYID + " TEXT," + KEY_NAME + " TEXT," + KEY_ADDRESS
			+ " TEXT," + KEY_CITY + " TEXT," + KEY_STATE + " TEXT," + KEY_ZIP
			+ " TEXT," + KEY_COUNTRY + " TEXT," + KEY_PHONE + " TEXT,"
			+ KEY_CONTACT + " TEXT" + ")";

	String CREATE_MIC_TABLE7 = "CREATE TABLE " + TABLE_MIS2 + "(" + KEY_USERID
			+ " TEXT," + KEY_PWD + " TEXT," + KEY_DEVICE_ID + " TEXT,"
			+ KEY_COMPANYID + " TEXT," + KEY_AFQ + " INTEGER," + KEY_UNAME
			+ " TEXT" + ")";

	String CREATE_MIC_TABLE8 = "CREATE TABLE " + TABLE_MIC6 + "("
			+ KEY_FROMLOCATION + " TEXT ," + KEY_TOLOCATION + " TEXT" + ")";

	// CREATING MPR TABLES
	String CREATE_MPR_TABLE1 = "CREATE TABLE " + TABLE_MPR1 + "("
			+ KEY_ITEMNUMBER + " TEXT," + KEY_CONVFACTOR + " REAL,"
			+ KEY_UOM + " TEXT" + ")";

	String CREATE_MPR_TABLE2 = "CREATE TABLE " + TABLE_MPR2 + "("
			+ KEY_LOCATION_CODE + " TEXT" + ")";

	String CREATE_MPR_TABLE3 = "CREATE TABLE " + TABLE_MPR3 + "("
			+ KEY_ITEMNUMBER + " TEXT," + KEY_MANUNUMBER + " TEXT" + ")";

	String CREATE_MPR_TABLE4 = "CREATE TABLE " + TABLE_MPR4 + "("
			+ KEY_PONUMBER + " TEXT," + KEY_VENDORNUMBER + " TEXT,"
			+ KEY_ITEMNUMBER + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_UOM
			+ " TEXT," + KEY_QUANTITY_ORDER + " NUMERIC,"
			+ KEY_QUANTITY_RECEIVED + " NUMERIC," /*
												 * + KEY_QUANTITY_BO +
												 * " NUMERIC,"
												 */+ KEY_COMMENTS + " TEXT,"
			+ KEY_LOCATION_CODE + " TEXT," + KEY_LINENUMBER + " TEXT" /*
																	 * " TEXT,"
																	 * +
																	 * KEY_STATUS
																	 * +
																	 * " REAL,"
																	 * +
																	 * KEY_TYPE
																	 * + " TEXT"
																	 */+ ")";

	String CREATE_MPR_TABLE5 = "CREATE TABLE " + TABLE_MPR5 + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_PONUMBER + " TEXT,"
			+ KEY_ORDERDATE + " NUMERIC," + KEY_VENDORNUMBER + " TEXT" + ")";

	String CREATE_MPR_TABLE6 = "CREATE TABLE " + TABLE_MPR6 + "("
			+ KEY_PONUMBER + " TEXT," + KEY_VENDORNUMBER + " TEXT,"
			+ KEY_ITEMNUMBER + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_UOM
			+ " TEXT," + KEY_QUANTITY_ORDER + " NUMERIC,"
			+ KEY_QUANTITY_RECEIVED + " NUMERIC," + KEY_COMMENTS + " TEXT,"
			+ KEY_LOCATION_CODE + " TEXT," + KEY_RECEIPT_NUMBER + " TEXT,"
			+ KEY_LINENUMBER + " TEXT," + KEY_STATUS + " INTEGER,"
			+ KEY_RECEIPT_DAY + " TEXT," + KEY_RECEIPT_MONTH + " TEXT,"
			+ KEY_RECEIPT_YEAR + " TEXT" + ")";

	String CREATE_MPR_TABLE7 = "CREATE TABLE " + TABLE_MPR7 + "("
			+ KEY_ITEMNUMBER + " TEXT," + KEY_BAR_CODE + " TEXT" + ")";

	String CREATE_MPR_TABLE8 = "CREATE TABLE " + TABLE_MPR8 + "("
			+ KEY_VENDORNUMBER + " TEXT," + KEY_VENDORNAME + " TEXT,"
			+ KEY_ADDRESS1 + " TEXT," + KEY_ADDRESS2 + " TEXT," + KEY_NAMECITY
			+ " TEXT," + KEY_STATE_CODE + " TEXT," + KEY_ZIP + " TEXT,"
			+ KEY_COUNTRY + " TEXT," + KEY_PHONE + " TEXT" + ")";

	String CREATE_MPR_TABLE9 = "CREATE TABLE " + TABLE_MPR9 + "("
			+ KEY_PONUMBER + " TEXT," + KEY_STATUS + " INTEGER" + ")";

	/*String CREATE_INT_TABLE1 = "CREATE TABLE " + TABLE_INT1 + "("
			+ KEY_COST_CENTER + " TEXT" + ")";*/

	String CREATE_INT_TABLE2 = "CREATE TABLE " + TABLE_INT2 + "("
			+ KEY_INT_NUMBER + " TEXT," + KEY_COST_CENTER + " TEXT,"
			+ KEY_ITEMNUMBER + " TEXT," + KEY_ITEM_DESCRIPTION + " TEXT,"
			+ KEY_UOM + " TEXT," + KEY_QUANTITY_ORDER + " NUMERIC,"
			+ KEY_QUANTITY_SHIPPED + " NUMERIC," + KEY_LOCATION + " TEXT,"
			+ KEY_LINENUMBER + " TEXT," + KEY_INT_DATE + " TEXT" + ")";

	String CREATE_INT_TABLE3 = "CREATE TABLE " + TABLE_INT3 + "("
			+ KEY_ORDER_NUMBER + " TEXT," + KEY_ORDER_DATE + " TEXT,"
			+ KEY_CUSTOMER_NUMBER + " TEXT," + KEY_SHIP_TO_LOCATION_CODE
			+ " TEXT," + KEY_BILL_TO_NAME + " TEXT," + KEY_BILL_TO_ADDR1
			+ " TEXT," + KEY_BILL_TO_ADDR2 + " TEXT," + KEY_BILL_TO_CITY
			+ " TEXT," + KEY_BILL_TO_STATE + " TEXT," + KEY_BILL_TO_ZIP
			+ " TEXT," + KEY_BILL_TO_COUNTRY + " TEXT," + KEY_BILL_TO_PHONE
			+ " TEXT," + KEY_BILL_TO_FAX + " TEXT," + KEY_SHIP_TO_NAME
			+ " TEXT," + KEY_SHIP_TO_ADDR1 + " TEXT," + KEY_SHIP_TO_ADDR2
			+ " TEXT," + KEY_SHIP_TO_CITY + " TEXT," + KEY_SHIP_TO_STATE
			+ " TEXT," + KEY_SHIP_TO_ZIP + " TEXT," + KEY_SHIP_TO_COUNTRY
			+ " TEXT," + KEY_SHIP_TO_PHONE + " TEXT," + KEY_SHIP_TO_FAX
			+ " TEXT" + ")";

	String CREATE_INT_TABLE4 = "CREATE TABLE " + TABLE_INT4 + "("
			+ KEY_INT_NUMBER + " TEXT," + KEY_LINENUMBER + " TEXT,"
			+ KEY_ITEMNUMBER + " TEXT," + KEY_SERIAL_NUMBER + " TEXT" + ")";

	String CREATE_INT_TABLE5 = "CREATE TABLE " + TABLE_INT5 + "("
			+ KEY_INT_NUMBER + " TEXT," + KEY_COST_CENTER + " TEXT,"
			+ KEY_LINENUMBER + " TEXT," + KEY_ITEMNUMBER + " TEXT,"
			+ KEY_ITEM_DESCRIPTION + " TEXT," + KEY_UOM + " TEXT,"
			+ KEY_QUANTITY_ORDER + " NUMERIC," + KEY_QUANTITY_SHIPPED
			+ " NUMERIC," 
			+ KEY_SHIPMENT_NUMBER + " TEXT," + KEY_SHIP_DAY + " TEXT,"
			+ KEY_SHIP_MONTH + " TEXT," + KEY_SHIP_YEAR + " TEXT," + KEY_STATUS
			+ " INTEGER" + ")";

	String CREATE_INT_TABLE6 = "CREATE TABLE " + TABLE_INT6 + "("
			+ KEY_ITEMNUMBER + " TEXT," + KEY_UPC_NUMBER + " TEXT" + ")";

	String CREATE_INT_TABLE7 = "CREATE TABLE " + TABLE_INT7 + "("
			+ KEY_ITEMNUMBER + " TEXT," + KEY_MANUNUMBER + " TEXT" + ")";

	String CREATE_INT_TABLE8 = "CREATE TABLE " + TABLE_INT8 + "("
			+ KEY_INT_NUMBER + " TEXT," + KEY_STATUS + " INTEGER" + ")";
	
	//Creating Internal isssue tables
	String CREATE_MSE_TABLE1 = "CREATE TABLE " + TABLE_MSE1 + "("
			+ KEY_CUSTOMER_NUMBER + " TEXT," + KEY_CUSTOMER_NAME + " TEXT"
			+ ")";

	String CREATE_MSE_TABLE2 = "CREATE TABLE " + TABLE_MSE2 + "("
			+ KEY_ORDER_NUMBER + " TEXT," + KEY_CUSTOMER_NUMBER + " TEXT,"
			+ KEY_ITEMNUMBER + " TEXT," + KEY_ITEM_DESCRIPTION + " TEXT,"
			+ KEY_UOM + " TEXT," + KEY_QUANTITY_ORDER + " NUMERIC,"
			+ KEY_QUANTITY_SHIPPED + " NUMERIC," + KEY_LOCATION + " TEXT,"
			+ KEY_SHIP_VIA_CODE + " TEXT," + KEY_SHIP_VIA_DESC + " TEXT,"
			+ KEY_COMMENTS + " TEXT," + KEY_LINENUMBER + " TEXT,"
			+ KEY_PICKINGSEQUENCE + " TEXT" + ")";

	String CREATE_MSE_TABLE3 = "CREATE TABLE " + TABLE_MSE3 + "("
			+ KEY_ORDER_NUMBER + " TEXT," + KEY_ORDER_DATE + " TEXT,"
			+ KEY_CUSTOMER_NUMBER + " TEXT," + KEY_SHIP_TO_LOCATION_CODE
			+ " TEXT," + KEY_BILL_TO_NAME + " TEXT," + KEY_BILL_TO_ADDR1
			+ " TEXT," + KEY_BILL_TO_ADDR2 + " TEXT," + KEY_BILL_TO_CITY
			+ " TEXT," + KEY_BILL_TO_STATE + " TEXT," + KEY_BILL_TO_ZIP
			+ " TEXT," + KEY_BILL_TO_COUNTRY + " TEXT," + KEY_BILL_TO_PHONE
			+ " TEXT," + KEY_BILL_TO_FAX + " TEXT," + KEY_SHIP_TO_NAME
			+ " TEXT," + KEY_SHIP_TO_ADDR1 + " TEXT," + KEY_SHIP_TO_ADDR2
			+ " TEXT," + KEY_SHIP_TO_CITY + " TEXT," + KEY_SHIP_TO_STATE
			+ " TEXT," + KEY_SHIP_TO_ZIP + " TEXT," + KEY_SHIP_TO_COUNTRY
			+ " TEXT," + KEY_SHIP_TO_PHONE + " TEXT," + KEY_SHIP_TO_FAX
			+ " TEXT" + ")";

	String CREATE_MSE_TABLE4 = "CREATE TABLE " + TABLE_MSE4 + "("
			+ KEY_ORDER_NUMBER + " TEXT," + KEY_LINENUMBER + " TEXT,"
			+ KEY_ITEMNUMBER + " TEXT," + KEY_SERIAL_NUMBER + " TEXT" + ")";

	String CREATE_MSE_TABLE5 = "CREATE TABLE " + TABLE_MSE5 + "("
			+ KEY_ORDER_NUMBER + " TEXT," + KEY_CUSTOMER_NUMBER + " TEXT,"
			+ KEY_LINENUMBER + " TEXT," + KEY_ITEMNUMBER + " TEXT,"
			+ KEY_ITEM_DESCRIPTION + " TEXT," + KEY_UOM + " TEXT,"
			+ KEY_QUANTITY_ORDER + " NUMERIC," + KEY_QUANTITY_SHIPPED
			+ " NUMERIC," + KEY_SHIP_VIA + " TEXT," + KEY_COMMENTS + " TEXT,"
			+ KEY_SHIPMENT_NUMBER + " TEXT," + KEY_SHIP_DAY + " TEXT,"
			+ KEY_SHIP_MONTH + " TEXT," + KEY_SHIP_YEAR + " TEXT," + KEY_STATUS
			+ " INTEGER" + ")";

	String CREATE_MSE_TABLE6 = "CREATE TABLE " + TABLE_MSE6 + "("
			+ KEY_ITEMNUMBER + " TEXT," + KEY_UPC_NUMBER + " TEXT" + ")";

	String CREATE_MSE_TABLE7 = "CREATE TABLE " + TABLE_MSE7 + "("
			+ KEY_ITEMNUMBER + " TEXT," + KEY_MANUNUMBER + " TEXT" + ")";

	String CREATE_MSE_TABLE8 = "CREATE TABLE " + TABLE_MSE8 + "("
			+ KEY_ORDER_NUMBER + " TEXT," + KEY_STATUS + " INTEGER" + ")";

	// Creating MLT tables

	String CREATE_MLT_TABLE1 = "CREATE TABLE " + TABLE_MLT1 + "("
			+ KEY_DOCNUMBER + " NUMERIC," + KEY_ITEMNUMBER + " TEXT,"
			+ KEY_FROMLOCATION + " TEXT," + KEY_TOLOCATION + " TEXT,"
			+ KEY_QUANTITY + " NUMERIC," + KEY_UOM + " NUMERIC" + ")";

	String CREATE_MLT_TABLE2 = "CREATE TABLE " + TABLE_MLT2 + "("
			+ KEY_DOCNUMBER + " TEXT," + KEY_TRFNUMBER + " TEXT,"
			+ KEY_EXPECTEDARRDAY + " NUMERIC," + KEY_EXPECTEDARRMONTH
			+ " NUMERIC," + KEY_EXPECTEDARRYEAR + " NUMERIC," + KEY_ADDCOST
			+ " NUMERIC," + KEY_DESCRIPTION + " TEXT," + KEY_STATUS
			+ " INTEGER" + ")";
	String CREATE_MLT_TABLE3 = "CREATE TABLE " + TABLE_MLT3 + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ITEMNUMBER + " TEXT,"
			+ KEY_DESCRIPTION + " TEXT" + ")";

	String CREATE_MLT_TEMP_TABLE = "CREATE TABLE " + TABLE_MLT7 + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ITEMNUMBER + " TEXT,"
			+ KEY_DESCRIPTION + " TEXT," + KEY_FROMLOCATION + " TEXT,"
			+ KEY_TOLOCATION + " TEXT," + KEY_QUANTITY + " TEXT," + KEY_UOM
			+ " TEXT" + ")";

	/*
	 * String CREATE_MLT_TABLE3= "CREATE TABLE " + TABLE_MLT3 + "(" +
	 * KEY_ITEMNUMBER + " TEXT" + KEY_DESCRIPTION + " TEXT" +")";
	 */

	String CREATE_MLT_TABLE4 = "CREATE TABLE " + TABLE_MLT4 + "("
			+ KEY_ITEMNUMBER + " TEXT," + KEY_UOM + " TEXT" + ")";

	String CREATE_MLT_TABLE5 = "CREATE TABLE " + TABLE_MLT5 + "("
			+ KEY_LOCATION + " TEXT" + ")";

	String CREATE_MLT_TABLE6 = "CREATE TABLE " + TABLE_MLT6 + "("
			+ KEY_ITEMNUMBER + " TEXT," + KEY_UPCCODE + " TEXT" + ")";

	public DatabaseHandler(Context context) {

		File root_path = Environment.getExternalStorageDirectory();

		DATABASE_FILE_PATH = new File(root_path.getAbsoluteFile() + "/"
				+ "Android/MobInvSuit");
		if (!DATABASE_FILE_PATH.exists()) {
			if (DATABASE_FILE_PATH.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		fpath = new File(DATABASE_FILE_PATH, DATABASE_NAME);

		if (!fpath.exists()) {
			// db.setVersion(1);
			db = SQLiteDatabase.openOrCreateDatabase(DATABASE_FILE_PATH
					+ File.separator + DATABASE_NAME, null);

			try {

				db.beginTransaction();
				db.execSQL(CREATE_MIS_SETTING);
				db.execSQL(CREATE_MIC_TABLE1);
				db.execSQL(CREATE_MIC_TABLE2);
				db.execSQL(CREATE_MIC_TABLE3);
				db.execSQL(CREATE_MIC_TABLE4);
				db.execSQL(CREATE_MIC_TABLE5);
				db.execSQL(CREATE_MIC_TABLE6);
				db.execSQL(CREATE_MIC_TABLE7);
				db.execSQL(CREATE_MIC_TABLE8);

				// creating mpr
				db.execSQL(CREATE_MPR_TABLE1);
				db.execSQL(CREATE_MPR_TABLE2);
				db.execSQL(CREATE_MPR_TABLE3);
				db.execSQL(CREATE_MPR_TABLE4);
				db.execSQL(CREATE_MPR_TABLE5);
				db.execSQL(CREATE_MPR_TABLE6);
				db.execSQL(CREATE_MPR_TABLE7);
				db.execSQL(CREATE_MPR_TABLE8);
				db.execSQL(CREATE_MPR_TABLE9);

				// Executing MSE tables
				db.execSQL(CREATE_MSE_TABLE1);
				db.execSQL(CREATE_MSE_TABLE2);
				db.execSQL(CREATE_MSE_TABLE3);
				db.execSQL(CREATE_MSE_TABLE4);
				db.execSQL(CREATE_MSE_TABLE5);
				db.execSQL(CREATE_MSE_TABLE6);
				db.execSQL(CREATE_MSE_TABLE7);
				db.execSQL(CREATE_MSE_TABLE8);
				
				//Executing Internal tables
				/*db.execSQL(CREATE_INT_TABLE1);*/
				db.execSQL(CREATE_INT_TABLE2);
				db.execSQL(CREATE_INT_TABLE3);
				db.execSQL(CREATE_INT_TABLE4);
				db.execSQL(CREATE_INT_TABLE5);
				db.execSQL(CREATE_INT_TABLE6);
				db.execSQL(CREATE_INT_TABLE7);
				db.execSQL(CREATE_INT_TABLE8);
				
				

				// Executing MLT tables
				db.execSQL(CREATE_MLT_TABLE1);
				db.execSQL(CREATE_MLT_TABLE2);
				db.execSQL(CREATE_MLT_TABLE3);
				db.execSQL(CREATE_MLT_TABLE4);
				db.execSQL(CREATE_MLT_TABLE5);
				db.execSQL(CREATE_MLT_TABLE6);
				db.execSQL(CREATE_MLT_TEMP_TABLE);

				db.setTransactionSuccessful();
				Log.i("Success", "All Table Created successfully...");
			} catch (Exception e) {
				db.endTransaction();
				errCode = "Error 501";
				msg = "Table Creation failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);
			} finally {
				db.endTransaction();
			}

			db.close();
		}
	}

	public void deleteMicLocation() {
		try {
			db.execSQL("DELETE FROM " + TABLE_MIC6);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void addMicLocation(String frmLoc, String toLoc) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(KEY_FROMLOCATION, frmLoc);
			cv.put(KEY_TOLOCATION, toLoc);
			db.insert(TABLE_MIC6, null, cv);

			Log.i("Success", "Data Inserted in MIC LOC");
			db.close();
		} catch (Exception exe) {

			Log.e("Error", exe.getMessage());
		}

	}

	public String[] getMicLocation() {

		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MIC6;

		String[] str = new String[2];
		Cursor cursor_locdetails = db.rawQuery(q, null);

		if (cursor_locdetails != null && cursor_locdetails.moveToFirst()) {
			str[0] = cursor_locdetails.getString(0);
			str[1] = cursor_locdetails.getString(1);

		} else {
			Log.i("No Data", "");
		}

		return str;
	}

	public String addMic_conversionfactor(MIC_Conversionfactor m1) {
		String result = "";
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(KEY_ITEMNUMBER, m1.getItemno());
			cv.put(KEY_CONVFACTOR, m1.getCf());
			cv.put(KEY_UOM, m1.getUom());
			db.insert(TABLE_MIC1, null, cv);
			result = "success";
			Log.i("Success", "Data Inserted");
			db.close();
		} catch (Exception exe) {
			result = "error";
			Log.e("Error", exe.getMessage());
		}
		return result;
	}

	public String addMic_inventory(JSONObject jobj) {
		String result = "";
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(KEY_LOCATION, jobj.getString("LOCATION"));
			cv.put(KEY_ITEMNUMBER, jobj.getString("ITEMNO"));
			cv.put(KEY_DESCRIPTION, jobj.getString("DESCRIPTION"));
			cv.put(KEY_PICKINGSEQUENCE, jobj.getString("PICKINGSEQ"));
			cv.put(KEY_QTYCOUNTED, jobj.getString("QTYCOUNTED"));
			cv.put(KEY_QTYONHAND, jobj.getString("QTYONHAND"));
			cv.put(KEY_STATUS, 0);
			cv.put(KEY_STOCKUNIT, jobj.getString("STOCKUNIT"));
			db.insert(TABLE_MIC2, null, cv);
			result = "success";
			Log.i("Success", "Data Inserted in " + TABLE_MIC2);
			db.close();
		} catch (Exception exe) {
			result = "error";
			Log.e("Error", exe.getMessage());
		}
		return result;
	}

	public String addMic_manufacturenumber(JSONObject jobj) {
		String result = "";
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(KEY_MANUNUMBER, jobj.getString("MANITEMNO"));
			cv.put(KEY_ITEMNUMBER, jobj.getString("ITEMNO"));
			db.insert(TABLE_MIC3, null, cv);
			result = "success";
			Log.i("Success", "Data Inserted in " + TABLE_MIC3);
			db.close();
		} catch (Exception exe) {
			result = "error";
			Log.e("Error", exe.getMessage());
		}
		return result;

	}

	public String addMic_UOMInternal(MIC_UOMInternal m4) {
		String result = "";
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(KEY_LOCATION, m4.getLocation());
			cv.put(KEY_ITEMNUMBER, m4.getItemno());
			cv.put(KEY_QTYCOUNTED, m4.getQc());
			cv.put(KEY_UOM, m4.getUom());
			db.insert(TABLE_MIC4, null, cv);
			result = "success";
			Log.i("Success", "Data Inserted in " + TABLE_MIC4);
			db.close();
		} catch (Exception exe) {
			result = "error";
			Log.e("Error", exe.getMessage());
		}
		return result;
	}

	public String addMic_upc(JSONObject jobj) {
		String result = "";
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(KEY_ITEMNUMBER, jobj.getString("ITEMNO"));
			cv.put(KEY_UPCCODE, jobj.getString("UPCCODE"));

			db.insert(TABLE_MIC5, null, cv);
			result = "success";
			Log.i("Success", "Data Inserted in " + TABLE_MIC5);
			db.close();
		} catch (Exception exe) {
			result = "error";
			Log.e("Error", exe.getMessage());
		}
		return result;
	}

	public String LOAD_COMPANYID(String Deviceid) {
		String COMPANYID = "";
		try {
			String Query = "SELECT CompanyID FROM MIS_USER Where "
					+ KEY_DEVICE_ID + " = '" + Deviceid + "'";
			getReadableDatabase();
			Cursor cursor = GetData1(Query);

			if (cursor != null && cursor.moveToNext() == true) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					COMPANYID = cursor.getString(
							cursor.getColumnIndex("CompanyID")).toString();
					cursor.moveToNext();
				}

			}
			cursor.close();
			closeDatabase();
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 502";
			msg = "Load companyId failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

		return COMPANYID;
	}
	public String UserID(String Deviceid) {
		String USERID = "";
		try {
			String Query = "SELECT UserID FROM MIS_USER Where "
					+ KEY_DEVICE_ID + " = '" + Deviceid + "'";
			getReadableDatabase();
			Cursor cursor = GetData1(Query);

			if (cursor != null && cursor.moveToNext() == true) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					USERID = cursor.getString(
							cursor.getColumnIndex("UserID")).toString();
					cursor.moveToNext();
				}

			}
			cursor.close();
			closeDatabase();
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 502_User";
			msg = "Load companyId failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

		return USERID;
	}

	/*
	 * public void delete() { Cursor c; String a="DELETE FROM TABLE_MIC2";
	 * SQLiteDatabase s=this.getReadableDatabase(); c=s.rawQuery(a, null); }
	 */

	public String addMis_company(MIS_Company ms1) {
		String res = "";
		try {

			ContentValues cv = new ContentValues();
			cv.put(KEY_COMPANYID, ms1.getCid());
			cv.put(KEY_NAME, ms1.getName());
			cv.put(KEY_ADDRESS, ms1.getAddress());
			cv.put(KEY_CITY, ms1.getCity());
			cv.put(KEY_STATE, ms1.getState());
			cv.put(KEY_ZIP, ms1.getZip());
			cv.put(KEY_COUNTRY, ms1.getCountry());
			cv.put(KEY_PHONE, ms1.getPhone());
			cv.put(KEY_CONTACT, ms1.getContact());
			db.insert(TABLE_MIS1, null, cv);
			res = "success";
			Log.i("Success", "Data Inserted for Company");

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 508";
			msg = "Add Mis Company failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
			res = "error";

		}
		return res;
	}

	public String addMis_User(MIS_User ms2) {
		String res = "";
		try {

			ContentValues cv = new ContentValues();
			cv.put(KEY_USERID, ms2.getUserid());
			cv.put(KEY_PWD, ms2.getPwd());
			cv.put(KEY_DEVICE_ID, ms2.getDeviceid());
			cv.put(KEY_COMPANYID, ms2.getCid());
			cv.put(KEY_AFQ, ms2.getAfq());
			cv.put(KEY_UNAME, ms2.getUname());
			db.insert(TABLE_MIS2, null, cv);
			res = "success";
			Log.i("Success", "Data Inserted for User");

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 509";
			msg = "Add MIS User failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			res = "error";
			db.close();
		}
		return res;
	}

	public String addSettings(MIS_Setting mis_setting) {
		String res = "";
		try {

			ContentValues cv = new ContentValues();
			cv.put(KEY_DEVICE_ID, mis_setting.getDeviceId());
			cv.put(KEY_IP, mis_setting.getIpAddress());
			db.insert(TABLE_SETTINGS, null, cv);
			Log.i("Success", "Data Inserted");
			res = "success";
		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 510";
			msg = "Add Setting failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			res = "error";
			db.close();
		}
		//

		return res;
	}

	public String insertIntoInternal(MIC_UOMInternal mic_UOMInternal) {
		String result = "";
		try {
			ContentValues cv = new ContentValues();
			cv.put(KEY_LOCATION, mic_UOMInternal.getLocation());
			cv.put(KEY_ITEMNUMBER, mic_UOMInternal.getItemno());
			cv.put(KEY_QTYCOUNTED, mic_UOMInternal.getQc());
			cv.put(KEY_UOM, mic_UOMInternal.getUom());
			db.insert(TABLE_MIC4, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_MIC4);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 522";
			msg = "insertIntoInternal failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;
	}

	public String updateMIC(MIC_Inventory mic_Inventory) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();
			String ordQty = mic_Inventory.getQc();

			String loc = mic_Inventory.getLocation();
			String item = mic_Inventory.getItemno();

			cv.put(KEY_QTYCOUNTED, ordQty);

			db.update(TABLE_MIC2, cv, KEY_LOCATION + "= '" + loc + "' AND "
					+ KEY_ITEMNUMBER + " = '" + item + "' ", null);

			Log.i("Success", "Data Updated in table " + TABLE_MIC2);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 523";
			msg = "updateMIC failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public String getCompanyName() {
		String result = "";
		Cursor cursor = null;

		String q = "SELECT Name FROM " + DatabaseHandler.TABLE_MIS1;

		cursor = db.rawQuery(q, null);

		try {
			while (cursor.moveToNext()) {
				result = cursor.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 52310";
			msg = "getCompanyName failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}
		cursor.close();
		return result;

	}

	public List<MIC_OrderDetails> getdata(String Loc) {

		Cursor cursor = null;
		List<MIC_OrderDetails> ordlst = new ArrayList<MIC_OrderDetails>();

		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MIC2
				+ " WHERE Location ='" + Loc + "'";

		cursor = db.rawQuery(q, null);

		try {
			while (cursor.moveToNext()) {
				MIC_OrderDetails mic_OrderDetails = new MIC_OrderDetails();

				mic_OrderDetails.setItemNumber(cursor.getString(2));
				mic_OrderDetails.setItemDescription(cursor.getString(3));
				mic_OrderDetails.setPickSeq(cursor.getString(4));
				mic_OrderDetails.setQtyonHand(cursor.getString(5));
				mic_OrderDetails.setQtyCount(cursor.getString(6));
				mic_OrderDetails.setUnit(cursor.getString(7));

				ordlst.add(mic_OrderDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5210";
			msg = "MIC_OrderDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return ordlst;

	}

	public MIS_Setting getSetting() {

		try {
			MIS_Setting mis_setting = null;
			String q = "SELECT * FROM " + DatabaseHandler.TABLE_SETTINGS;

			Cursor c = db.rawQuery(q, null);
			int i = c.getCount();
			if (c.getCount() > 0) {
				if (c != null) {
					c.moveToFirst();
					mis_setting = new MIS_Setting();
					mis_setting.setDeviceId(c.getString(0));
					mis_setting.setIpAddress(c.getString(1));
				}

				c.close();
				return mis_setting;
			}
		} catch (Exception e) {
			errCode = "Error 511";
			msg = "Get Setting failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
		}

		return null;
	}

	public Cursor retrieveData(int id) {
		Cursor cursor = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MIC2 + " WHERE "
				+ DatabaseHandler.KEY_ID + " = '" + id + "'";
		cursor = db.rawQuery(q, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
		}

		return cursor;
	}

	public void UpdateData(String Query) {
		db.execSQL(Query);
	}

	public Cursor GetData1(String query) {
		Cursor c = null;
		c = db.rawQuery(query, null);
		return c;
	}

	public MIC_Manufacturenumber getItemFromManfNum_mic(String manfCode) {

		MIC_Manufacturenumber manf = null;
		Cursor cursor = db.rawQuery("select ItemNumber from " + TABLE_MIC3
				+ " where " + KEY_MANUNUMBER + " = '" + manfCode + "' ", null);

		try {
			while (cursor.moveToNext()) {
				manf = new MIC_Manufacturenumber();

				manf.setItemno(cursor.getString(0));
				/*
				 * manf.setManuf_itemno(cursor.getString(1));
				 * manf.setManuf_uom(cursor.getString(2));
				 * manf.setCompany_code(cursor.getString(3));
				 */
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 540";
			msg = "getItemFromManfNum_mic failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);

			db.close();
		}

		cursor.close();

		return manf;
	}

	public MIC_Inventory getMicInventoryDetails(String loc, String it) {
		Cursor cursor_orddetails = null;
		MIC_Inventory mic_Inventory = null;
		try {

			String q = "SELECT * FROM " + DatabaseHandler.TABLE_MIC2
					+ " WHERE " + DatabaseHandler.KEY_LOCATION + " = '" + loc
					+ "'" + " and " + KEY_ITEMNUMBER + " = '" + it + "' ";

			cursor_orddetails = db.rawQuery(q, null);

			if (cursor_orddetails.moveToFirst()) {
				mic_Inventory = new MIC_Inventory();
				mic_Inventory.setLocation(cursor_orddetails.getString(1));
				mic_Inventory.setItemno(cursor_orddetails.getString(2));
				mic_Inventory
						.setItemdescription(cursor_orddetails.getString(3));
				mic_Inventory.setPickingseq(cursor_orddetails.getString(4));
				mic_Inventory.setQoh(cursor_orddetails.getString(5));
				mic_Inventory.setQc(cursor_orddetails.getString(6));
				mic_Inventory.setStockunit(cursor_orddetails.getString(7));
				mic_Inventory.setStatus(cursor_orddetails.getInt(8));

				return mic_Inventory;
			} else {
				Log.i("No Data", "");
			}
		} catch (Exception e) {
			db.endTransaction();
			errCode = "Error 5211";
			msg = "getmicInventoryDetails failed.";
			errMsg = errCode + " : " + msg;
			db.close();
			LogfileCreator.appendLog(errMsg);
		}

		cursor_orddetails.close();

		return mic_Inventory;
	}

	public MIC_UPC getItemFromUpc_mic(String UpcCode) {

		MIC_UPC upc = null;
		Cursor cursor = db.rawQuery("select ItemNumber from " + TABLE_MIC5
				+ " where " + KEY_UPCCODE + " = '"

				+ UpcCode + "' ", null);

		try {
			while (cursor.moveToNext()) {
				upc = new MIC_UPC();

				upc.setItemno(cursor.getString(0));
				/*
				 * manf.setManuf_itemno(cursor.getString(1));
				 * manf.setManuf_uom(cursor.getString(2));
				 * manf.setCompany_code(cursor.getString(3));
				 */
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5413";
			msg = "getItemFromUpc_mic failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return upc;
	}

	public void deleteFromOrderTable(String ordno) {
		// TODO Auto-generated method stub
		try {
			db.execSQL("DELETE FROM " + TABLE_MSE2 + " WHERE "
					+ KEY_ORDER_NUMBER + "='" + ordno + "' ");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 535";
			msg = "deleteFromOrderTable failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}
	public void deleteFromIntTable(String intno) {
		// TODO Auto-generated method stub
		try {
			db.execSQL("DELETE FROM " + TABLE_INT2 + " WHERE "
					+ KEY_INT_NUMBER + "='" + intno + "' ");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 535";
			msg = "deleteFromOrderTable failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}

	public void deleteFromMasterTable(String ordno) {
		// TODO Auto-generated method stub
		try {
			db.execSQL("DELETE FROM " + TABLE_MSE3 + " WHERE "
					+ KEY_ORDER_NUMBER + "='" + ordno + "' ");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 535";
			msg = "deleteFromMasterTable failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}
	/*public void deleteFromIntMasterTable(String intno) {
		// TODO Auto-generated method stub
		try {
			db.execSQL("DELETE FROM " + TABLE_INT3 + " WHERE "
					+ KEY_INT_NUMBER + "='" + intno + "' ");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 535";
			msg = "deleteFromIntMasterTable failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}*/
	public void deleteFromTransTable(String ordno) {
		// TODO Auto-generated method stub
		try {
			db.execSQL("DELETE FROM " + TABLE_MSE5 + " WHERE "
					+ KEY_ORDER_NUMBER + "='" + ordno + "' ");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 535";
			msg = "deleteFromTransTable failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}
	public void deleteFromIntTransTable(String intno) {
		// TODO Auto-generated method stub
		try {
			db.execSQL("DELETE FROM " + TABLE_INT5 + " WHERE "
					+ KEY_INT_NUMBER + "='" + intno + "' ");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 535";
			msg = "deleteFromIntTransTable failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}

	public String deleteFromTransTable(String ordno, String itno) {
		// TODO Auto-generated method stub
		String res = "";
		try {
			db.execSQL("DELETE FROM " + TABLE_MSE5 + " WHERE "
					+ KEY_ORDER_NUMBER + "='" + ordno + "' AND "
					+ KEY_ITEMNUMBER + "='" + itno + "'");
			res = "success";
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 535";
			msg = "deleteFromTransTable failed.";
			errMsg = errCode + " : " + msg;
			res = "error";
			LogfileCreator.appendLog(errMsg);
			db.close();

		}
		return res;

	}

	public void deleteFromOrderTable_mpr(String ordno) {
		// TODO Auto-generated method stub
		try {
			db.execSQL("DELETE FROM " + TABLE_MPR4 + " WHERE " + KEY_PONUMBER
					+ "='" + ordno + "' ");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 535";
			msg = "deleteFromOrderTable failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}

	public void deleteFromMasterTable_mpr(String ordno) {
		// TODO Auto-generated method stub
		try {
			db.execSQL("DELETE FROM " + TABLE_MPR5 + " WHERE " + KEY_PONUMBER
					+ "='" + ordno + "' ");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 535";
			msg = "deleteFromMasterTable failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}

	public void deleteFromTransTable_mpr(String ordno) {
		// TODO Auto-generated method stub
		try {
			db.execSQL("DELETE FROM " + TABLE_MPR6 + " WHERE " + KEY_PONUMBER
					+ "='" + ordno + "' ");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 535";
			msg = "deleteFromTransTable failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}

	public String deleteFromTransTable_mpr(String ordno, String itno) {
		// TODO Auto-generated method stub
		String res = "";
		try {
			db.execSQL("DELETE FROM " + TABLE_MPR6 + " WHERE " + KEY_PONUMBER
					+ "='" + ordno + "' AND " + KEY_ITEMNUMBER + "='" + itno
					+ "'");
			res = "success";
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 535";
			msg = "deleteFromTransTable failed.";
			errMsg = errCode + " : " + msg;
			res = "error";
			LogfileCreator.appendLog(errMsg);
			db.close();

		}
		return res;

	}

	public boolean checkMicInventoryDetails(String loc, String item) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MIC2
				+ " WHERE " + DatabaseHandler.KEY_LOCATION + " = '" + loc + "'"
				+ " and " + KEY_ITEMNUMBER + " = '" + item + "' ";

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}

	public Cursor filteredItem(String st) throws SQLException {
		Cursor mcursor = null;
		try {
			if (st == null || st.length() == 0) {
				String query = "SELECT * FROM" + this.TABLE_MIC2 + "";
				mcursor = db.rawQuery(query, null);
			} else {
				mcursor = db.query(this.TABLE_MIC2, new String[] { this.KEY_ID,
						this.KEY_ITEMNUMBER, this.KEY_DESCRIPTION,
						this.KEY_PICKINGSEQUENCE, this.KEY_QTYONHAND,
						this.KEY_QTYCOUNTED, this.KEY_STOCKUNIT,
						this.KEY_STATUS }, this.KEY_ITEMNUMBER + " like '%"
						+ st + "%'", null, null, null, null);
			}
			if (mcursor != null) {
				mcursor.moveToFirst();
			}
		} catch (Exception e) {
			db.endTransaction();
			errCode = "Error 511";
			msg = "Filter MIC failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		return mcursor;

	}

	// If Database created and data is not present there then exception may come
	// Something cursor index0
	/* <<<---PRANESH-->>>> */
	public boolean GetData2(String uname, String pass) {
		boolean result = false;
		Cursor c1 = null;
		try {

			String query = "SELECT * FROM " + TABLE_MIS2 + "";
			c1 = db.rawQuery(query, null);
			if (c1.getCount() > 0) {
				c1.moveToFirst();
				String u = c1.getString(c1
						.getColumnIndex(DatabaseHandler.KEY_USERID));
				String p = c1.getString(c1
						.getColumnIndex(DatabaseHandler.KEY_PWD));

				if ((uname.trim().equalsIgnoreCase((u.trim())))
						&& (pass.trim().equalsIgnoreCase(p.trim()))) {

					result = true;

				} else {
					result = false;
				}
			}
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 543";
			msg = "GetData2 failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}
		c1.close();
		return result;
	}

	public void deleteConv() {
		try {
			db.execSQL("DELETE FROM MIC_CONVERSIONFACTOR");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 512-4";
			msg = "DELETE MIC_CONVERSIONFACTOR failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}

	public void deleteInv() {
		try {
			db.execSQL("DELETE FROM MIC_INVENTORY");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 512-3";
			msg = "DELETE MIC_INVENTORY failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}

	public void deleteManf() {
		try {
			db.execSQL("DELETE FROM MIC_MANUFACTURENUMBER");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 512-2";
			msg = "DELETE MIC_MANUFACTURENUMBER failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}

	public void deleteUpc() {
		try {
			db.execSQL("DELETE FROM MIC_UPC");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 512-1";
			msg = "DELETE MIC_UPC failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}

	public void DELETE_MICUOMINTERNAL(String loc) {
		try {
			db.execSQL("DELETE FROM MIC_UOMINTERNAL where Location='" + loc
					+ "'");
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 512";
			msg = "DELETE MICUOMINTERNAL failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}

	public String deleteTransaction(List<String> orderToDelete) {

		String result = "";
		try {
			for (int i = 0; i < orderToDelete.size(); i++) {
				db.execSQL("DELETE FROM " + TABLE_MSE5 + " WHERE "
						+ KEY_ORDER_NUMBER + "='" + orderToDelete.get(i) + "'");

				db.execSQL("DELETE FROM " + TABLE_MSE2 + " WHERE "
						+ KEY_ORDER_NUMBER + "='" + orderToDelete.get(i) + "'");

				db.execSQL("DELETE FROM " + TABLE_MSE3 + " WHERE "
						+ KEY_ORDER_NUMBER + "='" + orderToDelete.get(i) + "'");
				db.execSQL("DELETE FROM " + TABLE_MSE8 + " WHERE "
						+ KEY_ORDER_NUMBER + "='" + orderToDelete.get(i) + "'");
				result = "success";
			}

		} catch (Exception ex) {
			db.endTransaction();
			result = "error";
			errCode = "Error 5_13";
			msg = "Delete Shipment failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

		return result;

	}
	public String deleteInternalTransaction(List<String> orderToDelete) {

		String result = "";
		try {
			for (int i = 0; i < orderToDelete.size(); i++) {
				db.execSQL("DELETE FROM " + TABLE_INT5 + " WHERE "
						+ KEY_INT_NUMBER + "='" + orderToDelete.get(i) + "'");

				db.execSQL("DELETE FROM " + TABLE_INT2 + " WHERE "
						+ KEY_INT_NUMBER + "='" + orderToDelete.get(i) + "'");

				/*db.execSQL("DELETE FROM " + TABLE_INT3 + " WHERE "
						+ KEY_INT_NUMBER + "='" + orderToDelete.get(i) + "'");*/
				db.execSQL("DELETE FROM " + TABLE_INT8 + " WHERE "
						+ KEY_INT_NUMBER + "='" + orderToDelete.get(i) + "'");
				result = "success";
			}

		} catch (Exception ex) {
			db.endTransaction();
			result = "error";
			errCode = "Error 5_13";
			msg = "Delete deleteInternalTransaction failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

		return result;

	}

	public String deleteTrans(List<String> orderToDelete) {

		String result = "";
		try {
			for (int i = 0; i < orderToDelete.size(); i++) {
				db.execSQL("DELETE FROM " + TABLE_MPR5 + " WHERE "
						+ KEY_PONUMBER + "='" + orderToDelete.get(i) + "'");

				db.execSQL("DELETE FROM " + TABLE_MPR4 + " WHERE "
						+ KEY_PONUMBER + "='" + orderToDelete.get(i) + "'");

				db.execSQL("DELETE FROM " + TABLE_MPR6 + " WHERE "
						+ KEY_PONUMBER + "='" + orderToDelete.get(i) + "'");
				db.execSQL("DELETE FROM " + TABLE_MPR9 + " WHERE "
						+ KEY_PONUMBER + "='" + orderToDelete.get(i) + "'");
				result = "success";
			}

		} catch (Exception ex) {
			db.endTransaction();
			result = "error";
			errCode = "Error 5_13";
			msg = "Delete Purchase failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

		return result;

	}

	public void Delete_MSETrans() {
		try {
			db.execSQL("DELETE FROM " + TABLE_MSE5);
		} catch (Exception ex) {

			db.endTransaction();
			errCode = "Error 513";
			msg = "Delete_MSETrans failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}

	public Cursor getMPRVendorData(String purorder) {
		// TODO Auto-generated method stub
		Cursor cursor = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MPR5 + " WHERE "
				+ DatabaseHandler.KEY_PONUMBER + " = '" + purorder + "'";
		cursor = db.rawQuery(q, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
		}

		return cursor;

	}

	public Cursor getMSECustomerData(String orderno) {
		// TODO Auto-generated method stub
		Cursor cursor = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MSE3 + " WHERE "
				+ DatabaseHandler.KEY_ORDER_NUMBER + " = '" + orderno + "'";
		cursor = db.rawQuery(q, null);
		try {
			int i = cursor.getCount();

			if (cursor != null && i > 0) {
				cursor.moveToFirst();
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5270";
			msg = "getMSECustomerData failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}
		return cursor;

	}
	public Cursor getINTCostData(String intno) {
		// TODO Auto-generated method stub
		Cursor cursor = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_INT2 + " WHERE "
				+ DatabaseHandler.KEY_INT_NUMBER + " = '" + intno + "'";
		cursor = db.rawQuery(q, null);
		try {
			int i = cursor.getCount();

			if (cursor != null && i > 0) {
				cursor.moveToFirst();
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5270";
			msg = "getINTCostData failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}
		return cursor;

	}

	public Cursor getMPRFullDetails(String Po) {
		Cursor cursor = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MPR4 + " WHERE "
				+ DatabaseHandler.KEY_PONUMBER + " = '" + Po + "'";
		cursor = db.rawQuery(q, null);
		try {
			int i = cursor.getCount();

			if (cursor != null && i > 0) {
				cursor.moveToFirst();
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5270";
			msg = "getMSECustomerData failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		return cursor;
	}

	public List<MSE_OrderDetails> getMseDataMSE_OrderDetails(String ordNo) {
		Cursor cursor = null;
		List<MSE_OrderDetails> ordlst = new ArrayList<MSE_OrderDetails>();

		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MSE2 + " WHERE "
				+ DatabaseHandler.KEY_ORDER_NUMBER + " = '" + ordNo + "'";

		cursor = db.rawQuery(q, null);

		try {
			while (cursor.moveToNext()) {
				MSE_OrderDetails mse_OrderDetails = new MSE_OrderDetails();

				mse_OrderDetails.setItemNumber(cursor.getString(2));
				mse_OrderDetails.setItemDescription(cursor.getString(3));
				mse_OrderDetails.setPickingSequence(cursor.getString(12));
				mse_OrderDetails.setQtyOrdred(cursor.getInt(5));
				mse_OrderDetails.setQtyShiped(cursor.getInt(6));
				mse_OrderDetails.setUom(cursor.getString(4));
				mse_OrderDetails.setShipViaCode(cursor.getString(8));
				mse_OrderDetails.setComments(cursor.getString(10));

				ordlst.add(mse_OrderDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 520";
			msg = "getMseDataMSE_OrderDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return ordlst;
	}
	public List<Internal_OrderDetails> getInternalDataInternal_OrderDetails(String intNo) {
		Cursor cursor_intdetails = null;
		List<Internal_OrderDetails> ordlst = new ArrayList<Internal_OrderDetails>();

		String q = "SELECT * FROM " + DatabaseHandler.TABLE_INT2 + " WHERE "
				+ DatabaseHandler.KEY_INT_NUMBER + " = '" + intNo + "'";

		cursor_intdetails = db.rawQuery(q, null);

		try {
			while (cursor_intdetails.moveToNext()) {
				int shipqty=cursor_intdetails.getInt(6);
				int ordqty=cursor_intdetails.getInt(5);
				if(ordqty!=0)
				{
				Internal_OrderDetails internal_OrderDetails = new Internal_OrderDetails();

				internal_OrderDetails.setIntNumber(cursor_intdetails.getString(0));
				internal_OrderDetails.setCostCeneter(cursor_intdetails.getString(1));
				internal_OrderDetails.setItemNumber(cursor_intdetails.getString(2));
				internal_OrderDetails.setItemDescription(cursor_intdetails.getString(3));
				internal_OrderDetails.setUom(cursor_intdetails.getString(4));
				internal_OrderDetails.setQtyOrdred(cursor_intdetails.getInt(5));
				internal_OrderDetails.setQtyShiped(cursor_intdetails.getInt(6));
				internal_OrderDetails.setLocation(cursor_intdetails.getString(7));
				internal_OrderDetails.setLineNumber(cursor_intdetails.getString(8));
				internal_OrderDetails.setIidate(cursor_intdetails.getString(9));

				ordlst.add(internal_OrderDetails);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 520";
			msg = "getInternalDataInternal_OrderDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor_intdetails.close();

		return ordlst;
	}

	public MSE_MasterDetails getMseMasterDetails(String ordNo) {
		Cursor cursor_masdetails = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MSE3 + " WHERE "
				+ DatabaseHandler.KEY_ORDER_NUMBER + " = '" + ordNo + "'";
		cursor_masdetails = db.rawQuery(q, null);

		if (cursor_masdetails != null) {
			cursor_masdetails.moveToFirst();
		}

		MSE_MasterDetails mse_MasterDetails = new MSE_MasterDetails();

		mse_MasterDetails.setOrdNumber(cursor_masdetails.getString(0));
		mse_MasterDetails.setOrdDate(cursor_masdetails.getString(1));
		mse_MasterDetails.setCustNumber(cursor_masdetails.getString(2));
		mse_MasterDetails.setShiptoLocCode(cursor_masdetails.getString(3));
		mse_MasterDetails.setBillName(cursor_masdetails.getString(4));
		mse_MasterDetails.setBillAddress1(cursor_masdetails.getString(5));
		mse_MasterDetails.setBillAddress2(cursor_masdetails.getString(6));
		mse_MasterDetails.setBillCity(cursor_masdetails.getString(7));
		mse_MasterDetails.setBillState(cursor_masdetails.getString(8));
		mse_MasterDetails.setBillZip(cursor_masdetails.getString(9));
		mse_MasterDetails.setBillCountry(cursor_masdetails.getString(10));
		mse_MasterDetails.setBillPhone(cursor_masdetails.getString(11));
		mse_MasterDetails.setBillFax(cursor_masdetails.getString(12));
		mse_MasterDetails.setShipToName(cursor_masdetails.getString(13));
		mse_MasterDetails.setShipToAddr1(cursor_masdetails.getString(14));
		mse_MasterDetails.setShipToAddr2(cursor_masdetails.getString(15));
		mse_MasterDetails.setShipToCity(cursor_masdetails.getString(16));
		mse_MasterDetails.setShipToState(cursor_masdetails.getString(17));
		mse_MasterDetails.setShipToZip(cursor_masdetails.getString(18));
		mse_MasterDetails.setShipToCountry(cursor_masdetails.getString(19));
		mse_MasterDetails.setShipToPhone(cursor_masdetails.getString(20));
		mse_MasterDetails.setShipToFax(cursor_masdetails.getString(21));

		cursor_masdetails.close();

		return mse_MasterDetails;

	}

	public MSE_OrderDetails getMseOrderDetails(String ordNo) {
		Cursor cursor_orddetails = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MSE2 + " WHERE "
				+ DatabaseHandler.KEY_ORDER_NUMBER + " = '" + ordNo + "'";
		cursor_orddetails = db.rawQuery(q, null);

		if (cursor_orddetails != null) {
			cursor_orddetails.moveToFirst();
		}

		MSE_OrderDetails mse_OrderDetails = new MSE_OrderDetails();

		mse_OrderDetails.setOrdNumber(cursor_orddetails.getString(0));
		mse_OrderDetails.setCustNumber(cursor_orddetails.getString(1));
		mse_OrderDetails.setItemNumber(cursor_orddetails.getString(2));
		mse_OrderDetails.setItemDescription(cursor_orddetails.getString(3));
		mse_OrderDetails.setUom(cursor_orddetails.getString(4));
		mse_OrderDetails.setQtyOrdred(cursor_orddetails.getInt(5));
		mse_OrderDetails.setQtyShiped(cursor_orddetails.getInt(6));
		mse_OrderDetails.setLocation(cursor_orddetails.getString(7));
		mse_OrderDetails.setShipViaCode(cursor_orddetails.getString(8));
		mse_OrderDetails.setShipviaDescription(cursor_orddetails.getString(9));
		mse_OrderDetails.setComments(cursor_orddetails.getString(10));
		mse_OrderDetails.setLineNumber(cursor_orddetails.getString(11));
		mse_OrderDetails.setPickingSequence(cursor_orddetails.getString(12));

		cursor_orddetails.close();
		return mse_OrderDetails;
	}
	public Internal_OrderDetails getInternalOrderDetails(String intNo) {
		Cursor cursor_intdetails = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_INT2 + " WHERE "
				+ DatabaseHandler.KEY_INT_NUMBER + " = '" + intNo + "'";
		cursor_intdetails = db.rawQuery(q, null);

		if (cursor_intdetails != null) {
			cursor_intdetails.moveToFirst();
		}

		Internal_OrderDetails internal_OrderDetails = new Internal_OrderDetails();

		internal_OrderDetails.setIntNumber(cursor_intdetails.getString(0));
		internal_OrderDetails.setCostCeneter(cursor_intdetails.getString(1));
		internal_OrderDetails.setItemNumber(cursor_intdetails.getString(2));
		internal_OrderDetails.setItemDescription(cursor_intdetails.getString(3));
		internal_OrderDetails.setUom(cursor_intdetails.getString(4));
		internal_OrderDetails.setQtyOrdred(cursor_intdetails.getInt(5));
		internal_OrderDetails.setQtyShiped(cursor_intdetails.getInt(6));
		internal_OrderDetails.setLocation(cursor_intdetails.getString(7));
		internal_OrderDetails.setLineNumber(cursor_intdetails.getString(8));
		internal_OrderDetails.setIidate(cursor_intdetails.getString(9));
		

		cursor_intdetails.close();
		return internal_OrderDetails;
	}

	public MSE_OrderDetails getMseOrderDetails(String ordNo, String it) {
		Cursor cursor_orddetails = null;
		MSE_OrderDetails mse_OrderDetails = null;
		try {

			String q = "SELECT * FROM " + DatabaseHandler.TABLE_MSE2
					+ " WHERE " + DatabaseHandler.KEY_ORDER_NUMBER + " = '"
					+ ordNo + "'" + " and " + KEY_ITEMNUMBER + " = '" + it
					+ "' ";

			cursor_orddetails = db.rawQuery(q, null);

			if (cursor_orddetails.moveToFirst()) {
				mse_OrderDetails = new MSE_OrderDetails();
				mse_OrderDetails.setOrdNumber(cursor_orddetails.getString(0));
				mse_OrderDetails.setCustNumber(cursor_orddetails.getString(1));
				mse_OrderDetails.setItemNumber(cursor_orddetails.getString(2));
				mse_OrderDetails.setItemDescription(cursor_orddetails
						.getString(3));
				mse_OrderDetails.setUom(cursor_orddetails.getString(4));
				mse_OrderDetails.setQtyOrdred(cursor_orddetails.getInt(5));
				mse_OrderDetails.setQtyShiped(cursor_orddetails.getInt(6));
				mse_OrderDetails.setLocation(cursor_orddetails.getString(7));
				mse_OrderDetails.setShipViaCode(cursor_orddetails.getString(8));
				mse_OrderDetails.setShipviaDescription(cursor_orddetails
						.getString(9));
				mse_OrderDetails.setComments(cursor_orddetails.getString(10));
				mse_OrderDetails.setLineNumber(cursor_orddetails.getString(11));
				mse_OrderDetails.setPickingSequence(cursor_orddetails
						.getString(12));
				return mse_OrderDetails;
			} else {
				Log.i("No Data", "");
			}
		} catch (Exception e) {
			db.endTransaction();
			errCode = "Error 521";
			msg = "getMseOrderDetails failed.";
			errMsg = errCode + " : " + msg;
			db.close();
			LogfileCreator.appendLog(errMsg);
		}

		cursor_orddetails.close();

		return mse_OrderDetails;
	}
	public Internal_OrderDetails getInternalOrderDetails(String intNo, String it) {
		Cursor cursor_intdetails = null;
		Internal_OrderDetails internal_OrderDetails = null;
		try {

			String q = "SELECT * FROM " + DatabaseHandler.TABLE_INT2
					+ " WHERE " + DatabaseHandler.KEY_INT_NUMBER + " = '"
					+ intNo + "'" + " and " + KEY_ITEMNUMBER + " = '" + it
					+ "' ";

			cursor_intdetails = db.rawQuery(q, null);

			if (cursor_intdetails.moveToFirst()) {
				
				internal_OrderDetails = new Internal_OrderDetails();
				internal_OrderDetails.setIntNumber(cursor_intdetails.getString(0));
				internal_OrderDetails.setCostCeneter(cursor_intdetails.getString(1));
				internal_OrderDetails.setItemNumber(cursor_intdetails.getString(2));
				internal_OrderDetails.setItemDescription(cursor_intdetails.getString(3));
				internal_OrderDetails.setUom(cursor_intdetails.getString(4));
				internal_OrderDetails.setQtyOrdred(cursor_intdetails.getInt(5));
				internal_OrderDetails.setQtyShiped(cursor_intdetails.getInt(6));
				internal_OrderDetails.setLocation(cursor_intdetails.getString(7));
				internal_OrderDetails.setLineNumber(cursor_intdetails.getString(8));
				internal_OrderDetails.setIidate(cursor_intdetails.getString(9));
				return internal_OrderDetails;
			} else {
				Log.i("No Data", "");
			}
		} catch (Exception e) {
			db.endTransaction();
			errCode = "Error 521";
			msg = "getInternalOrderDetails failed.";
			errMsg = errCode + " : " + msg;
			db.close();
			LogfileCreator.appendLog(errMsg);
		}

		cursor_intdetails.close();

		return internal_OrderDetails;
	}

	public boolean getMse_TransDetails(String ordNo, String item) {

		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MSE5
				+ " WHERE " + DatabaseHandler.KEY_ORDER_NUMBER + " = '" + ordNo
				+ "'" + " and " + KEY_ITEMNUMBER + " = '" + item + "' ";
		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}

	public boolean checkMse_OrderDetails(String ordNo, String item) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MSE2
				+ " WHERE " + DatabaseHandler.KEY_ORDER_NUMBER + " = '" + ordNo
				+ "'" + " and " + KEY_ITEMNUMBER + " = '" + item + "' ";

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}
	public boolean checkInternal_OrderDetails(String intNo, String item) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_INT2
				+ " WHERE " + DatabaseHandler.KEY_INT_NUMBER + " = '" + intNo
				+ "'" + " and " + KEY_ITEMNUMBER + " = '" + item + "' ";

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}

	public boolean checkMISSetting() {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_SETTINGS;

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}

	public boolean checkGetData2ContainsData() {
		boolean flag = false;
		try {

			String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MIS2;
			int numRows = (int) DatabaseUtils.longForQuery(db, q, null);
			if (numRows > 0) {
				flag = true;
				return flag;
			} else {
				return flag;
			}
		} catch (Exception e) {

			errCode = "Error 542";
			msg = "checkGetData2ContainsData failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
		}
		return flag;
	}

	public String addMse_OrderDetails(MSE_OrderDetails mse_OrderDetails) {
		// TODO Auto-generated method stub
		String result = "";
		try {
			ContentValues cv = new ContentValues();
			cv.put(KEY_ORDER_NUMBER, mse_OrderDetails.getOrdNumber());
			cv.put(KEY_CUSTOMER_NUMBER, mse_OrderDetails.getCustNumber());
			cv.put(KEY_ITEMNUMBER, mse_OrderDetails.getItemNumber());
			cv.put(KEY_ITEM_DESCRIPTION, mse_OrderDetails.getItemDescription());
			cv.put(KEY_UOM, mse_OrderDetails.getUom());
			cv.put(KEY_QUANTITY_ORDER, mse_OrderDetails.getQtyOrdred());
			cv.put(KEY_QUANTITY_SHIPPED, mse_OrderDetails.getQtyShiped());
			cv.put(KEY_LOCATION, mse_OrderDetails.getLocation());
			cv.put(KEY_SHIP_VIA_CODE, mse_OrderDetails.getShipViaCode());
			cv.put(KEY_SHIP_VIA_DESC, mse_OrderDetails.getShipviaDescription());
			cv.put(KEY_COMMENTS, mse_OrderDetails.getComments());
			cv.put(KEY_LINENUMBER, mse_OrderDetails.getLineNumber());
			cv.put(KEY_PICKINGSEQUENCE, mse_OrderDetails.getPickingSequence());
			db.insert(TABLE_MSE2, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_MSE2);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 522";
			msg = "addMse_OrderDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;
	}
	public String addInternal_OrderDetails(Internal_OrderDetails internal_OrderDetails) {
		// TODO Auto-generated method stub
		String result = "";
		try {
			ContentValues cv = new ContentValues();
			cv.put(KEY_INT_NUMBER, internal_OrderDetails.getIntNumber());
			cv.put(KEY_COST_CENTER, internal_OrderDetails.getCostCeneter());
			
			cv.put(KEY_ITEMNUMBER, internal_OrderDetails.getItemNumber());
			cv.put(KEY_ITEM_DESCRIPTION, internal_OrderDetails.getItemDescription());
			cv.put(KEY_UOM, internal_OrderDetails.getUom());
			cv.put(KEY_QUANTITY_ORDER, internal_OrderDetails.getQtyOrdred());
			cv.put(KEY_QUANTITY_SHIPPED, internal_OrderDetails.getQtyShiped());
			cv.put(KEY_LOCATION, internal_OrderDetails.getLocation());
			cv.put(KEY_LINENUMBER, internal_OrderDetails.getLineNumber());
			cv.put(KEY_INT_DATE, internal_OrderDetails.getIidate());
			
			db.insert(TABLE_INT2, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_INT2);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 522";
			msg = "addInternal_OrderDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;
	}

	public String updateMse_OrderDetails(MSE_OrderDetails mse_OrderDetails,
			String flag) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();
			Integer ordQty = mse_OrderDetails.getQtyOrdred();
			String ord = mse_OrderDetails.getOrdNumber();
			String item = mse_OrderDetails.getItemNumber();
			String com = mse_OrderDetails.getComments();
			/*
			 * String sql="UPDATE " + TABLE_MSE2 + " SET " + KEY_COMMENTS + com
			 * + "," + KEY_QUANTITY_SHIPPED + shpQty + " WHERE " +
			 * KEY_ORDER_NUMBER + "= '" + ord + "' AND " + KEY_ITEMNUMBER +
			 * " = '" + item + " ' ";
			 */

			// cv.put(KEY_ORDER_NUMBER, mse_OrderDetails.getOrdNumber());
			// cv.put(KEY_CUSTOMER_NUMBER, mse_OrderDetails.getCustNumber());
			// cv.put(KEY_ITEMNUMBER, mse_OrderDetails.getItemNumber());
			// cv.put(KEY_ITEM_DESCRIPTION,
			// mse_OrderDetails.getItemDescription());
			// cv.put(KEY_UOM, mse_OrderDetails.getUom());
			cv.put(KEY_QUANTITY_ORDER, mse_OrderDetails.getQtyOrdred());
			if (flag.equals("Ship"))
				cv.put(KEY_QUANTITY_SHIPPED, mse_OrderDetails.getQtyShiped());
			else
				cv.put(KEY_QUANTITY_SHIPPED, "0");

			// cv.put(KEY_LOCATION, mse_OrderDetails.getLocation());
			// cv.put(KEY_SHIP_VIA_CODE, mse_OrderDetails.getShipViaCode());
			// cv.put(KEY_SHIP_VIA_DESC,
			// mse_OrderDetails.getShipviaDescription());
			cv.put(KEY_COMMENTS, com);
			// cv.put(KEY_LINENUMBER, mse_OrderDetails.getLineNumber());
			// cv.put(KEY_PICKINGSEQUENCE,
			// mse_OrderDetails.getPickingSequence());

			db.update(TABLE_MSE2, cv, KEY_ORDER_NUMBER + "= '" + ord + "' AND "
					+ KEY_ITEMNUMBER + " = '" + item + "' ", null);

			Log.i("Success", "Data Updated in table " + TABLE_MSE2);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 523";
			msg = "updateMse_OrderDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}
	public String updateInternal_OrderDetails(Internal_OrderDetails internal_OrderDetails,
			String flag) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();
			Integer ordQty = internal_OrderDetails.getQtyOrdred();
			String ord = internal_OrderDetails.getIntNumber();
			String item = internal_OrderDetails.getItemNumber();

			cv.put(KEY_QUANTITY_ORDER, internal_OrderDetails.getQtyOrdred());
			if (flag.equals("Ship"))
				cv.put(KEY_QUANTITY_SHIPPED, internal_OrderDetails.getQtyShiped());
			else
				cv.put(KEY_QUANTITY_SHIPPED, "0");

			// cv.put(KEY_LOCATION, mse_OrderDetails.getLocation());
			// cv.put(KEY_SHIP_VIA_CODE, mse_OrderDetails.getShipViaCode());
			// cv.put(KEY_SHIP_VIA_DESC,
			// mse_OrderDetails.getShipviaDescription());
			
			// cv.put(KEY_LINENUMBER, mse_OrderDetails.getLineNumber());
			// cv.put(KEY_PICKINGSEQUENCE,
			// mse_OrderDetails.getPickingSequence());

			db.update(TABLE_INT2, cv, KEY_INT_NUMBER + "= '" + ord + "' AND "
					+ KEY_ITEMNUMBER + " = '" + item + "' ", null);

			Log.i("Success", "Data Updated in table " + TABLE_INT2);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 523";
			msg = "updateInternal_OrderDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}
	public String updateSettings(MIS_Setting mis_setting) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();
			String deviceId = mis_setting.getDeviceId();// .getQtyOrdred();
			String ipAddress = mis_setting.getIpAddress();

			cv.put(KEY_DEVICE_ID, deviceId);
			cv.put(KEY_IP, ipAddress);

			db.update(TABLE_SETTINGS, cv, null, null);

			Log.i("Success", "Data Updated in table " + TABLE_SETTINGS);
			result = "success";
		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 524";
			msg = "updateSettings failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();
		}

		return result;

	}

	public void addMse_CustomerDetails(MSE_Customer mse_Customer) {
		// TODO Auto-generated method stub
		try {
			ContentValues cv = new ContentValues();

			cv.put(KEY_CUSTOMER_NUMBER, mse_Customer.getCustNumber());
			cv.put(KEY_CUSTOMER_NAME, mse_Customer.getCustName());
			db.insert(TABLE_MSE1, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_MSE1);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 525";
			msg = "addMse_CustomerDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

	}

	public String addMse_MasterDetails(MSE_MasterDetails mse_MasterDetails) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			ContentValues cv = new ContentValues();

			cv.put(KEY_ORDER_NUMBER, mse_MasterDetails.getOrdNumber());
			cv.put(KEY_ORDER_DATE, mse_MasterDetails.getOrdDate());
			cv.put(KEY_CUSTOMER_NUMBER, mse_MasterDetails.getCustNumber());
			cv.put(KEY_SHIP_TO_LOCATION_CODE,
					mse_MasterDetails.getShiptoLocCode());
			cv.put(KEY_BILL_TO_NAME, mse_MasterDetails.getBillName());
			cv.put(KEY_BILL_TO_ADDR1, mse_MasterDetails.getBillAddress1());
			cv.put(KEY_BILL_TO_ADDR2, mse_MasterDetails.getBillAddress2());
			cv.put(KEY_BILL_TO_CITY, mse_MasterDetails.getBillCity());
			cv.put(KEY_BILL_TO_STATE, mse_MasterDetails.getBillState());
			cv.put(KEY_BILL_TO_ZIP, mse_MasterDetails.getBillZip());
			cv.put(KEY_BILL_TO_COUNTRY, mse_MasterDetails.getBillCountry());
			cv.put(KEY_BILL_TO_PHONE, mse_MasterDetails.getBillPhone());
			cv.put(KEY_BILL_TO_FAX, mse_MasterDetails.getBillFax());
			cv.put(KEY_SHIP_TO_NAME, mse_MasterDetails.getShipToName());
			cv.put(KEY_SHIP_TO_ADDR1, mse_MasterDetails.getShipToAddr1());
			cv.put(KEY_SHIP_TO_ADDR2, mse_MasterDetails.getShipToAddr2());
			cv.put(KEY_SHIP_TO_CITY, mse_MasterDetails.getShipToCity());
			cv.put(KEY_SHIP_TO_STATE, mse_MasterDetails.getShipToState());
			cv.put(KEY_SHIP_TO_ZIP, mse_MasterDetails.getShipToZip());
			cv.put(KEY_SHIP_TO_COUNTRY, mse_MasterDetails.getShipToCountry());
			cv.put(KEY_SHIP_TO_PHONE, mse_MasterDetails.getShipToPhone());
			cv.put(KEY_SHIP_TO_FAX, mse_MasterDetails.getShipToFax());

			db.insert(TABLE_MSE3, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_MSE3);

			result = "success";
		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 526";
			msg = "addMse_MasterDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();
		}

		return result;
	}

	public boolean checkMaster_Details(String ordNo, String custNo) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MSE3
				+ " WHERE " + DatabaseHandler.KEY_ORDER_NUMBER + " = '" + ordNo
				+ "'" + " and " + KEY_CUSTOMER_NUMBER + " = '" + custNo + "' ";
		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}

	public String updateMse_MasterDetails(MSE_MasterDetails mse_MasterDetails) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			ContentValues cv = new ContentValues();

			String ordNo = mse_MasterDetails.getOrdNumber();
			String custNo = mse_MasterDetails.getCustNumber();

			cv.put(KEY_ORDER_DATE, mse_MasterDetails.getOrdDate());
			cv.put(KEY_SHIP_TO_LOCATION_CODE,
					mse_MasterDetails.getShiptoLocCode());
			cv.put(KEY_BILL_TO_NAME, mse_MasterDetails.getBillName());
			cv.put(KEY_BILL_TO_ADDR1, mse_MasterDetails.getBillAddress1());
			cv.put(KEY_BILL_TO_ADDR2, mse_MasterDetails.getBillAddress2());
			cv.put(KEY_BILL_TO_CITY, mse_MasterDetails.getBillCity());
			cv.put(KEY_BILL_TO_STATE, mse_MasterDetails.getBillState());
			cv.put(KEY_BILL_TO_ZIP, mse_MasterDetails.getBillZip());
			cv.put(KEY_BILL_TO_COUNTRY, mse_MasterDetails.getBillCountry());
			cv.put(KEY_BILL_TO_PHONE, mse_MasterDetails.getBillPhone());
			cv.put(KEY_BILL_TO_FAX, mse_MasterDetails.getBillFax());
			cv.put(KEY_SHIP_TO_NAME, mse_MasterDetails.getShipToName());
			cv.put(KEY_SHIP_TO_ADDR1, mse_MasterDetails.getShipToAddr1());
			cv.put(KEY_SHIP_TO_ADDR2, mse_MasterDetails.getShipToAddr2());
			cv.put(KEY_SHIP_TO_CITY, mse_MasterDetails.getShipToCity());
			cv.put(KEY_SHIP_TO_STATE, mse_MasterDetails.getShipToState());
			cv.put(KEY_SHIP_TO_ZIP, mse_MasterDetails.getShipToZip());
			cv.put(KEY_SHIP_TO_COUNTRY, mse_MasterDetails.getShipToCountry());
			cv.put(KEY_SHIP_TO_PHONE, mse_MasterDetails.getShipToPhone());
			cv.put(KEY_SHIP_TO_FAX, mse_MasterDetails.getShipToFax());

			db.update(TABLE_MSE3, cv, KEY_ORDER_NUMBER + "= '" + ordNo
					+ "' AND " + KEY_CUSTOMER_NUMBER + "= '" + custNo + " '",
					null);

			Log.i("Success", "Data Updated in table " + TABLE_MSE3);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 527";
			msg = "updateMse_MasterDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public String updateMse_UpcDetails(MSE_Upc mse_upc) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			String item = mse_upc.getItemNo();// etItemNumber();
			String com = mse_upc.getUpcNo();// getComments();
			/*
			 * String sql="UPDATE " + TABLE_MSE2 + " SET " + KEY_COMMENTS + com
			 * + "," + KEY_QUANTITY_SHIPPED + shpQty + " WHERE " +
			 * KEY_ORDER_NUMBER + "= '" + ord + "' AND " + KEY_ITEMNUMBER +
			 * " = '" + item + " ' ";
			 */

			// cv.put(KEY_ORDER_NUMBER, mse_OrderDetails.getOrdNumber());
			// cv.put(KEY_CUSTOMER_NUMBER, mse_OrderDetails.getCustNumber());
			// cv.put(KEY_ITEMNUMBER, mse_OrderDetails.getItemNumber());
			// cv.put(KEY_ITEM_DESCRIPTION,
			// mse_OrderDetails.getItemDescription());
			// cv.put(KEY_UOM, mse_OrderDetails.getUom());
			// cv.put(KEY_QUANTITY_ORDER, mse_OrderDetails.getQtyOrdred());
			// cv.put(KEY_ITEMNUMBER, item);
			// cv.put(KEY_LOCATION, mse_OrderDetails.getLocation());
			// cv.put(KEY_SHIP_VIA_CODE, mse_OrderDetails.getShipViaCode());
			// cv.put(KEY_SHIP_VIA_DESC,
			// mse_OrderDetails.getShipviaDescription());
			cv.put(KEY_UPC_NUMBER, com);
			// cv.put(KEY_LINENUMBER, mse_OrderDetails.getLineNumber());
			// cv.put(KEY_PICKINGSEQUENCE,
			// mse_OrderDetails.getPickingSequence());

			db.update(TABLE_MSE6, cv, KEY_ITEMNUMBER + "= '" + item + "'", null);

			Log.i("Success", "Data Updated in table " + TABLE_MSE6);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 528";
			msg = "updateMse_UpcDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}
	public String updateInternal_UpcDetails(Internal_Upc internal_upc) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			String item = internal_upc.getItemNo();// etItemNumber();
			String com = internal_upc.getUpcNo();// getComments();
			/*
			 * String sql="UPDATE " + TABLE_MSE2 + " SET " + KEY_COMMENTS + com
			 * + "," + KEY_QUANTITY_SHIPPED + shpQty + " WHERE " +
			 * KEY_ORDER_NUMBER + "= '" + ord + "' AND " + KEY_ITEMNUMBER +
			 * " = '" + item + " ' ";
			 */

			// cv.put(KEY_ORDER_NUMBER, mse_OrderDetails.getOrdNumber());
			// cv.put(KEY_CUSTOMER_NUMBER, mse_OrderDetails.getCustNumber());
			// cv.put(KEY_ITEMNUMBER, mse_OrderDetails.getItemNumber());
			// cv.put(KEY_ITEM_DESCRIPTION,
			// mse_OrderDetails.getItemDescription());
			// cv.put(KEY_UOM, mse_OrderDetails.getUom());
			// cv.put(KEY_QUANTITY_ORDER, mse_OrderDetails.getQtyOrdred());
			// cv.put(KEY_ITEMNUMBER, item);
			// cv.put(KEY_LOCATION, mse_OrderDetails.getLocation());
			// cv.put(KEY_SHIP_VIA_CODE, mse_OrderDetails.getShipViaCode());
			// cv.put(KEY_SHIP_VIA_DESC,
			// mse_OrderDetails.getShipviaDescription());
			cv.put(KEY_UPC_NUMBER, com);
			// cv.put(KEY_LINENUMBER, mse_OrderDetails.getLineNumber());
			// cv.put(KEY_PICKINGSEQUENCE,
			// mse_OrderDetails.getPickingSequence());

			db.update(TABLE_INT6, cv, KEY_ITEMNUMBER + "= '" + item + "'", null);

			Log.i("Success", "Data Updated in table " + TABLE_INT6);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 528";
			msg = "updateInternal_UpcDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public boolean checkMse_ManfDetails(String item, String manf) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MSE7
				+ " WHERE " + DatabaseHandler.KEY_ITEMNUMBER + " = '" + item
				+ "'AND " + KEY_MANUNUMBER + "= '" + manf + "'";

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}
	public boolean checkInt_ManfDetails(String item, String manf) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_INT7
				+ " WHERE " + DatabaseHandler.KEY_ITEMNUMBER + " = '" + item
				+ "'AND " + KEY_MANUNUMBER + "= '" + manf + "'";

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}


	public String addMse_ManfDetails(Manf_Number01 mse_Manf) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			ContentValues cv = new ContentValues();

			cv.put(KEY_ITEMNUMBER, mse_Manf.getManuf_itemno());
			cv.put(KEY_MANUNUMBER, mse_Manf.getManuf_manitemno());

			db.insert(TABLE_MSE7, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_MSE7);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 529";
			msg = "addMse_ManfDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";

		}

		return result;
	}
	public String addInt_ManfDetails(Internal_Manf_Number01 mse_Manf) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			ContentValues cv = new ContentValues();

			cv.put(KEY_ITEMNUMBER, mse_Manf.getManuf_itemno());
			cv.put(KEY_MANUNUMBER, mse_Manf.getManuf_manitemno());

			db.insert(TABLE_INT7, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_INT7);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 529";
			msg = "addInt_ManfDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";

		}

		return result;
	}

	public String addMse_UpcDetails(MSE_Upc mse_Upc) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			ContentValues cv = new ContentValues();

			cv.put(KEY_ITEMNUMBER, mse_Upc.getItemNo());
			cv.put(KEY_UPC_NUMBER, mse_Upc.getUpcNo());

			db.insert(TABLE_MSE6, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_MSE6);

			result = "success";
		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 531";
			msg = "addMse_UpcDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;
	}
	public String addInternal_UpcDetails(Internal_Upc internal_Upc) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			ContentValues cv = new ContentValues();

			cv.put(KEY_ITEMNUMBER, internal_Upc.getItemNo());
			cv.put(KEY_UPC_NUMBER, internal_Upc.getUpcNo());

			db.insert(TABLE_INT6, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_INT6);

			result = "success";
		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 531";
			msg = "addInternal_UpcDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;
	}


	public boolean checkUpc_Details(String item) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MSE6
				+ " WHERE " + DatabaseHandler.KEY_ITEMNUMBER + " = '" + item
				+ "'";

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}
	public boolean checkInternalUpc_Details(String item) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_INT6
				+ " WHERE " + DatabaseHandler.KEY_ITEMNUMBER + " = '" + item
				+ "'";

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}
	
	public String addMse_TransDetails(MSE_OrderDetails mse_OrderDetails) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			String s = mse_OrderDetails.getCustNumber();
			String s1 = mse_OrderDetails.getLineNumber();

			cv.put(KEY_ORDER_NUMBER, mse_OrderDetails.getOrdNumber());
			cv.put(KEY_CUSTOMER_NUMBER, mse_OrderDetails.getCustNumber());
			cv.put(KEY_LINENUMBER, mse_OrderDetails.getLineNumber());
			cv.put(KEY_ITEMNUMBER, mse_OrderDetails.getItemNumber());
			cv.put(KEY_ITEM_DESCRIPTION, mse_OrderDetails.getItemDescription());
			cv.put(KEY_UOM, mse_OrderDetails.getUom());
			cv.put(KEY_QUANTITY_ORDER, mse_OrderDetails.getQtyOrdred());
			cv.put(KEY_QUANTITY_SHIPPED, mse_OrderDetails.getQtyShiped());
			cv.put(KEY_SHIP_VIA, mse_OrderDetails.getShipViaCode());
			cv.put(KEY_COMMENTS, mse_OrderDetails.getComments());
			cv.put(KEY_STATUS, 0);

			db.insert(TABLE_MSE5, null, cv);

			Log.i("Success", "Data inserted in table " + TABLE_MSE5);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 532";
			msg = "addMse_TransDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();
		}

		return result;

	}
	public String addInternal_TransDetails(Internal_OrderDetails internal_OrderDetails) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			

			cv.put(KEY_INT_NUMBER, internal_OrderDetails.getIntNumber());
			cv.put(KEY_COST_CENTER, internal_OrderDetails.getCostCeneter());
			cv.put(KEY_LINENUMBER, internal_OrderDetails.getLineNumber());
			cv.put(KEY_ITEMNUMBER, internal_OrderDetails.getItemNumber());
			cv.put(KEY_ITEM_DESCRIPTION, internal_OrderDetails.getItemDescription());
			cv.put(KEY_UOM, internal_OrderDetails.getUom());
			cv.put(KEY_QUANTITY_ORDER, internal_OrderDetails.getQtyOrdred());
			cv.put(KEY_QUANTITY_SHIPPED, internal_OrderDetails.getQtyShiped());
			cv.put(KEY_STATUS, 0);

			db.insert(TABLE_INT5, null, cv);

			Log.i("Success", "Data inserted in table " + TABLE_INT5);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 532";
			msg = "addInternal_TransDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();
		}

		return result;

	}

	public String updateMse_ManfDetails(Manf_Number01 mse_Manf) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			String manf_item = mse_Manf.getManuf_manitemno();// etItemNumber();
			String item = mse_Manf.getManuf_itemno();// getComments();
			/*
			 * String sql="UPDATE " + TABLE_MSE2 + " SET " + KEY_COMMENTS + com
			 * + "," + KEY_QUANTITY_SHIPPED + shpQty + " WHERE " +
			 * KEY_ORDER_NUMBER + "= '" + ord + "' AND " + KEY_ITEMNUMBER +
			 * " = '" + item + " ' ";
			 */

			// cv.put(KEY_ORDER_NUMBER, mse_OrderDetails.getOrdNumber());
			// cv.put(KEY_CUSTOMER_NUMBER, mse_OrderDetails.getCustNumber());
			// cv.put(KEY_ITEMNUMBER, mse_OrderDetails.getItemNumber());
			// cv.put(KEY_ITEM_DESCRIPTION,
			// mse_OrderDetails.getItemDescription());
			// cv.put(KEY_UOM, mse_OrderDetails.getUom());
			// cv.put(KEY_QUANTITY_ORDER, mse_OrderDetails.getQtyOrdred());
			// cv.put(KEY_ITEMNUMBER, item);
			// cv.put(KEY_LOCATION, mse_OrderDetails.getLocation());
			// cv.put(KEY_SHIP_VIA_CODE, mse_OrderDetails.getShipViaCode());
			// cv.put(KEY_SHIP_VIA_DESC,
			// mse_OrderDetails.getShipviaDescription());
			cv.put(KEY_MANUNUMBER, manf_item);
			// cv.put(KEY_LINENUMBER, mse_OrderDetails.getLineNumber());
			// cv.put(KEY_PICKINGSEQUENCE,
			// mse_OrderDetails.getPickingSequence());

			db.update(TABLE_MSE7, cv, KEY_ITEMNUMBER + "='" + item + "' AND "
					+ KEY_MANUNUMBER + " ='" + manf_item + "'", null);

			Log.i("Success", "Data Updated in table " + TABLE_MSE7);
			result = "success";

		} catch (Exception exe) {
			exe.printStackTrace();
			errCode = "Error 530";
			msg = "updateMse_ManfDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}
	public String updateInt_ManfDetails(Internal_Manf_Number01 mse_Manf) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			String manf_item = mse_Manf.getManuf_manitemno();// etItemNumber();
			String item = mse_Manf.getManuf_itemno();// getComments();
			/*
			 * String sql="UPDATE " + TABLE_MSE2 + " SET " + KEY_COMMENTS + com
			 * + "," + KEY_QUANTITY_SHIPPED + shpQty + " WHERE " +
			 * KEY_ORDER_NUMBER + "= '" + ord + "' AND " + KEY_ITEMNUMBER +
			 * " = '" + item + " ' ";
			 */

			// cv.put(KEY_ORDER_NUMBER, mse_OrderDetails.getOrdNumber());
			// cv.put(KEY_CUSTOMER_NUMBER, mse_OrderDetails.getCustNumber());
			// cv.put(KEY_ITEMNUMBER, mse_OrderDetails.getItemNumber());
			// cv.put(KEY_ITEM_DESCRIPTION,
			// mse_OrderDetails.getItemDescription());
			// cv.put(KEY_UOM, mse_OrderDetails.getUom());
			// cv.put(KEY_QUANTITY_ORDER, mse_OrderDetails.getQtyOrdred());
			// cv.put(KEY_ITEMNUMBER, item);
			// cv.put(KEY_LOCATION, mse_OrderDetails.getLocation());
			// cv.put(KEY_SHIP_VIA_CODE, mse_OrderDetails.getShipViaCode());
			// cv.put(KEY_SHIP_VIA_DESC,
			// mse_OrderDetails.getShipviaDescription());
			cv.put(KEY_MANUNUMBER, manf_item);
			// cv.put(KEY_LINENUMBER, mse_OrderDetails.getLineNumber());
			// cv.put(KEY_PICKINGSEQUENCE,
			// mse_OrderDetails.getPickingSequence());

			db.update(TABLE_INT7, cv, KEY_ITEMNUMBER + "='" + item + "' AND "
					+ KEY_MANUNUMBER + " ='" + manf_item + "'", null);

			Log.i("Success", "Data Updated in table " + TABLE_INT7);
			result = "success";

		} catch (Exception exe) {
			exe.printStackTrace();
			errCode = "Error 530";
			msg = "updateInt_ManfDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}
	public String updateMse_TransDetails(MSE_OrderDetails mse_OrderDetails) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			String ord = mse_OrderDetails.getOrdNumber();
			String item = mse_OrderDetails.getItemNumber();
			String com = mse_OrderDetails.getComments();
			// String item=mse_OrderDetails.getItemNumber();

			// cv.put(KEY_ORDER_NUMBER, mse_OrderDetails.getOrdNumber());
			// cv.put(KEY_CUSTOMER_NUMBER, mse_OrderDetails.getCustNumber());
			// cv.put(KEY_LINENUMBER, mse_OrderDetails.getLineNumber());
			// cv.put(KEY_ITEMNUMBER, mse_OrderDetails.getItemNumber());
			// cv.put(KEY_ITEM_DESCRIPTION,
			// mse_OrderDetails.getItemDescription());
			// cv.put(KEY_UOM, mse_OrderDetails.getUom());
			cv.put(KEY_QUANTITY_ORDER, mse_OrderDetails.getQtyOrdred());
			cv.put(KEY_QUANTITY_SHIPPED, mse_OrderDetails.getQtyShiped());
			// cv.put(KEY_SHIP_VIA, mse_OrderDetails.getShipViaCode());
			cv.put(KEY_COMMENTS, mse_OrderDetails.getComments());

			cv.put(KEY_STATUS, 0);

			/*
			 * Here we are giving cond KEY_SHIPMENT_NUMBER=ORd since suppose we
			 * exported first time and and its not exported then it will add ord
			 * no in the shipment col so we are giving or cond so that next time
			 * if PDA user try to make some changes then it update transaction
			 * table correcttly
			 */

			db.update(TABLE_MSE5, cv, KEY_ORDER_NUMBER + "= '" + ord + "' AND "
					+ KEY_ITEMNUMBER + " = '" + item + "' AND ("
					+ KEY_SHIPMENT_NUMBER + " IS NULL OR "
					+ KEY_SHIPMENT_NUMBER + "='" + ord + "')", null);

			Log.i("Success", "Data Updated in table " + TABLE_MSE5);

			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 533";
			msg = "updateMse_TransDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}
		return result;

	}
	
	public String updateInternal_TransDetails(Internal_OrderDetails internal_OrderDetails) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			String intno = internal_OrderDetails.getIntNumber();
			String item = internal_OrderDetails.getItemNumber();
			/*String com = internal_OrderDetails.getComments();*/
			// String item=mse_OrderDetails.getItemNumber();

			// cv.put(KEY_ORDER_NUMBER, mse_OrderDetails.getOrdNumber());
			// cv.put(KEY_CUSTOMER_NUMBER, mse_OrderDetails.getCustNumber());
			// cv.put(KEY_LINENUMBER, mse_OrderDetails.getLineNumber());
			// cv.put(KEY_ITEMNUMBER, mse_OrderDetails.getItemNumber());
			// cv.put(KEY_ITEM_DESCRIPTION,
			// mse_OrderDetails.getItemDescription());
			// cv.put(KEY_UOM, mse_OrderDetails.getUom());
			cv.put(KEY_QUANTITY_ORDER, internal_OrderDetails.getQtyOrdred());
			cv.put(KEY_QUANTITY_SHIPPED, internal_OrderDetails.getQtyShiped());
			// cv.put(KEY_SHIP_VIA, mse_OrderDetails.getShipViaCode());
			/*cv.put(KEY_COMMENTS, mse_OrderDetails.getComments());*/

			cv.put(KEY_STATUS, 0);

			/*
			 * Here we are giving cond KEY_SHIPMENT_NUMBER=ORd since suppose we
			 * exported first time and and its not exported then it will add ord
			 * no in the shipment col so we are giving or cond so that next time
			 * if PDA user try to make some changes then it update transaction
			 * table correcttly
			 */

			db.update(TABLE_INT5, cv, KEY_INT_NUMBER + "= '" + intno + "' AND "
					+ KEY_ITEMNUMBER + " = '" + item + "' AND ("
					+ KEY_SHIPMENT_NUMBER + " IS NULL OR "
					+ KEY_SHIPMENT_NUMBER + "='" + intno + "')", null);

			Log.i("Success", "Data Updated in table " + TABLE_INT5);

			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 533";
			msg = "updateInternal_TransDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}
		return result;

	}
	

	public String addMse_SerialDetails(MSE_SerialNo mse_Serial) {
		// TODO Auto-generated method stub
		String res = null;
		try {
			ContentValues cv = new ContentValues();

			cv.put(KEY_ORDER_NUMBER, mse_Serial.getOrderNumber());
			cv.put(KEY_LINENUMBER, mse_Serial.getLineNumber());
			cv.put(KEY_ITEMNUMBER, mse_Serial.getItemNumber());
			cv.put(KEY_SERIAL_NUMBER, mse_Serial.getSerialNumber());
			db.insert(TABLE_MSE4, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_MSE4);
			res = "success";
		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 534";
			msg = "addMse_SerialDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			res = "error";
			db.close();
		}

		return res;
	}

	public void deleteFromSerialNo(String i) {
		// TODO Auto-generated method stub
		try {
			db.execSQL("DELETE FROM " + TABLE_MSE4 + " where "
					+ KEY_SERIAL_NUMBER + "=" + i);
		} catch (Exception ex) {
			db.endTransaction();
			errCode = "Error 535";
			msg = "deleteFromSerialNo failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

	}

	public Cursor getShipNo(String OrdNo) {
		// TODO Auto-generated method stub
		Cursor cursor_transdetails = null;
		String sqlquery = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MSE5
				+ " WHERE " + KEY_ORDER_NUMBER + " = '" + OrdNo + "' AND "
				+ KEY_SHIPMENT_NUMBER + " = '" + OrdNo + "'";
		int numRows = (int) DatabaseUtils.longForQuery(db, sqlquery, null);

		if (numRows > 0) {
			String q = "SELECT " + KEY_SHIPMENT_NUMBER + "  FROM "
					+ DatabaseHandler.TABLE_MSE5 + " WHERE " + KEY_ORDER_NUMBER
					+ " = '" + OrdNo + "' AND " + KEY_SHIPMENT_NUMBER + " = '"
					+ OrdNo + "'";
			cursor_transdetails = db.rawQuery(q, null);

			if (cursor_transdetails != null) {
				cursor_transdetails.moveToFirst();
			}
		} else {

			String q = "SELECT MAX(" + KEY_SHIPMENT_NUMBER + ")  FROM "
					+ DatabaseHandler.TABLE_MSE5 + " WHERE " + KEY_ORDER_NUMBER
					+ " = '" + OrdNo + "'"; // + KEY_STATUS + " =1";
			cursor_transdetails = db.rawQuery(q, null);

			if (cursor_transdetails != null) {
				cursor_transdetails.moveToFirst();
			}
		}

		return cursor_transdetails;

	}
	public Cursor getShipNoforII(String OrdNo) {
		// TODO Auto-generated method stub
		Cursor cursor_transdetails = null;
		String sqlquery = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_INT5
				+ " WHERE " + KEY_INT_NUMBER + " = '" + OrdNo + "' AND "
				+ KEY_SHIPMENT_NUMBER + " = '" + OrdNo + "'";
		int numRows = (int) DatabaseUtils.longForQuery(db, sqlquery, null);

		if (numRows > 0) {
			String q = "SELECT " + KEY_SHIPMENT_NUMBER + "  FROM "
					+ DatabaseHandler.TABLE_INT5 + " WHERE " + KEY_INT_NUMBER
					+ " = '" + OrdNo + "' AND " + KEY_SHIPMENT_NUMBER + " = '"
					+ OrdNo + "'";
			cursor_transdetails = db.rawQuery(q, null);

			if (cursor_transdetails != null) {
				cursor_transdetails.moveToFirst();
			}
		} else {

			String q = "SELECT MAX(" + KEY_SHIPMENT_NUMBER + ")  FROM "
					+ DatabaseHandler.TABLE_INT5 + " WHERE " + KEY_INT_NUMBER
					+ " = '" + OrdNo + "'"; // + KEY_STATUS + " =1";
			cursor_transdetails = db.rawQuery(q, null);

			if (cursor_transdetails != null) {
				cursor_transdetails.moveToFirst();
			}
		}

		return cursor_transdetails;

	}
	public boolean checkStatusforII(String OrdNo) {
		boolean flag=false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_INT5 + " WHERE "
				+ KEY_INT_NUMBER + " = '" + OrdNo + "' AND " + KEY_STATUS
				+ " =1" + " AND " + KEY_SHIPMENT_NUMBER +"='ok'";		
		

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);
		
		if (numRows > 0) {
			flag= true;
		}
		else
		{
			flag=false;
		}
		return flag;
		
		
		
	}


	public Cursor getMseTransData(String OrdNo) {
		// TODO Auto-generated method stub
		Cursor cursor_transdetails = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MSE5 + " WHERE "
				+ KEY_ORDER_NUMBER + " = '" + OrdNo + "' AND " + KEY_STATUS
				+ " =0";
		cursor_transdetails = db.rawQuery(q, null);

		if (cursor_transdetails != null) {
			cursor_transdetails.moveToFirst();
		}

		MSE_Trans mse_TansDetails = new MSE_Trans();

		mse_TansDetails.setOrdNumber(cursor_transdetails.getString(0));
		mse_TansDetails.setCustNumber(cursor_transdetails.getString(1));
		mse_TansDetails.setLineNumber(cursor_transdetails.getString(2));
		mse_TansDetails.setItemNumber(cursor_transdetails.getString(3));
		mse_TansDetails.setItemDescription(cursor_transdetails.getString(4));
		mse_TansDetails.setUom(cursor_transdetails.getString(5));
		mse_TansDetails.setQtyOrdered(cursor_transdetails.getInt(6));
		mse_TansDetails.setQtyShipped(cursor_transdetails.getInt(7));
		mse_TansDetails.setShipVia(cursor_transdetails.getString(8));
		mse_TansDetails.setComments(cursor_transdetails.getString(9));
		mse_TansDetails.setShipNumber(cursor_transdetails.getString(10));
		mse_TansDetails.setShipDay(cursor_transdetails.getString(11));
		mse_TansDetails.setShipMonth(cursor_transdetails.getString(12));
		mse_TansDetails.setShipYear(cursor_transdetails.getString(13));
		mse_TansDetails.setStatus(cursor_transdetails.getInt(14));

		return cursor_transdetails;

	}
	public Cursor getInternalTransData(String intNo) {
		// TODO Auto-generated method stub
		Cursor cursor_transdetails = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_INT5 + " WHERE "
				+ KEY_INT_NUMBER + " = '" + intNo + "' AND " + KEY_STATUS
				+ " =0";
		cursor_transdetails = db.rawQuery(q, null);

		if (cursor_transdetails != null) {
			cursor_transdetails.moveToFirst();
		}

		Internal_Trans internal_TansDetails = new Internal_Trans();

		internal_TansDetails.setIntNumber(cursor_transdetails.getString(0));
		internal_TansDetails.setCostName(cursor_transdetails.getString(1));
		internal_TansDetails.setLineNumber(cursor_transdetails.getString(2));
		internal_TansDetails.setItemNumber(cursor_transdetails.getString(3));
		internal_TansDetails.setItemDescription(cursor_transdetails.getString(4));
		internal_TansDetails.setUom(cursor_transdetails.getString(5));
		internal_TansDetails.setQtyOrdered(cursor_transdetails.getInt(6));
		internal_TansDetails.setQtyShipped(cursor_transdetails.getInt(7));
		internal_TansDetails.setShipNumber(cursor_transdetails.getString(8));
		internal_TansDetails.setShipDay(cursor_transdetails.getString(9));
		internal_TansDetails.setShipMonth(cursor_transdetails.getString(10));
		internal_TansDetails.setShipYear(cursor_transdetails.getString(11));
		internal_TansDetails.setStatus(cursor_transdetails.getInt(12));

		return cursor_transdetails;

	}
	public List<MSE_Trans> getDistinctOrderMseTrans() {
		Cursor cursor_transdetails = null;
		List<MSE_Trans> ordlist = new ArrayList<MSE_Trans>();

		String q = "SELECT DISTINCT " + KEY_ORDER_NUMBER + " , "
				+ KEY_CUSTOMER_NUMBER + " FROM " + DatabaseHandler.TABLE_MSE5
				+ " WHERE " + KEY_STATUS + "=0";
		cursor_transdetails = db.rawQuery(q, null);
		while (cursor_transdetails.moveToNext()) {
			MSE_Trans mse_TansDetails = new MSE_Trans();

			mse_TansDetails.setOrdNumber(cursor_transdetails.getString(0));
			mse_TansDetails.setCustNumber(cursor_transdetails.getString(1));

			ordlist.add(mse_TansDetails);
		}
		cursor_transdetails.close();
		return ordlist;

	}
	public List<Internal_Trans> getDistinctOrderInternalTrans() {
		Cursor cursor_transdetails = null;
		List<Internal_Trans> ordlist = new ArrayList<Internal_Trans>();

		String q = "SELECT DISTINCT " + KEY_INT_NUMBER + " , "
				+ KEY_COST_CENTER + " FROM " + DatabaseHandler.TABLE_INT5
				+ " WHERE " + KEY_STATUS + "=0";
		cursor_transdetails = db.rawQuery(q, null);
		while (cursor_transdetails.moveToNext()) {
			Internal_Trans internal_TansDetails = new Internal_Trans();

			internal_TansDetails.setIntNumber(cursor_transdetails.getString(0));
			internal_TansDetails.setCostName(cursor_transdetails.getString(1));

			ordlist.add(internal_TansDetails);
		}
		cursor_transdetails.close();
		return ordlist;

	}

	public String Update_MSETrans(String Shipno, String expOrd) {
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			String current = dateFormat.format(date);
			String arr[] = current.split("/");

			String year = arr[0];
			String month = arr[1];
			String day = arr[2];
			System.out.println("Year" + year);
			System.out.println("Month" + month);
			System.out.println("Day" + day);

			cv.put(KEY_SHIPMENT_NUMBER, Shipno);
			cv.put(KEY_SHIP_YEAR, year);
			cv.put(KEY_SHIP_MONTH, month);
			cv.put(KEY_SHIP_DAY, day);
			cv.put(KEY_STATUS, 1);
			db.update(TABLE_MSE5, cv, KEY_ORDER_NUMBER + "= '" + expOrd
					+ "' AND " + KEY_STATUS + " =0 ", null);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 537";
			msg = "In Update...Update_MSETrans failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}
	public String Update_InternalTrans(String Shipno, String expOrd) {
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			String current = dateFormat.format(date);
			String arr[] = current.split("/");

			String year = arr[0];
			String month = arr[1];
			String day = arr[2];
			System.out.println("Year" + year);
			System.out.println("Month" + month);
			System.out.println("Day" + day);

			cv.put(KEY_SHIPMENT_NUMBER, Shipno);
			cv.put(KEY_SHIP_YEAR, year);
			cv.put(KEY_SHIP_MONTH, month);
			cv.put(KEY_SHIP_DAY, day);
			cv.put(KEY_STATUS, 1);
			db.update(TABLE_INT5, cv, KEY_INT_NUMBER + "= '" + expOrd
					+ "' AND " + KEY_STATUS + " =0 ", null);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 537";
			msg = "In Update...Update_InternalTrans failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}
	public String Update_MSETrans_Fail(String Shipno, String expOrd) {
		String result = null;
		try {

			ContentValues cv = new ContentValues();
			/*
			 * DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); Date
			 * date = new Date(); String current = dateFormat.format(date);
			 * String arr[] = current.split("/");
			 * 
			 * String year = arr[0]; String month = arr[1]; String day = arr[2];
			 * System.out.println("Year" + year); System.out.println("Month" +
			 * month); System.out.println("Day" + day);
			 */

			cv.put(KEY_SHIPMENT_NUMBER, Shipno);
			/*
			 * cv.put(KEY_SHIP_YEAR, year); cv.put(KEY_SHIP_MONTH, month);
			 * cv.put(KEY_SHIP_DAY, day); cv.put(KEY_STATUS, 1);
			 */
			db.update(TABLE_MSE5, cv, KEY_ORDER_NUMBER + "= '" + expOrd
					+ "' AND " + KEY_STATUS + " =0 ", null);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 537";
			msg = "In Update...Update_MSETrans failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}
	public String Update_InternalTrans_Fail(String Shipno, String expOrd) {
		String result = null;
		try {

			ContentValues cv = new ContentValues();
			/*
			 * DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); Date
			 * date = new Date(); String current = dateFormat.format(date);
			 * String arr[] = current.split("/");
			 * 
			 * String year = arr[0]; String month = arr[1]; String day = arr[2];
			 * System.out.println("Year" + year); System.out.println("Month" +
			 * month); System.out.println("Day" + day);
			 */

			cv.put(KEY_SHIPMENT_NUMBER, Shipno);
			/*
			 * cv.put(KEY_SHIP_YEAR, year); cv.put(KEY_SHIP_MONTH, month);
			 * cv.put(KEY_SHIP_DAY, day); cv.put(KEY_STATUS, 1);
			 */
			db.update(TABLE_INT5, cv, KEY_INT_NUMBER + "= '" + expOrd
					+ "' AND " + KEY_STATUS + " =0 ", null);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 537";
			msg = "In Update...Update_InternalTrans_Fail failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}


	public String Update_MLTTrans_Fail(String trfno, String docno) {
		String result = null;
		try {

			ContentValues cv = new ContentValues();
			/*
			 * DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); Date
			 * date = new Date(); String current = dateFormat.format(date);
			 * String arr[] = current.split("/");
			 * 
			 * String year = arr[0]; String month = arr[1]; String day = arr[2];
			 * System.out.println("Year" + year); System.out.println("Month" +
			 * month); System.out.println("Day" + day);
			 */

			cv.put(KEY_TRFNUMBER, trfno);
			/*
			 * cv.put(KEY_SHIP_YEAR, year); cv.put(KEY_SHIP_MONTH, month);
			 * cv.put(KEY_SHIP_DAY, day); cv.put(KEY_STATUS, 1);
			 */
			db.update(TABLE_MLT2, cv, KEY_DOCNUMBER + "= '" + docno + "' AND "
					+ KEY_STATUS + " =0 ", null);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 537";
			msg = "In Update...Update_MLTTrans_Fail failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public String Update_MPRTrans_Fail(String Recno, String expOrd) {
		String result = null;
		try {

			ContentValues cv = new ContentValues();
			/*
			 * DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); Date
			 * date = new Date(); String current = dateFormat.format(date);
			 * String arr[] = current.split("/");
			 * 
			 * String year = arr[0]; String month = arr[1]; String day = arr[2];
			 * System.out.println("Year" + year); System.out.println("Month" +
			 * month); System.out.println("Day" + day);
			 */

			cv.put(KEY_RECEIPT_NUMBER, Recno);
			/*
			 * cv.put(KEY_SHIP_YEAR, year); cv.put(KEY_SHIP_MONTH, month);
			 * cv.put(KEY_SHIP_DAY, day); cv.put(KEY_STATUS, 1);
			 */
			db.update(TABLE_MPR6, cv, KEY_PONUMBER + "= '" + expOrd + "' AND "
					+ KEY_STATUS + " =0 ", null);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 537";
			msg = "In Update...Update_MSETrans failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public boolean checkMSE() {
		boolean flag = false;

		ContentValues cv = new ContentValues();
		String query = "SELECT COUNT(*) FROM " + TABLE_MSE5;
		int numRows = (int) DatabaseUtils.longForQuery(db, query, null);

		if (numRows > 0) {
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}
	public boolean checkInternal() {
		boolean flag = false;

		ContentValues cv = new ContentValues();
		String query = "SELECT COUNT(*) FROM " + TABLE_INT5;
		int numRows = (int) DatabaseUtils.longForQuery(db, query, null);

		if (numRows > 0) {
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}


	public String checkShipInTrans1(String order, String itno) {
		String shpAvail = "";
		ContentValues cv = new ContentValues();
		/*
		 * String query = "SELECT  COUNT(*) FROM " + TABLE_MPR6 + " WHERE " +
		 * KEY_PONUMBER + " ='" + order + "' AND " + KEY_RECEIPT_NUMBER +
		 * " IS NULL ";
		 */

		String query = "SELECT COUNT(*) FROM " + TABLE_MSE5 + " WHERE "
				+ KEY_ORDER_NUMBER + " ='" + order + "' AND " + KEY_ITEMNUMBER
				+ " ='" + itno + "' AND " + KEY_SHIPMENT_NUMBER
				+ " IS NOT NULL AND " + KEY_SHIP_DAY + " IS NOT NULL AND "
				+ KEY_SHIP_MONTH + " IS NOT NULL AND " + KEY_SHIP_YEAR
				+ " IS NOT NULL";

		Cursor cur = db.rawQuery(query, null);
		/*
		 * int i = cur.getCount(); System.out.println("i="+i);
		 * 
		 * if (cur.getCount() > 1) { String query=""SELECT COUNT(*) FROM "+
		 * TABLE_MPR6 + " WHERE " + KEY_PONUMBER + " ='" + order + "' AND " +
		 * KEY_ITEMNUMBER + " ='" + itno + "' AND " + KEY_RECEIPT_NUMBER +
		 * " IS NULL";
		 * 
		 * System.out.println("Yes"); shpAvail = "Yes"; } else {
		 * System.out.println("No"); shpAvail = "No"; }
		 */

		System.out.println("Check query" + query);
		int numRows = (int) DatabaseUtils.longForQuery(db, query, null);
		System.out.println("Check receip" + numRows);
		if (numRows > 0) {
			String locquery = "SELECT COUNT(*) FROM " + TABLE_MSE5 + " WHERE "
					+ KEY_ORDER_NUMBER + " ='" + order + "' AND "
					+ KEY_ITEMNUMBER + " ='" + itno + "' AND "
					+ KEY_SHIPMENT_NUMBER + " IS NULL";
			// Cursor cursor = db.rawQuery(locquery, null);
			int locnumRows = (int) DatabaseUtils.longForQuery(db, locquery,
					null);
			if (locnumRows == 0) {
				shpAvail = "Yes";// add
			} else {

				shpAvail = "No";// update
			}

		} else {
			String locquery1 = "SELECT COUNT(*) FROM " + TABLE_MSE5 + " WHERE "
					+ KEY_ORDER_NUMBER + " ='" + order + "' AND "
					+ KEY_ITEMNUMBER + " ='" + itno + "'";
			int locnumRows = (int) DatabaseUtils.longForQuery(db, locquery1,
					null);
			if (locnumRows == 0) {
				shpAvail = "Yes";// add
			} else {

				shpAvail = "No";// update
			}

		}
		cur.close();
		return shpAvail;
	}
	public List<String> getOrderInTrans()
	{

		Cursor cursor_transdetails = null;
		List<String> ordlst = null;

		
			ordlst = new ArrayList<String>();

			
				String q = "SELECT distinct " + KEY_INT_NUMBER + " FROM " + DatabaseHandler.TABLE_INT5;
					
				cursor_transdetails = db.rawQuery(q, null);

				try {
					while (cursor_transdetails.moveToNext()) {
						ordlst.add(cursor_transdetails.getString(0));
					}
				} catch (Exception e) {
					e.printStackTrace();
					errCode = "Error 5388";
					msg = "getMseTrans failed.";
					errMsg = errCode + " : " + msg;
					LogfileCreator.appendLog(errMsg);
					db.close();

				}
			
		
		cursor_transdetails.close();

		return ordlst;
		
	}
	public String checkShipInTrans1_internal(String intno, String itno) {
		String shpAvail = "";
		ContentValues cv = new ContentValues();
		

		String query = "SELECT COUNT(*) FROM " + TABLE_INT5 + " WHERE "
				+ KEY_INT_NUMBER + " ='" + intno + "' AND " + KEY_ITEMNUMBER
				+ " ='" + itno + "' AND " + KEY_SHIPMENT_NUMBER
				+ " IS NOT NULL AND " + KEY_SHIP_DAY + " IS NOT NULL AND "
				+ KEY_SHIP_MONTH + " IS NOT NULL AND " + KEY_SHIP_YEAR
				+ " IS NOT NULL";

		Cursor cur = db.rawQuery(query, null);
		/*
		 * int i = cur.getCount(); System.out.println("i="+i);
		 * 
		 * if (cur.getCount() > 1) { String query=""SELECT COUNT(*) FROM "+
		 * TABLE_MPR6 + " WHERE " + KEY_PONUMBER + " ='" + order + "' AND " +
		 * KEY_ITEMNUMBER + " ='" + itno + "' AND " + KEY_RECEIPT_NUMBER +
		 * " IS NULL";
		 * 
		 * System.out.println("Yes"); shpAvail = "Yes"; } else {
		 * System.out.println("No"); shpAvail = "No"; }
		 */

		System.out.println("Check query" + query);
		int numRows = (int) DatabaseUtils.longForQuery(db, query, null);
		System.out.println("Check receip" + numRows);
		if (numRows > 0) {
			String locquery = "SELECT COUNT(*) FROM " + TABLE_INT5 + " WHERE "
					+ KEY_INT_NUMBER + " ='" + intno + "' AND "
					+ KEY_ITEMNUMBER + " ='" + itno + "' AND "
					+ KEY_SHIPMENT_NUMBER + " IS NULL";
			// Cursor cursor = db.rawQuery(locquery, null);
			int locnumRows = (int) DatabaseUtils.longForQuery(db, locquery,
					null);
			if (locnumRows == 0) {
				shpAvail = "Yes";// add
			} else {

				shpAvail = "No";// update
			}

		} else {
			String locquery1 = "SELECT COUNT(*) FROM " + TABLE_INT5 + " WHERE "
					+ KEY_INT_NUMBER + " ='" + intno + "' AND "
					+ KEY_ITEMNUMBER + " ='" + itno + "'";
			int locnumRows = (int) DatabaseUtils.longForQuery(db, locquery1,
					null);
			if (locnumRows == 0) {
				shpAvail = "Yes";// add
			} else {

				shpAvail = "No";// update
			}

		}
		cur.close();
		return shpAvail;
	}

	public List<MSE_Trans> getMseTrans(List<String> rec_ord) {
		Cursor cursor_transdetails = null;
		List<MSE_Trans> ordlst = null;

		if (rec_ord.size() > 0) {
			ordlst = new ArrayList<MSE_Trans>();

			for (int i = 0; i < rec_ord.size(); i++) {
				String q = "SELECT * FROM " + DatabaseHandler.TABLE_MSE5
						+ " WHERE " + KEY_ORDER_NUMBER + " = '"
						+ rec_ord.get(i) + " ' AND " + KEY_STATUS + " =0";
				cursor_transdetails = db.rawQuery(q, null);

				try {
					while (cursor_transdetails.moveToNext()) {
						MSE_Trans mse_TansDetails = new MSE_Trans();

						mse_TansDetails.setOrdNumber(cursor_transdetails
								.getString(0));
						mse_TansDetails.setCustNumber(cursor_transdetails
								.getString(1));
						mse_TansDetails.setLineNumber(cursor_transdetails
								.getString(2));
						mse_TansDetails.setItemNumber(cursor_transdetails
								.getString(3));
						mse_TansDetails.setItemDescription(cursor_transdetails
								.getString(4));
						mse_TansDetails
								.setUom(cursor_transdetails.getString(5));
						mse_TansDetails.setQtyOrdered(cursor_transdetails
								.getInt(6));
						mse_TansDetails.setQtyShipped(cursor_transdetails
								.getInt(7));
						mse_TansDetails.setShipVia(cursor_transdetails
								.getString(8));
						mse_TansDetails.setComments(cursor_transdetails
								.getString(9));
						mse_TansDetails.setShipNumber(cursor_transdetails
								.getString(10));
						mse_TansDetails.setShipDay(cursor_transdetails
								.getString(11));
						mse_TansDetails.setShipMonth(cursor_transdetails
								.getString(12));
						mse_TansDetails.setShipYear(cursor_transdetails
								.getString(13));
						mse_TansDetails.setStatus(cursor_transdetails
								.getInt(14));

						ordlst.add(mse_TansDetails);

					}
				} catch (Exception e) {
					e.printStackTrace();
					errCode = "Error 538";
					msg = "getMseTrans failed.";
					errMsg = errCode + " : " + msg;
					LogfileCreator.appendLog(errMsg);
					db.close();

				}
			}
		}
		cursor_transdetails.close();

		return ordlst;
	}

	public List<String> getMseTrans_(List<String> rec_ord) {
		Cursor cursor_transdetails = null;
		List<String> ordlst = null;
		System.out.println("Orders received--->" + rec_ord);
		if (rec_ord.size() > 0) {
			ordlst = new ArrayList<String>();

			for (int i = 0; i < rec_ord.size(); i++) {
				String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MSE5
						+ " WHERE " + KEY_ORDER_NUMBER + " ='" + rec_ord.get(i)
						+ "' AND " + KEY_STATUS + "=0 ";
				cursor_transdetails = db.rawQuery(q, null);

				try {
					int numRows = (int) DatabaseUtils.longForQuery(db, q, null);
					System.out.println("No of Rows selected using cond 1-->"
							+ numRows);
					// cursor_transdetails.moveToFirst();

					// /////////////IF EXIST IN MSE TRANS///////////////////
					if (numRows > 0) {
						while (cursor_transdetails.moveToNext()) {
							System.out.println("Order Added using cond 1-->"
									+ rec_ord.get(i));
							ordlst.add(rec_ord.get(i));

						}
					}

					// /////////////IF DOESN'T EXIST IN MSE
					// TRANS///////////////////
					else {
						String query = "SELECT COUNT(*) FROM "
								+ DatabaseHandler.TABLE_MSE5 + " WHERE "
								+ KEY_ORDER_NUMBER + "= '" + rec_ord.get(i)
								+ "'";
						Cursor cur = db.rawQuery(query, null);
						// cur.moveToFirst();
						int cnt = (int) DatabaseUtils.longForQuery(db, query,
								null);
						System.out.println("No of Rows selected using cond2-->"
								+ cnt);

						if (cnt < 1) {
							ordlst.add(rec_ord.get(i));

							System.out.println("Order Added using cond 2-->"
									+ rec_ord.get(i));

						}
						cur.close();

					}
				} catch (Exception e) {
					e.printStackTrace();
					errCode = "Error 550";
					msg = "getMseTrans failed.";
					errMsg = errCode + " : " + msg;
					LogfileCreator.appendLog(errMsg);
					db.close();

				}

			}
		}
		cursor_transdetails.close();

		return ordlst;
	}

	public List<MSE_Trans> getMseTrans() {
		Cursor cursor_transdetails = null;
		List<MSE_Trans> ordlst = new ArrayList<MSE_Trans>();

		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MSE5;
		cursor_transdetails = db.rawQuery(q, null);

		try {
			while (cursor_transdetails.moveToNext()) {
				MSE_Trans mse_TansDetails = new MSE_Trans();

				mse_TansDetails.setOrdNumber(cursor_transdetails.getString(0));
				mse_TansDetails.setCustNumber(cursor_transdetails.getString(1));
				mse_TansDetails.setLineNumber(cursor_transdetails.getString(2));
				mse_TansDetails.setItemNumber(cursor_transdetails.getString(3));
				mse_TansDetails.setItemDescription(cursor_transdetails
						.getString(4));
				mse_TansDetails.setUom(cursor_transdetails.getString(5));
				mse_TansDetails.setQtyOrdered(cursor_transdetails.getInt(6));
				mse_TansDetails.setQtyShipped(cursor_transdetails.getInt(7));
				mse_TansDetails.setShipVia(cursor_transdetails.getString(8));
				mse_TansDetails.setComments(cursor_transdetails.getString(9));
				mse_TansDetails
						.setShipNumber(cursor_transdetails.getString(10));
				mse_TansDetails.setShipDay(cursor_transdetails.getString(11));
				mse_TansDetails.setShipMonth(cursor_transdetails.getString(12));
				mse_TansDetails.setShipYear(cursor_transdetails.getString(13));
				mse_TansDetails.setStatus(cursor_transdetails.getInt(14));

				ordlst.add(mse_TansDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 539";
			msg = "getMseTrans failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();

		}

		cursor_transdetails.close();

		return ordlst;
	}

	public Cursor getSerial(String OrderNo) {
		Cursor cursor_serialdetails = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MSE4 + " WHERE "
				+ KEY_ORDER_NUMBER + " = '" + OrderNo + " '";
		cursor_serialdetails = db.rawQuery(q, null);
		int i = cursor_serialdetails.getCount();
		if (cursor_serialdetails.getCount() > 0) {
			if (cursor_serialdetails != null) {
				cursor_serialdetails.moveToFirst();
			}

			MSE_SerialNo mse_SerialNo = new MSE_SerialNo();

			mse_SerialNo.setOrderNumber(cursor_serialdetails.getString(0));
			mse_SerialNo.setLineNumber(cursor_serialdetails.getString(1));
			mse_SerialNo.setItemNumber(cursor_serialdetails.getString(2));
			mse_SerialNo.setSerialNumber(cursor_serialdetails.getString(3));

		}

		return cursor_serialdetails;
	}
	public Cursor getIntSerial(String intNo) {
		Cursor cursor_serialdetails = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_INT4 + " WHERE "
				+ KEY_INT_NUMBER + " = '" + intNo + " '";
		cursor_serialdetails = db.rawQuery(q, null);
		int i = cursor_serialdetails.getCount();
		if (cursor_serialdetails.getCount() > 0) {
			if (cursor_serialdetails != null) {
				cursor_serialdetails.moveToFirst();
			}

			Internal_SerialNo mse_SerialNo = new Internal_SerialNo();

			mse_SerialNo.setInternalNumber(cursor_serialdetails.getString(0));
			mse_SerialNo.setLineNumber(cursor_serialdetails.getString(1));
			mse_SerialNo.setItemNumber(cursor_serialdetails.getString(2));
			mse_SerialNo.setSerialNumber(cursor_serialdetails.getString(3));

		}

		return cursor_serialdetails;
	}


	public Manf_Number01 getItemFromManfNum(String manfCode) {

		Manf_Number01 manf = null;
		Cursor cursor = db.rawQuery("SELECT " + KEY_ITEMNUMBER + " FROM "
				+ TABLE_MSE7 + " WHERE " + KEY_MANUNUMBER + "='" + manfCode
				+ "' ", null);

		try {
			while (cursor.moveToNext()) {
				manf = new Manf_Number01();

				manf.setManuf_itemno(cursor.getString(0));

			}

		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 540";
			msg = "getItemFromManfNum failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);

			db.close();
		}

		cursor.close();

		return manf;
	}
	public Internal_Manf_Number01 getItemFromManfNumforInternal(String manfCode) {

		Internal_Manf_Number01 manf = null;
		Cursor cursor = db.rawQuery("SELECT " + KEY_ITEMNUMBER + " FROM "
				+ TABLE_INT7 + " WHERE " + KEY_MANUNUMBER + "='" + manfCode
				+ "' ", null);

		try {
			while (cursor.moveToNext()) {
				manf = new Internal_Manf_Number01();

				manf.setManuf_itemno(cursor.getString(0));

			}

		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 540";
			msg = "getItemFromManfNumforInternal failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);

			db.close();
		}

		cursor.close();

		return manf;
	}

	public Upc_Number getItemFromUpc(String UpcCode) {

		Upc_Number upc = null;
		Cursor cursor = db.rawQuery("select ItemNumber from " + TABLE_MSE6
				+ " where " + KEY_UPC_NUMBER + " = '"

				+ UpcCode + "' ", null);

		try {
			while (cursor.moveToNext()) {
				upc = new Upc_Number();

				upc.setItemno(cursor.getString(0));
				/*
				 * manf.setManuf_itemno(cursor.getString(1));
				 * manf.setManuf_uom(cursor.getString(2));
				 * manf.setCompany_code(cursor.getString(3));
				 */
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 541";
			msg = "getItemFromUpc failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return upc;
	}
	public Internal_Upc_Number getItemFromUpc_internal(String UpcCode) {

		Internal_Upc_Number upc = null;
		Cursor cursor = db.rawQuery("select ItemNumber from " + TABLE_INT6
				+ " where " + KEY_UPC_NUMBER + " = '"

				+ UpcCode + "' ", null);

		try {
			while (cursor.moveToNext()) {
				upc = new Internal_Upc_Number();

				upc.setItemno(cursor.getString(0));
				/*
				 * manf.setManuf_itemno(cursor.getString(1));
				 * manf.setManuf_uom(cursor.getString(2));
				 * manf.setCompany_code(cursor.getString(3));
				 */
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 541";
			msg = "getItemFromUpc_internal failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return upc;
	}


	public void addTempOrd(List<String> ordList) {

		// TODO Auto-generated method stub

		try {
			ContentValues cv = new ContentValues();
			for (int i = 0; i < ordList.size(); i++) {
				cv.put(KEY_ORDER_NUMBER, ordList.get(i));
				cv.put(KEY_STATUS, 0);
				db.insert(TABLE_MSE8, null, cv);
			}
			Log.i("Success", "Data Inserted in table " + TABLE_MSE8);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 522379";
			msg = "addTempOrd failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);

		}

	}
	public void addTempInt(List<String> intList) {

		// TODO Auto-generated method stub

		try {
			ContentValues cv = new ContentValues();
			for (int i = 0; i < intList.size(); i++) {
				cv.put(KEY_INT_NUMBER, intList.get(i));
				cv.put(KEY_STATUS, 0);
				db.insert(TABLE_INT8, null, cv);
			}
			Log.i("Success", "Data Inserted in table " + TABLE_INT8);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 522379";
			msg = "addTempInt failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);

		}

	}

	public String UpdateTempOrd(String Ordno) {
		String result = null;
		try {

			ContentValues cv = new ContentValues();
			/*
			 * DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); Date
			 * date = new Date(); String current = dateFormat.format(date);
			 * String arr[] = current.split("/");
			 * 
			 * String year = arr[0]; String month = arr[1]; String day = arr[2];
			 * System.out.println("Year" + year); System.out.println("Month" +
			 * month); System.out.println("Day" + day);
			 */

			cv.put(KEY_STATUS, 1);
			/*
			 * cv.put(KEY_SHIP_YEAR, year); cv.put(KEY_SHIP_MONTH, month);
			 * cv.put(KEY_SHIP_DAY, day); cv.put(KEY_STATUS, 1);
			 */
			db.update(TABLE_MSE8, cv, KEY_ORDER_NUMBER + "= '" + Ordno + "'",
					null);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 537";
			msg = "In Update...Update_MSETrans failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}
	public String UpdateTempInt(String intno) {
		String result = null;
		try {

			ContentValues cv = new ContentValues();
			/*
			 * DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); Date
			 * date = new Date(); String current = dateFormat.format(date);
			 * String arr[] = current.split("/");
			 * 
			 * String year = arr[0]; String month = arr[1]; String day = arr[2];
			 * System.out.println("Year" + year); System.out.println("Month" +
			 * month); System.out.println("Day" + day);
			 */

			cv.put(KEY_STATUS, 1);
			/*
			 * cv.put(KEY_SHIP_YEAR, year); cv.put(KEY_SHIP_MONTH, month);
			 * cv.put(KEY_SHIP_DAY, day); cv.put(KEY_STATUS, 1);
			 */
			db.update(TABLE_INT8, cv, KEY_INT_NUMBER + "= '" + intno + "'",
					null);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 537";
			msg = "In UpdateTempInt failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}
	public List<String> getTempExportedOrd() {

		Cursor cursor = null;
		List<String> ordlst = null;

		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MSE8 + " WHERE "
				+ KEY_STATUS + "='1'";

		cursor = db.rawQuery(q, null);

		try {
			if (cursor.getCount() > 0) {
				ordlst = new ArrayList<String>();
				while (cursor.moveToNext()) {

					ordlst.add(cursor.getString(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5200";
			msg = "getTempOrd failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return ordlst;

	}
	public List<String> getTempExportedInternal() {

		Cursor cursor = null;
		List<String> ordlst = null;

		String q = "SELECT * FROM " + DatabaseHandler.TABLE_INT8 + " WHERE "
				+ KEY_STATUS + "='1'";

		cursor = db.rawQuery(q, null);

		try {
			if (cursor.getCount() > 0) {
				ordlst = new ArrayList<String>();
				while (cursor.moveToNext()) {

					ordlst.add(cursor.getString(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5200";
			msg = "getTempExportedInternal failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return ordlst;

	}
	public List<String> getTempOrd() {

		Cursor cursor = null;
		List<String> ordlst = null;

		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MSE8 + " WHERE "
				+ KEY_STATUS + "='0'";

		cursor = db.rawQuery(q, null);

		try {
			if (cursor.getCount() > 0) {
				ordlst = new ArrayList<String>();
				while (cursor.moveToNext()) {

					ordlst.add(cursor.getString(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5200";
			msg = "getTempOrd failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return ordlst;

	}
	public List<String> getTempIntNum() {

		Cursor cursor = null;
		List<String> ordlst = null;

		String q = "SELECT * FROM " + DatabaseHandler.TABLE_INT8 + " WHERE "
				+ KEY_STATUS + "='0'";

		cursor = db.rawQuery(q, null);

		try {
			if (cursor.getCount() > 0) {
				ordlst = new ArrayList<String>();
				while (cursor.moveToNext()) {

					ordlst.add(cursor.getString(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5200";
			msg = "getTempIntNum failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return ordlst;

	}

	public void deleteTempOrd() {

		try {

			db.execSQL("DELETE FROM " + TABLE_MSE8);
		} catch (Exception e) {

			Log.e("ERROR", e.getLocalizedMessage());
		}

	}
	public void deleteTempIntno() {

		try {

			db.execSQL("DELETE FROM " + TABLE_INT8);
		} catch (Exception e) {

			Log.e("ERROR", e.getLocalizedMessage());
		}

	}

	public void deleteTempOrd(String order) {

		try {

			db.execSQL("DELETE FROM " + TABLE_MSE8 + " where "
					+ KEY_ORDER_NUMBER + " ='" + order + "'");
		} catch (Exception e) {

			Log.e("ERROR", e.getLocalizedMessage());
		}

	}
	public void deleteTempIntno(String intno) {

		try {

			db.execSQL("DELETE FROM " + TABLE_INT8 + " where "
					+ KEY_INT_NUMBER + " ='" + intno + "'");
		} catch (Exception e) {

			Log.e("ERROR", e.getLocalizedMessage());
		}

	}


	public void deleteTempOrd(ArrayList<String> ordList) {
		try {
			for (int i = 0; i < ordList.size(); i++) {
				db.execSQL("DELETE FROM " + TABLE_MSE8 + " where "
						+ KEY_ORDER_NUMBER + " ='" + ordList.get(i) + "'");
			}
		} catch (Exception e) {

			Log.e("ERROR", e.getLocalizedMessage());
		}

	}

	// //////////////////////////////////////////////////////
	// MPR
	// ///////////////////////////////////////////////

	public Manf_Number01_mpr getItemFromManfNum_mpr(String manfCode) {

		Manf_Number01_mpr manf = null;
		Cursor cursor = db.rawQuery("select ItemNumber from " + TABLE_MPR3
				+ " where " + KEY_MANUNUMBER + " = '" + manfCode + "' ", null);

		try {
			while (cursor.moveToNext()) {
				manf = new Manf_Number01_mpr();

				manf.setItemno(cursor.getString(0));
				/*
				 * manf.setManuf_itemno(cursor.getString(1));
				 * manf.setManuf_uom(cursor.getString(2));
				 * manf.setCompany_code(cursor.getString(3));
				 */
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 540";
			msg = "getItemFromManfNum_mpr failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);

			db.close();
		}

		cursor.close();

		return manf;
	}

	public MPR_Upc getItemFromUpc_mpr(String UpcCode) {

		MPR_Upc upc = null;
		Cursor cursor = db.rawQuery("select ItemNumber from " + TABLE_MPR7
				+ " where " + KEY_BAR_CODE + " = '"

				+ UpcCode + "' ", null);

		try {
			while (cursor.moveToNext()) {
				upc = new MPR_Upc();

				upc.setItem_number(cursor.getString(0));
				/*
				 * manf.setManuf_itemno(cursor.getString(1));
				 * manf.setManuf_uom(cursor.getString(2));
				 * manf.setCompany_code(cursor.getString(3));
				 */
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5413";
			msg = "getItemFromUpc_mpr failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return upc;
	}

	public boolean checkMpr_ManfDetails(String item, String Manf) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MPR3
				+ " WHERE " + DatabaseHandler.KEY_MANUNUMBER + " = '" + Manf
				+ "' AND " + KEY_ITEMNUMBER + "= '" + item + "'";

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}

	public String updateMpr_ManfDetails(Manf_Number01_mpr mpr_Manf) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			String manf_item = mpr_Manf.getMn();// etItemNumber();
			String item = mpr_Manf.getItemno();// getComments();
			/*
			 * String sql="UPDATE " + TABLE_mpr2 + " SET " + KEY_COMMENTS + com
			 * + "," + KEY_QUANTITY_SHIPPED + shpQty + " WHERE " +
			 * KEY_ORDER_NUMBER + "= '" + ord + "' AND " + KEY_ITEMNUMBER +
			 * " = '" + item + " ' ";
			 */

			// cv.put(KEY_ORDER_NUMBER, mpr_OrderDetails.getOrdNumber());
			// cv.put(KEY_CUSTOMER_NUMBER, mpr_OrderDetails.getCustNumber());
			// cv.put(KEY_ITEMNUMBER, mpr_OrderDetails.getItemNumber());
			// cv.put(KEY_ITEM_DESCRIPTION,
			// mpr_OrderDetails.getItemDescription());
			// cv.put(KEY_UOM, mpr_OrderDetails.getUom());
			// cv.put(KEY_QUANTITY_ORDER, mpr_OrderDetails.getQtyOrdred());
			// cv.put(KEY_ITEMNUMBER, item);
			// cv.put(KEY_LOCATION, mpr_OrderDetails.getLocation());
			// cv.put(KEY_SHIP_VIA_CODE, mpr_OrderDetails.getShipViaCode());
			// cv.put(KEY_SHIP_VIA_DESC,
			// mpr_OrderDetails.getShipviaDescription());
			cv.put(KEY_MANUNUMBER, manf_item);
			// cv.put(KEY_LINENUMBER, mpr_OrderDetails.getLineNumber());
			// cv.put(KEY_PICKINGSEQUENCE,
			// mpr_OrderDetails.getPickingSequence());

			db.update(TABLE_MPR3, cv, KEY_ITEMNUMBER + "='" + item + "' AND "
					+ KEY_MANUNUMBER + " ='" + manf_item + "'", null);

			Log.i("Success", "Data Updated in table " + TABLE_MPR3);
			result = "success";

		} catch (Exception exe) {
			exe.printStackTrace();
			errCode = "Error 5370";
			msg = "updateMpr_ManfDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public String addMpr_ManfDetails(Manf_Number01_mpr mpr_Manf) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			ContentValues cv = new ContentValues();

			cv.put(KEY_ITEMNUMBER, mpr_Manf.getItemno());
			cv.put(KEY_MANUNUMBER, mpr_Manf.getMn());

			db.insert(TABLE_MPR3, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_MPR3);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 5279";
			msg = "addMpr_ManfDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";

		}

		return result;
	}

	public boolean checkMpr_OrderDetails(String ordNo, String item) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MPR4
				+ " WHERE " + DatabaseHandler.KEY_PONUMBER + " = '" + ordNo
				+ "'" + " and " + KEY_ITEMNUMBER + " = '" + item + "' ";

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}

	public String updateMpr_OrderDetails(MPR_OrderDetails mpr_OrderDetails,
			String flag) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();
			Integer ordQty = mpr_OrderDetails.getOrdered_qty();
			String ord = mpr_OrderDetails.getPo_number();
			String item = mpr_OrderDetails.getItem_no();
			String com = mpr_OrderDetails.getComments();
			/*
			 * String sql="UPDATE " + TABLE_mpr2 + " SET " + KEY_COMMENTS + com
			 * + "," + KEY_QUANTITY_SHIPPED + shpQty + " WHERE " +
			 * KEY_ORDER_NUMBER + "= '" + ord + "' AND " + KEY_ITEMNUMBER +
			 * " = '" + item + " ' ";
			 */

			// cv.put(KEY_ORDER_NUMBER, mpr_OrderDetails.getOrdNumber());
			// cv.put(KEY_CUSTOMER_NUMBER, mpr_OrderDetails.getCustNumber());
			// cv.put(KEY_ITEMNUMBER, mpr_OrderDetails.getItemNumber());
			// cv.put(KEY_ITEM_DESCRIPTION,
			// mpr_OrderDetails.getItemDescription());
			// cv.put(KEY_UOM, mpr_OrderDetails.getUom());
			cv.put(KEY_QUANTITY_ORDER, mpr_OrderDetails.getOrdered_qty());
			if (flag.equals("Ship"))
				cv.put(KEY_QUANTITY_RECEIVED,
						mpr_OrderDetails.getReceived_qty());
			else
				cv.put(KEY_QUANTITY_RECEIVED, "0");

			// cv.put(KEY_LOCATION, mpr_OrderDetails.getLocation());
			// cv.put(KEY_SHIP_VIA_CODE, mpr_OrderDetails.getShipViaCode());
			// cv.put(KEY_SHIP_VIA_DESC,
			// mpr_OrderDetails.getShipviaDescription());
			cv.put(KEY_COMMENTS, com);
			// cv.put(KEY_LINENUMBER, mpr_OrderDetails.getLineNumber());
			// cv.put(KEY_PICKINGSEQUENCE,
			// mpr_OrderDetails.getPickingSequence());

			db.update(TABLE_MPR4, cv, KEY_PONUMBER + "= '" + ord + "' AND "
					+ KEY_ITEMNUMBER + " = '" + item + "' ", null);

			Log.i("Success", "Data Updated in table " + TABLE_MPR4);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 5273";
			msg = "updateMpr_OrderDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public String addMpr_OrderDetails(MPR_OrderDetails mpr_OrderDetails) {
		// TODO Auto-generated method stub
		String result = "";
		try {
			ContentValues cv = new ContentValues();
			cv.put(KEY_PONUMBER, mpr_OrderDetails.getPo_number());
			cv.put(KEY_VENDORNUMBER, mpr_OrderDetails.getVd_code());
			cv.put(KEY_ITEMNUMBER, mpr_OrderDetails.getItem_no());
			cv.put(KEY_DESCRIPTION, mpr_OrderDetails.getDesc());
			cv.put(KEY_UOM, mpr_OrderDetails.getUom());
			cv.put(KEY_QUANTITY_ORDER, mpr_OrderDetails.getOrdered_qty());
			cv.put(KEY_QUANTITY_RECEIVED, "0");
			cv.put(KEY_COMMENTS, mpr_OrderDetails.getComments());
			cv.put(KEY_LOCATION_CODE, mpr_OrderDetails.getLoc_code());
			cv.put(KEY_LINENUMBER, mpr_OrderDetails.getLine_no());
			db.insert(TABLE_MPR4, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_MPR4);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 5272";
			msg = "addMpr_OrderDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public boolean checkmprMaster_Details(String ordNo, String custNo) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MPR5
				+ " WHERE " + DatabaseHandler.KEY_PONUMBER + " = '" + ordNo
				+ "'" + " and " + KEY_VENDORNUMBER + " = '" + custNo + "' ";
		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}

	public String updatempr_MasterDetails(MPR_MasterDetails mpr_MasterDetails) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			ContentValues cv = new ContentValues();

			String poNo = mpr_MasterDetails.getPo_number();
			String vendorid = mpr_MasterDetails.getVd_code();

			cv.put(KEY_ORDER_DATE, mpr_MasterDetails.getDate());

			db.update(TABLE_MPR5, cv, KEY_PONUMBER + "= '" + poNo + "' AND "
					+ KEY_VENDORNUMBER + "= '" + vendorid + " '", null);

			Log.i("Success", "Data Updated in table " + TABLE_MPR5);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 5277";
			msg = "updateMpr_MasterDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public String addMpr_MasterDetails(MPR_MasterDetails mpr_MasterDetails) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			ContentValues cv = new ContentValues();

			cv.put(KEY_PONUMBER, mpr_MasterDetails.getPo_number());
			cv.put(KEY_ORDER_DATE, mpr_MasterDetails.getDate());
			cv.put(KEY_VENDORNUMBER, mpr_MasterDetails.getVd_code());

			db.insert(TABLE_MPR5, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_MPR5);

			result = "success";
		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 5426";
			msg = "addmpr_MasterDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();
		}

		return result;
	}

	public boolean checkmprUpc_Details(String item) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MPR7
				+ " WHERE " + DatabaseHandler.KEY_ITEMNUMBER + " = '" + item
				+ "'";

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}

	public String updateMpr_UpcDetails(MPR_Upc mpr_upc) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			String item = mpr_upc.getItem_number();// etItemNumber();
			String com = mpr_upc.getBar_code();// getComments();
			/*
			 * String sql="UPDATE " + TABLE_mpr2 + " SET " + KEY_COMMENTS + com
			 * + "," + KEY_QUANTITY_SHIPPED + shpQty + " WHERE " +
			 * KEY_ORDER_NUMBER + "= '" + ord + "' AND " + KEY_ITEMNUMBER +
			 * " = '" + item + " ' ";
			 */

			// cv.put(KEY_ORDER_NUMBER, mpr_OrderDetails.getOrdNumber());
			// cv.put(KEY_CUSTOMER_NUMBER, mpr_OrderDetails.getCustNumber());
			// cv.put(KEY_ITEMNUMBER, mpr_OrderDetails.getItemNumber());
			// cv.put(KEY_ITEM_DESCRIPTION,
			// mpr_OrderDetails.getItemDescription());
			// cv.put(KEY_UOM, mpr_OrderDetails.getUom());
			// cv.put(KEY_QUANTITY_ORDER, mpr_OrderDetails.getQtyOrdred());
			// cv.put(KEY_ITEMNUMBER, item);
			// cv.put(KEY_LOCATION, mpr_OrderDetails.getLocation());
			// cv.put(KEY_SHIP_VIA_CODE, mpr_OrderDetails.getShipViaCode());
			// cv.put(KEY_SHIP_VIA_DESC,
			// mpr_OrderDetails.getShipviaDescription());
			cv.put(KEY_BAR_CODE, com);
			// cv.put(KEY_LINENUMBER, mpr_OrderDetails.getLineNumber());
			// cv.put(KEY_PICKINGSEQUENCE,
			// mpr_OrderDetails.getPickingSequence());

			db.update(TABLE_MPR7, cv, KEY_ITEMNUMBER + "= '" + item + "'", null);

			Log.i("Success", "Data Updated in table " + TABLE_MPR7);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 5285";
			msg = "updateMpr_UpcDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public String addMpr_UpcDetails(MPR_Upc mpr_Upc) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			ContentValues cv = new ContentValues();

			cv.put(KEY_ITEMNUMBER, mpr_Upc.getItem_number());
			cv.put(KEY_BAR_CODE, mpr_Upc.getBar_code());

			db.insert(TABLE_MPR7, null, cv);
			Log.i("Success", "Data Inserted in table " + TABLE_MPR7);

			result = "success";
		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 5331";
			msg = "addmpr_UpcDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;
	}

	public MPR_MasterDetails getMprMasterDetails(String ordNo) {
		Cursor cursor_masdetails = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MPR5 + " WHERE "
				+ DatabaseHandler.KEY_PONUMBER + " = '" + ordNo + "'";
		cursor_masdetails = db.rawQuery(q, null);

		if (cursor_masdetails != null) {
			cursor_masdetails.moveToFirst();
		}

		MPR_MasterDetails mpr_MasterDetails = new MPR_MasterDetails();

		mpr_MasterDetails.setPo_number(cursor_masdetails.getString(1));
		mpr_MasterDetails.setDate((Integer.parseInt(cursor_masdetails
				.getString(2).toString())));
		mpr_MasterDetails.setVd_code(cursor_masdetails.getString(3));

		cursor_masdetails.close();

		return mpr_MasterDetails;

	}

	public MPR_OrderDetails getMprOrderDetails(String ordNo) {
		Cursor cursor_orddetails = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MPR4 + " WHERE "
				+ DatabaseHandler.KEY_PONUMBER + " = '" + ordNo + "'";
		cursor_orddetails = db.rawQuery(q, null);

		if (cursor_orddetails != null) {
			cursor_orddetails.moveToFirst();
		}

		MPR_OrderDetails mse_OrderDetails = new MPR_OrderDetails();

		mse_OrderDetails.setPo_number(cursor_orddetails.getString(0));
		mse_OrderDetails.setVd_code(cursor_orddetails.getString(1));
		mse_OrderDetails.setItem_no(cursor_orddetails.getString(2));
		mse_OrderDetails.setDesc(cursor_orddetails.getString(3));
		mse_OrderDetails.setUom(cursor_orddetails.getString(4));
		mse_OrderDetails.setOrdered_qty(cursor_orddetails.getInt(5));
		mse_OrderDetails.setReceived_qty(cursor_orddetails.getInt(6));
		mse_OrderDetails.setComments(cursor_orddetails.getString(7));
		mse_OrderDetails.setLoc_code(cursor_orddetails.getString(8));
		mse_OrderDetails.setLine_no(cursor_orddetails.getString(9));

		cursor_orddetails.close();
		return mse_OrderDetails;
	}

	public String checkMseTransPendingExpo() {
		String result = "false";
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MSE5
				+ " WHERE " + KEY_STATUS + "=0";
		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			result = "true";
			return result;
		} else {
			result = "false";
			return result;
		}

	}
	public String checkIntTransPendingExpo() {
		String result = "false";
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_INT5
				+ " WHERE " + KEY_STATUS + "=0";
		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			result = "true";
			return result;
		} else {
			result = "false";
			return result;
		}

	}

	public String addMpr_Vendor(MPR_Vendor mpr_Vendor) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			ContentValues cv = new ContentValues();
			cv.put(KEY_VENDORNUMBER, mpr_Vendor.getVendor_id());
			cv.put(KEY_VENDORNAME, mpr_Vendor.getVendor_name());
			cv.put(KEY_ADDRESS1, mpr_Vendor.getStreet1());
			cv.put(KEY_ADDRESS2, mpr_Vendor.getStreet2());
			cv.put(KEY_NAMECITY, mpr_Vendor.getNamecity());
			cv.put(KEY_STATE_CODE, mpr_Vendor.getCode_state());
			cv.put(KEY_ZIP, mpr_Vendor.getCode_pstl());
			cv.put(KEY_COUNTRY, mpr_Vendor.getCode_country());
			cv.put(KEY_PHONE, mpr_Vendor.getPhone());
			db.insert(TABLE_MPR8, null, cv);
			result = "success";
			Log.i("Success", "Data Inserted in table " + TABLE_MPR8);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 5146";
			msg = "addMpr_Vendor failed.";
			errMsg = errCode + " : " + msg;
			result = "error";
			LogfileCreator.appendLog(errMsg);
			db.close();

		}
		return result;

	}

	public String updateMpr_Vendor(MPR_Vendor mpr_Vendor) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			ContentValues cv = new ContentValues();

			String vendorid = mpr_Vendor.getVendor_id();
			/*
			 * String vendorname=mpr_Vendor.getVendor_name(); String
			 * add1=mpr_Vendor.getStreet1(); String
			 * add2=mpr_Vendor.getStreet2(); String
			 * namecity=mpr_Vendor.getNamecity(); String
			 * statecode=mpr_Vendor.getCode_state(); String
			 * pstlcode=mpr_Vendor.getCode_pstl(); String
			 * countrycode=mpr_Vendor.getCode_country(); String
			 * phone=mpr_Vendor.getPhone();
			 */

			cv.put(KEY_VENDORNAME, mpr_Vendor.getVendor_name());
			cv.put(KEY_ADDRESS1, mpr_Vendor.getStreet1());
			cv.put(KEY_ADDRESS2, mpr_Vendor.getStreet2());
			cv.put(KEY_NAMECITY, mpr_Vendor.getNamecity());
			cv.put(KEY_STATE_CODE, mpr_Vendor.getCode_state());
			cv.put(KEY_ZIP, mpr_Vendor.getCode_pstl());
			cv.put(KEY_COUNTRY, mpr_Vendor.getCode_country());
			cv.put(KEY_PHONE, mpr_Vendor.getPhone());

			db.update(TABLE_MPR8, cv, KEY_VENDORNUMBER + "= '" + vendorid
					+ "' ", null);

			Log.i("Success", "Data Updated in table " + TABLE_MPR8);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 5290";
			msg = "mpr_Vendor failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public String checkShipInTrans_mpr(String order) {
		String shpAvail = "";
		ContentValues cv = new ContentValues();
		String query = "SELECT " + KEY_RECEIPT_NUMBER + " FROM " + TABLE_MPR6
				+ " WHERE " + KEY_PONUMBER + " ='" + order + "' AND "
				+ KEY_RECEIPT_NUMBER + " IS NULL ";

		Cursor cur = db.rawQuery(query, null);
		int i = cur.getCount();
		if (cur.getCount() > 1) {

			shpAvail = "Yes";
		} else {
			shpAvail = "No";
		}
		cur.close();
		return shpAvail;
	}

	public boolean checkMpr_VendorDetails(String vid) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MPR8
				+ " WHERE " + DatabaseHandler.KEY_VENDORNUMBER + " = '" + vid
				+ "'";

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}

	public List<MPR_OrderDetails> getMprDataMPR_OrderDetails(String ordNo) {
		Cursor cursor = null;
		List<MPR_OrderDetails> ordlst = new ArrayList<MPR_OrderDetails>();

		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MPR4 + " WHERE "
				+ DatabaseHandler.KEY_PONUMBER + " = '" + ordNo + "'";

		cursor = db.rawQuery(q, null);

		try {
			while (cursor.moveToNext()) {
				MPR_OrderDetails mpr_OrderDetails = new MPR_OrderDetails();

				mpr_OrderDetails.setItem_no(cursor.getString(2));
				mpr_OrderDetails.setDesc(cursor.getString(3));
				mpr_OrderDetails.setUom(cursor.getString(4));
				mpr_OrderDetails.setOrdered_qty(cursor.getInt(5));
				mpr_OrderDetails.setReceived_qty(cursor.getInt(6));
				mpr_OrderDetails.setComments(cursor.getString(7));
				mpr_OrderDetails.setLoc_code(cursor.getString(8));

				ordlst.add(mpr_OrderDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5210";
			msg = "getMprDataMPR_OrderDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return ordlst;
	}

	public boolean getMpr_TransDetails(String ordNo, String item) {

		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MPR6
				+ " WHERE " + DatabaseHandler.KEY_PONUMBER + " = '" + ordNo
				+ "'" + " and " + KEY_ITEMNUMBER + " = '" + item + "' ";
		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			System.out.println("flag=" + flag);
			return flag;
		} else {
			System.out.println("flag=" + flag);
			return flag;
		}

	}

	public Cursor getMPRCustomerData(String orderno) {
		// TODO Auto-generated method stub
		Cursor cursor = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MPR5 + " WHERE "
				+ DatabaseHandler.KEY_PONUMBER + " = '" + orderno + "'";
		cursor = db.rawQuery(q, null);
		try {
			int i = cursor.getCount();

			if (cursor != null && i > 0) {
				cursor.moveToFirst();
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5270";
			msg = "getMPRCustomerData failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}
		return cursor;

	}

	public MPR_OrderDetails getMprOrderDetails(String ordNo, String it) {
		Cursor cursor_orddetails = null;
		MPR_OrderDetails mpr_OrderDetails = null;
		try {

			String q = "SELECT * FROM " + DatabaseHandler.TABLE_MPR4
					+ " WHERE " + DatabaseHandler.KEY_PONUMBER + " = '" + ordNo
					+ "'" + " and " + KEY_ITEMNUMBER + " = '" + it + "' ";

			cursor_orddetails = db.rawQuery(q, null);

			if (cursor_orddetails.moveToFirst()) {
				mpr_OrderDetails = new MPR_OrderDetails();
				mpr_OrderDetails.setPo_number(cursor_orddetails.getString(0));
				mpr_OrderDetails.setVd_code(cursor_orddetails.getString(1));
				mpr_OrderDetails.setItem_no(cursor_orddetails.getString(2));
				mpr_OrderDetails.setDesc(cursor_orddetails.getString(3));
				mpr_OrderDetails.setUom(cursor_orddetails.getString(4));
				mpr_OrderDetails.setOrdered_qty(cursor_orddetails.getInt(5));
				mpr_OrderDetails.setReceived_qty(cursor_orddetails.getInt(6));
				mpr_OrderDetails.setComments(cursor_orddetails.getString(7));
				mpr_OrderDetails.setLoc_code(cursor_orddetails.getString(8));
				mpr_OrderDetails.setLine_no(cursor_orddetails.getString(9));

				return mpr_OrderDetails;
			} else {
				Log.i("No Data", "");
			}
		} catch (Exception e) {
			db.endTransaction();
			errCode = "Error 5211";
			msg = "getmprOrderDetails failed.";
			errMsg = errCode + " : " + msg;
			db.close();
			LogfileCreator.appendLog(errMsg);
		}

		cursor_orddetails.close();

		return mpr_OrderDetails;
	}

	public String addMpr_TransDetails(MPR_OrderDetails mpr_OrderDetails) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			String s = mpr_OrderDetails.getVd_code();
			String s1 = mpr_OrderDetails.getLine_no();

			cv.put(KEY_PONUMBER, mpr_OrderDetails.getPo_number());
			cv.put(KEY_VENDORNUMBER, mpr_OrderDetails.getVd_code());
			cv.put(KEY_ITEMNUMBER, mpr_OrderDetails.getItem_no());
			cv.put(KEY_DESCRIPTION, mpr_OrderDetails.getDesc());
			cv.put(KEY_UOM, mpr_OrderDetails.getUom());
			cv.put(KEY_QUANTITY_ORDER, mpr_OrderDetails.getOrdered_qty());
			cv.put(KEY_QUANTITY_RECEIVED, mpr_OrderDetails.getReceived_qty());
			cv.put(KEY_COMMENTS, mpr_OrderDetails.getComments());
			cv.put(KEY_LOCATION_CODE, mpr_OrderDetails.getLoc_code());
			cv.put(KEY_LINENUMBER, mpr_OrderDetails.getLine_no());
			cv.put(KEY_STATUS, 0);

			db.insert(TABLE_MPR6, null, cv);

			Log.i("Success", "Data inserted in table " + TABLE_MPR6);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 532";
			msg = "addmpr_TransDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();
		}

		return result;

	}

	public String updateMpr_TransDetails(MPR_OrderDetails mpr_OrderDetails) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			String ord = mpr_OrderDetails.getPo_number();
			String item = mpr_OrderDetails.getItem_no();
			String com = mpr_OrderDetails.getComments();
			// String item=mpr_OrderDetails.getItemNumber();

			// cv.put(KEY_ORDER_NUMBER, mpr_OrderDetails.getOrdNumber());
			// cv.put(KEY_CUSTOMER_NUMBER, mpr_OrderDetails.getCustNumber());
			// cv.put(KEY_LINENUMBER, mpr_OrderDetails.getLineNumber());
			// cv.put(KEY_ITEMNUMBER, mpr_OrderDetails.getItemNumber());
			// cv.put(KEY_DESCRIPTION,
			// mpr_OrderDetails.getItemDescription());
			// cv.put(KEY_UOM, mpr_OrderDetails.getUom());
			cv.put(KEY_QUANTITY_ORDER, mpr_OrderDetails.getOrdered_qty());
			cv.put(KEY_QUANTITY_RECEIVED, mpr_OrderDetails.getReceived_qty());
			// cv.put(KEY_SHIP_VIA, mpr_OrderDetails.getShipViaCode());
			cv.put(KEY_COMMENTS, mpr_OrderDetails.getComments());

			cv.put(KEY_STATUS, 0);

			db.update(TABLE_MPR6, cv, KEY_PONUMBER + "= '" + ord + "' AND "
					+ KEY_ITEMNUMBER + " = '" + item + "' AND "
					+ KEY_RECEIPT_NUMBER + " IS NULL", null);
			/*
			 * Here we are giving cond KEY_RECEIPT_NUMBER=PO since suppose we
			 * exported first time and and its not exported then it will add ord
			 * no in the shipment col so we are giving or cond so that next time
			 * if PDA user try to make some changes then it update transaction
			 * table correcttly
			 */

			db.update(TABLE_MPR6, cv, KEY_PONUMBER + "= '" + ord + "' AND "
					+ KEY_ITEMNUMBER + " = '" + item + "' AND ("
					+ KEY_RECEIPT_NUMBER + " IS NULL OR " + KEY_RECEIPT_NUMBER
					+ "='" + ord + "')", null);

			Log.i("Success", "Data Updated in table " + TABLE_MPR6);

			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 5334";
			msg = "updatempr_TransDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}
		return result;

	}

	public List<MPR_Trans> getDistinctOrderMprTrans() {
		Cursor cursor_transdetails = null;
		List<MPR_Trans> ordlist = new ArrayList<MPR_Trans>();

		String q = "SELECT DISTINCT " + KEY_PONUMBER + " , " + KEY_VENDORNUMBER
				+ " FROM " + DatabaseHandler.TABLE_MPR6 + " WHERE "
				+ KEY_STATUS + "=0";
		cursor_transdetails = db.rawQuery(q, null);
		while (cursor_transdetails.moveToNext()) {
			MPR_Trans mpr_TansDetails = new MPR_Trans();

			mpr_TansDetails.setPoNumber(cursor_transdetails.getString(0));
			mpr_TansDetails.setVendorId(cursor_transdetails.getString(1));

			ordlist.add(mpr_TansDetails);
		}
		cursor_transdetails.close();
		return ordlist;

	}

	public Cursor getReceiptNo(String OrdNo) {
		// TODO Auto-generated method stub
		Cursor cursor_transdetails = null;
		String sqlquery = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MPR6
				+ " WHERE " + KEY_PONUMBER + " = '" + OrdNo + "' AND "
				+ KEY_RECEIPT_NUMBER + " = '" + OrdNo + "'";
		int numRows = (int) DatabaseUtils.longForQuery(db, sqlquery, null);

		if (numRows > 0) {
			String q = "SELECT " + KEY_RECEIPT_NUMBER + "  FROM "
					+ DatabaseHandler.TABLE_MPR6 + " WHERE " + KEY_PONUMBER
					+ " = '" + OrdNo + "' AND " + KEY_RECEIPT_NUMBER + " = '"
					+ OrdNo + "'";
			cursor_transdetails = db.rawQuery(q, null);

			if (cursor_transdetails != null) {
				cursor_transdetails.moveToFirst();
			}
		} else {

			String q = "SELECT MAX(" + KEY_RECEIPT_NUMBER + ")  FROM "
					+ DatabaseHandler.TABLE_MPR6 + " WHERE " + KEY_PONUMBER
					+ " = '" + OrdNo + "'"; // + KEY_STATUS + " =1";
			cursor_transdetails = db.rawQuery(q, null);

			if (cursor_transdetails != null) {
				cursor_transdetails.moveToFirst();
			}
		}

		return cursor_transdetails;

	}

	public Cursor getMprTransData(String OrdNo) {
		// TODO Auto-generated method stub
		Cursor cursor_transdetails = null;
		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MPR6 + " WHERE "
				+ KEY_PONUMBER + " = '" + OrdNo + "' AND " + KEY_STATUS + " =0";
		cursor_transdetails = db.rawQuery(q, null);

		if (cursor_transdetails != null) {
			cursor_transdetails.moveToFirst();
		}

		MPR_Trans mpr_TansDetails = new MPR_Trans();

		mpr_TansDetails.setPoNumber(cursor_transdetails.getString(0));
		mpr_TansDetails.setVendorId(cursor_transdetails.getString(1));
		mpr_TansDetails.setItemNumber(cursor_transdetails.getString(2));
		mpr_TansDetails.setDesc(cursor_transdetails.getString(3));
		mpr_TansDetails.setUom(cursor_transdetails.getString(4));
		mpr_TansDetails.setOrdQty(cursor_transdetails.getDouble(5));
		mpr_TansDetails.setRecQty(cursor_transdetails.getDouble(6));
		mpr_TansDetails.setComment(cursor_transdetails.getString(7));
		mpr_TansDetails.setLocCode(cursor_transdetails.getString(8));
		mpr_TansDetails.setReceiptNumber(cursor_transdetails.getString(9));
		mpr_TansDetails.setLineNumber(cursor_transdetails.getString(10));
		mpr_TansDetails.setStatus(cursor_transdetails.getInt(11));
		mpr_TansDetails.setReceiptDay(cursor_transdetails.getString(12));
		mpr_TansDetails.setReceiptMonth(cursor_transdetails.getString(13));
		mpr_TansDetails.setReceiptYear(cursor_transdetails.getString(14));

		return cursor_transdetails;

	}

	public String Update_MPRTrans(String Shipno, String expOrd) {
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			String current = dateFormat.format(date);
			String arr[] = current.split("/");

			String year = arr[0];
			String month = arr[1];
			String day = arr[2];
			System.out.println("Year" + year);
			System.out.println("Month" + month);
			System.out.println("Day" + day);

			cv.put(KEY_RECEIPT_NUMBER, Shipno);
			cv.put(KEY_RECEIPT_YEAR, year);
			cv.put(KEY_RECEIPT_MONTH, month);
			cv.put(KEY_RECEIPT_DAY, day);
			cv.put(KEY_STATUS, 1);

			/*
			 * String value = checkReceiptInTrans(expOrd); // No if
			 * (!value.equalsIgnoreCase("Yes")) { System.out.println("UPDATE");
			 */
			db.update(TABLE_MPR6, cv, KEY_PONUMBER + "= '" + expOrd + "' AND "
					+ KEY_STATUS + " =0", null);
			/*
			 * } // Yes else { System.out.println("INSERT");
			 * 
			 * try { String ord = expOrd; getReadableDatabase(); Cursor cur =
			 * getMprTransData(ord); closeDatabase(); cv.put(KEY_PONUMBER,
			 * cur.getString(0)); cv.put(KEY_VENDORNUMBER, cur.getString(1));
			 * cv.put(KEY_ITEMNUMBER, cur.getString(2)); cv.put(KEY_DESCRIPTION,
			 * cur.getString(3)); cv.put(KEY_UOM, cur.getString(4));
			 * cv.put(KEY_QUANTITY_ORDER, cur.getInt(5));
			 * cv.put(KEY_QUANTITY_RECEIVED, cur.getInt(6));
			 * cv.put(KEY_COMMENTS, cur.getString(7)); cv.put(KEY_LOCATION_CODE,
			 * cur.getString(8)); cv.put(KEY_LINENUMBER, cur.getString(10));
			 * 
			 * db.insert(TABLE_MPR6, null, cv); Log.i("Success",
			 * "Data Inserted in " + TABLE_MPR6); cur.close(); } catch
			 * (Exception exe) { db.endTransaction(); errCode = "Error 7536";
			 * msg = "In Insert Update_MPRTrans failed."; errMsg = errCode +
			 * " : " + msg; LogfileCreator.appendLog(errMsg); db.close(); }
			 * 
			 * }
			 */
		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 7537";
			msg = "In Update...Update_MPRTrans failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	/*
	 * public String checkReceiptInTrans(String order) { String shpAvail = "";
	 * ContentValues cv = new ContentValues(); String query = "SELECT " +
	 * KEY_RECEIPT_NUMBER + " FROM " + TABLE_MPR6 + " WHERE " + KEY_PONUMBER +
	 * " ='" + order + "' AND " + KEY_RECEIPT_NUMBER + " IS NULL ";
	 * System.out.println("MyQ" + query);
	 * 
	 * Cursor cur = db.rawQuery(query, null); int i = cur.getCount();
	 * System.out.println("i=" + i);
	 * 
	 * if (cur.getCount() > 1) { System.out.println("Yes"); shpAvail = "Yes"; }
	 * else { System.out.println("No"); shpAvail = "No"; } cur.close(); return
	 * shpAvail;
	 * 
	 * }
	 */

	public String checkReceiptInTrans(String order, String itno) {

		String shpAvail = "";
		ContentValues cv = new ContentValues();

		String query = "SELECT COUNT(*) FROM " + TABLE_MPR6 + " WHERE "
				+ KEY_PONUMBER + " ='" + order + "' AND " + KEY_ITEMNUMBER
				+ " ='" + itno + "' AND " + KEY_RECEIPT_NUMBER
				+ " IS NOT NULL AND " + KEY_RECEIPT_DAY + " IS NOT NULL AND "
				+ KEY_RECEIPT_MONTH + " IS NOT NULL AND " + KEY_RECEIPT_YEAR
				+ " IS NOT NULL";

		Cursor cur = db.rawQuery(query, null);

		System.out.println("Check query" + query);
		int numRows = (int) DatabaseUtils.longForQuery(db, query, null);
		System.out.println("Check receip" + numRows);
		if (numRows > 0) {
			String locquery = "SELECT COUNT(*) FROM " + TABLE_MPR6 + " WHERE "
					+ KEY_PONUMBER + " ='" + order + "' AND " + KEY_ITEMNUMBER
					+ " ='" + itno + "' AND " + KEY_RECEIPT_NUMBER + " IS NULL";
			int locnumRows = (int) DatabaseUtils.longForQuery(db, locquery,
					null);
			if (locnumRows == 0) {
				shpAvail = "Yes";// add
			} else {

				shpAvail = "No";// update
			}

		} else {
			String locquery1 = "SELECT COUNT(*) FROM " + TABLE_MPR6 + " WHERE "
					+ KEY_PONUMBER + " ='" + order + "' AND " + KEY_ITEMNUMBER
					+ " ='" + itno + "'";
			int locnumRows = (int) DatabaseUtils.longForQuery(db, locquery1,
					null);
			if (locnumRows == 0) {
				shpAvail = "Yes";// add
			} else {

				shpAvail = "No";// update
			}

		}
		cur.close();
		return shpAvail;
	}

	public boolean checkMPR() {
		boolean flag = false;

		ContentValues cv = new ContentValues();
		String query = "SELECT COUNT(*) FROM " + TABLE_MPR6;
		int numRows = (int) DatabaseUtils.longForQuery(db, query, null);

		if (numRows > 0) {
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}

	public String checkMprTransPendingExpo() {
		String result = "false";
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MPR6
				+ " WHERE " + KEY_STATUS + "=0";
		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			result = "true";
			return result;
		} else {
			result = "false";
			return result;
		}

	}
	

	public void addTempPo(List<String> poList) {

		// TODO Auto-generated method stub

		try {
			ContentValues cv = new ContentValues();
			for (int i = 0; i < poList.size(); i++) {
				cv.put(KEY_PONUMBER, poList.get(i));
				cv.put(KEY_STATUS, 0);
				db.insert(TABLE_MPR9, null, cv);
			}
			Log.i("Success", "Data Inserted in table " + TABLE_MPR9);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 522379";
			msg = "addMpr_TempPoDetails failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);

		}

	}

	public void deleteTempPo() {

		try {

			db.execSQL("DELETE FROM " + TABLE_MPR9);
		} catch (Exception e) {

			Log.e("ERROR", e.getLocalizedMessage());
		}

	}

	public void deleteTempPo(String po) {

		try {

			db.execSQL("DELETE FROM " + TABLE_MPR9 + " where " + KEY_PONUMBER
					+ " ='" + po + "'");
		} catch (Exception e) {

			Log.e("ERROR", e.getLocalizedMessage());
		}

	}

	public void deleteTempPo(ArrayList<String> poList) {
		try {
			for (int i = 0; i < poList.size(); i++) {
				db.execSQL("DELETE FROM " + TABLE_MPR9 + " where "
						+ KEY_PONUMBER + " ='" + poList.get(i) + "'");
			}
		} catch (Exception e) {

			Log.e("ERROR", e.getLocalizedMessage());
		}

	}

	public String UpdateTempPo(String Pono) {
		String result = null;
		try {

			ContentValues cv = new ContentValues();
			/*
			 * DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); Date
			 * date = new Date(); String current = dateFormat.format(date);
			 * String arr[] = current.split("/");
			 * 
			 * String year = arr[0]; String month = arr[1]; String day = arr[2];
			 * System.out.println("Year" + year); System.out.println("Month" +
			 * month); System.out.println("Day" + day);
			 */

			cv.put(KEY_STATUS, 1);
			/*
			 * cv.put(KEY_SHIP_YEAR, year); cv.put(KEY_SHIP_MONTH, month);
			 * cv.put(KEY_SHIP_DAY, day); cv.put(KEY_STATUS, 1);
			 */
			db.update(TABLE_MPR9, cv, KEY_PONUMBER + "= '" + Pono + "'", null);

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 537";
			msg = "In Update...UpdateMPR TEMPs failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public List<String> getTempExportedPo() {

		Cursor cursor = null;
		List<String> polst = null;

		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MPR9 + " WHERE "
				+ KEY_STATUS + "='1'";

		cursor = db.rawQuery(q, null);

		try {
			if (cursor.getCount() > 0) {
				polst = new ArrayList<String>();
				while (cursor.moveToNext()) {

					polst.add(cursor.getString(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5200";
			msg = "getTempExportedPo failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return polst;

	}

	public List<String> getTempPo() {

		Cursor cursor = null;
		List<String> polst = null;

		String q = "SELECT * FROM " + DatabaseHandler.TABLE_MPR9 + " WHERE "
				+ KEY_STATUS + "='0'";

		cursor = db.rawQuery(q, null);

		try {
			if (cursor.getCount() > 0) {
				polst = new ArrayList<String>();
				while (cursor.moveToNext()) {

					polst.add(cursor.getString(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 5200";
			msg = "getTempPo failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return polst;

	}

	/*
	 * //////////////////////////////////////////////////////////////////////////
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * MLT
	 */// ////////////////////////////////////////////////////////////////////////

	
	public String checkMltTransPendingExpo() {
		String result = "false";
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MLT2
				+ " WHERE " + KEY_STATUS + "=0";
		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			result = "true";
			return result;
		} else {
			result = "false";
			return result;
		}

	}
	
	// For Scanning
	public MLT_Details getItemFromTemp(String itno) {
		String tempsql = "SELECT  * FROM " + DatabaseHandler.TABLE_MLT7
				+ " WHERE " + KEY_ITEMNUMBER + "='" + itno + "'";
		Cursor alltempCursor = db.rawQuery(tempsql, null);
		MLT_Details details=null;
		try {
			while (alltempCursor.moveToNext()) {
				details=new MLT_Details();
				details.setMlt_from(alltempCursor.getString(3));
				details.setMlt_to(alltempCursor.getString(4));
				details.setMlt_qty(alltempCursor.getString(5));
				details.setMlt_uom(alltempCursor.getString(6));
				
			}
		}
		catch(Exception e)
		{
			db.endTransaction();
			errCode = "Error 5139patt";
			msg = "getItemFromTemp failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			
		}
		return details;
	}

	public String get_Temp_TABLE_Data() {
		String result = "";

		System.out.println("static---->" + doc);
		ContentValues cv = new ContentValues();

		String tempsql = "SELECT " + KEY_ITEMNUMBER + "," + KEY_FROMLOCATION
				+ "," + KEY_TOLOCATION + "," + KEY_QUANTITY + "," + KEY_UOM
				+ " FROM " + DatabaseHandler.TABLE_MLT7;
		Cursor tempCursor = db.rawQuery(tempsql, null);

		String tabsql = "SELECT COUNT (*) FROM " + TABLE_MLT1;

		int numRows = (int) DatabaseUtils.longForQuery(db, tabsql, null);

		if (numRows > 0) {

			String maxSelect = "SELECT MAX(" + KEY_DOCNUMBER + ") FROM "
					+ TABLE_MLT1;
			Cursor maxCursor = db.rawQuery(maxSelect, null);
			while (maxCursor.moveToNext()) {
				System.out.println(maxCursor.getInt(0));
			}

			Cursor cursorMax = db.query(TABLE_MLT1, new String[] { "MAX("
					+ KEY_DOCNUMBER + ")" }, null, null, null, null, null);
			try {
				cursorMax.moveToFirst();
				doc_val = cursorMax.getInt(0);
				doc_val++;
			}

			catch (NullPointerException ex) {
				db.endTransaction();
				errCode = "Error 5139";
				msg = "get_Temp_TABLE_Data failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);
				result = "error";
			} catch (Exception ex) {
				System.out.println("doc_val" + doc_val);
				db.endTransaction();
				errCode = "Error 5139pat";
				msg = "get_Temp_TABLE_Data failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);
				result = "error";
			} finally {
				cursorMax.close();
			}
		} else {
			doc_val = 1;
		}

		try {
			while (tempCursor.moveToNext()) {

				cv.put(KEY_DOCNUMBER, doc_val);
				cv.put(KEY_ITEMNUMBER, tempCursor.getString(0));
				cv.put(KEY_FROMLOCATION, tempCursor.getString(1));
				cv.put(KEY_TOLOCATION, tempCursor.getString(2));
				cv.put(KEY_QUANTITY, tempCursor.getString(3));
				cv.put(KEY_UOM, tempCursor.getString(4));

				db.insert(TABLE_MLT1, null, cv);
				result = "success";

			}
			Log.i("Success", "Data Inserted in table " + TABLE_MLT1);
		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 51329";
			msg = "get_Temp_TABLE_Data failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";

		}
		tempCursor.close();
		db.close();
		return result;
	}

	public String updateMLT_Temp_Table(MLT_Details mlt_Details) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			ContentValues cv = new ContentValues();

			String itemno = mlt_Details.getMlt_itemno();
			String desc = mlt_Details.getMlt_description();
			String from = mlt_Details.getMlt_from();
			String to = mlt_Details.getMlt_to();
			String qty = mlt_Details.getMlt_qty();
			String uom = mlt_Details.getMlt_uom();

			cv.put(KEY_ITEMNUMBER, itemno);
			cv.put(KEY_DESCRIPTION, desc);
			cv.put(KEY_FROMLOCATION, from);
			cv.put(KEY_TOLOCATION, to);
			cv.put(KEY_QUANTITY, qty);
			cv.put(KEY_UOM, uom);

			db.update(TABLE_MLT7, cv, KEY_ITEMNUMBER + "= '" + itemno + "'",
					null);

			Log.i("Success", "Data Updated in table " + TABLE_MLT7);
			result = "success";

		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 52513";
			msg = "updateMLT_Temp_Table failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";
			db.close();

		}

		return result;

	}

	public void addMLT_Temp_Table(MLT_Details details) {
		try {
			ContentValues cv = new ContentValues();
			cv.put(KEY_ITEMNUMBER, details.getMlt_itemno());
			cv.put(KEY_DESCRIPTION, details.getMlt_description());
			cv.put(KEY_FROMLOCATION, details.getMlt_from());
			cv.put(KEY_TOLOCATION, details.getMlt_to());
			cv.put(KEY_QUANTITY, details.getMlt_qty());
			cv.put(KEY_UOM, details.getMlt_uom());
			db.insert(TABLE_MLT7, null, cv);
			Log.i("SuccessFullyy", "Data Inserted in Temp table");
			db.close();
		} catch (Exception e) {

			System.out.println(e);
		}

	}

	public String addMltUPC(MLT_UPC up) {
		String res = "error";
		try {
			ContentValues cv = new ContentValues();

			cv.put(KEY_ITEMNUMBER, up.getItemno());
			cv.put(KEY_UPCCODE, up.getUpc());
			db.insert(TABLE_MLT6, null, cv);
			Log.i("SuccessFullyy", "Data Inserted");
			res = "success";
			db.close();
			return res;
		} catch (Exception e) {
			db.endTransaction();
			errCode = "Error 900";
			msg = "GetData2 failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}
		return res;
	}

	public String addMltUOM(MLT_UOM u) {
		String res = "Error";
		try {
			ContentValues cv = new ContentValues();
			cv.put(KEY_ITEMNUMBER, u.getItemno());
			cv.put(KEY_UOM, u.getUom());
			db.insert(TABLE_MLT4, null, cv);
			res = "success";
			Log.i("SuccessFullyy", "Data Inserted");
			db.close();
			return res;
		} catch (Exception e) {
			db.endTransaction();
			errCode = "Error 902";
			msg = "GetData2 failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}
		return res;
	}

	public String addMltLocation(MLT_LOCATION l) {
		String res = "Error";
		try {
			ContentValues cv = new ContentValues();
			cv.put(KEY_LOCATION, l.getLocation());
			db.insert(TABLE_MLT5, null, cv);
			res = "success";
			Log.i("SuccessFullyy", "Data Inserted");
			db.close();
			return res;
		} catch (Exception e) {
			db.endTransaction();
			errCode = "Error 901";
			msg = "GetData2 failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}
		return res;
	}

	public String addMltItemDetails(MLT_Inventory i) {
		String res = "Error";
		try {
			ContentValues cv = new ContentValues();
			cv.put(KEY_ITEMNUMBER, i.getItemno());
			cv.put(KEY_DESCRIPTION, i.getDesc());
			db.insert(TABLE_MLT3, null, cv);
			res = "success";
			Log.i("SuccessFully", "Data Inserted");
			db.close();
			return res;
		} catch (Exception e) {
			db.endTransaction();
			errCode = "Error 903";
			msg = "GetData2 failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}
		return res;
	}

	public String addMlt_UOMINternal(MLT_UOMIntenal mlt) {
		String res = "Error";
		try {

			ContentValues cv = new ContentValues();
			cv.put(KEY_LOCATION, mlt.getLocation());
			cv.put(KEY_ITEMNUMBER, mlt.getItemno());
			cv.put(KEY_QTYCOUNTED, mlt.getQc());
			cv.put(KEY_UOM, mlt.getUom());
			db.insert(TABLE_MLT1, null, cv);
			res = "success";
			Log.i("Successfull", "Data Inserted");
			db.close();
			return res;
		} catch (Exception e) {
			db.endTransaction();
			errCode = "Error 900";
			msg = "GetData2 failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}
		return res;
	}

	public boolean checkMLT_Details(String item) {
		boolean flag = false;
		String q = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MLT7
				+ " WHERE " + DatabaseHandler.KEY_ITEMNUMBER + " = '" + item
				+ "'";

		int numRows = (int) DatabaseUtils.longForQuery(db, q, null);

		if (numRows > 0) {
			flag = true;
			return flag;
		} else {
			return flag;
		}

	}

	public boolean checkDoc() {
		String tabsql = "SELECT COUNT (*) FROM " + TABLE_MLT1;
		int numRows = (int) DatabaseUtils.longForQuery(db, tabsql, null);
		if (numRows > 0) {
			return true;
		}
		return false;
	}

	public int maxDocNo() {
		int doc_val = 1;
		String result = "";
		String tabsql = "SELECT COUNT (*) FROM " + TABLE_MLT1;

		int numRows = (int) DatabaseUtils.longForQuery(db, tabsql, null);

		if (numRows > 0) {

			Cursor cursorMax = db.query(TABLE_MLT1, new String[] { "MAX("
					+ KEY_DOCNUMBER + ")" }, null, null, null, null, null);
			try {
				cursorMax.moveToFirst();
				doc_val = cursorMax.getInt(0);

			}

			catch (NullPointerException ex) {
				db.endTransaction();
				errCode = "Error 5139";
				msg = "get_Temp_TABLE_Data failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);
				result = "error";
			} catch (Exception ex) {
				System.out.println("doc_val" + doc_val);
				db.endTransaction();
				errCode = "Error 5139pat";
				msg = "get_Temp_TABLE_Data failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);
				result = "error";
			} finally {
				cursorMax.close();
			}
		}
		return doc_val;
	}

	public String addMltHeader(MltHeader mltHeader) {
		int doc_val = 1;
		String result = "";
		String tabsql = "SELECT COUNT (*) FROM " + TABLE_MLT1;

		int numRows = (int) DatabaseUtils.longForQuery(db, tabsql, null);

		if (numRows > 0) {

			Cursor cursorMax = db.query(TABLE_MLT1, new String[] { "MAX("
					+ KEY_DOCNUMBER + ")" }, null, null, null, null, null);
			try {
				cursorMax.moveToFirst();
				doc_val = cursorMax.getInt(0);

			}

			catch (NullPointerException ex) {
				db.endTransaction();
				errCode = "Error 5139";
				msg = "get_Temp_TABLE_Data failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);
				result = "error";
			} catch (Exception ex) {
				System.out.println("doc_val" + doc_val);
				db.endTransaction();
				errCode = "Error 5139pat";
				msg = "get_Temp_TABLE_Data failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);
				result = "error";
			} finally {
				cursorMax.close();
			}
		} else {
			doc_val = 1;
		}
		try {
			try {
				ContentValues cv = new ContentValues();

				mltHeader.setDocNo(doc_val);
				int status = 0;
				cv.put(KEY_DOCNUMBER, mltHeader.getDocNo());
				cv.put(KEY_ADDCOST, mltHeader.getAddCost());
				cv.put(KEY_EXPECTEDARRDAY, mltHeader.getDay());
				cv.put(KEY_EXPECTEDARRMONTH, mltHeader.getMonth());
				cv.put(KEY_EXPECTEDARRYEAR, mltHeader.getYear());
				cv.put(KEY_DESCRIPTION, mltHeader.getDesc());
				cv.put(KEY_STATUS, status);
				String s = KEY_DOCNUMBER;
				System.out.println("Document No" + doc_val);
				db.insert(TABLE_MLT2, null, cv);
				Log.i("Success", "Data Inserted in table " + TABLE_MLT2);
				result = "success";

			} catch (Exception exe) {
				db.endTransaction();
				errCode = "Error 522";
				msg = "addMltHeader failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);
				result = "error";
				db.close();

			}

			Log.i("Success", "Data Inserted in table " + TABLE_MLT1);
		} catch (Exception exe) {
			db.endTransaction();
			errCode = "Error 51329";
			msg = "addMltHeader failed.";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			result = "error";

		}
		return result;

	}

	public List<String> getMltDistinctDocNo() {
		Cursor cursor_transdetails = null;
		List<String> docNo = null;

		String q = "SELECT DISTINCT " + KEY_DOCNUMBER + " FROM " + TABLE_MLT2;

		cursor_transdetails = db.rawQuery(q, null);
		if (cursor_transdetails.getCount() > 0) {
			docNo = new ArrayList<String>();
			while (cursor_transdetails.moveToNext()) {

				docNo.add(cursor_transdetails.getString(0));
			}
		}
		cursor_transdetails.close();
		return docNo;

	}

	public List<MltTrans> getMltDistinctDocNo_Trans() {
		Cursor cursor_transdetails = null;
		List<MltTrans> docNo = null;
		MltTrans mltTrans = null;
		String q = "SELECT DISTINCT " + KEY_DOCNUMBER + " FROM " + TABLE_MLT2
				+ " WHERE " + KEY_STATUS + "=0";

		cursor_transdetails = db.rawQuery(q, null);
		if (cursor_transdetails.getCount() > 0) {
			docNo = new ArrayList<MltTrans>();
			while (cursor_transdetails.moveToNext()) {
				mltTrans = new MltTrans();
				mltTrans.setStr1(cursor_transdetails.getString(0));
				docNo.add(mltTrans);
			}
		}
		cursor_transdetails.close();
		return docNo;

	}

	public Cursor getMltDocDetails(String docNo) {
		Cursor cursor = null;

		cursor = db.rawQuery("select * from " + TABLE_MLT1
				+ " where DocNumber = '" + docNo + "' ", null);

		return cursor;

	}

	public Integer getMltMaxDocInHeader() {
		String tabsql = "SELECT COUNT (*) FROM " + TABLE_MLT1;
		int doc = 0;
		int numRows = (int) DatabaseUtils.longForQuery(db, tabsql, null);

		if (numRows > 0) {

			Cursor cursorMax = db.query(TABLE_MLT1, new String[] { "MAX("
					+ KEY_DOCNUMBER + ")" }, null, null, null, null, null);
			try {
				cursorMax.moveToFirst();
				doc = cursorMax.getInt(0);

			}

			catch (NullPointerException ex) {
				db.endTransaction();
				errCode = "Error 5139";
				msg = "get_Temp_TABLE_Data failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);

			} catch (Exception ex) {

				db.endTransaction();
				errCode = "Error 5139pat";
				msg = "get_Temp_TABLE_Data failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);

			} finally {
				cursorMax.close();
			}
		}

		return doc;
	}

	public Integer getMltMaxDocInDetails() {
		String tabsql = "SELECT COUNT (*) FROM " + TABLE_MLT1;
		int doc = 0;
		int numRows = (int) DatabaseUtils.longForQuery(db, tabsql, null);

		if (numRows > 0) {

			Cursor cursorMax = db.query(TABLE_MLT2, new String[] { "MAX("
					+ KEY_DOCNUMBER + ")" }, null, null, null, null, null);
			try {
				cursorMax.moveToFirst();
				doc = cursorMax.getInt(0);

			}

			catch (NullPointerException ex) {
				db.endTransaction();
				errCode = "Error 5139";
				msg = "get_Temp_TABLE_Data failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);

			} catch (Exception ex) {

				db.endTransaction();
				errCode = "Error 5139pat";
				msg = "get_Temp_TABLE_Data failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);

			} finally {
				cursorMax.close();
			}
		}
		return doc;

	}

	public Cursor getMltHeader(String docNo) {
		Cursor cursor = null;
		cursor = db.rawQuery("select * from " + TABLE_MLT2
				+ " where DocNumber = '" + docNo + "' ", null);

		return cursor;

	}

	public List<MLT_Details> getmltdata() {
		Cursor c = null;
		List<MLT_Details> arr = new ArrayList<MLT_Details>();
		String ss = "SELECT MLT_INVENTORY.ItemNumber,MLT_INVENTORY.Description,"
				+ "TEMP_MLT.FromLoc,TEMP_MLT.ToLoc,TEMP_MLT.Qty,TEMP_MLT.UOM FROM "
				+ "MLT_INVENTORY LEFT OUTER JOIN TEMP_MLT ON MLT_INVENTORY.ItemNumber"
				+ "=TEMP_MLT.ItemNumber";
		c = db.rawQuery(ss, null);
		// ////////////////////////////////////////
		if (c.moveToFirst()) {
			do {
				MLT_Details mlt_Details = new MLT_Details();

				mlt_Details.setMlt_itemno(c.getString(0));
				mlt_Details.setMlt_description(c.getString(1));
				mlt_Details.setMlt_from(c.getString(2));
				mlt_Details.setMlt_to(c.getString(3));
				mlt_Details.setMlt_qty(c.getString(4));
				mlt_Details.setMlt_uom(c.getString(5));
				arr.add(mlt_Details);

			} while (c.moveToNext());
		}
		c.close();
		db.close();
		return arr;
	}

	public List<MLT_Details> getmltdata_() {

		// TODO Auto-generated method stub
		Cursor c = null;
		List<MLT_Details> arr = new ArrayList<MLT_Details>();
		String ss = "SELECT  * FROM " + DatabaseHandler.TABLE_MLT3;
		// MLT_Details mlt_Details=new MLT_Details();
		c = db.rawQuery(ss, null);
		// ////////////////////////////////////////
		if (c.moveToFirst()) {
			do {
				MLT_Details mlt_Details = new MLT_Details();

				mlt_Details.setMlt_itemno(c.getString(1));
				mlt_Details.setMlt_description(c.getString(2));
				// mlt_Details.setMlt_from(c.getString(3));
				// mlt_Details.setMlt_to(c.getString(4));
				arr.add(mlt_Details);

			} while (c.moveToNext());
		}
		c.close();
		db.close();
		return arr;
	}

	public List<String> getMltAllDetails() {
		List<String> list = null;

		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_MLT5;

		Cursor cursor = db.rawQuery(selectQuery, null);// selectQuery,selectedArguments

		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			list = new ArrayList<String>();
			if (cursor.moveToFirst()) {
				do {
					list.add(cursor.getString(0));// adding 2nd column data
				} while (cursor.moveToNext());
			}
		}
		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return list;
	}

	public List<String> getMltAllUomDetails(String itemNo) {
		List<String> list = null;
		Cursor cursor;

		cursor = db.rawQuery(
				"select distinct UOM from MLT_INVUOM where ItemNumber = '"
						+ itemNo + "' ", null);
		String uomStr = "";

		try {
			if (cursor.getCount() > 0) {
				list = new ArrayList<String>();

				while (cursor.moveToNext()) {
					uomStr = cursor.getString(0);
					list.add(uomStr);

					// to swap ea uom to first position in list
					if (uomStr.equals("Ea.")) {
						int cIndex = list.indexOf(uomStr);
						Collections.swap(list, cIndex, 0);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errCode = "Error 616";
			msg = "UOM getting failed(Based on Item number).";
			errMsg = errCode + " : " + msg;
			LogfileCreator.appendLog(errMsg);
			db.close();
		}

		cursor.close();

		return list;
	}

	public List<String> getMltAllToDetails() {
		List<String> list = null;

		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_MLT5;

		Cursor cursor = db.rawQuery(selectQuery, null);// selectQuery,selectedArguments

		// looping through all rows and adding to list
		if (cursor.getCount() > 0) {
			list = new ArrayList<String>();
			if (cursor.moveToFirst()) {
				do {
					list.add(cursor.getString(0));// adding 2nd column data
				} while (cursor.moveToNext());
			}
		}
		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return list;
	}

	// //////////***** MLT ***** ///////////

	// MLT Item

	public MLT_Inventory getItemInventory(String Item) {

		Cursor cursor_Mlt = null;
		MLT_Inventory mlt_Item = null;

		try {

			String q = "SELECT * FROM " + DatabaseHandler.TABLE_MLT3
					+ " WHERE " + DatabaseHandler.KEY_ITEMNUMBER + " = '"
					+ Item + "'";
			cursor_Mlt = db.rawQuery(q, null);

			if (cursor_Mlt.moveToFirst()) {
				mlt_Item = new MLT_Inventory();
				mlt_Item.setItemno(cursor_Mlt.getString(1));

				mlt_Item.setDesc(cursor_Mlt.getString(2));

				return mlt_Item;
			} else {
				Log.i("No Data", "");
			}
		} catch (Exception e) {
			db.endTransaction();
			errCode = "Error 1000";
			msg = "getmltItemDetails failed.";
			errMsg = errCode + " : " + msg;
			db.close();
			LogfileCreator.appendLog(errMsg);
		}

		cursor_Mlt.close();

		return mlt_Item;

	}

	// MLT UPC

	public MLT_UPC getItemFromUpc_mlt(String upc) {
		Cursor cursor_Mlt = null;
		MLT_UPC mlt_upc = null;

		try {

			String q = "SELECT * FROM " + DatabaseHandler.TABLE_MLT6
					+ " WHERE " + DatabaseHandler.KEY_UPCCODE + " = '" + upc
					+ "'";
			cursor_Mlt = db.rawQuery(q, null);

			if (cursor_Mlt.moveToFirst()) {

				mlt_upc = new MLT_UPC();

				mlt_upc.setItemno(cursor_Mlt.getString(0));
				mlt_upc.setUpc(cursor_Mlt.getString(1));

				return mlt_upc;
			} else {
				Log.i("No Data", "");
			}
		} catch (Exception e) {
			db.endTransaction();
			errCode = "Error 1001";
			msg = "getmltUPCDetails failed.";
			errMsg = errCode + " : " + msg;
			db.close();
			cursor_Mlt.close();
			LogfileCreator.appendLog(errMsg);
		}

		cursor_Mlt.close();

		return mlt_upc;

	}

	public void deleteMltInv() {
		try {

			db.execSQL("DELETE FROM MLT_INVENTORY");
		} catch (Exception e) {

			Log.e("ERROR", e.getLocalizedMessage());
		}
	}

	public void deleteMltTempData() {
		try {
			db.execSQL("DELETE FROM TEMP_MLT");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void deleteMltLoc() {
		try {
			db.execSQL("DELETE FROM MLT_LOCATION");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void deleteMltUom() {
		try {
			db.execSQL("DELETE FROM MLT_INVUOM");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void deleteMltUPC() {
		try {
			db.execSQL("DELETE FROM MLT_UPC");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void deleteDoc(List<String> doc) {
		try {
			for (int i = 0; i < doc.size(); i++) {

				db.execSQL("DELETE FROM " + TABLE_MLT1 + " where DocNumber='"
						+ doc.get(i) + "'");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void updateMLT_Header(String doc, String trf) {
		try {

			try {

				ContentValues cv = new ContentValues();

				cv.put(KEY_TRFNUMBER, trf);
				cv.put(KEY_STATUS, 1);

				db.update(TABLE_MLT2, cv, KEY_DOCNUMBER + "= '" + doc + "' ",
						null);

				Log.i("Success", "Data Updated in table " + TABLE_MLT2);

			} catch (Exception exe) {
				db.endTransaction();
				errCode = "Error 5675";
				msg = "updateMLT DOC failed.";
				errMsg = errCode + " : " + msg;
				LogfileCreator.appendLog(errMsg);
				db.close();

			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public Cursor getTrfNo(String docNo) {
		// TODO Auto-generated method stub
		Cursor cursor_transdetails = null;
		String sqlquery = "SELECT COUNT(*) FROM " + DatabaseHandler.TABLE_MLT2
				+ " WHERE " + KEY_DOCNUMBER + " = '" + docNo + "' AND "
				+ KEY_TRFNUMBER + " = '" + docNo + "'";
		int numRows = (int) DatabaseUtils.longForQuery(db, sqlquery, null);

		if (numRows > 0) {
			String q = "SELECT " + KEY_TRFNUMBER + "  FROM "
					+ DatabaseHandler.TABLE_MLT2 + " WHERE " + KEY_DOCNUMBER
					+ " = '" + docNo + "' AND " + KEY_TRFNUMBER + " = '"
					+ docNo + "'";
			cursor_transdetails = db.rawQuery(q, null);

			if (cursor_transdetails != null) {
				cursor_transdetails.moveToFirst();
			}
		} else {

			String q = "SELECT MAX(" + KEY_TRFNUMBER + ")  FROM "
					+ DatabaseHandler.TABLE_MLT2 + " WHERE " + KEY_DOCNUMBER
					+ " = '" + docNo + "'"; // + KEY_STATUS + " =1";
			cursor_transdetails = db.rawQuery(q, null);

			if (cursor_transdetails != null) {
				cursor_transdetails.moveToFirst();
			}
		}

		return cursor_transdetails;

	}

	public void updateMLT_Header(List<String> doc) {
		try {
			for (int i = 0; i < doc.size(); i++) {
				String result = null;
				try {

					ContentValues cv = new ContentValues();

					cv.put(KEY_STATUS, 1);

					db.update(TABLE_MLT2, cv,
							KEY_DOCNUMBER + "= '" + doc.get(i) + "' ", null);

					Log.i("Success", "Data Updated in table " + TABLE_MLT2);
					result = "success";

				} catch (Exception exe) {
					db.endTransaction();
					errCode = "Error 5675";
					msg = "updateMLT DOC failed.";
					errMsg = errCode + " : " + msg;
					LogfileCreator.appendLog(errMsg);
					result = "error";
					db.close();

				}

			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public String deleteHead(List<String> doc) {
		String result="error";
		try {
			for (int i = 0; i < doc.size(); i++) {

				db.execSQL("DELETE FROM " + TABLE_MLT2 + " where DocNumber='"
						+ doc.get(i) + "'");
				db.execSQL("DELETE FROM " + TABLE_MLT1 + " where DocNumber='"
						+ doc.get(i) + "'");
			}
		result="success";
		} catch (Exception e) {
			result="error";
			System.out.println(e);
		}
		return result;
	}
	public String deleteExportedDetails(List<String> doc) {
		String result="error";
		try {
			for (int i = 0; i < doc.size(); i++) {
				String query="SELECT COUNT(*) FROM "+ TABLE_MLT2 + "WHERE DocNumber='" +  doc.get(i) + "' AND Status=1";
				int numRows = (int) DatabaseUtils.longForQuery(db, query, null);	
				if(numRows>0)
				{
					db.execSQL("DELETE FROM " + TABLE_MLT2 + " where DocNumber='"
							+ doc.get(i) + "'");
					db.execSQL("DELETE FROM " + TABLE_MLT1 + " where DocNumber='"
							+ doc.get(i) + "'");
				}
				result="success";		
			}
		
		} catch (Exception e) {
			result="error";
			System.out.println(e);
		}
		return result;
	}

	public void openWritableDatabase() {
		if (fpath.exists()) {

			db = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH

			+ File.separator + DATABASE_NAME, null,

			SQLiteDatabase.OPEN_READWRITE);
		}
		Log.i("Db opened", "Writable Db opened.");
	}

	// /////////////////////////////////////////////////////////
	public void closeDatabase() {
		this.db.close();
		Log.i("Db closed", "Db closed.");
	}

	// //////////////////////////////////////////////////////////////////
	public SQLiteDatabase getReadableDatabase() {

		if (fpath.exists()) {

			db = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH

			+ File.separator + DATABASE_NAME, null,

			SQLiteDatabase.OPEN_READONLY);
		}

		return db;
	}

	// ///////////////////////////////////////////////////
	public SQLiteDatabase getWritableDatabase() {
		if (fpath.exists()) {

			db = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH

			+ File.separator + DATABASE_NAME, null,

			SQLiteDatabase.OPEN_READWRITE);
		}

		return db;
	}

	// method for transactions
	public void mBeginTransaction() {
		this.db.beginTransaction();
	}

	public void mprtTransactionSuccess() {
		this.db.setTransactionSuccessful();
	}

	public void mEndTransaction() {
		this.db.endTransaction();
	}

	public boolean isDbOpen() {
		return db.isOpen();
	}

}
