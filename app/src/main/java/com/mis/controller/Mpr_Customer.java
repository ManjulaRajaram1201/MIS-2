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
import com.mis.database.DatabaseHandler;
import com.mis.mpr.model.MPR_MasterDetails;
import com.mis.mpr.model.MPR_OrderDetails;
import com.mis.mpr.model.MPR_Upc;
import com.mis.mpr.model.MPR_Vendor;
import com.mis.mpr.model.Manf_Number01_mpr;
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

public class Mpr_Customer extends AppBaseActivity {
	// variable declaration
	static final float LIST_VIEW_EMPTY_VIEW_SIZE = 0.26f;//26%

	private static final int HTTP_REQUEST_TIMEOUT = 120000;
	private RadioButton rbOrderNo, rbCutomerNo;
	private RadioGroup radio_grpCust;
	private Spinner spnCategory, spnFilter;
	private EditText edtValue;
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
	public String GetMPRData = "GetMPRDataResult";
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
		setContentView(R.layout.mpr_cutomer);

		// Toast
		LayoutInflater inflater = getLayoutInflater();
		toastLayout = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		toastText = (TextView) toastLayout.findViewById(R.id.text);

		registerBaseActivityReceiver();

	
		radio_grpCust = (RadioGroup) findViewById(R.id.mpr_radioImpBasedOn);
		rbOrderNo = (RadioButton) findViewById(R.id.mpr_radioPono);
		rbCutomerNo = (RadioButton) findViewById(R.id.mpr_radiovendorno);

		spnCategory = (Spinner) findViewById(R.id.mpr_spnCategory);
		spnFilter = (Spinner) findViewById(R.id.mpr_spnFilter);

		edtValue = (EditText) findViewById(R.id.mpr_edtValue);

