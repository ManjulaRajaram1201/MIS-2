package com.mis.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mobinventorysuit.R;
import com.mis.adapter.MicListAdapter;
import com.mis.adapter.Mlt_sixTextviewAdapter;
import com.mis.common.AppBaseActivity;

import com.mis.common.LogfileCreator;
import com.mis.common.Login;
import com.mis.common.MIS_Setting;
import com.mis.common.MainActivity;
import com.mis.database.DatabaseHandler;
import com.mis.mic.model.MIC_OrderDetails;
import com.mis.mlt.model.MLT_Details;
import com.mis.controller.MltUpdate;
import com.mis.controller.InventoryCount.InflateList;
import com.mis.mlt.model.MLT_Inventory;
import com.mis.mlt.model.MLT_LOCATION;

import com.mis.mlt.model.MLT_UOM;
import com.mis.mlt.model.MLT_UPC;
import com.mis.mlt.model.MltHeader;
import com.mis.mlt.model.MltTrans;
import com.mis.mse.model.MSE_OrderDetails;

import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.Fragment.SavedState;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class MltMain extends AppBaseActivity {

	private RadioGroup radioScan;
	private EditText edtScan;
	private RadioButton radioItem;
	private RadioButton radioUpc;
	private static final int HTTP_REQUEST_TIMEOUT = 120000;
	View toastLayout;
	private String deviceID = "";
	// private Spinner From,To,UOM;
	TextView toastText;
	private EditText edtDate;
	private EditText edtAddCost;
	private EditText edtdesc;

	private ImageButton btndate;
	private DatePickerDialog mltDatePickerDialog;
	public String url;
	String result = "";
	public String CompanyID;
	private SimpleDateFormat dateFormatter;
	private String additionalCost = "";
	private String additionaldesc = "";
	public String GetMLTData = "GetMLTDataResult";
	JSONArray jarray;
	private ArrayAdapter<MLT_Details> adapter;
	private View mview;
	TelephonyManager telephonyManager;
	private DatabaseHandler databaseHandler;
	public ListView listview;
	int flag = 0;
	int came= 0;
	public List<String> list;
	private LayoutInflater mlayoutinflater;
	Cursor cursor;
	List<String> listt;
	MLT_Details mltdetails = new MLT_Details();
	List<MLT_Details> arr = new ArrayList<MLT_Details>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mlt_main);

		registerBaseActivityReceiver();

		databaseHandler = new DatabaseHandler(this);
		// Toast
		LayoutInflater inflater = getLayoutInflater();
		toastLayout = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		toastText = (TextView) toastLayout.findViewById(R.id.text);
		radioScan = (RadioGroup) findViewById(R.id.radioImpBasedOn);
		radioItem = (RadioButton) findViewById(R.id.radio_Item_Num);
		radioUpc = (RadioButton) findViewById(R.id.radio_Upc);
		edtScan = (EditText) findViewById(R.id.edt_mlt_Search);

		listview = (ListView) findViewById(R.id.lst_mltfull);

		edtDate = (EditText) findViewById(R.id.edtmltDate);
		edtAddCost = (EditText) findViewById(R.id.edtmltCost);
		edtdesc = (EditText) findViewById(R.id.edtmltDesc);
		btndate = (ImageButton) findViewById(R.id.imgmltdate);

		databaseHandler.getWritableDatabase();
		String pendingexpo = databaseHandler.checkMltTransPendingExpo();
		databaseHandler.closeDatabase();
		came = getIntent().getIntExtra("Came", 0);

		if (pendingexpo.equals("true") && came == 1) {
			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					MltMain.this);
			alertDialog.setTitle("Confirmation");
			alertDialog.setIcon(R.drawable.warning);
			alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							Intent i = new Intent(MltMain.this, MltExport.class);
							startActivity(i);

						}
					});
			alertDialog.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							new InflateList().execute();
						}
					});
			alertDialog.setMessage("Do you want to Export Pending Documents?");

			alertDialog.show();

		}
		else if(pendingexpo.equals("false") && came == 1)
		{
			new InflateList().execute();
		}

		dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

		additionalCost = getIntent().getStringExtra("AdditionalCost");
		additionaldesc = getIntent().getStringExtra("AdditionalDesc");

		edtAddCost.setText(additionalCost);
		edtdesc.setText(additionaldesc);
		flag = getIntent().getIntExtra("flag", 0);

		if (flag == 1) {
			new InflateList().execute();

		}

		edtDate.setText(dateFormatter.format(new Date()));

		Calendar newCallender = Calendar.getInstance();
		mltDatePickerDialog = new DatePickerDialog(this,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub
						Calendar newDate = Calendar.getInstance();
						newDate.set(year, monthOfYear, dayOfMonth);
						edtDate.setText(dateFormatter.format(newDate.getTime()));
					}

				}, newCallender.get(Calendar.YEAR), newCallender
						.get(Calendar.MONTH), newCallender
						.get(Calendar.DAY_OF_MONTH));

		btndate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mltDatePickerDialog.show();

			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			Button mplus;
			Button mminus;
			ArrayAdapter<String> tempAdap = new ArrayAdapter<String>(
					MltMain.this, android.R.layout.simple_spinner_item);
			private Button Ok, Cancel;
			private EditText medtQtyCount;
			private Spinner spnFrom, spnTo, spnUOM;
			ArrayList<String> alst;
			MLT_Details list_obj = new MLT_Details();
			String ItemNo = "";
			String fromLoc = "";
			String quantity = "";
			String toLoc = "";
			String uom = "";

			// public String ItemNo;
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {

				additionalCost = edtAddCost.getText().toString();

				additionaldesc = edtdesc.getText().toString();

				list_obj = (MLT_Details) listview.getItemAtPosition(pos);

				fromLoc = list_obj.getMlt_from();
				quantity = list_obj.getMlt_qty();
				toLoc = list_obj.getMlt_to();
				uom = list_obj.getMlt_uom();
				ItemNo = list_obj.getMlt_itemno();

				LayoutInflater li = LayoutInflater.from(MltMain.this);
				View promptsView = li.inflate(R.layout.mltupdate, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MltMain.this);
				alertDialogBuilder.setView(promptsView);

				final AlertDialog alertDialog = alertDialogBuilder.create();

				alst = new ArrayList<String>();
				Ok = (Button) promptsView.findViewById(R.id.btn_mltshipOk);
				Cancel = (Button) promptsView
						.findViewById(R.id.btn_mltshipCancel);
				spnFrom = (Spinner) promptsView.findViewById(R.id.spn_frmLoc);
				spnTo = (Spinner) promptsView.findViewById(R.id.spn_ToLoc);
				medtQtyCount = (EditText) promptsView
						.findViewById(R.id.edtmltCost);
				spnUOM = (Spinner) promptsView.findViewById(R.id.spn_Uom);
				mplus = (Button) promptsView.findViewById(R.id.btn_mltshipIncr);
				mminus = (Button) promptsView
						.findViewById(R.id.btn_mltshipDecr);

				tempAdap = getDataFromDB();
				spnFrom.setAdapter(tempAdap);

				if (!(fromLoc == null)) {
					spnFrom.setSelection(tempAdap.getPosition(fromLoc));
				}

				tempAdap = getDataToDB();
				spnTo.setAdapter(tempAdap);

				if (!(toLoc == null)) {
					spnTo.setSelection(tempAdap.getPosition(toLoc));
				}

				tempAdap = getUomdata();
				spnUOM.setAdapter(tempAdap);

				if (!(uom == null)) {
					spnUOM.setSelection(tempAdap.getPosition(uom));
				}

				if (!(quantity == null)) {
					medtQtyCount.setText(quantity);
				} else
					medtQtyCount.setText("1");

				mplus.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String value = medtQtyCount.getText().toString();
						if (!value.equals("")) {
							String a = medtQtyCount.getText().toString();
							int b = Integer.parseInt(a);
							b = b + 1;
							a = a.valueOf(b);
							medtQtyCount.setText(a);
						} else {
							/*
							 * Toast.makeText(getApplicationContext(),
							 * "Please enter the Starting value"
							 * ,Toast.LENGTH_SHORT).show();
							 */
							toastText
									.setText("Please enter the Starting value");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, -100);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();

						}
					}
				});
				mminus.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String value = medtQtyCount.getText().toString();
						if (!value.equals("")) {
							int c = Integer.parseInt(medtQtyCount.getText()
									.toString());
							c = c - 1;
							medtQtyCount.setText(new Integer(c).toString());
						} else {

							toastText
									.setText("Please enter the Starting value");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, -100);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();

						}
					}
				});
				spnFrom.setOnItemSelectedListener(new OnItemSelectedListener() {

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
				spnTo.setOnItemSelectedListener(new OnItemSelectedListener() {

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
				spnUOM.setOnItemSelectedListener(new OnItemSelectedListener() {

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
				Ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						if (spnFrom
								.getItemAtPosition(
										spnFrom.getSelectedItemPosition())
								.toString().equals("1")
								&& spnTo.getItemAtPosition(
										spnTo.getSelectedItemPosition())
										.toString().equals("1")) {

							toastText.setText(" Select different location");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, -100);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();
							/*
							 * Toast.makeText(MLT_Main.this,
							 * "Select different location ",
							 * Toast.LENGTH_SHORT).show();
							 */

						} else if (spnFrom
								.getItemAtPosition(
										spnFrom.getSelectedItemPosition())
								.toString().equals("2")
								&& spnTo.getItemAtPosition(
										spnTo.getSelectedItemPosition())
										.toString().equals("2")) {/*
																 * Toast.makeText
																 * (
																 * MLT_Main.this
																 * ,
																 * "Select different location "
																 * , Toast.
																 * LENGTH_SHORT
																 * ).show();
																 */
							toastText.setText(" Select different location");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, -100);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();

						} else if (spnFrom
								.getItemAtPosition(
										spnFrom.getSelectedItemPosition())
								.toString().equals("3")
								&& spnTo.getItemAtPosition(
										spnTo.getSelectedItemPosition())
										.toString().equals("3")) {
							/*
							 * Toast.makeText(MLT_Main.this,
							 * "Select different location ",
							 * Toast.LENGTH_SHORT).show();
							 */
							toastText.setText(" select diffrent location");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, -100);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();

						} else if (spnFrom
								.getItemAtPosition(
										spnFrom.getSelectedItemPosition())
								.toString().equals("4")
								&& spnTo.getItemAtPosition(
										spnTo.getSelectedItemPosition())
										.toString().equals("4")) {
							/*
							 * Toast.makeText(MLT_Main.this,
							 * "Select different location ",
							 * Toast.LENGTH_SHORT).show();
							 */
							toastText.setText(" select diffrent location");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, -100);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();

						} else if (spnFrom
								.getItemAtPosition(
										spnFrom.getSelectedItemPosition())
								.toString().equals("TRANS")
								&& spnTo.getItemAtPosition(
										spnTo.getSelectedItemPosition())
										.toString().equals("TRANS")) {
							/*
							 * Toast.makeText(MLT_Main.this,
							 * "Select different location ",
							 * Toast.LENGTH_SHORT).show();
							 */
							toastText.setText(" select diffrent location");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, -100);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();

						} else {

							String value = medtQtyCount.getText().toString();
							if (!value.equals("")) {
								String froml = spnFrom.getItemAtPosition(
										spnFrom.getSelectedItemPosition())
										.toString();
								String tol = spnTo.getItemAtPosition(
										spnTo.getSelectedItemPosition())
										.toString();
								String uoml = spnUOM.getItemAtPosition(
										spnUOM.getSelectedItemPosition())
										.toString();
								String qyt = medtQtyCount.getText().toString();

								MLT_Details mlt_details = new MLT_Details();
								mlt_details.setMlt_itemno(ItemNo);
								/* mlt_details.setMlt_description(Desc); */
								mlt_details.setMlt_from(froml);
								mlt_details.setMlt_to(tol);
								mlt_details.setMlt_qty(qyt);
								mlt_details.setMlt_uom(uoml);

								databaseHandler.getReadableDatabase();
								boolean flag = databaseHandler
										.checkMLT_Details(ItemNo);
								databaseHandler.closeDatabase();

								if (flag == false) {
									databaseHandler.getWritableDatabase();
									databaseHandler
											.addMLT_Temp_Table(mlt_details);
									databaseHandler.closeDatabase();
								} else {

									databaseHandler.getWritableDatabase();
									databaseHandler
											.updateMLT_Temp_Table(mlt_details);
									databaseHandler.closeDatabase();
								}

								Intent i = new Intent(MltMain.this,
										MltMain.class);
								i.putExtra("flag", 1);
								i.putExtra("AdditionalCost", additionalCost);
								i.putExtra("AdditionalDesc", additionaldesc);

								startActivity(i);
							} else {
								/*
								 * Toast.makeText(getApplicationContext(),
								 * "Please enter the Counted Qty value"
								 * ,Toast.LENGTH_SHORT).show();
								 */
								toastText
										.setText("Please enter the Counted Qty value");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										-100);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
							}

						}
					}

				});
				alertDialog.show();

				Cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent i = new Intent(getApplicationContext(),
								MltMain.class);
						/*
						 * handler.getWritableDatabase();
						 * handler.deleteMltTempData(); handler.closeDatabase();
						 */
						i.putExtra("flag", 1);
						startActivity(i);

					}
				});

				/*
				 * Intent i1 = new Intent(MLT_Main.this, mltupdate.class);
				 * i1.putExtra("ItemNumber", ItemNo); i1.putExtra("Description",
				 * Description); startActivity(i1);// , 1);
				 */
			}

			private ArrayAdapter<String> getDataFromDB() {
				// TODO Auto-generated method stub
				databaseHandler.getReadableDatabase();
				List<String> l = databaseHandler.getMltAllToDetails();
				databaseHandler.closeDatabase();

				// Creating adapter for spinner
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
						MltMain.this, android.R.layout.simple_spinner_item, l);

				// Drop down layout style - list view with radio button
				dataAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				// attaching data adapter to spinner

				return dataAdapter;
			}

			private ArrayAdapter<String> getDataToDB() {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				databaseHandler.getReadableDatabase();
				List<String> l = databaseHandler.getMltAllToDetails();
				databaseHandler.closeDatabase();

				// Creating adapter for spinner
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
						MltMain.this, android.R.layout.simple_spinner_item, l);

				// Drop down layout style - list view with radio button
				dataAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				// attaching data adapter to spinner
				// spnTo.setAdapter(dataAdapter);
				return dataAdapter;
			}

			private ArrayAdapter<String> getUomdata() {
				// TODO Auto-generated method stub

				// String ItemNo = getIntent().getStringExtra("ItemNumber");
				databaseHandler.getWritableDatabase();
				List<String> l = databaseHandler.getMltAllUomDetails(ItemNo);
				databaseHandler.closeDatabase();

				// Creating adapter for spinner
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
						MltMain.this, android.R.layout.simple_spinner_item, l);

				// Drop down layout style - list view with radio button
				dataAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				// attaching data adapter to spinner
				// spnUOM.setAdapter(dataAdapter);
				return dataAdapter;
			}

		});

		edtScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int radioButtonID = radioScan.getCheckedRadioButtonId();
				View radioClicked = radioScan.findViewById(radioButtonID);
				int idx = radioScan.indexOfChild(radioClicked);

				if (idx == 0) {
					// ItemNo
					try {

						Intent intent = new Intent(
								"com.google.zxing.client.android.SCAN");
						intent.putExtra("SCAN_MODE",
								"QR_CODE_MODE,PRODUCT_MODE");
						startActivityForResult(intent, 0);

					} catch (Exception e) {
						e.printStackTrace();

					}
				} else if (idx == 1) { // apr 26 barcode integration based on
										// Upc
					try {

						Intent intent = new Intent(
								"com.google.zxing.client.android.SCAN");
						intent.putExtra("SCAN_MODE",
								"QR_CODE_MODE,PRODUCT_MODE");
						startActivityForResult(intent, 1);

					} catch (Exception e) {
						e.printStackTrace();

					}
				}
			}

		});

		edtScan.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				if (hasFocus) {
					// TODO Auto-generated method stub

					int radioButtonID = radioScan.getCheckedRadioButtonId();
					View radioClicked = radioScan.findViewById(radioButtonID);
					int idx = radioScan.indexOfChild(radioClicked);
					// edtSearch.setText(""); // to set empty edit box
					// jun 28 barcode integration based on Upc
					if (idx == 0) {
						try {

							Intent intent = new Intent(
									"com.google.zxing.client.android.SCAN");
							intent.putExtra("SCAN_MODE",
									"QR_CODE_MODE,PRODUCT_MODE");
							startActivityForResult(intent, 0);

						} catch (Exception e) {
							e.printStackTrace();

						}
					} else if (idx == 1) { // apr 26 barcode integration based
											// on
											// ManfNo
						try {

							Intent intent = new Intent(
									"com.google.zxing.client.android.SCAN");
							intent.putExtra("SCAN_MODE",
									"QR_CODE_MODE,PRODUCT_MODE");
							startActivityForResult(intent, 1);

						} catch (Exception e) {
							e.printStackTrace();

						}

					}
				}
			}

		});

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		final Button Ok, Cancel;
		final EditText medtQtyCount;
		final Spinner spnFrom, spnTo, spnUOM;
		ArrayAdapter<String> tempAdap;
		final Integer count = 0;
		final Cursor cursor_orddetails;
		final ArrayList<String> alst;
		final Button mplus;
		final Button mminus;

		String fromLoc = null;
		String toLoc = null;
		String quantity = null;
		String uom = null;

		String itemNo, desc;

		LayoutInflater li = LayoutInflater.from(MltMain.this);
		View promptsView = li.inflate(R.layout.mltupdate, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				MltMain.this);
		alertDialogBuilder.setView(promptsView);
		// create alert dialog
		final AlertDialog alertDialog = alertDialogBuilder.create();

		alst = new ArrayList<String>();// initialize array list

		Ok = (Button) promptsView.findViewById(R.id.btn_mltshipOk);
		Cancel = (Button) promptsView.findViewById(R.id.btn_mltshipCancel);
		spnFrom = (Spinner) promptsView.findViewById(R.id.spn_frmLoc);
		spnTo = (Spinner) promptsView.findViewById(R.id.spn_ToLoc);
		medtQtyCount = (EditText) promptsView.findViewById(R.id.edtmltCost);
		spnUOM = (Spinner) promptsView.findViewById(R.id.spn_Uom);
		mplus = (Button) promptsView.findViewById(R.id.btn_mltshipIncr);
		mminus = (Button) promptsView.findViewById(R.id.btn_mltshipDecr);

		if (requestCode == 0) {
			// ItemNo
			if (resultCode == RESULT_OK) {
				Log.i("Scan resul format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				final String scanitNo = intent.getStringExtra("SCAN_RESULT");

				System.out.println("My Item" + scanitNo);

				MLT_Inventory mlt_Inventory = new MLT_Inventory();
				databaseHandler.getReadableDatabase();
				mlt_Inventory = databaseHandler.getItemInventory(scanitNo);
				databaseHandler.closeDatabase();

				databaseHandler.getReadableDatabase();
				MLT_Details details = databaseHandler.getItemFromTemp(scanitNo);
				databaseHandler.closeDatabase();
				if (details != null) {
					fromLoc = details.getMlt_from();
					toLoc = details.getMlt_to();
					quantity = details.getMlt_qty();
					uom = details.getMlt_uom();
				}
				tempAdap = getDataFromDB();
				if(tempAdap!=null)
				{
					spnFrom.setAdapter(tempAdap);
				}
				
				
				

				if (!(fromLoc == null)) {
					spnFrom.setSelection(tempAdap.getPosition(fromLoc));
				}

				tempAdap = getDataToDB();
				if(tempAdap!=null)
				{
					spnTo.setAdapter(tempAdap);
				}
				

				if (!(toLoc == null)) {
					spnTo.setSelection(tempAdap.getPosition(toLoc));
				}

				tempAdap = getUomdata(scanitNo);
				if(tempAdap!=null)
				{
				spnUOM.setAdapter(tempAdap);
				}
				
				if (!(uom == null)) {
					spnUOM.setSelection(tempAdap.getPosition(uom));
				}

				if (!(quantity == null)) {
					medtQtyCount.setText(quantity);
				} else
					medtQtyCount.setText("1");

				if (mlt_Inventory != null) {

					mplus.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							String value = medtQtyCount.getText().toString();
							if (!value.equals("")) {
								String a = medtQtyCount.getText().toString();
								int b = Integer.parseInt(a);
								b = b + 1;
								a = a.valueOf(b);
								medtQtyCount.setText(a);
							} else {
								/*
								 * Toast.makeText(getApplicationContext(),
								 * "Please enter the Starting value"
								 * ,Toast.LENGTH_SHORT).show();
								 */
								toastText
										.setText("Please enter the Starting value");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										-100);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();

							}
						}
					});
					mminus.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							String value = medtQtyCount.getText().toString();
							if (!value.equals("")) {
								int c = Integer.parseInt(medtQtyCount.getText()
										.toString());
								c = c - 1;
								medtQtyCount.setText(new Integer(c).toString());
							} else {

								toastText
										.setText("Please enter the Starting value");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										-100);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();

							}
						}
					});
					spnFrom.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {

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
					spnTo.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {

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
					spnUOM.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {

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
					Ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							if (spnFrom
									.getItemAtPosition(
											spnFrom.getSelectedItemPosition())
									.toString().equals("1")
									&& spnTo.getItemAtPosition(
											spnTo.getSelectedItemPosition())
											.toString().equals("1")) {

								toastText.setText(" Select different location");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										-100);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
								/*
								 * Toast.makeText(MLT_Main.this,
								 * "Select different location ",
								 * Toast.LENGTH_SHORT).show();
								 */

							} else if (spnFrom
									.getItemAtPosition(
											spnFrom.getSelectedItemPosition())
									.toString().equals("2")
									&& spnTo.getItemAtPosition(
											spnTo.getSelectedItemPosition())
											.toString().equals("2")) {/*
																	 * Toast.
																	 * makeText
																	 * (
																	 * MLT_Main
																	 * .this ,
																	 * "Select different location "
																	 * , Toast.
																	 * LENGTH_SHORT
																	 * ).show();
																	 */
								toastText.setText(" Select different location");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										-100);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();

							} else if (spnFrom
									.getItemAtPosition(
											spnFrom.getSelectedItemPosition())
									.toString().equals("3")
									&& spnTo.getItemAtPosition(
											spnTo.getSelectedItemPosition())
											.toString().equals("3")) {
								/*
								 * Toast.makeText(MLT_Main.this,
								 * "Select different location ",
								 * Toast.LENGTH_SHORT).show();
								 */
								toastText.setText(" select diffrent location");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										-100);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();

							} else if (spnFrom
									.getItemAtPosition(
											spnFrom.getSelectedItemPosition())
									.toString().equals("4")
									&& spnTo.getItemAtPosition(
											spnTo.getSelectedItemPosition())
											.toString().equals("4")) {
								/*
								 * Toast.makeText(MLT_Main.this,
								 * "Select different location ",
								 * Toast.LENGTH_SHORT).show();
								 */
								toastText.setText(" select diffrent location");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										-100);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();

							} else if (spnFrom
									.getItemAtPosition(
											spnFrom.getSelectedItemPosition())
									.toString().equals("TRANS")
									&& spnTo.getItemAtPosition(
											spnTo.getSelectedItemPosition())
											.toString().equals("TRANS")) {
								/*
								 * Toast.makeText(MLT_Main.this,
								 * "Select different location ",
								 * Toast.LENGTH_SHORT).show();
								 */
								toastText.setText(" select diffrent location");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										-100);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();

							} else {

								String value = medtQtyCount.getText()
										.toString();
								if (!value.equals("")) {
									String froml = spnFrom.getItemAtPosition(
											spnFrom.getSelectedItemPosition())
											.toString();
									String tol = spnTo.getItemAtPosition(
											spnTo.getSelectedItemPosition())
											.toString();
									String uoml = spnUOM.getItemAtPosition(
											spnUOM.getSelectedItemPosition())
											.toString();
									String qyt = medtQtyCount.getText()
											.toString();

									MLT_Details mlt_details = new MLT_Details();
									mlt_details.setMlt_itemno(scanitNo);
									/* mlt_details.setMlt_description(Desc); */
									mlt_details.setMlt_from(froml);
									mlt_details.setMlt_to(tol);
									mlt_details.setMlt_qty(qyt);
									mlt_details.setMlt_uom(uoml);

									databaseHandler.getReadableDatabase();
									boolean flag = databaseHandler
											.checkMLT_Details(scanitNo);
									databaseHandler.closeDatabase();

									if (flag == false) {
										databaseHandler.getWritableDatabase();
										databaseHandler
												.addMLT_Temp_Table(mlt_details);
										databaseHandler.closeDatabase();
									} else {

										databaseHandler.getWritableDatabase();
										databaseHandler
												.updateMLT_Temp_Table(mlt_details);
										databaseHandler.closeDatabase();
									}

									Intent i = new Intent(MltMain.this,
											MltMain.class);
									i.putExtra("flag", 1);
									i.putExtra("AdditionalCost", additionalCost);
									i.putExtra("AdditionalDesc", additionaldesc);

									startActivity(i);
								} else {
									/*
									 * Toast.makeText(getApplicationContext(),
									 * "Please enter the Counted Qty value"
									 * ,Toast.LENGTH_SHORT).show();
									 */
									toastText
											.setText("Please enter the Counted Qty value");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, -100);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();
								}

							}
						}

					});
					alertDialog.show();

					Cancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							Intent i = new Intent(getApplicationContext(),
									MltMain.class);
							/*
							 * handler.getWritableDatabase();
							 * handler.deleteMltTempData();
							 * handler.closeDatabase();
							 */
							i.putExtra("flag", 1);
							startActivity(i);

						}
					});

					/*
					 * Intent i1 = new Intent(MLT_Main.this, mltupdate.class);
					 * i1.putExtra("ItemNumber", ItemNo);
					 * i1.putExtra("Description", Description);
					 * startActivity(i1);// , 1);
					 */

				} else {
					/*
					 * Toast.makeText(this, "Item not available",
					 * Toast.LENGTH_LONG).show();
					 */

					toastText.setText("Item not available");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				}

			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Scanning Cancelled", Toast.LENGTH_LONG)
						.show();
				Log.i("Scanning cancelled ", "Scanning cancelled");
			}
		}

		// ////////////////////////////////////

		else if (requestCode == 1) {
			// UPC
			if (resultCode == RESULT_OK) {
				Log.i("Scan resul format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				String scanUpc = intent.getStringExtra("SCAN_RESULT");

				databaseHandler.getReadableDatabase();
				MLT_UPC mltUpc = databaseHandler.getItemFromUpc_mlt(scanUpc);
				databaseHandler.closeDatabase();
				MLT_Details details=null;
				if(mltUpc!=null)
				{
				databaseHandler.getReadableDatabase();
				 details = databaseHandler.getItemFromTemp(mltUpc
						.getItemno());
				databaseHandler.closeDatabase();
				}
				if (details != null) {
					fromLoc = details.getMlt_from();
					toLoc = details.getMlt_to();
					quantity = details.getMlt_qty();
					uom = details.getMlt_uom();
				}
				tempAdap = getDataFromDB();
				if(tempAdap!=null)
				{
					spnFrom.setAdapter(tempAdap);	
				}
				

				if (!(fromLoc == null)) {
					spnFrom.setSelection(tempAdap.getPosition(fromLoc));
				}

				tempAdap = getDataToDB();
				if(tempAdap!=null)
				{
				spnTo.setAdapter(tempAdap);
				}

				if (!(toLoc == null)) {
					spnTo.setSelection(tempAdap.getPosition(toLoc));
				}

				if(mltUpc!=null)
				{
					tempAdap = getUomdata(mltUpc.getItemno());
					if(tempAdap!=null)
					{
					spnUOM.setAdapter(tempAdap);
					}
				}
				

				if (!(uom == null)) {
					spnUOM.setSelection(tempAdap.getPosition(uom));
				}

				if (!(quantity == null)) {
					medtQtyCount.setText(quantity);
				} else
					medtQtyCount.setText("1");

				if (mltUpc != null) {
					final String ScanedItno = mltUpc.getItemno();
					String upc = mltUpc.getUpc();
					MLT_Inventory mlt_Inventory = new MLT_Inventory();
					databaseHandler.getReadableDatabase();
					mlt_Inventory = databaseHandler
							.getItemInventory(ScanedItno);
					databaseHandler.closeDatabase();

					if (upc.equals(scanUpc)) {

						mplus.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								String value = medtQtyCount.getText()
										.toString();
								if (!value.equals("")) {
									String a = medtQtyCount.getText()
											.toString();
									int b = Integer.parseInt(a);
									b = b + 1;
									a = a.valueOf(b);
									medtQtyCount.setText(a);
								} else {
									/*
									 * Toast.makeText(getApplicationContext(),
									 * "Please enter the Starting value"
									 * ,Toast.LENGTH_SHORT).show();
									 */
									toastText
											.setText("Please enter the Starting value");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, -100);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();

								}
							}
						});
						mminus.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String value = medtQtyCount.getText()
										.toString();
								if (!value.equals("")) {
									int c = Integer.parseInt(medtQtyCount
											.getText().toString());
									c = c - 1;
									medtQtyCount.setText(new Integer(c)
											.toString());
								} else {

									toastText
											.setText("Please enter the Starting value");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, -100);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();

								}
							}
						});
						spnFrom.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {

								String item = (String) arg0
										.getItemAtPosition(arg2);
								((TextView) arg0.getChildAt(0))
										.setTextColor(Color
												.parseColor("#000000"));
								((TextView) arg0.getChildAt(0)).setTextSize(20);

							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {
								// TODO Auto-generated method stub

							}
						});
						spnTo.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {

								String item = (String) arg0
										.getItemAtPosition(arg2);
								((TextView) arg0.getChildAt(0))
										.setTextColor(Color
												.parseColor("#000000"));
								((TextView) arg0.getChildAt(0)).setTextSize(20);

							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {
								// TODO Auto-generated method stub

							}
						});
						spnUOM.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {

								String item = (String) arg0
										.getItemAtPosition(arg2);
								((TextView) arg0.getChildAt(0))
										.setTextColor(Color
												.parseColor("#000000"));
								((TextView) arg0.getChildAt(0)).setTextSize(20);
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {
								// TODO Auto-generated method stub

							}
						});
						Ok.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

								if (spnFrom
										.getItemAtPosition(
												spnFrom.getSelectedItemPosition())
										.toString().equals("1")
										&& spnTo.getItemAtPosition(
												spnTo.getSelectedItemPosition())
												.toString().equals("1")) {

									toastText
											.setText(" Select different location");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, -100);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();
									/*
									 * Toast.makeText(MLT_Main.this,
									 * "Select different location ",
									 * Toast.LENGTH_SHORT).show();
									 */

								} else if (spnFrom
										.getItemAtPosition(
												spnFrom.getSelectedItemPosition())
										.toString().equals("2")
										&& spnTo.getItemAtPosition(
												spnTo.getSelectedItemPosition())
												.toString().equals("2")) {/*
																		 * Toast.
																		 * makeText
																		 * (
																		 * MLT_Main
																		 * .this
																		 * ,
																		 * "Select different location "
																		 * ,
																		 * Toast
																		 * .
																		 * LENGTH_SHORT
																		 * )
																		 * .show
																		 * ();
																		 */
									toastText
											.setText(" Select different location");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, -100);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();

								} else if (spnFrom
										.getItemAtPosition(
												spnFrom.getSelectedItemPosition())
										.toString().equals("3")
										&& spnTo.getItemAtPosition(
												spnTo.getSelectedItemPosition())
												.toString().equals("3")) {
									/*
									 * Toast.makeText(MLT_Main.this,
									 * "Select different location ",
									 * Toast.LENGTH_SHORT).show();
									 */
									toastText
											.setText(" select diffrent location");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, -100);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();

								} else if (spnFrom
										.getItemAtPosition(
												spnFrom.getSelectedItemPosition())
										.toString().equals("4")
										&& spnTo.getItemAtPosition(
												spnTo.getSelectedItemPosition())
												.toString().equals("4")) {
									/*
									 * Toast.makeText(MLT_Main.this,
									 * "Select different location ",
									 * Toast.LENGTH_SHORT).show();
									 */
									toastText
											.setText(" select diffrent location");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, -100);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();

								} else if (spnFrom
										.getItemAtPosition(
												spnFrom.getSelectedItemPosition())
										.toString().equals("TRANS")
										&& spnTo.getItemAtPosition(
												spnTo.getSelectedItemPosition())
												.toString().equals("TRANS")) {
									/*
									 * Toast.makeText(MLT_Main.this,
									 * "Select different location ",
									 * Toast.LENGTH_SHORT).show();
									 */
									toastText
											.setText(" select diffrent location");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, -100);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();

								} else {

									String value = medtQtyCount.getText()
											.toString();
									if (!value.equals("")) {
										String froml = spnFrom
												.getItemAtPosition(
														spnFrom.getSelectedItemPosition())
												.toString();
										String tol = spnTo
												.getItemAtPosition(
														spnTo.getSelectedItemPosition())
												.toString();
										String uoml = spnUOM
												.getItemAtPosition(
														spnUOM.getSelectedItemPosition())
												.toString();
										String qyt = medtQtyCount.getText()
												.toString();

										MLT_Details mlt_details = new MLT_Details();
										mlt_details.setMlt_itemno(ScanedItno);
										/* mlt_details.setMlt_description(Desc); */
										mlt_details.setMlt_from(froml);
										mlt_details.setMlt_to(tol);
										mlt_details.setMlt_qty(qyt);
										mlt_details.setMlt_uom(uoml);

										databaseHandler.getReadableDatabase();
										boolean flag = databaseHandler
												.checkMLT_Details(ScanedItno);
										databaseHandler.closeDatabase();

										if (flag == false) {
											databaseHandler
													.getWritableDatabase();
											databaseHandler
													.addMLT_Temp_Table(mlt_details);
											databaseHandler.closeDatabase();
										} else {

											databaseHandler
													.getWritableDatabase();
											databaseHandler
													.updateMLT_Temp_Table(mlt_details);
											databaseHandler.closeDatabase();
										}

										Intent i = new Intent(MltMain.this,
												MltMain.class);
										i.putExtra("flag", 1);
										i.putExtra("AdditionalCost",
												additionalCost);
										i.putExtra("AdditionalDesc",
												additionaldesc);

										startActivity(i);
									} else {
										/*
										 * Toast.makeText(getApplicationContext()
										 * ,
										 * "Please enter the Counted Qty value"
										 * ,Toast.LENGTH_SHORT).show();
										 */
										toastText
												.setText("Please enter the Counted Qty value");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0,
												-100);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();
									}

								}
							}

						});
						alertDialog.show();

						Cancel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

								Intent i = new Intent(getApplicationContext(),
										MltMain.class);
								/*
								 * handler.getWritableDatabase();
								 * handler.deleteMltTempData();
								 * handler.closeDatabase();
								 */
								i.putExtra("flag", 1);
								startActivity(i);

							}
						});

					} else {
						/*
						 * Toast.makeText(this, "Item not available",
						 * Toast.LENGTH_LONG).show();
						 */

						toastText.setText("Item not available");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.setView(toastLayout);
						toast.show();
					}

				} else {/*
						 * Toast.makeText(this, "Item not available",
						 * Toast.LENGTH_LONG).show();
						 */

					toastText.setText("Item not available");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

				}

			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Scanning Canceled", Toast.LENGTH_LONG)
						.show();

				Log.i("Scanning cancelled ", "Scanning cancelled");
			}

		}

	}

	private ArrayAdapter<String> getDataFromDB() {
		// TODO Auto-generated method stub
		databaseHandler.getReadableDatabase();
		List<String> l = databaseHandler.getMltAllToDetails();
		databaseHandler.closeDatabase();
		ArrayAdapter<String> dataAdapter=null;
		if(l!=null)
		{
			// Creating adapter for spinner
			 dataAdapter = new ArrayAdapter<String>(
					MltMain.this, android.R.layout.simple_spinner_item, l);

			// Drop down layout style - list view with radio button
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		}

		// attaching data adapter to spinner
		return dataAdapter;
	}

	private ArrayAdapter<String> getDataToDB() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		databaseHandler.getReadableDatabase();
		List<String> l = databaseHandler.getMltAllToDetails();
		databaseHandler.closeDatabase();
		ArrayAdapter<String> dataAdapter =null;
		if(l!=null)
		{
			// Creating adapter for spinner
		dataAdapter = new ArrayAdapter<String>(
					MltMain.this, android.R.layout.simple_spinner_item, l);

			// Drop down layout style - list view with radio button
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		}
		
		// attaching data adapter to spinner
		return dataAdapter;
	}

	private ArrayAdapter<String> getUomdata(String ScanedItno) {
		// TODO Auto-generated method stub

		// String ItemNo = getIntent().getStringExtra("ItemNumber");
		databaseHandler.getWritableDatabase();
		List<String> l = databaseHandler.getMltAllUomDetails(ScanedItno);
		databaseHandler.closeDatabase();
		ArrayAdapter<String> dataAdapter=null;
		if(l!=null)
		{
			// Creating adapter for spinner
			 dataAdapter = new ArrayAdapter<String>(
					MltMain.this, android.R.layout.simple_spinner_item, l);

			// Drop down layout style - list view with radio button
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			
		}
		// attaching data adapter to spinner
		return dataAdapter;

	}

	private void deletesqlitedata() {
		// TODO Auto-generated method stub
		databaseHandler.getWritableDatabase();
		databaseHandler.deleteMltInv();
		databaseHandler.closeDatabase();

	}

	private JSONObject doResponse(String Url) {

		JSONObject jsonResponse = null;
		
		// Http connections and data streams
		URL url;
		HttpURLConnection httpURLConnection = null;
		OutputStreamWriter outputStreamWriter = null;

		try {

			// open connection to the server
			url = new URL(Url);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(HTTP_REQUEST_TIMEOUT);  
			httpURLConnection.setReadTimeout(HTTP_REQUEST_TIMEOUT); 
			StringBuilder stringBuilder = new StringBuilder();
			int responseCode = httpURLConnection.getResponseCode();

			// Check to make sure we got a valid status response
			// from the server,
			// then get the server JSON response if we did.
			if (responseCode == HttpURLConnection.HTTP_OK) {

				// read in each line of the response to the input
				// buffer
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(
								httpURLConnection.getInputStream(), "utf-8"));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line).append("\n");
				}

				bufferedReader.close(); // close out the input
										// stream

				try {
					// Copy the JSON response to a local JSONObject
					jsonResponse = new JSONObject(stringBuilder.toString());
					jsonResponse = jsonResponse.getJSONObject(GetMLTData);
				} catch (JSONException je) {
					je.printStackTrace();
				}

			}
			

		} catch (Exception e) {

			Log.e("ERROR", e.getLocalizedMessage());
		}
		return jsonResponse;
	}

	class RefreshingList extends AsyncTask<String, String, String> {
		ProgressDialog pd;
		String result = "";

		public RefreshingList() {
			pd = new ProgressDialog(MltMain.this);
			pd.setCancelable(false);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pd.setMessage("Refreshing Data..");
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			flag = 0;
			// To delete Temporary data when new clicked
			databaseHandler.getWritableDatabase();
			databaseHandler.deleteMltTempData();
			databaseHandler.closeDatabase();
			result = "success";
			return result;

		}

		protected void onPostExecute(String result) {

			if (result.equals("success")) {
				pd.setMessage("Coping Data..");

				new InflateList().execute();

				pd.setMessage("Refreshing Success..");

				pd.dismiss();
				edtAddCost.setText("");
				edtdesc.setText("");
			} else {

				pd.dismiss();

				toastText.setText("Item not available");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(toastLayout);
				toast.show();

			}

		}

	}

	class SaveUpdatedData extends AsyncTask<String, String, String> {
		ProgressDialog pd;

		public SaveUpdatedData() {
			pd = new ProgressDialog(MltMain.this);
			pd.setCancelable(false);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pd.setMessage("Loading Data..");
			pd.show();
		}

		@Override
		protected String doInBackground(String... results) {
			// TODO Auto-generated method stub

			pd.setMessage("Moving Data...");

			databaseHandler.getWritableDatabase();
			String result = databaseHandler.get_Temp_TABLE_Data();
			databaseHandler.closeDatabase();

			return result;
		}

		protected void onPostExecute(String result) {

			if (result.equals("success")) {

				databaseHandler.getWritableDatabase();
				databaseHandler.deleteMltTempData();
				databaseHandler.closeDatabase();

				pd.setMessage("Saving Header Details...");

				try {

					MltHeader mltheader = new MltHeader();

					SharedPreferences shareddoc = getSharedPreferences(
							"Doc_No", MODE_PRIVATE);
					int docNo = shareddoc.getInt("doc", 1);
					mltheader.setDocNo(docNo);

					if (edtdesc.getText().toString().trim().length() > 0
							&& edtAddCost.getText().toString().trim().length() > 0) {
						mltheader.setDesc(edtdesc.getText().toString());
						mltheader.setAddCost(Integer.parseInt(edtAddCost
								.getText().toString()));

						String date = edtDate.getText().toString();
						String cur_date[] = date.split("-");
						System.out.println("date" + date);
						System.out.println("date" + cur_date[0]);
						System.out.println("date" + cur_date[1]);
						System.out.println("date" + cur_date[2]);

						mltheader.setDay(Integer.parseInt(cur_date[0]));
						mltheader.setMonth(Integer.parseInt(cur_date[1]));
						mltheader.setYear(Integer.parseInt(cur_date[2]));

						databaseHandler.getWritableDatabase();
						databaseHandler.addMltHeader(mltheader);
						databaseHandler.closeDatabase();

						result = "success";
					} else {
						if (edtdesc.getText().toString().trim().length() > 0) {
							mltheader.setDesc(edtdesc.getText().toString());
						} else {
							mltheader.setDesc("");
						}

						if (edtAddCost.getText().toString().trim().length() > 0) {
							mltheader.setAddCost(Integer.parseInt(edtAddCost
									.getText().toString()));
						} else {
							mltheader.setAddCost(0);
						}

						String curDate = dateFormatter.format(new Date());
						String cur_date[] = curDate.split("-");

						mltheader.setDay(Integer.parseInt(cur_date[0]));
						mltheader.setMonth(Integer.parseInt(cur_date[1]));
						mltheader.setYear(Integer.parseInt(cur_date[2]));

						databaseHandler.getWritableDatabase();
						databaseHandler.addMltHeader(mltheader);
						databaseHandler.closeDatabase();
						edtAddCost.setText(" ");
						edtdesc.setText(" ");
					}

				} catch (Exception ex) {
					result = "error";
					ex.printStackTrace();
				}

				pd.setMessage("Data Saved Successfully");
				pd.dismiss();

				new RefreshingList().execute();

			} else {
				pd.dismiss();
				/*
				 * Toast.makeText(MLT_Main.this, "Error while Saving data...",
				 * Toast.LENGTH_LONG).show();
				 */
				toastText.setText("Please perform transaction before Saving");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(toastLayout);
				toast.show();
			}

		}

		protected void onProgressUpdate(String... progress) {
			this.pd.setMessage(progress[0]);
		}
	}

	class ImportMLTData extends AsyncTask<String, String, String> {
		ProgressDialog pd;

		public ImportMLTData() {
			pd = new ProgressDialog(MltMain.this);
			pd.setCancelable(false);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pd.setMessage("Loading Data..");
			pd.show();
		}

		@Override
		protected String doInBackground(String... results) {
			// TODO Auto-generated method stub
			String result = "";
			databaseHandler.getWritableDatabase();
			deletesqlitedata();
			databaseHandler.closeDatabase();

			databaseHandler.getWritableDatabase();
			deleteLocation();
			databaseHandler.closeDatabase();

			databaseHandler.getWritableDatabase();
			deleteUOM();
			databaseHandler.closeDatabase();

			databaseHandler.getWritableDatabase();
			deleteUPC();
			databaseHandler.closeDatabase();

			databaseHandler.getReadableDatabase();
			MIS_Setting mis_Setting = databaseHandler.getSetting();

			if (mis_Setting.equals(null)) {
				telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				deviceID = telephonyManager.getDeviceId();

				if (deviceID == null) {
					deviceID = Build.SERIAL;
					if (deviceID == null) {
						deviceID = "0000";
					}
				}
			} else {

				deviceID = mis_Setting.getDeviceId();

			}
			databaseHandler.closeDatabase();

			databaseHandler.getReadableDatabase();
			CompanyID = databaseHandler.LOAD_COMPANYID(deviceID);
			databaseHandler.closeDatabase();
			try {
				MIS_Setting mis_setting = new MIS_Setting();

				databaseHandler.getReadableDatabase();
				mis_setting = databaseHandler.getSetting();
				databaseHandler.closeDatabase();

				String deviceId = mis_setting.getDeviceId();
				String ipAddress = mis_setting.getIpAddress();

				String full = "http://" + ipAddress
						+ "/MISWCFService/Service.svc/GetMLTData";
				String FullUrl = full + "/" + CompanyID;

				JSONObject jobject = doResponse(FullUrl);

				if (jobject != null) {

					result = fethitemDetails(jobject);

					if (result.equals("success") || result.equals("")) {
						result = FetchItemUom(jobject);

					} else {

						result = "error";
					}
					if (result.equals("success") || result.equals("")) {

						result = Location(jobject);

					} else {
						result = "error";
					}
					if (result.equals("success") || result.equals("")) {
						result = fetchUpc(jobject);
					} else {
						result = "error";
					}
				}

			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				result = "error";
			}

			return result;
		}

		protected void onProgressUpdate(String... progress) {
			super.onProgressUpdate(progress);
			// pd.setProgress(Integer.parseInt(progress[0]));
		}

		protected void onPostExecute(String result) {

			if (!result.equals("error")) {
				pd.dismiss();
				new InflateList().execute();
				// Toast.makeText(getApplicationContext(),
				// "Data Loaded Successfully",Toast.LENGTH_SHORT).show();
				toastText.setText("Data Loaded Successfully");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(toastLayout);
				toast.show();

			} else {
				if (result.equals("error")) {
					pd.dismiss();
					toastText.setText("Error While Importing !! Data Problem");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				}

			}

		}

		private void deleteUPC() {
			// TODO Auto-generated method stub
			databaseHandler.getWritableDatabase();
			databaseHandler.deleteMltUPC();
			databaseHandler.closeDatabase();
		}

		private void deleteUOM() {
			// TODO Auto-generated method stub
			databaseHandler.getWritableDatabase();
			databaseHandler.deleteMltUom();
			databaseHandler.closeDatabase();
		}

		private void deleteLocation() {
			// TODO Auto-generated method stub

			databaseHandler.getWritableDatabase();
			databaseHandler.deleteMltLoc();
			databaseHandler.closeDatabase();
		}

		private String fetchUpc(JSONObject jobject) {
			String res = "";
			try {
				jarray = jobject.getJSONArray("UPC");
				if (jarray != null) {
					for (int i = 0; i < jarray.length(); i++) {
						JSONObject jobject2 = jarray.getJSONObject(i);
						databaseHandler.openWritableDatabase();
						res = databaseHandler.addMltUPC(new MLT_UPC(jobject2
								.getString("ITEMNO"), jobject2
								.getString("UPCCODE")));
						databaseHandler.closeDatabase();
					}
				}
				jarray = null;
				return res;
			} catch (Exception e) {
				res = "error";
				e.printStackTrace();
			}

			return res;
		}

		private String fethitemDetails(JSONObject jobject) {
			// TODO Auto-generated method stub
			String res = "";
			try {

				jarray = jobject.getJSONArray("ItamDetails");
				if (jarray != null) {
					for (int i = 0; i < jarray.length(); i++) {
						JSONObject jobject2 = jarray.getJSONObject(i);
						databaseHandler.getWritableDatabase();
						res = databaseHandler
								.addMltItemDetails(new MLT_Inventory(jobject2
										.getString("ITEMNO"), jobject2
										.getString("DESC")));
						databaseHandler.closeDatabase();
					}
				}
				jarray = null;
				return res;
			} catch (Exception e) {
				res = "error";
				e.printStackTrace();
			}
			return res;

		}
	}

	private String FetchItemUom(JSONObject jobject) {
		String res = "";
		try {
			jarray = jobject.getJSONArray("ItemUom");
			if (jarray != null) {
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject jobject2 = jarray.getJSONObject(i);
					databaseHandler.openWritableDatabase();
					res = databaseHandler.addMltUOM(new MLT_UOM(jobject2
							.getString("ITEMNO"), jobject2.getString("UNIT")));
					databaseHandler.closeDatabase();
				}
			}
			jarray = null;
			return res;
		} catch (Exception e) {
			res = "error";
			e.printStackTrace();
		}
		return res;
	}

	public String Location(JSONObject jobject) {
		String res = "";
		try {

			jarray = jobject.getJSONArray("Loaction1");
			if (jarray != null) {
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject jobject2 = jarray.getJSONObject(i);
					databaseHandler.openWritableDatabase();
					res = databaseHandler.addMltLocation(new MLT_LOCATION(
							jobject2.getString("LOCATION")));
					databaseHandler.closeDatabase();
				}
			}
			jarray = null;
			return res;
		} catch (Exception e) {
			res = "error";
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.mltmain, menu);
		return super.onCreateOptionsMenu(menu);

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // TODO
	 * Auto-generated method stub MenuInflater inflater = getMenuInflater();
	 * inflater.inflate(R.menu.mltmain, menu); //hint if(OList_Str==null)
	 * menu.getItem(1).setEnabled(false); else menu.getItem(1).setEnabled(true);
	 * 
	 * return true; }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.main_mnu_import) {

			// TODO Auto-generated method stub

			// These methods are for deleting tables before inserting fresh
			// data

			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					MltMain.this);
			alertDialog.setTitle("Confirmation");
			alertDialog.setIcon(R.drawable.warning);
			alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							new ImportMLTData().execute();

						}
					});
			alertDialog.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			alertDialog.setMessage("Do you want to Import?");

			alertDialog.show();

		} else if (id == R.id.main_mnu_Export) {

			// TODO Auto-generated method stub
			List<MltTrans> ordList = new ArrayList<MltTrans>();
			databaseHandler.getReadableDatabase();

			ordList = databaseHandler.getMltDistinctDocNo_Trans();
			databaseHandler.closeDatabase();
			if (ordList != null) {
				SharedPreferences shareddoc = getSharedPreferences("Doc_No",
						MODE_PRIVATE);
				int docHeader = shareddoc.getInt("doc", 1);
				int docDetail = shareddoc.getInt("doc", 1);
				if ((docHeader == docDetail)
						&& (docHeader != 0 || docDetail != 0)) {
					Intent i = new Intent(getApplicationContext(),
							MltExport.class);
					startActivity(i);
				} else {
					toastText.setText("Transaction Not Available for Export1");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

				}

			}

			else {

				toastText.setText("Please Save the Transaction before Export");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(toastLayout);
				toast.show();
			}

		} else if (id == R.id.main_mnu_new) {
			new RefreshingList().execute();
		} else if (id == R.id.main_mnu_save) {
			new SaveUpdatedData().execute();
		} else {

			// TODO Auto-generated method stub

			Intent i = new Intent(getApplicationContext(), Login.class);
			startActivity(i);

		}
		return super.onOptionsItemSelected(item);
	}

	
	class InflateList extends AsyncTask<String, String, String> {
		ProgressDialog dialog;
		Context context;
	
		public InflateList() {
			dialog = new ProgressDialog(MltMain.this);
			dialog.setCancelable(false);
			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Please wait while Data is Loading...");
			dialog.show();
		}

		@Override
		protected String  doInBackground(String... params) {
			// TODO Auto-generated method stub
			String result = "";
			
			try {
				if (flag == 1) {

					databaseHandler.getReadableDatabase();
					arr = databaseHandler.getmltdata();
					databaseHandler.closeDatabase();
					flag = 0;

					

				} else {
					databaseHandler.getReadableDatabase();
					arr = databaseHandler.getmltdata_();
					databaseHandler.closeDatabase();


				}

				

				result = "success";
			

			}

			catch (Exception e) {
				result = "error";
				

				dialog.dismiss();
				Log.e("Failed", e.getLocalizedMessage());
			}
			return result;
		}

		protected void onPostExecute(String result) {
			dialog.setMessage("Inflating Data...");
			if (result.equals(("success"))) {
				adapter = new Mlt_sixTextviewAdapter(MltMain.this, arr);
				listview.setAdapter(adapter);
				dialog.dismiss();
			} else {
				dialog.dismiss();
				toastText.setText("Problem in loading Items");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
				toast.setDuration(Toast.LENGTH_LONG);
				toast.setView(toastLayout);
				toast.show();
			}
		}

	}

	
	
	
	/*public void insertToList() {
		try {

			List<MLT_Details> arr = new ArrayList<MLT_Details>();

			if (flag == 1) {

				databaseHandler.getReadableDatabase();
				arr = databaseHandler.getmltdata();
				databaseHandler.closeDatabase();
				flag = 0;

				adapter = new Mlt_sixTextviewAdapter(MltMain.this, arr);
				listview.setAdapter(adapter);
				adapter.notifyDataSetChanged();

			} else {
				databaseHandler.getReadableDatabase();
				arr = databaseHandler.getmltdata_();
				databaseHandler.closeDatabase();

				adapter = new Mlt_sixTextviewAdapter(MltMain.this, arr);
				listview.setAdapter(adapter);
				// adapter.notifyDataSetChanged();

			}
		}

		catch (Exception e) {
			Log.e("Failed", e.getLocalizedMessage());
		}

	}
*/
	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent i = new Intent(getApplicationContext(), Login.class);

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
