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
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.example.mobinventorysuit.R;
import com.mis.adapter.MicListAdapter;
import com.mis.common.AppBaseActivity;
import com.mis.common.LogfileCreator;
import com.mis.common.Login;
import com.mis.common.MIS_Setting;
import com.mis.common.MainActivity;
import com.mis.database.DatabaseHandler;
import com.mis.mic.model.MIC_Conversionfactor;
import com.mis.mic.model.MIC_Inventory;
import com.mis.mic.model.MIC_Manufacturenumber;
import com.mis.mic.model.MIC_OrderDetails;
import com.mis.mic.model.MIC_UOMInternal;
import com.mis.mic.model.MIC_UPC;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class InventoryCount extends AppBaseActivity {
	private static final int HTTP_REQUEST_TIMEOUT = 120000;
	View toastLayout;
	TextView toastText;
	private DatabaseHandler dbhelper;
	String itemno, desc, pickSeq, loc, uom;
	String qtyonHand, qtyCount;

	ListView lstView;
	public Cursor cursor;
	private Spinner mspinner;
	private EditText msearchtext;
	private RadioGroup radioScan;
	private RadioButton radioUpc;
	private RadioButton radioItem;
	private RadioButton radioManf;

	private List<String> mlocList;
	private ArrayAdapter<String> mlocAdapt;
	public String loc1;
	public static String spinlocation;
	public String fromLoc;
	public String toLoc;
	InventoryCount qc;
	public SharedPreferences locationDatas;

	private MicListAdapter ordList;
	public JSONObject putmicdata;
	public JSONObject exportdata;
	public JSONArray valueheader;
	public JSONArray valueheaderdetail;
	public String companyid;
	TelephonyManager telephonemanager;
	final String GetMICData = "GetMICDataResult";
	LogfileCreator lfc = new LogfileCreator();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.inventorycount);
		mspinner = (Spinner) findViewById(R.id.sploc);
		lstView = (ListView) findViewById(R.id.lst_msefull_mic);
		msearchtext = (EditText) findViewById(R.id.edt_Search_mic);

		radioScan = (RadioGroup) findViewById(R.id.radioScanBasedOn_mic);
		radioUpc = (RadioButton) findViewById(R.id.radioUpc_mic);
		radioItem = (RadioButton) findViewById(R.id.radioInum_mic);
		radioManf = (RadioButton) findViewById(R.id.radioNum_mic);

		registerBaseActivityReceiver();

		qc = new InventoryCount();
		dbhelper = new DatabaseHandler(this);

		telephonemanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		dbhelper.getReadableDatabase();
		companyid = dbhelper.LOAD_COMPANYID(telephonemanager.getDeviceId());
		dbhelper.closeDatabase();

		// Toast
		LayoutInflater inflater = getLayoutInflater();
		toastLayout = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		toastText = (TextView) toastLayout.findViewById(R.id.text);

		// Checking it is return from update or not
		int returnFrmUpdate = getIntent().getIntExtra("et", 0);

		if (returnFrmUpdate == 1) {
			spinlocation = getIntent().getStringExtra("LOCATION");
			loc = getIntent().getStringExtra("LOCATION");
		}
		dbhelper.getReadableDatabase();
		int val = GetCount();
		dbhelper.closeDatabase();
		if (val > 0 && spinlocation != null) {
			new InflateList(spinlocation).execute();
		}

		// Checking fromLoc n toLoc is empty or not
		// if empty means import not done
		locationDatas = PreferenceManager.getDefaultSharedPreferences(this);
		fromLoc = locationDatas.getString("From Location", "");
		toLoc = locationDatas.getString("To Location", "");

		dbhelper.getReadableDatabase();
		String[] locdetails = dbhelper.getMicLocation();
		dbhelper.closeDatabase();
		if (fromLoc.equalsIgnoreCase("") && toLoc.equalsIgnoreCase("")) {
			if (locdetails[0] != null || locdetails[1] != null) {
				fromLoc = locdetails[0];
				toLoc = locdetails[1];

			}
		}
		if (!fromLoc.equalsIgnoreCase("") && !toLoc.equalsIgnoreCase("")) {
			mlocList = new ArrayList<String>();
			if (returnFrmUpdate == 1) {

				mlocList.add(spinlocation);
				if (spinlocation.equals(fromLoc)) {
					mlocList.add(toLoc);
				} else {
					mlocList.add(fromLoc);
				}

			} else {
				mlocList.add(toLoc);// 1
				mlocList.add(fromLoc);// 4
			}

			/*
			 * mlocAdapt = new ArrayAdapter<String>(this,
			 * android.R.layout.simple_dropdown_item_1line, mlocList);
			 * mspinner.setAdapter(mlocAdapt);
			 */
			mlocAdapt = new ArrayAdapter<String>(InventoryCount.this,
					android.R.layout.simple_dropdown_item_1line, mlocList);
			mspinner.setAdapter(mlocAdapt);
			/*
			 * String[] spin_arry = new String[2]; if (mlocList != null ||
			 * mlocList.size() > 0) { for (int i = 0; i < mlocList.size(); i++)
			 * { spin_arry[i] = mlocList.get(i); } mlocAdapt = new
			 * CustomArrayAdapter<CharSequence>(this, spin_arry);
			 * mspinner.setAdapter(mlocAdapt);
			 * 
			 * }
			 */

			// else condition if returnFrmUpdate=0
			loc = mspinner
					.getItemAtPosition(mspinner.getSelectedItemPosition())
					.toString();

			lstView.setOnItemClickListener(new OnItemClickListener() {
				Button mok;
				Button mcancel;
				Button mplus;
				Button mminus;
				EditText medtQtyCount;
				EditText medtItem;
				EditText medtdesc;

				EditText medtuom;

				Cursor mCursor;
				String ItemNo;
				String Desc;
				String StockUnit;

				String PickSeq, loc;

				String qtyCount, qtyonHand;

				DatabaseHandler dbHandler;
				ArrayList<String> Uomarray;
				ArrayList<String> Convarray;
				ArrayList<String> rtnArrayQtyCountedInternal;
				ArrayList<String> rtnArrayQtyUOMInternal;
				String[] UOM;
				String[] ConvFact;
				String[] ConvUOM;
				Boolean tblMIC;

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub

					LayoutInflater li = LayoutInflater
							.from(InventoryCount.this);
					View promptsView = li
							.inflate(R.layout.quantityupdate, null);

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							InventoryCount.this);
					alertDialogBuilder.setView(promptsView);

					// create alert dialog
					final AlertDialog alertDialog = alertDialogBuilder.create();

					dbHandler = new DatabaseHandler(InventoryCount.this);
					medtuom = (EditText) promptsView
							.findViewById(R.id.edt_mseshipuom_mic);
					mok = (Button) promptsView
							.findViewById(R.id.btn_mseshipOk_mic);
					mcancel = (Button) promptsView
							.findViewById(R.id.btn_mseshipCancel_mic);
					mplus = (Button) promptsView
							.findViewById(R.id.btn_mseshipIncr_mic);
					mminus = (Button) promptsView
							.findViewById(R.id.btn_mseshipDecr_mic);
					medtQtyCount = (EditText) promptsView
							.findViewById(R.id.edt_shipShiped_mic);
					medtdesc = (EditText) promptsView
							.findViewById(R.id.edt_mseshipQtyOrd_mic);
					medtItem = (EditText) promptsView
							.findViewById(R.id.edt_mseshipItemNo_mic);

					MIC_OrderDetails list_obj = new MIC_OrderDetails();
					list_obj = (MIC_OrderDetails) lstView
							.getItemAtPosition(position);
					ItemNo = list_obj.getItemNumber();
					Desc = list_obj.getItemDescription();
					PickSeq = list_obj.getPickSeq();
					StockUnit = list_obj.getUnit();
					qtyonHand = list_obj.getQtyonHand();// This value gives
														// QOHand
					qtyCount = list_obj.getQtyCount();
					loc = mspinner.getItemAtPosition(
							mspinner.getSelectedItemPosition()).toString();

					medtItem.setText(ItemNo);
					medtdesc.setText(Desc);
					medtQtyCount.setText(qtyCount);
					medtuom.setText(StockUnit);

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
					mok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							/*
							 * UOM[mspinnerUom.getSelectedItemPosition()] =
							 * medtQtyCount .getText().toString();
							 */
							String value = medtQtyCount.getText().toString();
							if (!value.equals("")) {
								MIC_UOMInternal mic_uom = new MIC_UOMInternal();
								mic_uom.setLocation(loc);
								mic_uom.setItemno(ItemNo);
								String updatedqtyCount = medtQtyCount.getText()
										.toString();

								if (!qtyCount.equals(updatedqtyCount)) {
									mic_uom.setQc(Double
											.parseDouble(updatedqtyCount));
									mic_uom.setUom(StockUnit);

									MIC_Inventory mic_Inventory = new MIC_Inventory();
									mic_Inventory.setItemdescription(Desc);
									mic_Inventory.setItemno(ItemNo);
									mic_Inventory.setLocation(loc);
									mic_Inventory.setPickingseq(PickSeq);
									mic_Inventory.setQc(updatedqtyCount);
									mic_Inventory.setQoh(qtyonHand);
									mic_Inventory.setStockunit(StockUnit);

									dbHandler.getWritableDatabase();
									String result = dbHandler
											.insertIntoInternal(mic_uom);
									if (result.equals("success")) {
										result = dbHandler
												.updateMIC(mic_Inventory);
									}
									dbHandler.closeDatabase();
								}

								Intent i = new Intent(InventoryCount.this,
										InventoryCount.class);
								i.putExtra("et", 1);
								i.putExtra("LOCATION", loc);
								// i.putExtra("ID", ID);
								startActivity(i);
								// InventoryCount.this.finish();
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
					});
					mcancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							alertDialog.cancel();

						}
					});

					// show it
					alertDialog.show();
				}
			});

			mspinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					spinlocation = mspinner.getItemAtPosition(arg2).toString();
					loc = spinlocation;
					String item = (String) arg0.getItemAtPosition(arg2);
					((TextView) arg0.getChildAt(0)).setTextColor(Color
							.parseColor("#000000"));
					((TextView) arg0.getChildAt(0)).setTextSize(20);

					new InflateList(spinlocation).execute();

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
		}

		msearchtext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int radioButtonID = radioScan.getCheckedRadioButtonId();
				View radioClicked = radioScan.findViewById(radioButtonID);
				int idx = radioScan.indexOfChild(radioClicked);
				// edtSearch.setText(""); // to set empty edit box
				// jun 28 barcode integration based on ItemNo
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
				} else if (idx == 1) { // apr 26 barcode integration based on
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
				} else if (idx == 2) { // apr 26 barcode integration based on
					// UPC
					try {

						Intent intent = new Intent(
								"com.google.zxing.client.android.SCAN");
						intent.putExtra("SCAN_MODE",
								"QR_CODE_MODE,PRODUCT_MODE");
						startActivityForResult(intent, 2);

					} catch (Exception e) {
						e.printStackTrace();

					}
				}

			}
		});
		msearchtext.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
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
					} else if (idx == 2) { // apr 26 barcode integration based
											// on
						// ItemNo
						try {

							Intent intent = new Intent(
									"com.google.zxing.client.android.SCAN");
							intent.putExtra("SCAN_MODE",
									"QR_CODE_MODE,PRODUCT_MODE");
							startActivityForResult(intent, 2);

						} catch (Exception e) {
							e.printStackTrace();

						}
					}
				}
			}
		});

	}

	public class CustomArrayAdapter extends ArrayAdapter<String> {

		private List<String> objects;
		private Context context;

		public CustomArrayAdapter(Context context, int resourceId,
				List<String> objects) {
			super(context, resourceId, objects);
			this.objects = objects;
			this.context = context;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.spinner_item, parent, false);
			TextView label = (TextView) row.findViewById(R.id.textspinner);
			label.setText(objects.get(position));

			if (position == 0) {// Special style for dropdown header
				label.setTextColor(Color.parseColor("#000000"));
			}

			return row;
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		loc = mspinner.getItemAtPosition(mspinner.getSelectedItemPosition())
				.toString();

		final String ItemNo;
		final String Desc;
		final String StockUnit;

		final String PickSeq;

		final String qtyCount;
		final String qtyonHand;
		final Button mok;
		final Button mcancel;
		final Button mplus;
		final Button mminus;
		final EditText medtQtyCount;
		final EditText medtItem;
		final EditText medtdesc;
		final EditText medtuom;
		final DatabaseHandler dbHandler;
		final String[] UOM = null;

		LayoutInflater li = LayoutInflater.from(InventoryCount.this);
		View promptsView = li.inflate(R.layout.quantityupdate, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				InventoryCount.this);
		alertDialogBuilder.setView(promptsView);

		// create alert dialog
		final AlertDialog alertDialog = alertDialogBuilder.create();

		dbHandler = new DatabaseHandler(InventoryCount.this);
		medtuom = (EditText) promptsView.findViewById(R.id.edt_mseshipuom_mic);
		mok = (Button) promptsView.findViewById(R.id.btn_mseshipOk_mic);
		mcancel = (Button) promptsView.findViewById(R.id.btn_mseshipCancel_mic);
		mplus = (Button) promptsView.findViewById(R.id.btn_mseshipIncr_mic);
		mminus = (Button) promptsView.findViewById(R.id.btn_mseshipDecr_mic);
		medtQtyCount = (EditText) promptsView
				.findViewById(R.id.edt_shipShiped_mic);
		medtdesc = (EditText) promptsView
				.findViewById(R.id.edt_mseshipQtyOrd_mic);
		medtItem = (EditText) promptsView
				.findViewById(R.id.edt_mseshipItemNo_mic);

		if (requestCode == 1) {
			// Manf No
			if (resultCode == RESULT_OK) {
				Log.i("Scan result format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				String manfCode = intent.getStringExtra("SCAN_RESULT");

				dbhelper.getReadableDatabase();
				MIC_Manufacturenumber manf = dbhelper
						.getItemFromManfNum_mic(manfCode);
				dbhelper.closeDatabase();

				if (manf != null) {
					String Itno = manf.getItemno();
					dbhelper.getReadableDatabase();
					boolean flag = dbhelper.checkMicInventoryDetails(loc, Itno);
					dbhelper.closeDatabase();
					if (flag == true) {
						dbhelper.getReadableDatabase();
						MIC_Inventory mic_inventory = dbhelper
								.getMicInventoryDetails(loc, manf.getItemno());
						dbhelper.closeDatabase();

						ItemNo = mic_inventory.getItemno();
						Desc = mic_inventory.getItemdescription();
						PickSeq = mic_inventory.getPickingseq();
						StockUnit = mic_inventory.getStockunit();
						qtyonHand = mic_inventory.getQoh();// This value gives
															// QOHand
						qtyCount = mic_inventory.getQc();

						medtItem.setText(ItemNo);
						medtdesc.setText(Desc);
						medtQtyCount.setText(qtyCount);
						medtuom.setText(StockUnit);

						mplus.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								String a = medtQtyCount.getText().toString();
								int b = Integer.parseInt(a);
								b = b + 1;
								a = a.valueOf(b);
								medtQtyCount.setText(a);
							}
						});
						mminus.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								int c = Integer.parseInt(medtQtyCount.getText()
										.toString());
								c = c - 1;
								medtQtyCount.setText(new Integer(c).toString());
							}
						});
						mok.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								/*
								 * UOM[mspinnerUom.getSelectedItemPosition()] =
								 * medtQtyCount .getText().toString();
								 */
								MIC_UOMInternal mic_uom = new MIC_UOMInternal();
								mic_uom.setLocation(loc);
								mic_uom.setItemno(ItemNo);
								String updatedqtyCount = medtQtyCount.getText()
										.toString();

								if (!qtyCount.equals(updatedqtyCount)) {
									mic_uom.setQc(Double
											.parseDouble(updatedqtyCount));
									mic_uom.setUom(StockUnit);

									MIC_Inventory mic_Inventory = new MIC_Inventory();
									mic_Inventory.setItemdescription(Desc);
									mic_Inventory.setItemno(ItemNo);
									mic_Inventory.setLocation(loc);
									mic_Inventory.setPickingseq(PickSeq);
									mic_Inventory.setQc(updatedqtyCount);
									mic_Inventory.setQoh(qtyonHand);
									mic_Inventory.setStockunit(StockUnit);

									dbHandler.getWritableDatabase();
									String result = dbHandler
											.insertIntoInternal(mic_uom);
									if (result.equals("success")) {
										result = dbHandler
												.updateMIC(mic_Inventory);
									}
									dbHandler.closeDatabase();
								}
								Intent i = new Intent(InventoryCount.this,
										InventoryCount.class);
								i.putExtra("et", 1);
								i.putExtra("LOCATION", loc);
								// i.putExtra("ID", ID);
								startActivity(i);
								// InventoryCount.this.finish();

							}
						});
						mcancel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								alertDialog.cancel();

							}
						});

						// show it
						alertDialog.show();

					} else {/*
							 * Toast.makeText(this, "Item not available",
							 * Toast.LENGTH_SHORT).show();
							 */

						toastText.setText("Item Not Available");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
						toast.setDuration(Toast.LENGTH_LONG);
						toast.setView(toastLayout);
						toast.show();
					}
				} else {
					/*
					 * Toast.makeText(this, "Item not available",
					 * Toast.LENGTH_SHORT).show();
					 */
					toastText.setText("Item Not Available");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_LONG);
					toast.setView(toastLayout);
					toast.show();
				}
			} else if (resultCode == RESULT_CANCELED) {

				Toast.makeText(this, "Scanning Cancelled", Toast.LENGTH_LONG)
						.show();

				Log.i("Scanning cancelled ", "Scanning cancelled");

			}
		} else if (requestCode == 0) {
			// ItemNo
			if (resultCode == RESULT_OK) {
				Log.i("Scan resul format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				String itNo = intent.getStringExtra("SCAN_RESULT");

				dbhelper.getReadableDatabase();
				MIC_Inventory mic_inventory = dbhelper.getMicInventoryDetails(
						loc, itNo);
				dbhelper.closeDatabase();

				if (mic_inventory != null) {

					loc = mspinner.getItemAtPosition(
							mspinner.getSelectedItemPosition()).toString();

					ItemNo = mic_inventory.getItemno();
					Desc = mic_inventory.getItemdescription();
					PickSeq = mic_inventory.getPickingseq();
					StockUnit = mic_inventory.getStockunit();
					qtyonHand = mic_inventory.getQoh();// This value gives
														// QOHand
					qtyCount = mic_inventory.getQc();

					medtItem.setText(ItemNo);
					medtdesc.setText(Desc);
					medtQtyCount.setText(qtyCount);
					medtuom.setText(StockUnit);

					mplus.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							String a = medtQtyCount.getText().toString();
							int b = Integer.parseInt(a);
							b = b + 1;
							a = a.valueOf(b);
							medtQtyCount.setText(a);
						}
					});
					mminus.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							int c = Integer.parseInt(medtQtyCount.getText()
									.toString());
							c = c - 1;
							medtQtyCount.setText(new Integer(c).toString());
						}
					});
					mok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							/*
							 * UOM[mspinnerUom.getSelectedItemPosition()] =
							 * medtQtyCount .getText().toString();
							 */
							MIC_UOMInternal mic_uom = new MIC_UOMInternal();
							mic_uom.setLocation(loc);
							mic_uom.setItemno(ItemNo);
							String updatedqtyCount = medtQtyCount.getText()
									.toString();

							if (!qtyCount.equals(updatedqtyCount)) {
								mic_uom.setQc(Double
										.parseDouble(updatedqtyCount));
								mic_uom.setUom(StockUnit);

								MIC_Inventory mic_Inventory = new MIC_Inventory();
								mic_Inventory.setItemdescription(Desc);
								mic_Inventory.setItemno(ItemNo);
								mic_Inventory.setLocation(loc);
								mic_Inventory.setPickingseq(PickSeq);
								mic_Inventory.setQc(updatedqtyCount);
								mic_Inventory.setQoh(qtyonHand);
								mic_Inventory.setStockunit(StockUnit);

								dbHandler.getWritableDatabase();
								String result = dbHandler
										.insertIntoInternal(mic_uom);
								if (result.equals("success")) {
									result = dbHandler.updateMIC(mic_Inventory);
								}
								dbHandler.closeDatabase();
							}
							Intent i = new Intent(InventoryCount.this,
									InventoryCount.class);
							i.putExtra("et", 1);
							i.putExtra("LOCATION", loc);
							// i.putExtra("ID", ID);
							startActivity(i);
							// InventoryCount.this.finish();

						}
					});
					mcancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							alertDialog.cancel();

						}
					});

					// show it
					alertDialog.show();

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
		else if (requestCode == 2) {
			// Manf No
			if (resultCode == RESULT_OK) {
				Log.i("Scan result format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				String upcCode = intent.getStringExtra("SCAN_RESULT");

				dbhelper.getReadableDatabase();
				MIC_UPC upc = dbhelper.getItemFromUpc_mic(upcCode);
				dbhelper.closeDatabase();

				if (upc != null) {
					String Itno = upc.getItemno();
					dbhelper.getReadableDatabase();
					boolean flag = dbhelper.checkMicInventoryDetails(loc, Itno);
					dbhelper.closeDatabase();
					if (flag == true) {
						dbhelper.getReadableDatabase();
						MIC_Inventory mic_inventory = dbhelper
								.getMicInventoryDetails(loc, upc.getItemno());
						dbhelper.closeDatabase();

						/*
						 * Intent i = new Intent(InventoryCount.this,
						 * QuantityUpdate.class);
						 * 
						 * i.putExtra("itemNo", mic_inventory.getItemno());
						 * i.putExtra("qtyonHand", mic_inventory.getQoh());
						 * i.putExtra("qtyCounted", mic_inventory.getQc());
						 * i.putExtra("loc", mic_inventory.getLocation());
						 * i.putExtra("desc",
						 * mic_inventory.getItemdescription());
						 * i.putExtra("pickSeq", mic_inventory.getPickingseq());
						 * i.putExtra("uom", mic_inventory.getStockunit());
						 * startActivity(i);
						 */
						ItemNo = mic_inventory.getItemno();
						Desc = mic_inventory.getItemdescription();
						PickSeq = mic_inventory.getPickingseq();
						StockUnit = mic_inventory.getStockunit();
						qtyonHand = mic_inventory.getQoh();// This value gives
															// QOHand
						qtyCount = mic_inventory.getQc();

						medtItem.setText(ItemNo);
						medtdesc.setText(Desc);
						medtQtyCount.setText(qtyCount);
						medtuom.setText(StockUnit);

						mplus.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								String a = medtQtyCount.getText().toString();
								int b = Integer.parseInt(a);
								b = b + 1;
								a = a.valueOf(b);
								medtQtyCount.setText(a);
							}
						});
						mminus.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								int c = Integer.parseInt(medtQtyCount.getText()
										.toString());
								c = c - 1;
								medtQtyCount.setText(new Integer(c).toString());
							}
						});
						mok.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								/*
								 * UOM[mspinnerUom.getSelectedItemPosition()] =
								 * medtQtyCount .getText().toString();
								 */
								MIC_UOMInternal mic_uom = new MIC_UOMInternal();
								mic_uom.setLocation(loc);
								mic_uom.setItemno(ItemNo);
								String updatedqtyCount = medtQtyCount.getText()
										.toString();

								if (!qtyCount.equals(updatedqtyCount)) {
									mic_uom.setQc(Double
											.parseDouble(updatedqtyCount));
									mic_uom.setUom(StockUnit);

									MIC_Inventory mic_Inventory = new MIC_Inventory();
									mic_Inventory.setItemdescription(Desc);
									mic_Inventory.setItemno(ItemNo);
									mic_Inventory.setLocation(loc);
									mic_Inventory.setPickingseq(PickSeq);
									mic_Inventory.setQc(updatedqtyCount);
									mic_Inventory.setQoh(qtyonHand);
									mic_Inventory.setStockunit(StockUnit);

									dbHandler.getWritableDatabase();
									String result = dbHandler
											.insertIntoInternal(mic_uom);
									if (result.equals("success")) {
										result = dbHandler
												.updateMIC(mic_Inventory);
									}
									dbHandler.closeDatabase();
								}
								Intent i = new Intent(InventoryCount.this,
										InventoryCount.class);
								i.putExtra("et", 1);
								i.putExtra("LOCATION", loc);
								// i.putExtra("ID", ID);
								startActivity(i);
								// InventoryCount.this.finish();

							}
						});
						mcancel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								alertDialog.cancel();

							}
						});

						// show it
						alertDialog.show();

					} else {/*
							 * Toast.makeText(this, "Item not available",
							 * Toast.LENGTH_SHORT).show();
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
						 * Toast.LENGTH_SHORT).show();
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

	}

	protected Object getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.inventory, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.main_mnu_import) {

			// get prompts.xml view
			LayoutInflater li = LayoutInflater.from(InventoryCount.this);
			View promptsView = li.inflate(R.layout.importmicdata, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					InventoryCount.this);

			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);

			final Button mbtn_ImpWorksheet;
			final Button mbtn_ImpLocation;

			final Spinner mspinner_FromLocation;
			final Spinner mspinner_ToLocation;
			final DatabaseHandler databaseHandler;
			final int POST_TASK = 1;
			final int GET_TASK = 2;
			final int taskType;
			final String InventoryDetails = "InventoryDetails";

			final String CompanyID;

			final TelephonyManager telephonemanager;

			final LayoutInflater mlayoutinflater;
			final View mview;
			final int progress_bar_type = 0;

			final SharedPreferences locationDatas;
			databaseHandler = new DatabaseHandler(InventoryCount.this);

			mbtn_ImpWorksheet = (Button) promptsView
					.findViewById(R.id.buimportworksheet);
			mbtn_ImpLocation = (Button) promptsView
					.findViewById(R.id.bugetlocations);
			mspinner_FromLocation = (Spinner) promptsView
					.findViewById(R.id.spn_frmLoc);
			mspinner_ToLocation = (Spinner) promptsView
					.findViewById(R.id.spn_toLoc);
			mbtn_ImpWorksheet.setEnabled(false);
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			telephonemanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			locationDatas = PreferenceManager
					.getDefaultSharedPreferences(InventoryCount.this);
			mspinner_FromLocation
					.setOnItemSelectedListener(new OnItemSelectedListener() {

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
			mspinner_ToLocation
					.setOnItemSelectedListener(new OnItemSelectedListener() {

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
			mbtn_ImpLocation.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					new GetLocation().execute();
				}

				class GetLocation extends
						AsyncTask<String, String, List<String>> {
					ProgressDialog locdialog;
					DatabaseHandler databaseHandler;

					GetLocation() {
						databaseHandler = new DatabaseHandler(
								getApplicationContext());
						locdialog = new ProgressDialog(InventoryCount.this);
						locdialog.setCancelable(false);
					}

					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						locdialog.setMessage("Loading Location...");
						locdialog.show();
					}

					@Override
					protected List<String> doInBackground(String... params) {
						// TODO Auto-generated method stub
						String result = "";
						/*
						 * ArrayAdapter<String> adapter = new
						 * ArrayAdapter<String>( InventoryCount.this,
						 * android.R.layout.simple_spinner_item);
						 */
						List<String> adapter = new ArrayList<String>();

						MIS_Setting mis_setting = new MIS_Setting();
						databaseHandler.getReadableDatabase();
						mis_setting = databaseHandler.getSetting();
						databaseHandler.closeDatabase();
						String deviceId = mis_setting.getDeviceId();
						String ipAddress = mis_setting.getIpAddress();

						String F_URL = "http://" + ipAddress
								+ "/MISWCFService/Service.svc/GetMICData";

						databaseHandler.getReadableDatabase();
						String CompanyID = databaseHandler
								.LOAD_COMPANYID(deviceId);
						databaseHandler.closeDatabase();

						String FulUrl = F_URL + "/" + CompanyID
								+ "/Location/0/0";
						try {
							JSONObject jobject = doLocResponse(FulUrl);
							if (jobject != null) {
								JSONArray jarray = jobject
										.getJSONArray("LocationDetail");
								JSONObject jobject2 = jarray.getJSONObject(0);
								String status = jobject2.getString("LOC");
								if (status.equals("encoError")
										|| status.equals("clientsideError")
										|| status.equals("socTimeError")
										|| status.equals("connTimeError")
										|| status.equals("jsonError")
										|| status.equals("hostconnError")
										|| status.equals("parseExcep")
										|| status.equals("io")
										|| status.equals("internetprob")) {
									adapter.add(status);

								} else {
									/*
									 * adapter.setDropDownViewResource(android.R.
									 * layout.simple_spinner_dropdown_item);
									 */if (jarray != null) {
										for (int i = 0; i < jarray.length(); i++) {
											jobject2 = jarray.getJSONObject(i);
											String str = jobject2
													.getString("LOC");
											adapter.add(str);

										}
										adapter.add("success");
									}

								}
							}
						}

						catch (Exception ex) {
							Toast.makeText(InventoryCount.this,
									ex.getLocalizedMessage(),
									Toast.LENGTH_SHORT).show();

							adapter.add("error");
							Log.e("ERROR", ex.getLocalizedMessage());
						}
						return adapter;

					}

					protected void onPostExecute(List<String> adap) {

						int adapSize = adap.size();
						String result = adap.get(adapSize - 1);
						if (result.equals("success")) {
							locdialog.dismiss();
							adap.remove("success");

							/*
							 * String[] spin_arry = new String[adap.size()]; if
							 * (adap != null || adap.size() > 0) { for (int i =
							 * 0; i < adap.size(); i++) { spin_arry[i] =
							 * adap.get(i); } }
							 */
							mlocAdapt = new ArrayAdapter<String>(
									InventoryCount.this,
									android.R.layout.simple_dropdown_item_1line,
									adap);

							mspinner_FromLocation.setAdapter(mlocAdapt);
							mspinner_ToLocation.setAdapter(mlocAdapt);

							mbtn_ImpLocation.setEnabled(false);
							mbtn_ImpWorksheet.setEnabled(true);

							toastText
									.setText("Locations Imported Successfully");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();

						} else {
							locdialog.dismiss();
							if (result.equals("encoError")
									|| result.equals("jsonError")
									|| result.equals("parseExcep")
									|| result.equals("io")) {
								toastText.setText("Improper Format of Data");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
								locdialog.dismiss();
							} else if (result.equals("socTimeError")
									|| result.equals("connTimeError")
									|| result.equals("parseExcep")) {
								toastText
										.setText("Time Out! Please check the Server Path and try again");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
								locdialog.dismiss();
							} else if (result.equals("hostconnError")
									|| result.equals("clientsideError")) {
								toastText
										.setText("Problem While Establishing Connection with Server");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
								locdialog.dismiss();
							} else if (result.equals("internetprob")) {
								toastText
										.setText("Check Your Internet Connectivity");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
								locdialog.dismiss();
							}

							else {
								toastText
										.setText("Error Occured while Importing Locations");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
							}

						}
					}
				}

				private JSONObject doLocResponse(String fulUrl) {
					// The JSON we will get back as a response from the server
					JSONObject jsonResponse = null;
					String result = "";
					JSONObject outerObject = new JSONObject();
					JSONArray outerArray = new JSONArray();
					JSONObject innerObject = new JSONObject();

					// Http connections and data streams
					URL url;
					HttpURLConnection httpURLConnection = null;
					OutputStreamWriter outputStreamWriter = null;

					try {

						// open connection to the server
						url = new URL(fulUrl);
						httpURLConnection = (HttpURLConnection) url
								.openConnection();
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
									new InputStreamReader(httpURLConnection
											.getInputStream(), "utf-8"));
							String line;
							while ((line = bufferedReader.readLine()) != null) {
								stringBuilder.append(line).append("\n");
							}

							bufferedReader.close(); // close out the input
													// stream

							try {
								// Copy the JSON response to a local JSONObject
								jsonResponse = new JSONObject(stringBuilder
										.toString());
								jsonResponse = jsonResponse
										.getJSONObject(GetMICData);
							} catch (JSONException je) {
								je.printStackTrace();
							}

						}
						else
						{
							result = "internetprob";

							try {
								innerObject.put("LOC", result);
								outerArray.put(innerObject);
								outerObject.put("LocationDetail", outerArray);
								jsonResponse = outerObject;
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					} catch (UnsupportedEncodingException e) {
						result = "encoError";

						try {
							innerObject.put("LOC", result);
							outerArray.put(innerObject);
							outerObject.put("LocationDetail", outerArray);
							jsonResponse = outerObject;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					} catch (ClientProtocolException e) {
						result = "clientsideError";
						try {
							innerObject.put("LOC", result);
							outerArray.put(innerObject);
							outerObject.put("LocationDetail", outerArray);
							jsonResponse = outerObject;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} catch (SocketTimeoutException e) {
						result = "socTimeError";
						try {

							innerObject.put("LOC", result);
							outerArray.put(innerObject);
							outerObject.put("LocationDetail", outerArray);
							jsonResponse = outerObject;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} catch (ConnectTimeoutException e) {
						result = "connTimeError";
						try {
							innerObject.put("LOC", result);
							outerArray.put(innerObject);
							outerObject.put("LocationDetail", outerArray);
							jsonResponse = outerObject;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} catch (HttpHostConnectException e) {
						result = "hostconnError";
						try {
							innerObject.put("LOC", result);
							outerArray.put(innerObject);
							outerObject.put("LocationDetail", outerArray);
							jsonResponse = outerObject;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} catch (ParseException pe) {
						result = "parseExcep";
						try {
							innerObject.put("LOC", result);
							outerArray.put(innerObject);
							outerObject.put("LocationDetail", outerArray);
							jsonResponse = outerObject;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} catch (IOException io) {
						result = "io";
						try {
							innerObject.put("LOC", result);
							outerArray.put(innerObject);
							outerObject.put("LocationDetail", outerArray);
							jsonResponse = outerObject;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					} finally {
						if (httpURLConnection != null) {
							httpURLConnection.disconnect(); // close out our
															// http connection
						}

						if (outputStreamWriter != null) {
							try {
								outputStreamWriter.close(); // close our output
															// stream
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
						}
					}

					// Return the JSON response from the server.
					return jsonResponse;
				}// doResponse

			});

			mbtn_ImpWorksheet.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {

					try {
						MIS_Setting mis_setting = new MIS_Setting();
						databaseHandler.getReadableDatabase();
						mis_setting = databaseHandler.getSetting();
						databaseHandler.closeDatabase();
						String deviceId = mis_setting.getDeviceId();
						String ipAddress = mis_setting.getIpAddress();

						String F_URL = "http://" + ipAddress
								+ "/MISWCFService/Service.svc/GetMICData";
						new LoadFromURL().execute();
					}

					catch (Exception ex) {
						Toast.makeText(InventoryCount.this,
								ex.getLocalizedMessage(), Toast.LENGTH_LONG)
								.show();

						Log.e("ERROR", ex.getLocalizedMessage());

					}

				}

				class LoadFromURL extends AsyncTask<String, String, String> {

					ProgressDialog dialog;

					public LoadFromURL() {

						dialog = new ProgressDialog(InventoryCount.this);
						dialog.setCancelable(false);
					}

					protected void onPreExecute() {
						super.onPreExecute();
						dialog.setMessage("Please wait until worksheet is loaded..");
						dialog.show();
					}

					@Override
					protected String doInBackground(String... results) {
						String result = "";
						MIS_Setting mis_setting = new MIS_Setting();

						databaseHandler.getReadableDatabase();
						mis_setting = databaseHandler.getSetting();
						databaseHandler.closeDatabase();

						String deviceId = mis_setting.getDeviceId();
						String ipAddress = mis_setting.getIpAddress();

						String F_URL = "http://" + ipAddress
								+ "/MISWCFService/Service.svc/GetMICData";

						databaseHandler.getReadableDatabase();
						String CompanyID = databaseHandler
								.LOAD_COMPANYID(deviceId);
						databaseHandler.closeDatabase();

						// dialog.setMessage("Refreshing worksheet..");
						try {

							String FulUrl = F_URL
									+ "/"
									+ CompanyID
									+ "/WORKSHEET/"
									+ mspinner_FromLocation.getSelectedItem()
											.toString()
									+ "/"
									+ mspinner_ToLocation.getSelectedItem()
											.toString();

							JSONObject jobject = doWorkSheetResponse(FulUrl);
							String status = chkStatus(jobject);
							if (jobject != null && status.equals("Allow")) {
								try {
									dbhelper.getWritableDatabase();
									dbhelper.deleteConv();
									dbhelper.deleteManf();
									dbhelper.deleteInv();
									dbhelper.deleteUpc();
									dbhelper.closeDatabase();

									result = ConversionFactor(jobject);

									if (result.equals("success")
											|| result.equals(""))

										result = ManufactureNumber(jobject);

									if (result.equals("success")
											|| result.equals(""))

										result = InventoryDetails(jobject);

									if (result.equals("success")
											|| result.equals(""))
										result = UPC(jobject);

									SharedPreferences.Editor editor = locationDatas
											.edit();
									editor.putString(
											"From Location",
											mspinner_FromLocation
													.getItemAtPosition(
															mspinner_FromLocation
																	.getSelectedItemPosition())
													.toString());
									editor.putString(
											"To Location",
											mspinner_ToLocation
													.getItemAtPosition(
															mspinner_ToLocation
																	.getSelectedItemPosition())
													.toString());
									editor.commit();

									String frmLoc = mspinner_FromLocation
											.getItemAtPosition(
													mspinner_FromLocation
															.getSelectedItemPosition())
											.toString();
									String toLoc = mspinner_ToLocation
											.getItemAtPosition(
													mspinner_ToLocation
															.getSelectedItemPosition())
											.toString();

									System.out.println("FROM" + frmLoc + "TO"
											+ toLoc);
									databaseHandler.getWritableDatabase();
									databaseHandler.deleteMicLocation();
									databaseHandler.closeDatabase();

									databaseHandler.getWritableDatabase();
									databaseHandler.addMicLocation(frmLoc,
											toLoc);
									databaseHandler.closeDatabase();

									result = "success";
								} catch (Exception e) {
									result = "error";
								}
							} else {
								result = status;
							}

						} catch (Exception e) {
							Log.e("Error", e.getMessage());
							dialog.dismiss();
							result = "error";
						}
						return result;
					}

					private String chkStatus(JSONObject completeOrder) {
						// TODO Auto-generated method stub
						String result = null;
						try {

							JSONArray jarray = completeOrder
									.getJSONArray("ConversationFactor");
							if (!jarray.isNull(0)) {
								JSONObject jobject2 = jarray.getJSONObject(0);
								String str = jobject2.getString("ITEMNO");

								if (str.contains("socTimeError".toString())
										|| str.contains("connTimeError"
												.toString())) {
									result = "time out";

								} else if (str.contains("internetprob"
										.toString())) {
									result = "Internet Prob";
								} else if ((str.contains("clientsideError"
										.toString()) || str
										.contains("hostconnError".toString()))
										&& result == null) {

									result = "Connection prob";

								} else if ((str.contains("parseExcep"
										.toString())
										|| str.contains("io".toString())
										|| str.contains("jsonError".toString()) || str
											.contains("encoError".toString()))
										&& result == null) {
									result = "Data prob";

								}

							}
							jarray = completeOrder
									.getJSONArray("InventoryDetails");
							if (!jarray.isNull(0)) {
								JSONObject jobject2 = jarray.getJSONObject(0);
								String str = jobject2.getString("ITEMNO");

								if (result == null
										&& (str.contains("socTimeError"
												.toString()) || str
												.contains("connTimeError"
														.toString()))) {
									result = "time out";

								} else if (result == null
										&& (str.contains("clientsideError"
												.toString()) || str
												.contains("hostconnError"
														.toString()))) {

									result = "Connection prob";

								} else if (result == null
										&& (str.contains("parseExcep"
												.toString())
												|| str.contains("io".toString())
												|| str.contains("jsonError"
														.toString()) || str
													.contains("encoError"
															.toString()))) {
									result = "Data prob";

								}

							}

							jarray = completeOrder
									.getJSONArray("ManufactureNumber");
							if (!jarray.isNull(0)) {
								JSONObject jobject2 = jarray.getJSONObject(0);
								String str = jobject2.getString("ITEMNO");

								if (result == null
										&& (str.contains("socTimeError"
												.toString()) || str
												.contains("connTimeError"
														.toString()))) {
									result = "time out";

								} else if (result == null
										&& (str.contains("clientsideError"
												.toString()) || str
												.contains("hostconnError"
														.toString()))) {

									result = "Connection prob";

								} else if (result == null
										&& (str.contains("parseExcep"
												.toString())
												|| str.contains("io".toString())
												|| str.contains("jsonError"
														.toString()) || str
													.contains("encoError"
															.toString()))) {
									result = "Data prob";

								}

							}

							jarray = completeOrder.getJSONArray("UPC");
							if (!jarray.isNull(0)) {
								JSONObject jobject2 = jarray.getJSONObject(0);
								String str = jobject2.getString("ITEMNO");

								if (result == null
										&& (str.contains("socTimeError"
												.toString()) || str
												.contains("connTimeError"
														.toString()))) {
									result = "time out";

								} else if (result == null
										&& (str.contains("clientsideError"
												.toString()) || str
												.contains("hostconnError"
														.toString()))) {

									result = "Connection prob";

								} else if (result == null
										&& (str.contains("parseExcep"
												.toString())
												|| str.contains("io".toString())
												|| str.contains("jsonError"
														.toString()) || str
													.contains("encoError"
															.toString()))) {
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

					private String UPC(JSONObject jobject) {
						// TODO Auto-generated method stub
						String result = "";
						try {
							JSONArray jarray = jobject.getJSONArray("UPC");
							if (jarray != null) {
								databaseHandler.getWritableDatabase();
								for (int i = 0; i < jarray.length(); i++) {
									JSONObject jobject2 = jarray
											.getJSONObject(i);

									result = databaseHandler
											.addMic_upc(jobject2);
								}
								databaseHandler.closeDatabase();
							}
							jarray = null;
						} catch (Exception ex) {
							Toast.makeText(InventoryCount.this,
									ex.getLocalizedMessage(), Toast.LENGTH_LONG)
									.show();
							result = "error";
							dialog.dismiss();
							Log.e("ERROR", ex.getLocalizedMessage());
						}
						return result;
					}

					private String InventoryDetails(JSONObject jobject) {
						// TODO Auto-generated method stub
						String result = "";
						String test = "";
						try {
							JSONArray jarray = jobject
									.getJSONArray(InventoryDetails);
							if (jarray != null) {
								databaseHandler.getWritableDatabase();
								for (int i = 0; i < jarray.length(); i++) {
									JSONObject jobject2 = jarray
											.getJSONObject(i);

									result = databaseHandler
											.addMic_inventory(jobject2);
									test = jobject2.getString("ITEMNO");
								}
								databaseHandler.closeDatabase();
								jarray = null;
							}

						} catch (Exception ex) {
							Toast.makeText(InventoryCount.this,
									ex.getLocalizedMessage(), Toast.LENGTH_LONG)
									.show();
							result = "error";
							dialog.dismiss();
							Log.e("ERROR", ex.getLocalizedMessage());
							lfc.appendLog("Error in Item " + test);
						}
						return result;
					}

					private String ManufactureNumber(JSONObject jobject) {
						// TODO Auto-generated method stub
						String result = "";
						try {
							JSONArray jarray = jobject
									.getJSONArray("ManufactureNumber");

							if (jarray != null) {
								databaseHandler.getWritableDatabase();
								for (int i = 0; i < jarray.length(); i++) {
									JSONObject jobject2 = jarray
											.getJSONObject(i);

									result = databaseHandler
											.addMic_manufacturenumber(jobject2);

								}
								databaseHandler.closeDatabase();
							}
							jarray = null;
						} catch (Exception ex) {
							Toast.makeText(InventoryCount.this,
									ex.getLocalizedMessage(), Toast.LENGTH_LONG)
									.show();
							dialog.dismiss();
							result = "error";
							Log.e("ERROR", ex.getLocalizedMessage());
						}
						return result;
					}

					private String ConversionFactor(JSONObject jobject) {
						// TODO Auto-generated method stub
						String result = "";
						String test = "";
						try {
							JSONArray jarray = jobject
									.getJSONArray("ConversationFactor");
							// Log.i("jArray Lenbgth","fgf"+jarray.length());
							databaseHandler.getWritableDatabase();
							if (jarray != null) {
								for (int i = 0; i < jarray.length(); i++) {
									JSONObject jobject2 = jarray
											.getJSONObject(i);
									test = jobject2.getString("ITEMNO");
									result = databaseHandler
											.addMic_conversionfactor(new MIC_Conversionfactor(
													jobject2.getString("ITEMNO"),
													Double.parseDouble(jobject2
															.getString("CONVERSION")),
													jobject2.getString("UNIT")));

								}
								databaseHandler.closeDatabase();
							}
							jarray = null;
						} catch (Exception ex) {
							Toast.makeText(InventoryCount.this,
									ex.getLocalizedMessage(), Toast.LENGTH_LONG)
									.show();
							dialog.dismiss();
							result = "error";

							lfc.appendLog("Error in Item " + test);
							Log.e("ERROR", ex.getLocalizedMessage());
						}
						return result;
					}

					private JSONObject doWorkSheetResponse(String Url) {

						// The JSON we will get back as a response from the
						// server
						JSONObject jsonResponse = null;
						String result = "";
						JSONObject outerObject = new JSONObject();
						JSONArray outerArray = new JSONArray();
						JSONObject innerObject = new JSONObject();

						// Http connections and data streams
						URL url;
						HttpURLConnection httpURLConnection = null;
						OutputStreamWriter outputStreamWriter = null;

						try {

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
											.getJSONObject(GetMICData);
								} catch (JSONException je) {
									
									result = "jsonError";
									try {
										innerObject.put("ITEMNO", result);
										outerArray.put(innerObject);
										outerObject.put("ConversationFactor",
												outerArray);

										innerObject.put("ITEMNO", result);
										outerArray.put(innerObject);
										outerObject.put("InventoryDetails", outerArray);

										innerObject.put("ITEMNO", result);
										outerArray.put(innerObject);
										outerObject
												.put("ManufactureNumber", outerArray);

										innerObject.put("ITEMNO", result);
										outerArray.put(innerObject);
										outerObject.put("UPC", outerArray);

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
									innerObject.put("ITEMNO", result);
									outerArray.put(innerObject);
									outerObject.put("ConversationFactor",
											outerArray);

									innerObject.put("ITEMNO", result);
									outerArray.put(innerObject);
									outerObject.put("InventoryDetails", outerArray);

									innerObject.put("ITEMNO", result);
									outerArray.put(innerObject);
									outerObject
											.put("ManufactureNumber", outerArray);

									innerObject.put("ITEMNO", result);
									outerArray.put(innerObject);
									outerObject.put("UPC", outerArray);

									jsonResponse = outerObject;
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						} catch (UnsupportedEncodingException e) {
							result = "encoError";

							try {
								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("ConversationFactor",
										outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("InventoryDetails", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject
										.put("ManufactureNumber", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("UPC", outerArray);

								jsonResponse = outerObject;
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						} catch (ClientProtocolException e) {
							result = "clientsideError";
							try {
								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("ConversationFactor",
										outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("InventoryDetails", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject
										.put("ManufactureNumber", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("UPC", outerArray);

								jsonResponse = outerObject;
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (SocketTimeoutException e) {
							result = "socTimeError";
							try {
								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("ConversationFactor",
										outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("InventoryDetails", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject
										.put("ManufactureNumber", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("UPC", outerArray);

								jsonResponse = outerObject;
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (ConnectTimeoutException e) {
							result = "connTimeError";
							try {
								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("ConversationFactor",
										outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("InventoryDetails", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject
										.put("ManufactureNumber", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("UPC", outerArray);

								jsonResponse = outerObject;
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (HttpHostConnectException e) {
							result = "hostconnError";
							try {
								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("ConversationFactor",
										outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("InventoryDetails", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject
										.put("ManufactureNumber", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("UPC", outerArray);

								jsonResponse = outerObject;
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (ParseException pe) {
							result = "parseExcep";
							try {
								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("ConversationFactor",
										outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("InventoryDetails", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject
										.put("ManufactureNumber", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("UPC", outerArray);

								jsonResponse = outerObject;
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (IOException io) {
							result = "io";
							try {
								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("ConversationFactor",
										outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("InventoryDetails", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject
										.put("ManufactureNumber", outerArray);

								innerObject.put("ITEMNO", result);
								outerArray.put(innerObject);
								outerObject.put("UPC", outerArray);

								jsonResponse = outerObject;
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} finally {
							if (httpURLConnection != null) {
								httpURLConnection.disconnect(); // close out our
																// http
																// connection
							}

							if (outputStreamWriter != null) {
								try {
									outputStreamWriter.close(); // close our
																// output stream
								} catch (IOException ioe) {
									ioe.printStackTrace();
								}
							}
						}

						// Return the JSON response from the server.
						return jsonResponse;

					}

					// TODO Auto-generated method stub
					private String convertStreamToString(InputStream is) {

						BufferedReader reader = new BufferedReader(
								new InputStreamReader(is));
						StringBuilder sb = new StringBuilder();

						String line = null;
						try {
							while ((line = reader.readLine()) != null) {
								sb.append(line + "\n");
							}
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								is.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						return sb.toString();

					}

					protected void onProgressUpdate(String... progress) {
						super.onProgressUpdate(progress);
						// pd.setProgress(Integer.parseInt(progress[0]));
					}

					protected void onPostExecute(String result) {
						if (result.equals("success")) {
							dialog.dismiss();

							Intent i = new Intent(InventoryCount.this,
									InventoryCount.class);
							startActivity(i);
							InventoryCount.this.finish();
						} else {

							if (result.contains("time out".toString())) {
								/*
								 * Toast.makeText(MseImportData.this,
								 * "Time Out! Please check the Server Path and try again"
								 * , Toast.LENGTH_LONG).show();
								 */
								toastText
										.setText("Time Out! Please check the Server Path and try again");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
								dialog.dismiss();
							} else if (result.contains("Connection prob"
									.toString())) {
								/*
								 * Toast.makeText( MseImportData.this,
								 * "Problem while establishing connection with Server"
								 * , Toast.LENGTH_LONG).show();
								 */toastText
										.setText("Problem While Establishing Connection with Server");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
								dialog.dismiss();
							} else if (result.contains("Data prob".toString())) {
								/*
								 * Toast.makeText(MseImportData.this,
								 * "Improper Format of Data", Toast.LENGTH_LONG)
								 * .show();
								 */toastText.setText("Improper Format of Data");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
								dialog.dismiss();
							} else if (result.contains("Internet Prob"
									.toString())) {
								/*
								 * Toast.makeText(MseImportData.this,
								 * "Improper Format of Data", Toast.LENGTH_LONG)
								 * .show();
								 */toastText
										.setText("Check your Internet Connectivity");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
								dialog.dismiss();
							}

							else {
								/*
								 * Toast.makeText(MseImportData.this,
								 * "Order Not Imported Successfully",
								 * Toast.LENGTH_LONG).show();
								 */
								toastText
										.setText("WorkSheet not Imported Successfully");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
								dialog.dismiss();
							}
						}
					}

				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

			/* } */

		} else if (id == R.id.main_mnu_Export) {
			new Export().execute();
		} else {
			Intent i2 = new Intent(InventoryCount.this, Login.class);
			startActivity(i2);
			InventoryCount.this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	class InflateList extends AsyncTask<String, String, List<MIC_OrderDetails>> {
		ProgressDialog dialog;
		Context context;
		String spinLoc;

		public InflateList(String spinloc) {
			dialog = new ProgressDialog(InventoryCount.this);
			dialog.setCancelable(false);
			this.spinLoc = spinloc;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Please wait while Data is Loading...");
			dialog.show();
		}

		@Override
		protected List<MIC_OrderDetails> doInBackground(String... params) {
			// TODO Auto-generated method stub
			String result = "";
			List<MIC_OrderDetails> lst = new ArrayList<MIC_OrderDetails>();
			try {

				dbhelper.getReadableDatabase();
				lst = dbhelper.getdata(spinLoc);
				dbhelper.closeDatabase();

				result = "success";
				MIC_OrderDetails mic_OrderDetails = new MIC_OrderDetails();
				mic_OrderDetails.setResult(result);
				lst.add(mic_OrderDetails);

			}

			catch (Exception e) {
				result = "error";
				MIC_OrderDetails mic_OrderDetails = new MIC_OrderDetails();
				mic_OrderDetails.setResult(result);
				lst.add(mic_OrderDetails);
				dialog.dismiss();
				Log.e("Failed", e.getLocalizedMessage());
			}
			return lst;
		}

		protected void onPostExecute(List<MIC_OrderDetails> lst) {
			dialog.setMessage("Inflating Data...");
			if (lst.get(lst.size() - 1).getResult().contains(("success"))) {
				ordList = new MicListAdapter(InventoryCount.this, lst);
				lstView.setAdapter(ordList);
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

	/*
	 * public String inserttolist(String SpnLocation) { String result=""; try {
	 * 
	 * List<MIC_OrderDetails> lst = new ArrayList<MIC_OrderDetails>();
	 * dbhelper.getReadableDatabase(); lst = dbhelper.getdata(SpnLocation);
	 * dbhelper.closeDatabase();
	 * 
	 * ordList = new MicListAdapter(getApplicationContext(), lst);
	 * lstView.setAdapter(ordList); result="success"; }
	 * 
	 * catch (Exception e) { result="error"; Log.e("Failed",
	 * e.getLocalizedMessage()); } return result;
	 * 
	 * }
	 */

	public String ExportData() {
		String result = "";
		putmicdata = new JSONObject();
		exportdata = new JSONObject();
		String Query = "SELECT * FROM MIC_UOMINTERNAL where Location='" + loc
				+ "'";
		String Location, ItemNo, UOM;
		int QTYCounted;
		valueheader = new JSONArray();
		valueheaderdetail = new JSONArray();

		try {
			dbhelper.getReadableDatabase();
			int Count = GetCount();
			dbhelper.closeDatabase();
			if (Count > 0) {
				dbhelper.getReadableDatabase();
				Cursor cur = dbhelper.GetData1(Query);
				JSONArray jarray1;
				JSONArray jarray2;
				if (cur != null && cur.moveToFirst() == true) {
					cur.moveToFirst();
					while (!cur.isAfterLast()) {

						Location = cur
								.getString(cur.getColumnIndex("Location"));
						ItemNo = cur
								.getString(cur.getColumnIndex("ItemNumber"));
						QTYCounted = cur.getInt(cur
								.getColumnIndex("QuantityCounted"));

						jarray1 = new JSONArray();
						jarray1.put(0, Location);
						jarray1.put(1, ItemNo);
						jarray1.put(2, QTYCounted);

						jarray2 = new JSONArray();
						jarray2.put(0, Location);
						jarray2.put(1, ItemNo);
						jarray2.put(2, QTYCounted);

						valueheader.put(jarray1);
						valueheaderdetail.put(jarray2);
						cur.moveToNext();

					}
				}
				cur.close();
				dbhelper.closeDatabase();
				putmicdata.put("CompanyID", companyid);
				putmicdata.put("ValueHeader", valueheader);
				putmicdata.put("ValueHeaderDetail", valueheaderdetail);
				result = "success";
			} else {
				result = "no data";
			}
		} catch (Exception e) {
			result = "error";

			Log.e("Failed", e.getLocalizedMessage());
		}
		return result;
	}

	public int GetCount() {

		int Count = 0;
		Cursor mCur = null;
		String Query = "SELECT COUNT(*) FROM MIC_UOMINTERNAL where Location='"
				+ loc + "'";
		try {

			mCur = dbhelper.GetData1(Query);
			if (mCur != null) {
				mCur.moveToFirst();
				Count = mCur.getInt(0);
			}
		} catch (Exception ex) {
			Log.e("Failed", ex.getLocalizedMessage());
		}
		mCur.close();

		return Count;
	}

	/*
	 * public void getBarCodeData(String itmNumber) {
	 * 
	 * Cursor c=null; try {
	 * 
	 * String qtyCountQry = "SELECT " + DatabaseHandler.KEY_QTYCOUNTED +
	 * " FROM " + DatabaseHandler.TABLE_MIC2 + " WHERE " +
	 * DatabaseHandler.KEY_ITEMNUMBER + "='" + itmNumber + "'"; SQLiteDatabase
	 * sq = db.getReadableDatabase(); c = sq.rawQuery(qtyCountQry, null);
	 * c.moveToFirst(); String q2 = c.getString(c
	 * .getColumnIndex(DatabaseHandler.KEY_QTYCOUNTED));
	 * Toast.makeText(InventoryCount.this, "Quantity Count is " + q2,
	 * Toast.LENGTH_SHORT).show(); int q=Integer.parseInt(q2); q=q+1; String
	 * qty=String.valueOf(q); ContentValues cv=new ContentValues();
	 * cv.put(DatabaseHandler.KEY_ITEMNUMBER, qty);
	 * sq.update(DatabaseHandler.TABLE_MIC2, cv, "QuantityCounted='"+qty+"'",
	 * null);
	 * 
	 * sca.notifyDataSetChanged(); String
	 * incrementQry="INSERT INTO "+DatabaseHandler
	 * .TABLE_MIC2+"WHERE"+DatabaseHandler.KEY_QTYCOUNTED+"="+(q-1)+"";
	 * SQLiteDatabase sd = db.getWritableDatabase(); c=sd.rawQuery(incrementQry,
	 * null); inserttolist(spinlocation);
	 * 
	 * } catch (Exception e) { Toast.makeText(InventoryCount.this, "Exception "
	 * + e, Toast.LENGTH_SHORT).show(); }
	 * 
	 * }
	 */
	class Export extends AsyncTask<String, String, String> {

		ProgressDialog dialog;

		public Export() {

			dialog = new ProgressDialog(InventoryCount.this);
			dialog.setCancelable(false);

		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Exporting WorkSheet..");
			dialog.show();
		}

		protected String doInBackground(String... results) {
			String result = "";
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			MIS_Setting mis_setting = new MIS_Setting();
			dbhelper.getReadableDatabase();
			mis_setting = dbhelper.getSetting();
			dbhelper.closeDatabase();
			String ipAddress = mis_setting.getIpAddress();

			String F_URL = "http://" + ipAddress
					+ "/MISWCFService/Service.svc/PutMIC";

			result = ExportData();
			if (result.equals("success")) {
				String total;
				HttpResponse response = null;

				HttpClient httpclient = new DefaultHttpClient();

				if (isConnected(getApplicationContext())) {
					try {
						HttpPost httppost = new HttpPost(F_URL);

						System.out.println("URL...." + F_URL);

						httppost.setHeader("Accept", "application/json");
						httppost.setHeader("Content-Type", "application/json");
						JSONStringer jsonStringer = new JSONStringer().object()
								.key("putmicdata").object().key("CompanyID")
								.value(companyid).key("ValueHeader")
								.value(valueheader).key("ValueHeaderDetail")
								.value(valueheaderdetail).endObject();
						StringEntity entity = new StringEntity(
								jsonStringer.toString());

						System.out.println("String...."
								+ jsonStringer.toString());
						entity.setContentType(new BasicHeader(
								HTTP.CONTENT_TYPE, "application/json"));
						httppost.setEntity(entity);

						response = httpclient.execute(httppost);

						StatusLine statusLine = response.getStatusLine();
						int statusCode = statusLine.getStatusCode();

						System.out.println("StatusCode for MIC" + statusCode);

						if (response != null) {
							HttpEntity httpEntity = response.getEntity();
							total = EntityUtils.toString(httpEntity);
							dbhelper.getWritableDatabase();
							dbhelper.DELETE_MICUOMINTERNAL(loc);
							dbhelper.closeDatabase();
						}

						result = "success";
					}

					catch (UnsupportedEncodingException e) {
						result = "encoError";
					} catch (ClientProtocolException e) {
						result = "clientsideError";
					} catch (SocketTimeoutException e) {
						result = "socTimeError";
					} catch (ConnectTimeoutException e) {
						result = "connTimeError";

					} catch (JSONException ex) {
						result = "jsonError";
					} catch (HttpHostConnectException e) {
						result = "hostconnError";
					} catch (ParseException pe) {
						result = "parseExcep";
					} catch (IOException io) {
						result = "io";
					} catch (Exception exp) {
						result = "error";

					}

				} else {
					result = "internetprob";
				}
			} else {
				result = "error";
			}

			return result;
		}

		protected void onProgressUpdate(String... progress) {
			super.onProgressUpdate(progress);
			// pd.setProgress(Integer.parseInt(progress[0]));
		}

		protected void onPostExecute(String result) {

			if (result.equals("success")) {
				dialog.dismiss();
				/*
				 * Toast.makeText(InventoryCount.this,
				 * "Data Exported Successfully", Toast.LENGTH_SHORT) .show();
				 */

				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						InventoryCount.this);
				alertDialog.setTitle("Info");
				alertDialog.setIcon(R.drawable.rsz_ok1);
				alertDialog.setCancelable(false);
				alertDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Log.i("Transaction success",
										"Transaction success.");

							}
						});
				alertDialog.setMessage("WorkSheet Exported Successfully");

				alertDialog.show();

			} else if (result.equals("no data")) {
				dialog.dismiss();
				/*
				 * Toast.makeText(InventoryCount.this,
				 * "No Transaction For Export", Toast.LENGTH_SHORT).show();
				 */
				toastText.setText("Transaction Not Available");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(toastLayout);
				toast.show();
			} else {
				dialog.dismiss();
				/*
				 * Toast.makeText(InventoryCount.this, "Error while Exporting",
				 * Toast.LENGTH_SHORT).show();
				 */

				if (result.contains("socTimeError".toString())
						|| result.contains("connTimeError".toString())) {

					toastText
							.setText("Time Out! Please check the Server Path and try again");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

				} else if (result.contains("clientsideError".toString())
						|| result.contains("hostconnError".toString())) {
					toastText
							.setText("Problem While Establishing Connection with Server");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

				} else if (result.contains("parseExcep".toString())
						|| result.contains("io".toString())
						|| result.contains("jsonError".toString())
						|| result.contains("encoError".toString())) {
					toastText.setText("Improper Format of Data");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				} else if (result.contains("internetprob".toString())) {
					toastText.setText("Check Your Internet Connectivity");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				}

				else {
					toastText.setText("Error while Exporting WorkSheet");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				}

			}
		}

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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent i2 = new Intent(InventoryCount.this, Login.class);
			startActivity(i2);
			InventoryCount.this.finish();

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unRegisterBaseActivityReceiver();
	}

}