		lstGetCutomer = (ListView) findViewById(R.id.mpr_lstGetVendor);
		lstGetOrder = (ListView) findViewById(R.id.mpr_lstGetPo);

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
	
		
		telephonemanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		databaseHandler = new DatabaseHandler(this);
		String[] fil1 = { "Starts with", "Contains" };
		ArrayAdapter<String> spin2 = new ArrayAdapter<String>(
				Mpr_Customer.this,R.layout.spinner_item, fil1);
		spnFilter.setAdapter(spin2);
		String[] cat1 = { "Number", "Exact" };
		ArrayAdapter<String> spin1 = new ArrayAdapter<String>(
				Mpr_Customer.this,R.layout.spinner_item, cat1);
		spnCategory.setAdapter(spin1);
		spnFilter.setOnItemSelectedListener(new OnItemSelectedListener() {

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

		spnCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

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

				Intent i = new Intent(Mpr_Customer.this, MprImportData.class);
				startActivity(i);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mprvendorimport, menu);
		// hint
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
		if (id == R.id.mprvendor_mnu_vendor) {
			new LoadCustomerData("cust").execute();

		} else if (id == R.id.mprvendor_mnu_po) {

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
							 * Toast.makeText(Mpr_Customer.this,
							 * "Please select the Vendors...",
							 * Toast.LENGTH_SHORT).show();
							 */
							toastText.setText("Please Select the Vendors..");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();
						}

					}
				} else {
					/*
					 * Toast.makeText(Mpr_Customer.this,
					 * "Please get the Vendor and try again!!",
					 * Toast.LENGTH_SHORT).show();
					 */
					toastText.setText("Please Get the Vendors and Try Again!!");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

		} else if (id == R.id.mprvendor_mnu_po_details) {

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
							 * Toast.makeText( Mpr_Customer.this,
							 * "Please select the Orders and try again!!",
							 * Toast.LENGTH_SHORT).show();
							 */
							toastText
									.setText("Please select the Orders and try again!!");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();
						}
					}
				} else {
					/*
					 * Toast.makeText(Mpr_Customer.this,
					 * "Please get the Orders and try again!!",
					 * Toast.LENGTH_SHORT).show();
					 */
					toastText.setText("Please Get the Orders and Try Again!!");
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

			Intent i = new Intent(Mpr_Customer.this, MprOrderList.class);
			i.putExtra("Came", 2);
			startActivity(i);

		}
		return super.onOptionsItemSelected(item);
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
			dialog = new ProgressDialog(Mpr_Customer.this);
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
			dialog.setMessage("Data Saved Successfully...");

			if (result.equals("success")) {
				dialog.setMessage("Saving Data...");
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

				MprOrderList.returnfrm = "true";

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
				databaseHandler.deleteTempPo();
				databaseHandler.closeDatabase();

	
				databaseHandler.getWritableDatabase();
				databaseHandler.addTempPo(selected_Ord);
				databaseHandler.closeDatabase();
				Intent i = new Intent(Mpr_Customer.this, MprOrderList.class);

				startActivity(i);

			} else {

				if (result.contains("time out".toString())) {
					/*
					 * Toast.makeText(Mpr_Customer.this, "Time Out! Please check the Server Path and try again",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Time Out! Please check the Server Path and try again");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else if (result.contains("Connection prob".toString())) {
					/*
					 * Toast.makeText( Mpr_Customer.this,
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
				} else if (result.contains("Data prob".toString())) {
					/*
					 * Toast.makeText(Mpr_Customer.this,
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
					 * Toast.makeText(Mpr_Customer.this,
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
					+ "/MISWCFService/Service.svc/GetMPRData";
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
						if (result.equals("success"))
							result = vendorDetails(completeOrder);

					} catch (Exception e) {

						result = "error";
					}

				} else {
					result = status;
				}
				return result;
			} catch (Exception e) {

				Toast.makeText(Mpr_Customer.this, e.getLocalizedMessage(),
						Toast.LENGTH_LONG).show();

				Log.e("ERROR", e.getLocalizedMessage());
				return "error";
			}
		}
		return result;
	}

	private String chkStatus(JSONObject completeOrder) {
		// TODO Auto-generated method stub
		String result = null;
		try {

			jarray = completeOrder.getJSONArray("OrderDetail");
			if (!jarray.isNull(0)) {
				JSONObject jobject2 = jarray.getJSONObject(0);
				String str = jobject2.getString("PONUMBER");

				if (str.contains("socTimeError".toString())
						|| str.contains("connTimeError".toString())) {
					result = "time out";

				} else if ((str.contains("clientsideError".toString()) || str
						.contains("hostconnError".toString()))
						&& result == null) {

					result = "Connection prob";

				} else if ((str.contains("parseExcep".toString())
						|| str.contains("io".toString())
						|| str.contains("jsonError".toString()) || str
							.contains("encoError".toString()))
						&& result == null) {
					result = "Data prob";

				}

			}
			jarray = completeOrder.getJSONArray("OrderMaster");
			if (!jarray.isNull(0)) {
				JSONObject jobject2 = jarray.getJSONObject(0);
				String str = jobject2.getString("PONUMBER");

				if (result == null
						&& (str.contains("socTimeError".toString()) || str
								.contains("connTimeError".toString()))) {
					result = "time out";

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
			jarray = completeOrder.getJSONArray("Vendor");
			if (!jarray.isNull(0)) {
				JSONObject jobject2 = jarray.getJSONObject(0);
				String str = jobject2.getString("VENDORID");

				if (result == null
						&& (str.contains("socTimeError".toString()) || str
								.contains("connTimeError".toString()))) {
					result = "time out";

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

			jarray = completeOrder.getJSONArray("UPC");
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

	private String getManfNum(JSONObject completeOrder) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			jarray = completeOrder.getJSONArray("ManufactureNumber");
			if (!jarray.isNull(0)) {
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject jobject2 = jarray.getJSONObject(i);
					Manf_Number01_mpr mpr_Manf = new Manf_Number01_mpr();
					mpr_Manf.setItemno(jobject2.getString("ITEMNO"));
					mpr_Manf.setMn(jobject2.getString("MANITEMNO"));
					String itno = mpr_Manf.getItemno();
					String ManfNo = mpr_Manf.getMn();

					databaseHandler.getReadableDatabase();
					if (databaseHandler.checkMpr_ManfDetails(itno, ManfNo)) {
						databaseHandler.closeDatabase();
						databaseHandler.getWritableDatabase();
						result = databaseHandler
								.updateMpr_ManfDetails(mpr_Manf);
						databaseHandler.closeDatabase();

					} else {
						databaseHandler.getWritableDatabase();
						result = databaseHandler.addMpr_ManfDetails(mpr_Manf);
						databaseHandler.closeDatabase();
					}
				}
			} else {
				result = "success";
			}
			jarray = null;

		} catch (Exception ex) {
			Toast.makeText(Mpr_Customer.this, ex.getLocalizedMessage(),
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
			jarray = completeOrder.getJSONArray("OrderDetail");
			if (!jarray.isNull(0)) {
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject jobject2 = jarray.getJSONObject(i);
					databaseHandler.getWritableDatabase();

					MPR_OrderDetails mpr_OrderDetails = new MPR_OrderDetails();
					mpr_OrderDetails.setPo_number(jobject2
							.getString("PONUMBER"));

					mpr_OrderDetails.setVd_code(jobject2.getString("VDCODE"));// (cursor.getString(1));

					mpr_OrderDetails.setItem_no(jobject2.getString("ITEMNO"));// Number(cursor.getString(0));

					mpr_OrderDetails.setDesc(jobject2.getString("DESC"));// (cursor.getString(1));//(cursor.getString(1));
					mpr_OrderDetails.setUom(jobject2.getString("ORDERUNIT"));
					mpr_OrderDetails.setOrdered_qty(jobject2
							.getInt("OQORDERED"));
					mpr_OrderDetails.setReceived_qty(jobject2
							.getInt("OQRECEIVED"));
					mpr_OrderDetails.setComments(jobject2.getString("COMMENT"));
					mpr_OrderDetails
							.setLoc_code(jobject2.getString("LOCATION"));
					mpr_OrderDetails.setLine_no(jobject2.getString("LINENO"));

					String OrdNo = mpr_OrderDetails.getPo_number();
					String itNo = mpr_OrderDetails.getItem_no();
					databaseHandler.closeDatabase();
					databaseHandler.getReadableDatabase();
					if (databaseHandler.checkMpr_OrderDetails(OrdNo, itNo)) {
						databaseHandler.closeDatabase();
						databaseHandler.getWritableDatabase();
						result = databaseHandler.updateMpr_OrderDetails(
								mpr_OrderDetails, "Imp");
						databaseHandler.closeDatabase();
					} else {
						databaseHandler.getWritableDatabase();
						result = databaseHandler
								.addMpr_OrderDetails(mpr_OrderDetails);
						databaseHandler.closeDatabase();
					}
				}
			} else {
				result = "success";
			}
			jarray = null;
		} catch (Exception ex) {
			Toast.makeText(Mpr_Customer.this, ex.getLocalizedMessage(),
					Toast.LENGTH_LONG).show();
			Log.e("ERROR", ex.getLocalizedMessage());
		}
		return result;
	}

	private String vendorDetails(JSONObject completeOrder) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			jarray = completeOrder.getJSONArray("Vendor");
			if (!jarray.isNull(0)) {
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject jobject2 = jarray.getJSONObject(i);
					databaseHandler.getWritableDatabase();
					MPR_Vendor mpr_Vendor = new MPR_Vendor();
					mpr_Vendor.setVendor_id(jobject2.getString("VENDORID"));
					mpr_Vendor.setVendor_name(jobject2.getString("SHORTNAME"));
					mpr_Vendor.setStreet1(jobject2.getString("TEXTSTRE1"));
					mpr_Vendor.setStreet2(jobject2.getString("TEXTSTRE2"));
					mpr_Vendor.setNamecity(jobject2.getString("NAMECITY"));
					mpr_Vendor.setCode_state(jobject2.getString("CODESTTE"));
					mpr_Vendor.setCode_country(jobject2.getString("CODEPSTL"));
					mpr_Vendor.setCode_pstl(jobject2.getString("CODECTRY"));
					mpr_Vendor.setPhone(jobject2.getString("TEXTPHON1"));
					databaseHandler.closeDatabase();

					String vid = mpr_Vendor.getVendor_id();

					databaseHandler.getReadableDatabase();
					if (databaseHandler.checkMpr_VendorDetails(vid)) {
						databaseHandler.closeDatabase();
						databaseHandler.getWritableDatabase();
						result = databaseHandler.updateMpr_Vendor(mpr_Vendor);
						databaseHandler.closeDatabase();
					} else {
						databaseHandler.getWritableDatabase();
						result = databaseHandler.addMpr_Vendor(mpr_Vendor);
						databaseHandler.closeDatabase();
					}
				}
			} else {
				result = "success";
			}
			jarray = null;
		} catch (Exception ex) {
			Toast.makeText(Mpr_Customer.this, ex.getLocalizedMessage(),
					Toast.LENGTH_LONG).show();
			Log.e("ERROR", ex.getLocalizedMessage());
		}
		return result;

	}

	private String getorderMaster_Db(JSONObject completeOrder) {
		// TODO Auto-generated method stub
		String result = null;
		try {
			jarray = completeOrder.getJSONArray("OrderMaster");
			if (!jarray.isNull(0)) {
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject jobject2 = jarray.getJSONObject(i);
					databaseHandler.getWritableDatabase();
					MPR_MasterDetails mpr_MasterDetails = new MPR_MasterDetails();

					mpr_MasterDetails.setPo_number(jobject2
							.getString("PONUMBER"));
					mpr_MasterDetails.setDate(Integer.parseInt(jobject2
							.getString("DATE").toString()));
					mpr_MasterDetails.setVd_code(jobject2.getString("VDCODE"));

					String OrdNo = mpr_MasterDetails.getPo_number();// etItemNumber();
					String CustNo = mpr_MasterDetails.getVd_code();
					databaseHandler.closeDatabase();
					databaseHandler.getReadableDatabase();
					if (databaseHandler.checkmprMaster_Details(OrdNo, CustNo)) {
						databaseHandler.closeDatabase();
						databaseHandler.getWritableDatabase();
						result = databaseHandler
								.updatempr_MasterDetails(mpr_MasterDetails);
						databaseHandler.closeDatabase();
					} else {
						databaseHandler.getWritableDatabase();
						result = databaseHandler
								.addMpr_MasterDetails(mpr_MasterDetails);
						databaseHandler.closeDatabase();
					}

				}
			} else {
				result = "success";
			}
			jarray = null;
		} catch (Exception ex) {
			Toast.makeText(Mpr_Customer.this, ex.getLocalizedMessage(),
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
			jarray = completeOrder.getJSONArray("UPC");
			if (!jarray.isNull(0)) {
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject jobject2 = jarray.getJSONObject(i);
					databaseHandler.getWritableDatabase();
					//
					MPR_Upc mpr_Upc = new MPR_Upc();
					mpr_Upc.setItem_number(jobject2.getString("ITEMNO"));
					mpr_Upc.setBar_code(jobject2.getString("UPCCODE"));

					String itNo = mpr_Upc.getItem_number();// etItemNumber();
					databaseHandler.closeDatabase();
					databaseHandler.getReadableDatabase();
					if (databaseHandler.checkmprUpc_Details(itNo)) {
						databaseHandler.closeDatabase();
						databaseHandler.getWritableDatabase();
						result = databaseHandler.updateMpr_UpcDetails(mpr_Upc);
						databaseHandler.closeDatabase();
					} else {
						databaseHandler.getWritableDatabase();
						result = databaseHandler.addMpr_UpcDetails(mpr_Upc);
						databaseHandler.closeDatabase();
					}

				}
			} else {
				result = "success";
			}
			jarray = null;
		} catch (Exception ex) {
			Toast.makeText(Mpr_Customer.this, ex.getLocalizedMessage(),
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
			dialog = new ProgressDialog(Mpr_Customer.this);
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
			dialog.setMessage("Getting Orders for Selection...");

			ArrayList<String> det_List = new ArrayList<String>();
			String result = "";
			if (based_on.equals("cust"))
				dialog.setMessage("Getting Vendors for Selection...");
			else
				dialog.setMessage("Getting Po for Selection...");

			try {
				MIS_Setting mis_setting = new MIS_Setting();
				databaseHandler.getReadableDatabase();
				mis_setting = databaseHandler.getSetting();
				databaseHandler.closeDatabase();
				String deviceId = mis_setting.getDeviceId();
				databaseHandler.getReadableDatabase();
				CompanyID = databaseHandler.LOAD_COMPANYID(deviceId);
				databaseHandler.closeDatabase();
				String getCategory = spnCategory
						.getItemAtPosition(
								spnCategory.getSelectedItemPosition())
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
						System.out.println("CUST..." + CustList);

						// CustList = Cust_Str(CustList);

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
							} else if (OrdList.contains("clientsideError"
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
						adapter = new Chk_Adapter(Mpr_Customer.this, custList);
						lstGetCutomer.setAdapter(adapter);
						mcheck_cust = true;
					}

					else {
						adapter.clear();
						adapter = new Chk_Adapter(Mpr_Customer.this, custList);
						lstGetCutomer.setAdapter(adapter);
					}
					/*
					 * Toast.makeText(Mpr_Customer.this,
					 * "Please Select Vendor to get Orders",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText
							.setText("Please Select Vendors to get there Purchase Order");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
					dialog.dismiss();
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

				/*
				 * //////////////////////////////////////////////////////
				 * 
				 * ////////////////////////PRANESH///////////////////////
				 * 
				 * Checking the attempt its first or not if first then directly
				 * assign the adapter to listview else clear it and assign.
				 * 
				 * //////////////////////////////////////////////////////
				 */

				if (mcheck_ord == false) {
					adapter_ord = new Chk_Adapter_Ord(Mpr_Customer.this,
							ordList);
					lstGetOrder.setAdapter(adapter_ord);
					mcheck_cust = true;
				}

				else {
					adapter_ord.clear();
					adapter_ord = new Chk_Adapter_Ord(Mpr_Customer.this,
							ordList);
					lstGetOrder.setAdapter(adapter_ord);
				}
				if (ordList.size() > 0) {
					toastText.setText("Order Imported Successfully");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				}
				/*
				 * Toast.makeText(Mpr_Customer.this,
				 * "Order Imported Successfully", Toast.LENGTH_LONG).show();
				 */else {
					toastText.setText("No Order for Selected Vendors");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				}
				/*
				 * Toast.makeText(Mpr_Customer.this,
				 * "No Order for Selected Vendors", Toast.LENGTH_LONG).show();
				 */
				dialog.dismiss();
				invalidateOptionsMenu();
			}

			else {
				if (Cust_Ord_List.isEmpty() && based_on.equals("cust")) {
					toastText.setText("Vendors Not Available");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
										
				
					custList = new ArrayList<Chk_Model>();
				
					
					adapter = new Chk_Adapter(
							Mpr_Customer.this, custList);
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
							Mpr_Customer.this, ordList);
					lstGetOrder.setAdapter(adapter_ord);
				}
				else if (Cust_Ord_List.contains("time out".toString())) {
					/*
					 * Toast.makeText(Mpr_Customer.this, "Time Out! Please check the Server Path and try again",
					 * Toast.LENGTH_LONG).show();
					 */

					toastText.setText("Time Out! Please check the Server Path and try again");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else if (Cust_Ord_List.contains("Connection prob".toString())) {
					/*
					 * Toast.makeText( Mpr_Customer.this,
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
					 * Toast.makeText(Mpr_Customer.this,
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
					 * Toast.makeText(Mpr_Customer.this,
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
			requestArray.add("CUSTOMERNUMBER");// 2

			if (spnCategory
					.getItemAtPosition(spnCategory.getSelectedItemPosition())
					.toString().toUpperCase().equals("EXACT")) {

				requestArray.add(spnCategory
						.getItemAtPosition(
								spnCategory.getSelectedItemPosition())
						.toString().toUpperCase());// 3
				requestArray.add(edtValue.getText().toString().trim());// 4

			} else {
				String value = edtValue.getText().toString().trim();
				String filter = spnFilter
						.getItemAtPosition(spnFilter.getSelectedItemPosition())
						.toString().toUpperCase();

				/*
				 * space means %20 thats why we need to insert %20 as hardcoded
				 * request
				 */
				if (filter.equals("STARTS WITH")) {
					filter = "STARTS%20WITH";
				}

				requestArray.add(spnCategory
						.getItemAtPosition(
								spnCategory.getSelectedItemPosition())
						.toString().toUpperCase().toString());// 3
				// 4th para array
				requestArray.add(filter + "," + value);

			}

			break;
		case 2:
			requestArray.add(CompanyID.toString().toUpperCase());// 1
			requestArray.add("ORDERNUMBER");// 2
			requestArray.add("CUSTOMERNUMBER");// 3

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
			requestArray.add("NUMBER");// 3

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
					+ "/MISWCFService/Service.svc/GetMPRData";
			databaseHandler.getReadableDatabase();
			CompanyID = databaseHandler.LOAD_COMPANYID(deviceId);
			databaseHandler.closeDatabase();
			String FulUrl = F_URL + "/" + request.get(0) + "/" + request.get(1)
					+ "/" + request.get(2) + "/" + request.get(3);

			System.out.println("UL" + FulUrl);
			try {
				if (based_on.equals("ord")) {
					arrpo = getOrder(FulUrl);

				} else {
					arrpo = getCustomer(FulUrl);
				}
			} catch (Exception e) {
				Toast.makeText(Mpr_Customer.this, e.getLocalizedMessage(),
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
				jarray = jobject.getJSONArray("OrderNumber");

				if (jarray != null) {
					for (int i = 0; i < jarray.length(); i++) {

						JSONObject jobject2 = jarray.getJSONObject(i);
						String str = jobject2.getString("PONUMBER");
						arrord.add(str);
					}
				}

			}
			return arrord;
		} catch (Exception e) {
			Log.i("Failure", "Caught Exception " + e);
		}
		return arrord;

	}

	private ArrayList<String> getCustomer(String ord_Url) {
		JSONObject jobject = doCustResponse(ord_Url);
		ArrayList<String> arrord = new ArrayList<String>();

		try {
			if (jobject != null) {
				jarray = jobject.getJSONArray("Vendor");

				if (jarray != null) {
					for (int i = 0; i < jarray.length(); i++) {
						JSONObject jobject2 = jarray.getJSONObject(i);
						String str = jobject2.getString("VENDORID");
						arrord.add(str);

					}
				}

			}
			return arrord;
		} catch (Exception e) {
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

	/*
	 * private JSONObject doResponse(String URL) { JSONObject jobject = null;
	 * 
	 * try { HttpClient httpclient = new DefaultHttpClient(); HttpResponse
	 * response = null;
	 * 
	 * HttpGet httpget = new HttpGet(URL); try { response =
	 * httpclient.execute(httpget); if (response != null) { HttpEntity
	 * responseEntity = response.getEntity(); int ContentLength = (int)
	 * responseEntity.getContentLength(); char[] buffer = new
	 * char[ContentLength]; InputStream stream = responseEntity.getContent();
	 * InputStreamReader reader = new InputStreamReader(stream); int hasRead =
	 * 0; while (hasRead < ContentLength) { hasRead += reader.read(buffer,
	 * hasRead, ContentLength - hasRead); } stream.close(); jobject = new
	 * JSONObject(new String(buffer)); jobject =
	 * jobject.getJSONObject(GetMPRData);
	 * 
	 * } } catch (Exception e) { Log.e("ERROR", e.getLocalizedMessage()); }
	 * return jobject; }
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
							.getJSONObject(GetMPRData);
				} catch (JSONException je) {
					
						result = "jsonError";
						try {
							innerObject.put("PONUMBER", result);
							outerArray.put(innerObject);
							outerObject.put("OrderDetail", outerArray);

							innerObject.put("PONUMBER", result);
							outerArray.put(innerObject);
							outerObject.put("OrderMaster", outerArray);

							innerObject.put("ITEMNO", result);
							outerArray.put(innerObject);
							outerObject.put("UPC", outerArray);

							innerObject.put("ITEMNO", result);
							outerArray.put(innerObject);
							outerObject.put("ManufactureNumber", outerArray);

							innerObject.put("VENDORID", result);
							outerArray.put(innerObject);
							outerObject.put("Vendor", outerArray);

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
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderDetail", outerArray);

					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("UPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);

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
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderDetail", outerArray);

					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("UPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} catch (ClientProtocolException e) {
				result = "clientsideError";
				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderDetail", outerArray);

					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("UPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (SocketTimeoutException e) {
				result = "socTimeError";
				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderDetail", outerArray);

					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("UPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ConnectTimeoutException e) {
				result = "connTimeError";
				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderDetail", outerArray);

					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("UPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			catch (HttpHostConnectException e) {
				result = "hostconnError";
				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderDetail", outerArray);

					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("UPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ParseException pe) {
				result = "parseExcep";
				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderDetail", outerArray);

					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("UPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException io) {
				result = "io";
				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderDetail", outerArray);

					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderMaster", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("UPC", outerArray);

					innerObject.put("ITEMNO", result);
					outerArray.put(innerObject);
					outerObject.put("ManufactureNumber", outerArray);

					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);

					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
							.getJSONObject(GetMPRData);
				} catch (JSONException je) {
					
						result = "jsonError";
						try {
							innerObject.put("VENDORID", result);
							outerArray.put(innerObject);
							outerObject.put("Vendor", outerArray);
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
					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);
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
					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} catch (ClientProtocolException e) {
				result = "clientsideError";
				try {
					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (SocketTimeoutException e) {
				result = "socTimeError";
				try {
					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ConnectTimeoutException e) {
				result = "connTimeError";
				try {
					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}catch (HttpHostConnectException e) {
				result = "hostconnError";
				try {
					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ParseException pe) {
				result = "parseExcep";
				try {
					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException io) {
				result = "io";
				try {
					innerObject.put("VENDORID", result);
					outerArray.put(innerObject);
					outerObject.put("Vendor", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else {
			result = "internetprob";
			try {
				innerObject.put("VENDORID", result);
				outerArray.put(innerObject);
				outerObject.put("Vendor", outerArray);
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
							.getJSONObject(GetMPRData);
				} catch (JSONException je) {
					
						result = "jsonError";
						try {
							innerObject.put("PONUMBER", result);
							outerArray.put(innerObject);
							outerObject.put("OrderNumber", outerArray);
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
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderNumber", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			/*HttpGet httpget = new HttpGet(URL);
			try {
				HttpParams httpParameters = httpget.getParams();
				// Set the timeout in milliseconds until a connection is
				// established.
				int timeoutConnection = 120000;
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT)
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 120000;
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);
				response = httpclient.execute(httpget);
				if (response != null) {
					HttpEntity responseEntity = response.getEntity();
					int ContentLength = (int) responseEntity.getContentLength();
					char[] buffer = new char[ContentLength];
					InputStream stream = responseEntity.getContent();
					InputStreamReader reader = new InputStreamReader(stream);
					int hasRead = 0;
					while (hasRead < ContentLength) {
						hasRead += reader.read(buffer, hasRead, ContentLength
								- hasRead);
					}
					stream.close();
					jobject = new JSONObject(new String(buffer));
					jobject = jobject.getJSONObject(GetMPRData);

				}*/

			}

			catch (UnsupportedEncodingException e) {
				result = "encoError";

				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderNumber", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} catch (ClientProtocolException e) {
				result = "clientsideError";
				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderNumber", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (SocketTimeoutException e) {
				result = "socTimeError";
				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderNumber", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ConnectTimeoutException e) {
				result = "connTimeError";
				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderNumber", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (HttpHostConnectException e) {
				result = "hostconnError";
				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderNumber", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ParseException pe) {
				result = "parseExcep";
				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderNumber", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException io) {
				result = "io";
				try {
					innerObject.put("PONUMBER", result);
					outerArray.put(innerObject);
					outerObject.put("OrderNumber", outerArray);
					jsonResponse = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else {
			result = "internetprob";
			try {
				innerObject.put("PONUMBER", result);
				outerArray.put(innerObject);
				outerObject.put("OrderNumber", outerArray);
				jsonResponse = outerObject;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return jsonResponse;
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

			Intent i = new Intent(Mpr_Customer.this, MprOrderList.class);
			i.putExtra("Came", 2);
			startActivity(i);

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();

	}
}
