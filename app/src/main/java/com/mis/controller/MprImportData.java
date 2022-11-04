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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class MprImportData extends AppBaseActivity {

	private EditText met_From;
	private EditText met_To;
	private static final int HTTP_REQUEST_TIMEOUT = 120000;
	private Spinner m_Category;
	private Spinner m_Filter;
	private EditText m_Value;
	private RadioButton m_RbtnOrder;
	private RadioButton m_RbtnCust;

	private RadioGroup radioImpBasedOn;
	ListView listOrder;
	private DatabaseHandler databaseHandler;
	JSONArray jarray = null;
	public String CompanyID;
	Chk_Adapter adapter;
	public String F_URL;
	Context context;
	public String GetMPRData = "GetMPRDataResult";
	ArrayList<String> OList_Str;// = new ArrayList<String>();
	ArrayList<Chk_Model> oList_Model;// = new ArrayList<Chk_Model>();
	private Boolean m_chkAttempt = false;

	ArrayList<String> selected_ord;

	public TelephonyManager telephonemanager;

	private DatePicker dpResult;
	private int year;
	private int month;
	private int day;
	TextView toastText;
	View toastLayout;
	static final int DATE_DIALOG_ID = 999;

	private DatePickerDialog fromDatePickerDialog;
	private DatePickerDialog toDatePickerDialog;
	private SimpleDateFormat dateFormatter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mpr_order_import);
		registerBaseActivityReceiver();
		
		
		// Toast
		LayoutInflater inflater = getLayoutInflater();
		toastLayout = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		toastText = (TextView) toastLayout.findViewById(R.id.text);
	
		m_Category = (Spinner) findViewById(R.id.spn_OrderImpCategory);
		m_Filter = (Spinner) findViewById(R.id.spn_OrderImpFilter);
		// met_From = (EditText) findViewById(R.id.edt_OrderImpFromDate);
		listOrder = (ListView) findViewById(R.id.lst_ImpPurOrder);
		// met_To = (EditText) findViewById(R.id.edt_OrderImpToDate);
		
		m_Value = (EditText) findViewById(R.id.edt_OrderImpValue);
		radioImpBasedOn = (RadioGroup) findViewById(R.id.radioImpBasedOn);
		m_RbtnOrder = (RadioButton) findViewById(R.id.radio_OrderImport);
		m_RbtnCust = (RadioButton) findViewById(R.id.radio_VendorImport);

		m_RbtnOrder.setChecked(true);

		databaseHandler = new DatabaseHandler(this);

		telephonemanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		String[] fil_array = { "Starts with", "Contains","Date" };
		ArrayAdapter<String> m_FilterAdap = new ArrayAdapter<String>(
				MprImportData.this, R.layout.spinner_item, fil_array);
		m_Filter.setAdapter(m_FilterAdap);
		String[] cat_array = { "Number", "Exact" };
		ArrayAdapter<String> m_CategoryAdap = new ArrayAdapter<String>(
				MprImportData.this, R.layout.spinner_item, cat_array);
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
		m_RbtnCust.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent i = new Intent(MprImportData.this, Mpr_Customer.class);
				startActivity(i);

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
		// ////**********************/////////////
		// code for date and time in edit text

		dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		met_From = (EditText) findViewById(R.id.edt_OrderImpFromDate);
		met_To = (EditText) findViewById(R.id.edt_OrderImpToDate);
		met_From.setText(dateFormatter.format(new Date()));
		met_To.setText(dateFormatter.format(new Date()));
		met_From.requestFocus();
		met_To.requestFocus();
		Calendar newCallender = Calendar.getInstance();
		fromDatePickerDialog = new DatePickerDialog(this,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub
						Calendar newDate = Calendar.getInstance();
						newDate.set(year, monthOfYear, dayOfMonth);
						met_From.setText(dateFormatter.format(newDate.getTime()));
					}

				}, newCallender.get(Calendar.YEAR), newCallender
						.get(Calendar.MONTH), newCallender
						.get(Calendar.DAY_OF_MONTH));
		// newCallender=Calendar.getInstance();
		toDatePickerDialog = new DatePickerDialog(this,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub
						Calendar newDate = Calendar.getInstance();
						newDate.set(year, monthOfYear, dayOfMonth);
						met_To.setText(dateFormatter.format(newDate.getTime()));
					}

				}, newCallender.get(Calendar.YEAR), newCallender
						.get(Calendar.MONTH), newCallender
						.get(Calendar.DAY_OF_MONTH));

		met_From.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
					fromDatePickerDialog.show();
				

			}
		});
		met_To.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				
					toDatePickerDialog.show();
				
			}
		});

		
		met_From.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(hasFocus){
			    	fromDatePickerDialog.show();
			    }
			   }
			});
		met_To.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(hasFocus){
			    	toDatePickerDialog.show();
			    }
			   }
			});
		// code for exit button

		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mprimport, menu);
		//hint
		if(OList_Str==null)
			menu.getItem(1).setEnabled(false);
		else
	     menu.getItem(1).setEnabled(true);
		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.mprimport_mnu_order)
		{
			new LoadOrder().execute();
		}
		else if(id==R.id.mprimport_mnu_order_details)
		{

			// TODO Auto-generated method stub
			// adapter=new Chk_Adapter(getApplicationContext(), poList);
			// System.out.println(OList_Str);]
			try {
				if (!OList_Str.isEmpty()) {
					// System.out.println("IF");

					List<Chk_Model> poList_Details = new ArrayList<Chk_Model>();
					poList_Details = adapter.Po;
					selected_ord = new ArrayList<String>();// [poList.size()];

					for (int i = 0; i < poList_Details.size(); i++) {
						Chk_Model model = poList_Details.get(i);
						if (model.isSelected()) {

							selected_ord.add(model.getName());

						}
					}

					if (poList_Details.size() > 0) {

						if (selected_ord.size() > 0) {
							// System.out.println("IF IF");
							Boolean flag[] = { false };

							new LoadOrderDetails().execute(flag);
						} else {
							// System.out.println("ELSE ELSE");
							/*Toast.makeText(MprImportData.this,
									"Please Check the Orders..",
									Toast.LENGTH_SHORT).show();*/
							toastText.setText("Please Check the Orders..");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0,
									410);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();

						}
					}

				} else {
					// System.out.println("Else");

				/*	Toast.makeText(MprImportData.this,
							"Please get the Orders. first and try again!!",
							Toast.LENGTH_SHORT).show();*/
					
					toastText.setText("Please Get the Orders First and Try Again!!");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0,
							410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				}
			} catch (NullPointerException e) {
			/*	Toast.makeText(MprImportData.this,
						"Please select the Orders..", Toast.LENGTH_SHORT)
						.show();
*/
				toastText.setText("Please Select the Orders..");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(toastLayout);
				toast.show();
			}
		
		}
		else
		{
			Intent i = new Intent(MprImportData.this,
					MprOrderList.class);
			i.putExtra("Came", 2);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}

	class LoadOrderDetails extends AsyncTask<Boolean, String, String> {
		ProgressDialog dialog;
		Context context;

		String result = null;

		Integer arrLength;
		String queryStr = null;
		String impMsg = null;

		public LoadOrderDetails() {
			dialog = new ProgressDialog(MprImportData.this);
			dialog.setCancelable(false);

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Loading Data Started...");
			dialog.show();
		}

		@Override
		protected String doInBackground(Boolean... isExact) {
			// TODO Auto-generated method stub
		try {
			dialog.setMessage("Please wait until the Saving Process Completes...");
			
			
				if (isExact[0]) {

					try {
						// return String success or not
						String val = getOrderDetails(BuildRequestString(2,
								true, false));
						result = val;
					} catch (Exception e) {
						dialog.dismiss();
						result = "error";
					}

				}

				else {

					try {
						String val = getOrderDetails(BuildRequestString(2,
								false, false));
						result = val;
					} catch (Exception e) {
						dialog.dismiss();
						result = "error";
					}

				}

				return result;
			} catch (Exception e) {
				Log.i("Error while saving data", "Error" + e);
				result = "error";
			}
			return result;
		}

		protected void onPostExecute(String result) {
			// ArrayList<Po_Model> poList = new ArrayList<Po_Model>();
			dialog.setMessage("Data Saved Successfully...");
			
			if (result.equals("success")) {
				dialog.setMessage("Data Saved Successfully...");

				dialog.dismiss();
				MprOrderList.returnfrm = "true";

				Set<String> ordset = new HashSet<String>();
				ordset.addAll(selected_ord);

		
			
				//For Trail Demo
				databaseHandler.getWritableDatabase();
				databaseHandler.deleteTempPo();
				databaseHandler.closeDatabase();
				
				//For Trail Demo
				databaseHandler.getWritableDatabase();
				databaseHandler.addTempPo(selected_ord);
				databaseHandler.closeDatabase();
				
				Intent i = new Intent(MprImportData.this, MprOrderList.class);

				startActivity(i);



			} else {

				if (result.contains("time out".toString())) {
					/*Toast.makeText(MprImportData.this, "Time Out! Please check the Server Path and try again",
							Toast.LENGTH_LONG).show();*/
					toastText.setText("Time Out! Please check the Server Path and try again");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0,
							410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else if (result.contains("Connection prob".toString())) {
		/*			Toast.makeText(
							MprImportData.this,
							"Problem while establishing connection with Server",
							Toast.LENGTH_LONG).show();
*/
					
					toastText.setText("Problem while Establishing Connection with Server");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0,
							410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
					dialog.dismiss();
				} else if (result.contains("Data prob".toString())) {
					/*Toast.makeText(MprImportData.this,
							"Improper Format of Data", Toast.LENGTH_LONG)
							.show();*/
					
					toastText.setText("Improper Format of Data");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0,
							410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} 
				else if (result.contains("Internet Prob".toString())) {
					/*
					 * Toast.makeText(MseImportData.this,
					 * "Improper Format of Data", Toast.LENGTH_LONG) .show();
					 */toastText.setText("Check Your Internet Connectivity");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
					dialog.dismiss();
				} 
				else {
					/*Toast.makeText(MprImportData.this,
							"Order Not Imported Successfully",
							Toast.LENGTH_LONG).show();*/
					
					toastText.setText("Order Not Imported Successfully");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0,
							410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				}}

		}
	}

	class LoadOrder extends AsyncTask<String, String, ArrayList<String>> {

		ProgressDialog dialog;
		Context context;

		public LoadOrder() {
			dialog = new ProgressDialog(MprImportData.this);
			dialog.setCancelable(false);

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
		dialog.setMessage("Getting Order Details...");
			String result = "";
			MIS_Setting mis_setting = new MIS_Setting();
			databaseHandler.getReadableDatabase();
			mis_setting = databaseHandler.getSetting();
			databaseHandler.closeDatabase();

			String deviceId = mis_setting.getDeviceId();
			databaseHandler.getReadableDatabase();
			CompanyID = databaseHandler.LOAD_COMPANYID(deviceId);
			databaseHandler.closeDatabase();
			try {
				String getCategory = m_Category
						.getItemAtPosition(m_Category.getSelectedItemPosition())
						.toString().toUpperCase();
				OList_Str = new ArrayList<String>();
				if (getCategory.equals("EXACT")) {
					OList_Str = getOrders(BuildRequestString(1, true, false));
					/*
					 * OList_Str = Order_Str(OList_Str);
					 */if (!OList_Str.equals(null)) {
							if (OList_Str.contains("socTimeError".toString())
									|| OList_Str.contains("connTimeError"
											.toString())) {
								result = "time out";
								OList_Str.clear();
								OList_Str.add(result);
							} 
							else if (OList_Str
									.contains("internetprob".toString())) {
								result = "Internet Prob";
								OList_Str.clear();
								OList_Str.add(result);
							}
							else if (OList_Str.contains("clientsideError"
									.toString())
									|| OList_Str.contains("hostconnError"
											.toString())) {

								result = "Connection prob";
								OList_Str.clear();
								OList_Str.add(result);
							} else if (OList_Str.contains("parseExcep".toString())
									|| OList_Str.contains("io".toString())
									|| OList_Str.contains("jsonError".toString())
									|| OList_Str.contains("encoError".toString())) {
								result = "Data prob";
								OList_Str.clear();
								OList_Str.add(result);

							} else {
								result = "success";
								OList_Str.add(result);
							}
						}
				} else {
					OList_Str = getOrders(BuildRequestString(1, false, false));
					System.out.println("ORDER1..." + OList_Str);
					if (!OList_Str.equals(null)) {
						if (OList_Str.contains("socTimeError".toString())
								|| OList_Str.contains("connTimeError"
										.toString())) {
							result = "time out";
							OList_Str.clear();
							OList_Str.add(result);
						} 
						else if (OList_Str
								.contains("internetprob".toString())) {
							result = "Internet Prob";
							OList_Str.clear();
							OList_Str.add(result);
						}
						else if (OList_Str.contains("clientsideError"
								.toString())
								|| OList_Str.contains("hostconnError"
										.toString())) {

							result = "Connection prob";
							OList_Str.clear();
							OList_Str.add(result);
						} else if (OList_Str.contains("parseExcep".toString())
								|| OList_Str.contains("io".toString())
								|| OList_Str.contains("jsonError".toString())
								|| OList_Str.contains("encoError".toString())) {
							result = "Data prob";
							OList_Str.clear();
							OList_Str.add(result);

						} else {
							result = "success";
							OList_Str.add(result);
						}
					}
				}

			} catch (Exception ex) {
				dialog.dismiss();
				result = "error";
				OList_Str.add(result);
			}

			return OList_Str;
		}

		// POList global n we r passing so chnace of chnaging
		@Override
		protected void onPostExecute(ArrayList<String> OList) {
			oList_Model = new ArrayList<Chk_Model>();

		dialog.setMessage("Inflating Order Details...");

			if (OList.contains(("success"))) {

				for (int i = 0; i < OList.size() - 1; i++) {
					String str = OList.get(i);
					Chk_Model pp = new Chk_Model(str, false);
					oList_Model.add(pp);
				}
				//hint
				if(oList_Model.size()==0)
				{
					toastText.setText("Orders Not Available");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				}
				if (m_chkAttempt == false) {
					adapter = new Chk_Adapter(MprImportData.this, oList_Model);
					listOrder.setAdapter(adapter);
					m_chkAttempt = true;
				}

				else {
					adapter.clear();
					adapter = new Chk_Adapter(MprImportData.this, oList_Model);
					listOrder.setAdapter(adapter);
				}
			/*	Toast.makeText(MprImportData.this, "Order Imported Successfully", Toast.LENGTH_LONG).show();*/
				toastText.setText("Order Imported Successfully");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0,
						410);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(toastLayout);
				toast.show();
				
				dialog.dismiss();
				
				invalidateOptionsMenu();
			}

			else {

				if (OList_Str.contains("time out".toString())) {
					/*Toast.makeText(MprImportData.this, "Time Out! Please check the Server Path and try again",
							Toast.LENGTH_LONG).show();*/
					toastText.setText("Time Out! Please check the Server Path and try again");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0,
							410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else if (OList_Str.contains("Connection prob".toString())) {
					/*Toast.makeText(
							MprImportData.this,
							"Problem while establishing connection with Server",
							Toast.LENGTH_LONG).show();
*/
					toastText.setText("Problem while establishing connection with Server");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0,
							410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
					
					dialog.dismiss();
				} 
				else if (OList_Str.contains("Internet Prob".toString())) {
					/*
					 * Toast.makeText( MseImportData.this,
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
				}
				else if (OList_Str.contains("Data prob".toString())) {
					/*Toast.makeText(MprImportData.this,
							"Improper Format of Data", Toast.LENGTH_LONG)
							.show();*/
					
					toastText.setText("Improper Format of Data");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0,
							410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else {
					/*Toast.makeText(MprImportData.this,
							"Order Not Imported Successfully",
							Toast.LENGTH_LONG).show();*/
					
					toastText.setText("Order Not Imported Successfully");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0,
							410);
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

	public ArrayList<String> BuildRequestString(int Type, boolean isExact,
			boolean isDate) {
		
		
		ArrayList<String> requestArray = new ArrayList<String>();
		MIS_Setting mis_setting = new MIS_Setting();
		databaseHandler.getReadableDatabase();
		mis_setting = databaseHandler.getSetting();
		String deviceID = mis_setting.getDeviceId();
		databaseHandler.closeDatabase();
		// .LOAD_COMPANYID(deviceID);
		databaseHandler.getReadableDatabase();
		CompanyID = databaseHandler.LOAD_COMPANYID(deviceID);
		databaseHandler.closeDatabase();
		switch (Type) {
		case 1:

			requestArray.add(CompanyID.toString().toUpperCase());// 1
			requestArray.add("ORDERNUMBER");// 2

			if (m_Category
					.getItemAtPosition(m_Category.getSelectedItemPosition())
					.toString().toUpperCase().equals("EXACT")) {

				requestArray
						.add(m_Category
								.getItemAtPosition(
										m_Category.getSelectedItemPosition())
								.toString().toUpperCase());// 3
				requestArray.add(m_Value.getText().toString().trim());// 4

			} else if (m_Filter
					.getItemAtPosition(m_Filter.getSelectedItemPosition())
					.toString().toUpperCase().equals("DATE")) {

				String fromDate = met_From.getText().toString();
				fromDate = formatDate(fromDate);
				System.out.println(fromDate);

				String toDate = met_To.getText().toString();
				toDate = formatDate(toDate);
				System.out.println(toDate);

				requestArray.add(m_Filter
						.getItemAtPosition(m_Filter.getSelectedItemPosition())
						.toString().toUpperCase());// 3
				// 4th para array
				requestArray.add(fromDate + "," + toDate);

			} else {
				String value = m_Value.getText().toString().trim();
				String filter = m_Filter
						.getItemAtPosition(m_Filter.getSelectedItemPosition())
						.toString().toUpperCase();

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
			requestArray.add("COMPLETEORDER");// 2
			requestArray.add("NUMBER");// 3

			if (isExact) {
				String value = m_Value.getText().toString().toUpperCase().trim();
				requestArray.add(value);// 4
			} else {
				String sel_ord = "";
				System.out.println(selected_ord.size());
				for (int i = 0; i < selected_ord.size(); i++) {

					if (i != selected_ord.size() - 1) {
						sel_ord = sel_ord + (selected_ord.get(i) + ",");
					} else {
						sel_ord = sel_ord + (selected_ord.get(i));
					}
				}
				requestArray.add(sel_ord);
			}

		}
		return requestArray;

	}

	private String formatDate(String gotDate) {
		String date1[] = gotDate.split("-");
		String fmtDate = "";
		for (int i = 0; i < date1.length; i++) {
			fmtDate = fmtDate + date1[i];
		}
		return fmtDate;

	}

	public ArrayList<String> getOrders(ArrayList<String> request) {
		
		ArrayList<String> arrord = new ArrayList<String>();
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

			System.out.println("F_URL" + F_URL);
			String FulUrl = F_URL + "/" + request.get(0) + "/" + request.get(1)
					+ "/" + request.get(2) + "/" + request.get(3);

			System.out.println("F_URL" + FulUrl);
			try {
				JSONObject jobject = doResponse(FulUrl);
				if (jobject != null) {
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
			}

			catch (Exception e) {

				System.out.println(e);
			}

		}
		return arrord;

	}

	public String getOrderDetails(ArrayList<String> request) {
		JSONObject completeOrder;
		String result = "error";
		
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

			System.out.println("Full" + FulUrl);
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

				}
				else
				{
					result =status;
				}
				return result;
			} catch (Exception e) {
				
				Toast.makeText(MprImportData.this, e.getLocalizedMessage(),
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
					String itno=mpr_Manf.getItemno();
					String ManfNo = mpr_Manf.getMn();
					
					
					databaseHandler.getReadableDatabase();
					if (databaseHandler.checkMpr_ManfDetails(itno,ManfNo)) {
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
			Toast.makeText(MprImportData.this, ex.getLocalizedMessage(),
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
			Toast.makeText(MprImportData.this, ex.getLocalizedMessage(),
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
						result = databaseHandler.updateMpr_Vendor(
								mpr_Vendor);
						databaseHandler.closeDatabase();
					} else {
						databaseHandler.getWritableDatabase();
						result = databaseHandler
								.addMpr_Vendor(mpr_Vendor);
						databaseHandler.closeDatabase();
					}
				}
			} else {
				result = "success";
			}
			jarray = null;
		} catch (Exception ex) {
			Toast.makeText(MprImportData.this, ex.getLocalizedMessage(),
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
			Toast.makeText(MprImportData.this, ex.getLocalizedMessage(),
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
			Toast.makeText(MprImportData.this, ex.getLocalizedMessage(),
					Toast.LENGTH_LONG).show();
			result = "error";
			Log.e("ERROR", ex.getLocalizedMessage());
		}
		return result;
	}

	private JSONObject doResponse(String Url) {

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

		return jsonResponse;
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = new Intent(MprImportData.this,
					MprOrderList.class);
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