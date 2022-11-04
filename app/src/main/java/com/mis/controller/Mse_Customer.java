package com.mis.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mobinventorysuit.R;
import com.mis.adapter.Chk_Adapter_Ord;
import com.mis.adapter.Chk_Model;
import com.mis.common.MIS_Setting;
import com.mis.common.AppBaseActivity;
import com.mis.controller.MseImportData.LoadOrder;
import com.mis.database.DatabaseHandler;
import com.mis.mse.model.MSE_MasterDetails;
import com.mis.mse.model.MSE_OrderDetails;
import com.mis.mse.model.MSE_Upc;
import com.mis.mse.model.Manf_Number01;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Mse_Customer extends AppBaseActivity {
	// variable declaration
	static final float LIST_VIEW_EMPTY_VIEW_SIZE = 0.26f;//26%
	private static final int HTTP_REQUEST_TIMEOUT = 120000;
	
	private RadioButton rbOrderNo, rbCutomerNo;
	private RadioGroup radio_grpCust;
	private Spinner m_Category, m_Filter;
	private EditText edtValue;

	private ArrayAdapter<CharSequence> m_FilterAdap;
	private ArrayAdapter<CharSequence> m_CategoryAdap;
	private ListView lstGetCutomer, lstGetOrder;
	private Chk_Adapter adapter;
	TextView toastText;
	View toastLayout;
	private Chk_Adapter_Ord adapter_ord;
	private Boolean mcheck_cust = false;
	private Boolean mcheck_ord = false;

	String CompanyID;
	String sel_ord = "";
	DatabaseHandler databaseHandler;
	public TelephonyManager telephonemanager;
	
	ArrayList<String> selected_Cust;
	ArrayList<String> selected_Ord;
	public String GetMSEData = "GetMSEDataResult";
	JSONArray jarray = null;
	ArrayList<String> CustList = new ArrayList<String>();
	ArrayList<Chk_Model> custList;
	ArrayList<String> new_OrdList = new ArrayList<String>();
	ArrayList<String> new_CustList = new ArrayList<String>();

	ArrayList<String> OrdList = new ArrayList<String>();
	ArrayList<Chk_Model> ordList;
	Boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mse_cutomer);

		registerBaseActivityReceiver();

		
		radio_grpCust = (RadioGroup) findViewById(R.id.mse_radioCustImpBasedOn);
		rbOrderNo = (RadioButton) findViewById(R.id.mse_radioCustOrderno);
		rbCutomerNo = (RadioButton) findViewById(R.id.mse_radioCustno);

		m_Category = (Spinner) findViewById(R.id.mse_spnCustCategory);
		m_Filter = (Spinner) findViewById(R.id.mse_spnCustFilter);

		edtValue = (EditText) findViewById(R.id.mse_edtCustValue);

		lstGetCutomer = (ListView) findViewById(R.id.mse_lstCustGetCustomer);
		lstGetOrder = (ListView) findViewById(R.id.mse_lstCustGetOrder);

		rbCutomerNo.setChecked(true);
		
		//For Listview height
				Display display = getWindowManager().getDefaultDisplay();
				Point screenSize = new Point();
				display.getSize(screenSize);
				
				View custGetCustomerEmptyView = findViewById(R.id.custGetCustomerEmptyView);
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)custGetCustomerEmptyView.getLayoutParams();
				params.height = (int) (screenSize.y * LIST_VIEW_EMPTY_VIEW_SIZE);
				custGetCustomerEmptyView.setLayoutParams(params);
				lstGetCutomer.setEmptyView(findViewById(R.id.custGetCustomerEmptyView));
				
				View ordGetOrderEmptyView = findViewById(R.id.custGetOrderEmptyView);
				LinearLayout.LayoutParams paramsord = (LinearLayout.LayoutParams)ordGetOrderEmptyView.getLayoutParams();
				paramsord.height = (int) (screenSize.y * LIST_VIEW_EMPTY_VIEW_SIZE);
				custGetCustomerEmptyView.setLayoutParams(paramsord);
				
				lstGetOrder.setEmptyView(findViewById(R.id.custGetOrderEmptyView));
			

		// Toast
		LayoutInflater inflater = getLayoutInflater();
		toastLayout = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		toastText = (TextView) toastLayout.findViewById(R.id.text);

		telephonemanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		databaseHandler = new DatabaseHandler(this);
		/*String[] fil_array = { "Starts with", "Contains", "Date" };
		m_FilterAdap = new CustomArrayAdapter<CharSequence>(this, fil_array);
		m_Filter.setAdapter(m_FilterAdap);

		String[] cat_array = { "Number", "Exact" };
		m_CategoryAdap = new CustomArrayAdapter<CharSequence>(this, cat_array);
		m_Category.setAdapter(m_CategoryAdap);*/
		String[] fil_array = { "Starts with", "Contains" };
		/*m_Filter=inflater.inflate(R.layout.spinner_item,
				(ViewGroup) findViewById(R.id.spinner1));*/
	
		ArrayAdapter<String> m_FilterAdap = new ArrayAdapter<String>(
				Mse_Customer.this, R.layout.spinner_item, fil_array);
		m_Filter.setAdapter(m_FilterAdap);
		
		
		/*List<String> fil_array=new ArrayList<String>();
		fil_array.add("Starts with");
		
		fil_array.add("Contains");
		ArrayAdapter<String> myAdapter = new CustomArrayAdapter(this, R.layout.spinner_item, fil_array);
		m_Filter.setAdapter(myAdapter);*/
		
		String[] cat_array = { "Number", "Exact" };
		ArrayAdapter<String> m_CategoryAdap = new ArrayAdapter<String>(
				Mse_Customer.this, R.layout.spinner_item, cat_array);
		m_Category.setAdapter(m_CategoryAdap);
		

		m_Filter.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				String item = (String) arg0.getItemAtPosition(arg2);
				((TextView) arg0.getChildAt(0)).setTextColor(Color
						.parseColor("#000000"));
				((TextView) arg0.getChildAt(0)).setTextSize(20);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		m_Category.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				String item = (String) arg0.getItemAtPosition(arg2);
				((TextView) arg0.getChildAt(0)).setTextColor(Color
						.parseColor("#000000"));
				((TextView) arg0.getChildAt(0)).setTextSize(20);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		rbOrderNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent i = new Intent(Mse_Customer.this, MseImportData.class);
				startActivity(i);

			}
		});

		
	}

	/*
	 * //////////////////////////////////////////////////////
	 * 
	 * ////////////////////////PRANESH///////////////////////
	 * 
	 * 
	 * This Asynctask used to get all the order details that was selected
	 * 
	 * 
	 * 
	 * //////////////////////////////////////////////////////
	 */

	class LoadFullDetails extends AsyncTask<String, String, String> {
		ProgressDialog dialog;
		Context context;
		String result;

		public LoadFullDetails() {
			dialog = new ProgressDialog(Mse_Customer.this);
			dialog.setCancelable(false);

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Loading Data Started...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				dialog.setMessage("Please wait until the Saving Process Completes...");

				String val = getFullDetails(BuildRequestString(3, false, null));
				result = val;
				return result;
			} catch (Exception e) {
				dialog.dismiss();
				Log.i("Error while saving data", "Error" + e);
				result = "error";
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// ArrayList<Po_Model> poList = new ArrayList<Po_Model>();
			if (result.equals("success")) {
				dialog.setMessage("Data Saved Successfully...");

				dialog.dismiss();

				/*
				 * //////////////////////////////////////////////////////
				 * 
				 * ////////////////////////PRANESH///////////////////////
				 * 
				 * 
				 * The below below MseOrderList.returnfrm set to true since in
				 * the OrderList Screen we know that we are coming from
				 * MseCustomer.
				 * 
				 * 
				 * 
				 * //////////////////////////////////////////////////////
				 */

				MseOrderList.returnfrm = "true";

				/*
				 * //////////////////////////////////////////////////////
				 * 
				 * ////////////////////////PRANESH///////////////////////
				 * 
				 * 
				 * Down below we are creating one Set and putting all the
				 * selectedorders since its easy to put Set in a
				 * SharedPreference.
				 * 
				 * 
				 * 
				 * //////////////////////////////////////////////////////
				 */

				
	
				databaseHandler.getWritableDatabase();
				databaseHandler.deleteTempOrd();
				databaseHandler.closeDatabase();

	
				databaseHandler.getWritableDatabase();
				databaseHandler.addTempOrd(selected_Ord);
				databaseHandler.closeDatabase();

				Intent i = new Intent(Mse_Customer.this, MseOrderList.class);

				startActivity(i);

			} else {

				if (result.contains("time out".toString())) {
					/*
					 * Toast.makeText(Mse_Customer.this, "Time Out! Please check the Server Path and try again",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Time Out! Please check the Server Path and try again!!");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else if (result.contains("Connection prob".toString())) {
					/*
					 * Toast.makeText( Mse_Customer.this,
					 * "Problem while establishing connection with Server",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText
							.setText("Problem while establishing connection with Server");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else if (result.contains("Internet Prob".toString())) {
					/*
					 * Toast.makeText( Mse_Customer.this,
					 * "Problem while establishing connection with Server",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Check Your Internet Connectivity");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else if (result.contains("Data prob".toString())) {
					/*
					 * Toast.makeText(Mse_Customer.this,
					 * "Improper Format of Data", Toast.LENGTH_LONG) .show();
					 */
					toastText.setText("Improper Format of Data");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				}

				else {
					/*
					 * Toast.makeText(Mse_Customer.this,
					 * "Order Not Imported Successfully",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Order Not Imported Successfully");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				}
			}

		}
	}

	public String getFullDetails(ArrayList<String> request) {
		JSONObject completeOrder;
		String result = "error";
		if (!request.isEmpty()) {
			MIS_Setting mis_setting = new MIS_Setting();

			databaseHandler.getReadableDatabase();
			mis_setting = databaseHandler.getSetting();
			databaseHandler.closeDatabase();

			String deviceId = mis_setting.getDeviceId();
			String IpAddress = mis_setting.getIpAddress();

			String F_URL = "http://" + IpAddress
					+ "/MISWCFService/Service.svc/GetMSEData";
			databaseHandler.getReadableDatabase();
			CompanyID = databaseHandler.LOAD_COMPANYID(deviceId);
			databaseHandler.closeDatabase();
			String FulUrl = F_URL + "/" + request.get(0) + "/" + request.get(1)
					+ "/" + request.get(2) + "/" + request.get(3);
			System.out.println("FULL ADDRESS" + FulUrl);
			try {
				completeOrder = doCompleteResponse(FulUrl);

				String status = chkStatus(completeOrder);
				if (completeOrder != null && status.equals("Allow")) {
					try {
						result = getorderDetail_Db(completeOrder);

						if (result.equals("success"))
							result = getuPC_Db(completeOrder);

						if (result.equals("success"))
							result = getorderMaster_Db(completeOrder);

						if (result.equals("success"))
							result = getManfNum(completeOrder);

					} catch (Exception e) {
						result = "error";
					}

				} else {
					result = status;
				}
				return result;
			} catch (Exception e) {

				Log.e("ERROR", e.getLocalizedMessage());
				return result;
			}
		}
		return result;
	}

	private String chkStatus(JSONObject completeOrder) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			jarray = completeOrder.getJSONArray("FetchOrderDetail");
			if (!jarray.isNull(0)) {
				JSONObject jobject2 = jarray.getJSONObject(0);
				String str = jobject2.getString("ORDNUMBER");

				if (str.contains("socTimeError".toString())
						|| str.contains("connTimeError".toString())) {
					result = "time out";

				} else if ((str.contains("clientsideError".toString()) || str
						.contains("hostconnError".toString()))
						&& result == null) {

					result = "Connection prob";

				} else if ((str.contains("internetprob".toString()))
						&& result == null) {

					result = "Internet Prob";

				}

				else if ((str.contains("parseExcep".toString())
						|| str.contains("io".toString())
						|| str.contains("jsonError".toString()) || str
							.contains("encoError".toString()))
						&& result == null) {
					result = "Data prob";

				}

			}
			jarray = completeOrder.getJSONArray("FetchOrderMaster");
			if (!jarray.isNull(0)) {
				JSONObject jobject2 = jarray.getJSONObject(0);
				String str = jobject2.getString("ORDNUMBER");

				if (result == null
						&& (str.contains("socTimeError".toString()) || str
								.contains("connTimeError".toString()))) {
					result = "time out";

				} else if ((str.contains("internetprob".toString()))
						&& result == null) {

					result = "Internet Prob";

				} else if (result == null
						&& (str.contains("clientsideError".toString()) || str
								.contains("hostconnError".toString()))) {

					result = "Connection prob";

				} else if (result == null
						&& (str.contains("parseExcep".toString())
								|| str.contains("io".toString())
								|| str.contains("jsonError".toString()) || str
									.contains("encoError".toString()))) {
					result = "Data prob";

				}

			}

			jarray = completeOrder.getJSONArray("FetchUPC");
			if (!jarray.isNull(0)) {
				JSONObject jobject2 = jarray.getJSONObject(0);
				String str = jobject2.getString("ITEMNO");

				if (result == null
						&& (str.contains("socTimeError".toString()) || str
								.contains("connTimeError".toString()))) {
					result = "time out";

				} else if ((str.contains("internetprob".toString()))
						&& result == null) {

					result = "Internet Prob";

				} else if (result == null
						&& (str.contains("clientsideError".toString()) || str
								.contains("hostconnError".toString()))) {

					result = "Connection prob";

				} else if (result == null
						&& (str.contains("parseExcep".toString())
								|| str.contains("io".toString())
								|| str.contains("jsonError".toString()) || str
									.contains("encoError".toString()))) {
					result = "Data prob";

				}

			}

			jarray = completeOrder.getJSONArray("ManufactureNumber");
			if (!jarray.isNull(0)) {
				JSONObject jobject2 = jarray.getJSONObject(0);
				String str = jobject2.getString("ITEMNO");

				if (result == null
						&& (str.contains("socTimeError".toString()) || str
								.contains("connTimeError".toString()))) {
					result = "time out";

				} else if (result == null
						&& (str.contains("clientsideError".toString()) || str
								.contains("hostconnError".toString()))) {

					result = "Connection prob";

				} else if ((str.contains("internetprob".toString()))
						&& result == null) {

					result = "Internet Prob";

				} else if (result == null
						&& (str.contains("parseExcep".toString())
								|| str.contains("io".toString())
								|| str.contains("jsonError".toString()) || str
									.contains("encoError".toString()))) {
					result = "Data prob";

				}

			}
			if (result == null) {
				result = "Allow";
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	// Comment section is not there
	private String getManfNum(JSONObject completeOrder) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			jarray = completeOrder.getJSONArray("ManufactureNumber");
			if (!jarray.isNull(0)) {
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject jobject2 = jarray.getJSONObject(i);

					Manf_Number01 mse_Manf = new Manf_Number01();
					mse_Manf.setManuf_itemno(jobject2.getString("ITEMNO"));
					mse_Manf.setManuf_manitemno(jobject2.getString("MANITEMNO"));

					String manf = mse_Manf.getManuf_manitemno();
					String itNo = mse_Manf.getManuf_itemno();

					databaseHandler.getReadableDatabase();
					if (databaseHandler.checkMse_ManfDetails(itNo, manf)) {
						databaseHandler.closeDatabase();
						databaseHandler.getWritableDatabase();
						result = databaseHandler
								.updateMse_ManfDetails(mse_Manf);
						databaseHandler.closeDatabase();

					} else {
						databaseHandler.getWritableDatabase();
						result = databaseHandler.addMse_ManfDetails(mse_Manf);
						databaseHandler.closeDatabase();
					}
				}
			} else {
				result = "success";
			}
			jarray = null;

		} catch (Exception ex) {
			Toast.makeText(Mse_Customer.this, ex.getLocalizedMessage(),

			Toast.LENGTH_LONG).show();
			Log.e("ERROR", ex.getLocalizedMessage());
			result = "error";

		}
		return result;

	}

	// Comment section is not there
	private String getorderDetail_Db(JSONObject completeOrder) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			jarray = completeOrder.getJSONArray("FetchOrderDetail");
			if (!jarray.isNull(0)) {
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject jobject2 = jarray.getJSONObject(i);

					MSE_OrderDetails mse_OrderDetails = new MSE_OrderDetails();
					mse_OrderDetails.setOrdNumber(jobject2
							.getString("ORDNUMBER"));

					mse_OrderDetails.setCustNumber(jobject2
							.getString("CUSTOMER"));// (cursor.getString(1));

					mse_OrderDetails.setItemNumber(jobject2.getString("item"));// Number(cursor.getString(0));

					mse_OrderDetails.setItemDescription(jobject2
							.getString("DESC"));// (cursor.getString(1));//(cursor.getString(1));
					mse_OrderDetails.setUom(jobject2.getString("ORDUNIT"));
					mse_OrderDetails
							.setQtyOrdred(jobject2.getInt("QTYORDERED"));
					mse_OrderDetails
							.setQtyShiped(jobject2.getInt("QTYSHIPPED"));
					mse_OrderDetails
							.setLocation(jobject2.getString("LOCATION"));
					mse_OrderDetails.setShipViaCode(jobject2
							.getString("SHIPVIA"));
					mse_OrderDetails.setShipviaDescription(jobject2
							.getString("VIADESC"));
					mse_OrderDetails.setLineNumber((jobject2
							.getString("LINENUM")));
					mse_OrderDetails.setPickingSequence(jobject2
							.getString("PICKSEQ"));
					String OrdNo = mse_OrderDetails.getOrdNumber();
					String itNo = mse_OrderDetails.getItemNumber();

					databaseHandler.getReadableDatabase();
					if (databaseHandler.checkMse_OrderDetails(OrdNo, itNo)) {
						databaseHandler.closeDatabase();
						databaseHandler.getWritableDatabase();
						result = databaseHandler.updateMse_OrderDetails(
								mse_OrderDetails, "Imp");
						databaseHandler.closeDatabase();
					} else {
						databaseHandler.getWritableDatabase();
						result = databaseHandler
								.addMse_OrderDetails(mse_OrderDetails);
						databaseHandler.closeDatabase();
					}
				}
			} else {
				result = "success";
			}
			jarray = null;
		} catch (Exception ex) {
			Toast.makeText(Mse_Customer.this, ex.getLocalizedMessage(),
					Toast.LENGTH_LONG).show();
			Log.e("ERROR", ex.getLocalizedMessage());
		}
		return result;
	}

	private String getorderMaster_Db(JSONObject completeOrder) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			jarray = completeOrder.getJSONArray("FetchOrderMaster");
			if (!jarray.isNull(0)) {
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject jobject2 = jarray.getJSONObject(i);
					MSE_MasterDetails mse_MasterDetails = new MSE_MasterDetails();

					mse_MasterDetails.setOrdNumber(jobject2
							.getString("ORDNUMBER"));
					mse_MasterDetails.setOrdDate(jobject2.getString("ORDDATE"));
					mse_MasterDetails.setCustNumber(jobject2
							.getString("C_USTOMER"));
					mse_MasterDetails.setShiptoLocCode(jobject2
							.getString("SHIPTO"));
					mse_MasterDetails
							.setBillName(jobject2.getString("BILNAME"));
					mse_MasterDetails.setBillAddress1(jobject2
							.getString("BILADDR1"));
					mse_MasterDetails.setBillAddress2(jobject2
							.getString("BILADDR2"));
					mse_MasterDetails
							.setBillCity(jobject2.getString("BILCITY"));
					mse_MasterDetails.setBillState(jobject2
							.getString("BILSTATE"));
					mse_MasterDetails.setBillZip(jobject2.getString("BILZIP"));
					mse_MasterDetails.setBillCountry(jobject2
							.getString("BILCOUNTRY"));
					mse_MasterDetails.setBillPhone(jobject2
							.getString("BILPHONE"));
					mse_MasterDetails.setBillFax(jobject2.getString("BILFAX"));
					mse_MasterDetails.setShipToName(jobject2
							.getString("SHPNAME"));
					mse_MasterDetails.setShipToAddr1(jobject2
							.getString("SHPADDR1"));
					mse_MasterDetails.setShipToAddr2(jobject2
							.getString("SHPADDR2"));
					mse_MasterDetails.setShipToCity(jobject2
							.getString("SHPCITY"));
					mse_MasterDetails.setShipToState(jobject2
							.getString("SHPSTATE"));
					mse_MasterDetails
							.setShipToZip(jobject2.getString("SHPZIP"));
					mse_MasterDetails.setShipToCountry(jobject2
							.getString("SHPCOUNTRY"));
					mse_MasterDetails.setShipToPhone(jobject2
							.getString("SHPPHONE"));
					mse_MasterDetails
							.setShipToFax(jobject2.getString("SHPFAX"));

					String OrdNo = mse_MasterDetails.getOrdNumber();// etItemNumber();
					String CustNo = mse_MasterDetails.getCustNumber();

					databaseHandler.getReadableDatabase();
					if (databaseHandler.checkMaster_Details(OrdNo, CustNo)) {
						databaseHandler.closeDatabase();
						databaseHandler.getWritableDatabase();
						result = databaseHandler
								.updateMse_MasterDetails(mse_MasterDetails);
						databaseHandler.closeDatabase();
					} else {
						databaseHandler.getWritableDatabase();
						result = databaseHandler
								.addMse_MasterDetails(mse_MasterDetails);
						databaseHandler.closeDatabase();
					}

				}
			} else {
				result = "success";
			}
			jarray = null;
		} catch (Exception ex) {
			Toast.makeText(Mse_Customer.this, ex.getLocalizedMessage(),
					Toast.LENGTH_LONG).show();
			Log.e("ERROR", ex.getLocalizedMessage());
			result = "error";
		}
		return result;

	}

	private String getuPC_Db(JSONObject completeOrder) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			jarray = completeOrder.getJSONArray("FetchUPC");
			if (!jarray.isNull(0)) {
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject jobject2 = jarray.getJSONObject(i);

					MSE_Upc mse_Upc = new MSE_Upc();
					mse_Upc.setItemNo(jobject2.getString("ITEMNO"));
					mse_Upc.setUpcNo(jobject2.getString("upcData"));

					String itNo = mse_Upc.getItemNo();// etItemNumber();

					databaseHandler.getReadableDatabase();
					if (databaseHandler.checkUpc_Details(itNo)) {
						databaseHandler.closeDatabase();
						databaseHandler.getWritableDatabase();
						result = databaseHandler.updateMse_UpcDetails(mse_Upc);
						databaseHandler.closeDatabase();
					} else {
						databaseHandler.getWritableDatabase();
						result = databaseHandler.addMse_UpcDetails(mse_Upc);
						databaseHandler.closeDatabase();
					}

				}
			} else {
				result = "success";
			}
			jarray = null;
		} catch (Exception ex) {
			Toast.makeText(Mse_Customer.this, ex.getLocalizedMessage(),
					Toast.LENGTH_LONG).show();
			result = "error";
			Log.e("ERROR", ex.getLocalizedMessage());
		}
		return result;
	}

	/*
	 * //////////////////////////////////////////////////////
	 * 
	 * ////////////////////////PRANESH///////////////////////
	 * 
	 * 
	 * This Asyntask is used to get all the customer and respectiveOrder from
	 * Accpac.
	 * 
	 * It Depends on :- what is the request i.e
	 * 
	 * "cust" means get Customer Data "ord" means get all pending order
	 * 
	 * Returning an arrayList that contain cust nos./Orders
	 * //////////////////////////////////////////////////////
	 */

	class LoadCustomerData extends AsyncTask<String, String, ArrayList<String>> {

		ProgressDialog dialog;
		Context context;
		String based_on;

		public LoadCustomerData(String str) {
			dialog = new ProgressDialog(Mse_Customer.this);
			dialog.setCancelable(false);
			this.based_on = str;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Loading Data Started...");
			dialog.show();
		}

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			ArrayList<String> det_List = new ArrayList<String>();
			String result = "";
			//hint
			if (based_on.equals("cust"))
				dialog.setMessage("Getting Customers for Selection...");
			else
				dialog.setMessage("Getting Orders for Selection...");
			try {
				MIS_Setting mis_setting = new MIS_Setting();
				databaseHandler.getReadableDatabase();
				mis_setting = databaseHandler.getSetting();
				databaseHandler.closeDatabase();
				String deviceId = mis_setting.getDeviceId();
				databaseHandler.getReadableDatabase();
				CompanyID = databaseHandler.LOAD_COMPANYID(deviceId);
				databaseHandler.closeDatabase();
				String getCategory = m_Category
						.getItemAtPosition(m_Category.getSelectedItemPosition())
						.toString().toUpperCase();
				if (based_on.equals("cust")) {
					if (getCategory.equals("EXACT")) {

						CustList = getCustOrders(
								BuildRequestString(1, true, based_on), based_on);

						if (!CustList.equals(null)) {
							if (CustList.contains("socTimeError".toString())
									|| CustList.contains("connTimeError"
											.toString())) {
								result = "time out";
								CustList.clear();
								CustList.add(result);
							} else if (CustList.contains("internetprob"
									.toString())) {

								result = "Internet Prob";
								CustList.clear();
								CustList.add(result);
							} else if (CustList.contains("clientsideError"
									.toString())
									|| CustList.contains("hostconnError"
											.toString())) {

								result = "Connection prob";
								CustList.clear();
								CustList.add(result);
							} else if (CustList.contains("parseExcep"
									.toString())
									|| CustList.contains("io".toString())
									|| CustList
											.contains("jsonError".toString())
									|| CustList
											.contains("encoError".toString())) {
								result = "Data prob";
								CustList.clear();
								CustList.add(result);

							} else {
								result = "success";
								CustList.add(result);
							}
						}

					} else {
						CustList = getCustOrders(
								BuildRequestString(1, false, based_on),
								based_on);

						// CustList = getCustOrders(BuildRequestString(1, true,
						// based_on), based_on);

						if (!CustList.equals(null)) {
							if (CustList.contains("socTimeError".toString())
									|| CustList.contains("connTimeError"
											.toString())) {
								result = "time out";
								CustList.clear();
								CustList.add(result);
							} else if (CustList.contains("clientsideError"
									.toString())
									|| CustList.contains("hostconnError"
											.toString())) {

								result = "Connection prob";
								CustList.clear();
								CustList.add(result);
							} else if (CustList.contains("internetprob"
									.toString())) {

								result = "Internet Prob";
								CustList.clear();
								CustList.add(result);
							} else if (CustList.contains("parseExcep"
									.toString())
									|| CustList.contains("io".toString())
									|| CustList
											.contains("jsonError".toString())
									|| CustList
											.contains("encoError".toString())) {
								result = "Data prob";
								CustList.clear();
								CustList.add(result);

							} else {
								result = "success";
								CustList.add(result);
							}
						}

					}

					det_List = CustList;

					return CustList;
				} else if (based_on.equals("ord")) {
					if (getCategory.equals("EXACT")) {
						OrdList = getCustOrders(
								BuildRequestString(2, true, based_on), based_on);

						if (!OrdList.equals(null)) {
							if (OrdList.contains("socTimeError".toString())
									|| OrdList.contains("connTimeError"
											.toString())) {
								result = "time out";
								OrdList.clear();
								OrdList.add(result);
							} else if (OrdList.contains("internetprob"
									.toString())) {

								result = "Internet Prob";
								OrdList.clear();
								OrdList.add(result);
							}

							else if (OrdList.contains("clientsideError"
									.toString())
									|| OrdList.contains("hostconnError"
											.toString())) {

								result = "Connection prob";
								OrdList.clear();
								OrdList.add(result);
							} else if (OrdList
									.contains("parseExcep".toString())
									|| OrdList.contains("io".toString())
									|| OrdList.contains("jsonError".toString())
									|| OrdList.contains("encoError".toString())) {
								result = "Data prob";
								OrdList.clear();
								OrdList.add(result);

							} else {
								result = "success";
								OrdList.add(result);
							}
						}

					} else {
						OrdList = getCustOrders(
								BuildRequestString(2, false, based_on),
								based_on);
						if (!OrdList.equals(null)) {

							if (OrdList.contains("socTimeError".toString())
									|| OrdList.contains("connTimeError"
											.toString())) {
								result = "time out";
								OrdList.clear();
								OrdList.add(result);
							} else if (OrdList.contains("clientsideError"
									.toString())
									|| OrdList.contains("hostconnError"
											.toString())) {

								result = "Connection prob";
								OrdList.clear();
								OrdList.add(result);
							} else if (OrdList.contains("internetprob"
									.toString())) {

								result = "Internet Prob";
								OrdList.clear();
								OrdList.add(result);
							}

							else if (OrdList.contains("parseExcep".toString())
									|| OrdList.contains("io".toString())
									|| OrdList.contains("jsonError".toString())
									|| OrdList.contains("encoError".toString())) {
								result = "Data prob";
								OrdList.clear();
								OrdList.add(result);

							} else {
								result = "success";
								OrdList.add(result);
							}
						}
					}
					det_List = OrdList;
					return OrdList;
				}

			} catch (Exception ex) {
				dialog.dismiss();
				result = "error";
				if (based_on.equals("cust")) {
					CustList.add(result);
					det_List = CustList;
				} else {
					OrdList.add(result);
					det_List = OrdList;
				}
			}
			return det_List;

		}

		protected void onPostExecute(ArrayList<String> Cust_Ord_List) {
			dialog.setMessage("Inflating Data...");

			if (Cust_Ord_List.contains("success") && based_on.equals("cust")) {
				custList = new ArrayList<Chk_Model>();

				/*
				 * //////////////////////////////////////////////////////
				 * 
				 * ////////////////////////PRANESH///////////////////////
				 * 
				 * 
				 * This loop is used to get all the customer and generate a list
				 * of type Chk_Model.
				 * 
				 * 
				 * //////////////////////////////////////////////////////
				 */

				for (int i = 0; i < Cust_Ord_List.size() - 1; i++) {
					String str = Cust_Ord_List.get(i);
					Chk_Model pp = new Chk_Model(str, false);
					custList.add(pp);
				}
				// hint
				if (custList.size() == 0) {
					toastText.setText("Customers Not Available");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else {

					/*
					 * //////////////////////////////////////////////////////
					 * 
					 * ////////////////////////PRANESH///////////////////////
					 * 
					 * Checking the attempt its first or not if first then
					 * directly assign the adapter to listview else clear it and
					 * assign.
					 * 
					 * //////////////////////////////////////////////////////
					 */

					if (mcheck_cust == false) {
						adapter = new Chk_Adapter(Mse_Customer.this, custList);
						lstGetCutomer.setAdapter(adapter);
						mcheck_cust = true;
					}

					else {
						adapter.clear();
						adapter = new Chk_Adapter(Mse_Customer.this, custList);
						lstGetCutomer.setAdapter(adapter);
					}
					/*
					 * Toast.makeText(Mse_Customer.this,
					 * "Please Select Customer to get Orders",
					 * Toast.LENGTH_LONG).show();
					 */

					toastText
							.setText("Please Select the Customers to get Orders");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
//hint
					invalidateOptionsMenu();
				}
			} else if (Cust_Ord_List.contains("success")
					&& based_on.equals("ord")) {
				ordList = new ArrayList<Chk_Model>();

				/*
				 * //////////////////////////////////////////////////////
				 * 
				 * ////////////////////////PRANESH///////////////////////
				 * 
				 * 
				 * This loop is used to get all the Orders and generate a list
				 * of type Chk_Model.
				 * 
				 * 
				 * //////////////////////////////////////////////////////
				 */

				for (int i = 0; i < Cust_Ord_List.size() - 1; i++) {
					String str = Cust_Ord_List.get(i);
					Chk_Model pp = new Chk_Model(str, false);
					ordList.add(pp);
				}

				if (ordList.size() == 0) {
					toastText.setText("Orders Not Available");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else {

					/*
					 * //////////////////////////////////////////////////////
					 * 
					 * ////////////////////////PRANESH///////////////////////
					 * 
					 * Checking the attempt its first or not if first then
					 * directly assign the adapter to listview else clear it and
					 * assign.
					 * 
					 * //////////////////////////////////////////////////////
					 */

					if (mcheck_ord == false) {
						adapter_ord = new Chk_Adapter_Ord(Mse_Customer.this,
								ordList);
						lstGetOrder.setAdapter(adapter_ord);
						mcheck_cust = true;
					}

					else {
						adapter_ord.clear();
						adapter_ord = new Chk_Adapter_Ord(Mse_Customer.this,
								ordList);
						lstGetOrder.setAdapter(adapter_ord);
					}
					if (ordList.size() > 0) {
						/*
						 * Toast.makeText(Mse_Customer.this,
						 * "Order Imported Successfully",
						 * Toast.LENGTH_LONG).show();
						 */
						toastText.setText("Order Imported Successfully");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.setView(toastLayout);
						toast.show();
					} else {
						/*
						 * Toast.makeText(Mse_Customer.this,
						 * "No Order for Selected Customers",
						 * Toast.LENGTH_LONG).show();
						 */
						toastText.setText("No Order for Selected Customers");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.setView(toastLayout);
						toast.show();
					}
					dialog.dismiss();
					//hint
					invalidateOptionsMenu();
					
				}
			}

			else {
				if (Cust_Ord_List.isEmpty() && based_on.equals("cust")) {
					toastText.setText("Customers Not Available");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
										
				
					custList = new ArrayList<Chk_Model>();
				
					
					adapter = new Chk_Adapter(
							Mse_Customer.this, custList);
					lstGetCutomer.setAdapter(adapter);
					
					
				} else if (Cust_Ord_List.isEmpty() && based_on.equals("ord")) {
					toastText.setText("Orders Not Available");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
					
					ordList = new ArrayList<Chk_Model>();
				
					
					adapter_ord = new Chk_Adapter_Ord(
							Mse_Customer.this, ordList);
					lstGetOrder.setAdapter(adapter_ord);
				}


				else if (Cust_Ord_List.contains("time out".toString())) {
					/*
					 * Toast.makeText(Mse_Customer.this, "Time Out! Please check the Server Path and try again",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Time Out! Please check the Server Path and try again!!");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
					dialog.dismiss();
				} else if (Cust_Ord_List.contains("Connection prob".toString())) {
					/*
					 * Toast.makeText( Mse_Customer.this,
					 * "Problem while establishing connection with Server",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText
							.setText("Problem while establishing connection with Server!!");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
					dialog.dismiss();
				} else if (Cust_Ord_List.contains("Internet Prob".toString())) {
					/*
					 * Toast.makeText(MseImportData.this,
					 * "Improper Format of Data", Toast.LENGTH_LONG) .show();
					 */
					toastText.setText("Check Your Internet Connectivity");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
					dialog.dismiss();
				} else if (Cust_Ord_List.contains("Data prob".toString())) {
					/*
					 * Toast.makeText(Mse_Customer.this,
					 * "Improper Format of Data", Toast.LENGTH_LONG) .show();
					 */
					toastText.setText("Improper Format of Data");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else {
					/*
					 * Toast.makeText(Mse_Customer.this,
					 * "Order Not Imported Successfully",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Order Not Imported Successfully");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				}

			}
		}

		@Override
		protected void onProgressUpdate(String... progress) {
			super.onProgressUpdate(progress);

		}

	}

	/*
	 * //////////////////////////////////////////////////////
	 * 
	 * ////////////////////////PRANESH///////////////////////
	 * 
	 * 
	 * This Build Request is used to generate request url to consume the WCF
	 * service.
	 * 
	 * Case 1: For Customer
	 * 
	 * Case 2: For Orders
	 * 
	 * Case 3: For Full Details
	 * 
	 * We need to pass four params to consume WCF.
	 * 
	 * //////////////////////////////////////////////////////
	 */

	public ArrayList<String> BuildRequestString(int Type, boolean isExact,
			String based) {
		ArrayList<String> requestArray = new ArrayList<String>();
		MIS_Setting mis_setting = new MIS_Setting();
		databaseHandler.getReadableDatabase();
		mis_setting = databaseHandler.getSetting();
		databaseHandler.closeDatabase();
		String deviceId = mis_setting.getDeviceId();
		databaseHandler.getReadableDatabase();
		CompanyID = databaseHandler.LOAD_COMPANYID(deviceId);
		databaseHandler.closeDatabase();
		switch (Type) {
		case 1:

			requestArray.add(CompanyID.toString().toUpperCase());// 1
			requestArray.add("CUSTOMER");// 2

			if (m_Category
					.getItemAtPosition(m_Category.getSelectedItemPosition())
					.toString().toUpperCase().equals("EXACT")) {

				requestArray
						.add(m_Category
								.getItemAtPosition(
										m_Category.getSelectedItemPosition())
								.toString().toUpperCase());// 3
				requestArray.add(edtValue.getText().toString().trim());// 4

			} else {
				String value = edtValue.getText().toString().trim();
				String filter = m_Filter
						.getItemAtPosition(m_Filter.getSelectedItemPosition())
						.toString().toUpperCase();

				/*
				 * space means %20 thats why we need to insert %20 as hardcoded
				 * request
				 */
				if (filter.equals("STARTS WITH")) {
					filter = "STARTS%20WITH";
				}

				requestArray
						.add(m_Category
								.getItemAtPosition(
										m_Category.getSelectedItemPosition())
								.toString().toUpperCase().toString());// 3
				// 4th para array
				requestArray.add(filter + "," + value);

			}

			break;
		case 2:
			requestArray.add(CompanyID.toString().toUpperCase());// 1
			requestArray.add("ORDERNUMBER");// 2
			requestArray.add("CUSTOMERNUMBERS");// 3

			if (isExact) {
				String value = edtValue.getText().toString().toUpperCase()
						.trim();
				requestArray.add(value);// 4
			} else {

				String sel_cust = "";

				/*
				 * 
				 * Here the selected customer nos. should be separated by commas
				 * to generate the proper request format
				 */
				for (int i = 0; i < selected_Cust.size(); i++) {

					if (i != selected_Cust.size() - 1) {
						sel_cust = sel_cust + (selected_Cust.get(i) + ",");
					} else {
						sel_cust = sel_cust + (selected_Cust.get(i));
					}
				}
				requestArray.add(sel_cust);// 4

			}
			break;
		case 3:

			requestArray.add(CompanyID.toString().toUpperCase());// 1
			requestArray.add("COMPLETEORDER");// 2
			requestArray.add("ORDERNUMBER");// 3

			/*
			 * 
			 * Here the selected order nos. should be separated by commas to
			 * generate the proper request format
			 */

			for (int i = 0; i <= selected_Ord.size() - 1; i++) {

				if (i != selected_Ord.size() - 1) {
					sel_ord = sel_ord + (selected_Ord.get(i) + ",");
				} else {
					sel_ord = sel_ord + (selected_Ord.get(i));
				}
			}
			requestArray.add(sel_ord);// 4

		}
		return requestArray;

	}

	/*
	 * //////////////////////////////////////////////////////
	 * 
	 * ////////////////////////PRANESH///////////////////////
	 * 
	 * 
	 * Below method used to consume the WCF sevice based on the build request
	 * i.e Customers or Orders.
	 * 
	 * 
	 * //////////////////////////////////////////////////////
	 */

	public ArrayList<String> getCustOrders(ArrayList<String> request,
			String based_on) {

		ArrayList<String> arrpo = new ArrayList<String>();
		if (!request.isEmpty()) {
			MIS_Setting mis_setting = new MIS_Setting();
			databaseHandler.getReadableDatabase();
			mis_setting = databaseHandler.getSetting();
			databaseHandler.closeDatabase();
			String deviceId = mis_setting.getDeviceId();
			String ipAddress = mis_setting.getIpAddress();

			String F_URL = "http://" + ipAddress
					+ "/MISWCFService/Service.svc/GetMSEData";
			databaseHandler.getReadableDatabase();
			CompanyID = databaseHandler.LOAD_COMPANYID(deviceId);
			databaseHandler.closeDatabase();
			String FulUrl = F_URL + "/" + request.get(0) + "/" + request.get(1)
					+ "/" + request.get(2) + "/" + request.get(3);
			System.out.println("MSeCustomer.." + FulUrl);
			try {
				if (based_on.equals("ord")) {
					arrpo = getOrder(FulUrl);

				} else {
					arrpo = getCustomer(FulUrl);
				}
			} catch (Exception e) {
				Toast.makeText(Mse_Customer.this, e.getLocalizedMessage(),
						Toast.LENGTH_LONG).show();
				Log.e("ERROR", e.getLocalizedMessage());
			}
		}
		return arrpo;
	}

	private ArrayList<String> getOrder(String ord_Url) {
		JSONObject jobject = doOrdResponse(ord_Url);
		ArrayList<String> arrord = new ArrayList<String>();
		try {
			if (jobject != null) {
				//
				jarray = jobject.getJSONArray("orderNumbers");

				if (jarray != null) {
					for (int i = 0; i < jarray.length(); i++) {

						JSONObject jobject2 = jarray.getJSONObject(i);
						String str = jobject2.getString("ORDNUMBER");
						arrord.add(str);
					}
				}

			}
			return arrord;
		} catch (Exception e) {
			String str = "error";
			arrord.add(str);
			Log.i("Failure", "Caught Exception " + e);
		}
		return arrord;

	}

	private ArrayList<String> getCustomer(String ord_Url) {
		JSONObject jobject = doCustResponse(ord_Url);
		ArrayList<String> arrord = new ArrayList<String>();

		try {
			if (jobject != null) {
				jarray = jobject.getJSONArray("customerNumbers");

				if (jarray != null) {
					for (int i = 0; i < jarray.length(); i++) {
						JSONObject jobject2 = jarray.getJSONObject(i);
						String str = jobject2.getString("IDCUST");
						arrord.add(str);

					}
				}

			}
			return arrord;
		} catch (Exception e) {
			String str = "error";
			arrord.add(str);
			Log.i("Failure", "Caught Exception " + e);
		}
		return arrord;

	}

	/*
	 * //////////////////////////////////////////////////////
	 * 
	 * ////////////////////////PRANESH///////////////////////
	 * 
	 * 
	 * doResponse method is used to get response from WCF Service in JSON
	 * Format.
	 * 
	 * 
	 * //////////////////////////////////////////////////////
	 */

	private JSONObject doCompleteResponse(String Url) {

		JSONObject jsonResponse = null;
		String result = "";
		JSONObject outerObject = new JSONObject();
		JSONArray outerArray = new JSONArray();
		JSONObject innerObject = new JSONObject();

		// Http connections and data streams
		URL url;
		HttpURLConnection httpURLConnection = null;
		OutputStreamWriter outputStreamWriter = null;


		
		if (isConnected(this)) {
			try
			{
			// open connection to the server
			url = new URL(Url);
			httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(HTTP_REQUEST_TIMEOUT);  
			httpURLConnection.setReadTimeout(HTTP_REQUEST_TIMEOUT); 

			StringBuilder stringBuilder = new StringBuilder();
			int responseCode = httpURLConnection
					.getResponseCode();

			// Check to make sure we got a valid status response
			// from the server,
			// then get the server JSON response if we did.
			if (responseCode == HttpURLConnection.HTTP_OK) {

				// read in each line of the response to the
				// input
				// buffer
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(httpURLConnection
								.getInputStream(), "utf-8"));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line).append("\n");
				}

				bufferedReader.close(); // close out the input
										// stream

				try {
					// Copy the JSON response to a local
					// JSONObject
					jsonResponse = new JSONObject(stringBuilder
							.toString());
					jsonResponse = jsonResponse
							.getJSONObject(GetMSEData);
				} catch (JSONException je) {
					
						result = "jsonError";
						try {
							innerObject.put("ORDNUMBER", result);
							outerArray.put(innerObject);
							outerObject.put("FetchOrderDetail", outerArray);

							innerObject.put("ORDNUMBER", result);
							outerArray.put(innerObject);
							outerObject.put("FetchOrderMaster", outerArray);

							innerObject.put("ITEMNO", result);
							outerArray.put(innerObject);
							outerObject.put("FetchUPC", outerArray);

							innerObject.put("ITEMNO", result);
							outerArray.put(innerObject);
							outerObject.put("ManufactureNumber", outerArray);

							jsonResponse = outerObject;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
					 
					je.printStackTrace();
				}

			}
			else
			{
				result = "internetprob";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderDetail", outerArray);

					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("FetchUPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}



			catch (UnsupportedEncodingException e) {
				result = "encoError";

				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderDetail", outerArray);

					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("FetchUPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} catch (ClientProtocolException e) {
				result = "clientsideError";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderDetail", outerArray);

					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("FetchUPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (SocketTimeoutException e) {
				result = "socTimeError";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderDetail", outerArray);

					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("FetchUPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ConnectTimeoutException e) {
				result = "connTimeError";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderDetail", outerArray);

					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("FetchUPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (HttpHostConnectException e) {
				result = "hostconnError";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderDetail", outerArray);

					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("FetchUPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ParseException pe) {
				result = "parseExcep";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderDetail", outerArray);

					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("FetchUPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException io) {
				result = "io";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderDetail", outerArray);

					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("FetchOrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("FetchUPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else {
			result = "internetprob";
			try {
				innerObject.put("ORDNUMBER", result);
				outerArray.put(innerObject);
				outerObject.put("FetchOrderDetail", outerArray);

				innerObject.put("ORDNUMBER", result);
				outerArray.put(innerObject);
				outerObject.put("FetchOrderMaster", outerArray);

				innerObject.put("ITEMNO", result);
				outerArray.put(innerObject);
				outerObject.put("FetchUPC", outerArray);

				innerObject.put("ITEMNO", result);
				outerArray.put(innerObject);
				outerObject.put("ManufactureNumber", outerArray);

				jsonResponse = outerObject;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return jsonResponse;
	}

	private JSONObject doCustResponse(String Url) {

		JSONObject jsonResponse = null;
		String result = "";
		JSONObject outerObject = new JSONObject();
		JSONArray outerArray = new JSONArray();
		JSONObject innerObject = new JSONObject();

		// Http connections and data streams
		URL url;
		HttpURLConnection httpURLConnection = null;
		OutputStreamWriter outputStreamWriter = null;


		
		if (isConnected(this)) {
			try
			{
			// open connection to the server
			url = new URL(Url);
			httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(HTTP_REQUEST_TIMEOUT);  
			httpURLConnection.setReadTimeout(HTTP_REQUEST_TIMEOUT); 

			StringBuilder stringBuilder = new StringBuilder();
			int responseCode = httpURLConnection
					.getResponseCode();

			// Check to make sure we got a valid status response
			// from the server,
			// then get the server JSON response if we did.
			if (responseCode == HttpURLConnection.HTTP_OK) {

				// read in each line of the response to the
				// input
				// buffer
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(httpURLConnection
								.getInputStream(), "utf-8"));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line).append("\n");
				}

				bufferedReader.close(); // close out the input
										// stream

				try {
					// Copy the JSON response to a local
					// JSONObject
					jsonResponse = new JSONObject(stringBuilder
							.toString());
					jsonResponse = jsonResponse
							.getJSONObject(GetMSEData);
				} catch (JSONException je) {
					
						result = "jsonError";
						try {
							innerObject.put("IDCUST", result);
							outerArray.put(innerObject);
							outerObject.put("customerNumbers", outerArray);
							jsonResponse = outerObject;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					 
					je.printStackTrace();
				}

			}
			else
			{
				result = "internetprob";
				try {
					innerObject.put("IDCUST", result);
					outerArray.put(innerObject);
					outerObject.put("customerNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}


			catch (UnsupportedEncodingException e) {
				result = "encoError";

				try {
					innerObject.put("IDCUST", result);
					outerArray.put(innerObject);
					outerObject.put("customerNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} catch (ClientProtocolException e) {
				result = "clientsideError";
				try {
					innerObject.put("IDCUST", result);
					outerArray.put(innerObject);
					outerObject.put("customerNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (SocketTimeoutException e) {
				result = "socTimeError";
				try {
					innerObject.put("IDCUST", result);
					outerArray.put(innerObject);
					outerObject.put("customerNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ConnectTimeoutException e) {
				result = "connTimeError";
				try {
					innerObject.put("IDCUST", result);
					outerArray.put(innerObject);
					outerObject.put("customerNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (HttpHostConnectException e) {
				result = "hostconnError";
				try {
					innerObject.put("IDCUST", result);
					outerArray.put(innerObject);
					outerObject.put("customerNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ParseException pe) {
				result = "parseExcep";
				try {
					innerObject.put("IDCUST", result);
					outerArray.put(innerObject);
					outerObject.put("customerNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException io) {
				result = "io";
				try {
					innerObject.put("IDCUST", result);
					outerArray.put(innerObject);
					outerObject.put("customerNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else {
			result = "internetprob";
			try {
				innerObject.put("IDCUST", result);
				outerArray.put(innerObject);
				outerObject.put("customerNumbers", outerArray);
				jsonResponse = outerObject;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return jsonResponse;
	}

	private JSONObject doOrdResponse(String Url) {

		JSONObject jsonResponse = null;
		String result = "";
		JSONObject outerObject = new JSONObject();
		JSONArray outerArray = new JSONArray();
		JSONObject innerObject = new JSONObject();

		// Http connections and data streams
		URL url;
		HttpURLConnection httpURLConnection = null;
		OutputStreamWriter outputStreamWriter = null;

		if (isConnected(this)) {

			try
			{
			// open connection to the server
			url = new URL(Url);
			httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(HTTP_REQUEST_TIMEOUT);  
			httpURLConnection.setReadTimeout(HTTP_REQUEST_TIMEOUT); 

			StringBuilder stringBuilder = new StringBuilder();
			int responseCode = httpURLConnection
					.getResponseCode();

			// Check to make sure we got a valid status response
			// from the server,
			// then get the server JSON response if we did.
			if (responseCode == HttpURLConnection.HTTP_OK) {

				// read in each line of the response to the
				// input
				// buffer
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(httpURLConnection
								.getInputStream(), "utf-8"));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line).append("\n");
				}

				bufferedReader.close(); // close out the input
										// stream

				try {
					// Copy the JSON response to a local
					// JSONObject
					jsonResponse = new JSONObject(stringBuilder
							.toString());
					jsonResponse = jsonResponse
							.getJSONObject(GetMSEData);
				} catch (JSONException je) {
					
						result = "jsonError";
						try {
							innerObject.put("ORDNUMBER", result);
							outerArray.put(innerObject);
							outerObject.put("orderNumbers", outerArray);
							jsonResponse = outerObject;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					 
					je.printStackTrace();
				}

			}
			else
			{
				result = "internetprob";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("orderNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			}
			catch (UnsupportedEncodingException e) {
				result = "encoError";

				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("orderNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} catch (ClientProtocolException e) {
				result = "clientsideError";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("orderNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (SocketTimeoutException e) {
				result = "socTimeError";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("orderNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ConnectTimeoutException e) {
				result = "connTimeError";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("orderNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}  catch (HttpHostConnectException e) {
				result = "hostconnError";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("orderNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ParseException pe) {
				result = "parseExcep";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("orderNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException io) {
				result = "io";
				try {
					innerObject.put("ORDNUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("orderNumbers", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else {
			result = "internetprob";
			try {
				innerObject.put("ORDNUMBER", result);
				outerArray.put(innerObject);
				outerObject.put("orderNumbers", outerArray);
				jsonResponse = outerObject;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return jsonResponse;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.msecustimport, menu);
//hint
		if (CustList.size() == 0) {
			menu.getItem(1).setEnabled(false);
			menu.getItem(2).setEnabled(false);
		} else {
			if (OrdList.size() == 0) {
				menu.getItem(1).setEnabled(true);
				menu.getItem(2).setEnabled(false);
			} else {
				menu.getItem(1).setEnabled(true);
				menu.getItem(2).setEnabled(true);
			}
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.msecust_mnu_cust) {
			new LoadCustomerData("cust").execute();
		} else if (id == R.id.msecust_mnu_order) {

			// TODO Auto-generated method stub
			try {
				if (!CustList.isEmpty()) {
					List<Chk_Model> ordList_Details = new ArrayList<Chk_Model>();

					/*
					 * //////////////////////////////////////////////////////
					 * 
					 * ////////////////////////PRANESH///////////////////////
					 * 
					 * 
					 * Get Order only works if we supply cust no to it. Here,
					 * From Chk_Adapter having instance "adapter" we are taking
					 * the value since it contains all the customer nos
					 * 
					 * 
					 * //////////////////////////////////////////////////////
					 */

					ordList_Details = adapter.Po;
					selected_Cust = new ArrayList<String>();

					/*
					 * //////////////////////////////////////////////////////
					 * 
					 * ////////////////////////PRANESH///////////////////////
					 * 
					 * 
					 * The below loop is to select all the customers that are
					 * selected
					 * 
					 * 
					 * 
					 * //////////////////////////////////////////////////////
					 */

					for (int i = 0; i < ordList_Details.size(); i++) {
						Chk_Model model = ordList_Details.get(i);

						if (model.isSelected()) {

							selected_Cust.add(model.getName());
						}
					}
					if (ordList_Details.size() > 0) {
						if (selected_Cust.size() > 0) {
							new LoadCustomerData("ord").execute();

						} else {
							/*
							 * Toast.makeText(Mse_Customer.this,
							 * "Please select the Customers...",
							 * Toast.LENGTH_SHORT).show();
							 */
							toastText.setText("Please Select the Customers..");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();
						}

					}
				} else {
					/*
					 * Toast.makeText(Mse_Customer.this,
					 * "Please get the Customer and try again!!",
					 * Toast.LENGTH_SHORT).show();
					 */
					toastText
							.setText("Please Get the Customer and Try Again!!");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

		} else if (id == R.id.msecust_mnu_order_details) {

			// TODO Auto-generated method stub
			try {
				if (!OrdList.isEmpty()) {
					List<Chk_Model> ordList_Details = new ArrayList<Chk_Model>();
					ordList_Details = adapter_ord.Po;
					selected_Ord = new ArrayList<String>();

					/*
					 * //////////////////////////////////////////////////////
					 * 
					 * ////////////////////////PRANESH///////////////////////
					 * 
					 * 
					 * The below loop is to select all the orders that are
					 * selected
					 * 
					 * 
					 * 
					 * //////////////////////////////////////////////////////
					 */
					for (int i = 0; i < ordList_Details.size(); i++) {
						Chk_Model model = ordList_Details.get(i);
						if (model.isSelected()) {

							selected_Ord.add(model.getName());

						}
					}
					if (ordList_Details.size() > 0) {
						if (selected_Ord.size() > 0) {
							new LoadFullDetails().execute();
						} else {
							/*
							 * Toast.makeText( Mse_Customer.this,
							 * "Please select the Orders and try again!!",
							 * Toast.LENGTH_SHORT).show();
							 */
							toastText
									.setText("Please Select the Orders and Try Again!!");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();

						}
					}
				} else {
					/*
					 * Toast.makeText(Mse_Customer.this,
					 * "Please get the Orders and try again!!",
					 * Toast.LENGTH_SHORT).show();
					 */
					toastText.setText("Please get the Orders and Try Again!!");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

		} else {
			Intent i = new Intent(Mse_Customer.this, MseOrderList.class);
			i.putExtra("Came", 2);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean isConnected(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent i = new Intent(Mse_Customer.this, MseOrderList.class);
			i.putExtra("Came", 2);
			startActivity(i);

			// exportCancelMethod();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();

	}
}
