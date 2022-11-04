package com.mis.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.mobinventorysuit.R;
import com.mis.adapter.OrderListAdapter_mpr;
import com.mis.common.AppBaseActivity;
import com.mis.database.DatabaseHandler;
import com.mis.mpr.model.MPR_MasterDetails;
import com.mis.mpr.model.MPR_OrderDetails;
import com.mis.mpr.model.MPR_Upc;
import com.mis.mpr.model.Manf_Number01_mpr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.ListView;

public class MprItemList extends AppBaseActivity {
	private EditText ordNo;
	private EditText custNo;

	private RadioGroup radioScan;
	private EditText btnScan;
	private RadioButton radioUpc;
	private RadioButton radioItem;
	private RadioButton radioManf;

	String itemno, shipVia, pickSeq, uom, comment;
	Integer qtyOrderd, qtyShipd;

	private Button btnExit;
	private ListView lstDetails;
	TextView toastText;
	View toastLayout;
	private SQLiteDatabase db;
	MPR_MasterDetails mpr_MasterDetails = new MPR_MasterDetails();
	MPR_OrderDetails mpr_OrderDetails = new MPR_OrderDetails();

	DatabaseHandler dbhelper;
	private List<MPR_OrderDetails> ordList;
	String orderNo;
	public static String flag = "false";

	private ArrayAdapter<MPR_OrderDetails> adapter;


	public static ArrayList<String> selected_ord = new ArrayList<String>();;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mpr_itemlist);

		registerBaseActivityReceiver();
		ordNo = (EditText) findViewById(R.id.edt_mprPoNo);
		custNo = (EditText) findViewById(R.id.edt_mprVendorNo);

		btnScan = (EditText) findViewById(R.id.edt_mprSearch);
		btnExit = (Button) findViewById(R.id.btn_mprExit);
		radioScan = (RadioGroup) findViewById(R.id.radioScanBasedOn);
		lstDetails = (ListView) findViewById(R.id.lst_mprfull);

		radioUpc = (RadioButton) findViewById(R.id.radioUpc);
		radioItem = (RadioButton) findViewById(R.id.radioInum);
		radioManf = (RadioButton) findViewById(R.id.radioNum);

		// Toast
		LayoutInflater inflater = getLayoutInflater();
		toastLayout = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		toastText = (TextView) toastLayout.findViewById(R.id.text);

		dbhelper = new DatabaseHandler(this);
		ordList = new ArrayList<MPR_OrderDetails>();

		if (flag == "false") {
			orderNo = getIntent().getStringExtra("ordNo_OrderList");

		} else {
			orderNo = getIntent().getStringExtra("OrdNo_ShipMent");

			flag = "false";

		}

		try {
			dbhelper.getReadableDatabase();
			mpr_MasterDetails = dbhelper.getMprMasterDetails(orderNo);
			dbhelper.closeDatabase();

			dbhelper.getReadableDatabase();
			mpr_OrderDetails = dbhelper.getMprOrderDetails(orderNo);
			dbhelper.closeDatabase();

			ordNo.setText(mpr_OrderDetails.getPo_number());
			custNo.setText(mpr_OrderDetails.getVd_code());

			dbhelper.getReadableDatabase();
			ordList = dbhelper.getMprDataMPR_OrderDetails(orderNo);
			dbhelper.closeDatabase();

			adapter = new OrderListAdapter_mpr(this, ordList);
			lstDetails.setAdapter(adapter);

		} catch (Exception ex) {
			Log.e("Error", ex.getMessage());

		}
		btnExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MprItemList.this, MprOrderList.class);
				MprOrderList.returnfrm = "true";
				startActivity(i);
			}
		});
		lstDetails.setOnItemClickListener(new OnItemClickListener() {

			private EditText edt_itemNo;
			private EditText edt_qtyOrd;
			private EditText edt_qtyShiped;
			private EditText edt_pickSeq;
			private EditText edt_shipVia;
			// private EditText edt_serialNo;
			private EditText edt_comments;
			private EditText edt_uom;
			private Button btn_Incr;
			private Button btn_Decr;
			Integer count = 0;
			private Button btn_Ok;
			private Button btn_Cancel;
			String Ordno;
			// private Button btn_Add;
			Cursor cursor_orddetails;
			// private ListView lst_serial;

			ArrayList<String> alst;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				MPR_OrderDetails list_obj = new MPR_OrderDetails();
				list_obj = (MPR_OrderDetails) lstDetails
						.getItemAtPosition(position);

				itemno = list_obj.getItem_no();
				qtyOrderd = list_obj.getOrdered_qty();
				qtyShipd = list_obj.getReceived_qty();
				uom = list_obj.getUom();
				comment = list_obj.getComments();

				LayoutInflater li = LayoutInflater.from(MprItemList.this);
				View promptsView = li
						.inflate(R.layout.mpr_shipment_entry, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MprItemList.this);
				alertDialogBuilder.setView(promptsView);

				// create alert dialog

				if (qtyOrderd != 0 && qtyOrderd > 0) {
				
					btnExit.setVisibility(view.GONE);

					final AlertDialog alertDialog = alertDialogBuilder.create();
					/*
					 * Intent i = new Intent(MprItemList.this,
					 * MprShipmentEntry.class); i.putExtra("itemNo", itemno);
					 * i.putExtra("qtyOrder", qtyOrderd); i.putExtra("ordShipd",
					 * qtyShipd); i.putExtra("uom", uom); i.putExtra("ordNo",
					 * orderNo); i.putExtra("comment", comment);
					 * startActivity(i);
					 */

					// dbhelper = new DatabaseHandler(MprItemList.this);

					alst = new ArrayList<String>();// initialize array list
					/*
					 * aadap = new ArrayAdapter<String>(this,
					 * android.R.layout.simple_list_item_1, alst);
					 */
					edt_itemNo = (EditText) promptsView
							.findViewById(R.id.edt_mprshipItemNo);
					edt_qtyOrd = (EditText) promptsView
							.findViewById(R.id.edt_mprshipQtyOrd);
					edt_qtyShiped = (EditText) promptsView
							.findViewById(R.id.edt_shipShiped);
					/*
					 * pickSeq = (EditText)
					 * findViewById(R.id.edt_mseshipPickSeq); shipVia =
					 * (EditText) findViewById(R.id.edt_mseshipShipVia);
					 */
					// serialNo = (EditText)
					// findViewById(R.id.edt_mseshipSerial);
					edt_comments = (EditText) promptsView
							.findViewById(R.id.edt_mprshipComments);
					edt_uom = (EditText) promptsView
							.findViewById(R.id.edt_mprshipuom);

					btn_Ok = (Button) promptsView
							.findViewById(R.id.btn_mprshipOk);
					btn_Cancel = (Button) promptsView
							.findViewById(R.id.btn_mprshipCancel);
					btn_Incr = (Button) promptsView
							.findViewById(R.id.btn_mprshipIncr);
					btn_Decr = (Button) promptsView
							.findViewById(R.id.btn_mprshipDecr);

					if (qtyShipd == 0) {
						qtyShipd++;
					}

					edt_itemNo.setText(itemno);
					edt_qtyOrd.setText(qtyOrderd.toString());
					edt_qtyShiped.setText(qtyShipd.toString());
					edt_uom.setText(uom);
					edt_comments.setText(comment);

					alertDialog.setOnKeyListener(new OnKeyListener() {

						@Override
						public boolean onKey(DialogInterface dialog,
								int keyCode, KeyEvent event) {
							// TODO Auto-generated method stub
							if (keyCode == KeyEvent.KEYCODE_BACK) {

								btnExit.setVisibility(View.VISIBLE);
								alertDialog.cancel();
								return true;
							} else
								return false;

						}
					});

					btn_Incr.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							String value = edt_qtyShiped.getText().toString();
							if (!value.equals("")) {
								Integer val = Integer.parseInt(value);
								if (val < qtyOrderd) {
									val = val + 1;
									edt_qtyShiped.setText(val.toString());
								} else {
									/*
									 * Toast.makeText(getBaseContext(),
									 * "Maximum Shipped quantity reached",
									 * Toast.LENGTH_LONG).show();
									 */

									toastText
											.setText("Maximum Shipped quantity reached");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, 120);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();

								}
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
					btn_Decr.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							String value = edt_qtyShiped.getText().toString();
							if (!value.equals("")) {
								Integer val = Integer.parseInt(value);
								if (val >= 2) {
									val = val - 1;

									edt_qtyShiped.setText(val.toString());
								} else {
									/*
									 * Toast.makeText(getBaseContext(),
									 * "Minimum Shipped quantity reached",
									 * Toast.LENGTH_LONG).show();
									 */
									toastText
											.setText("Minimum Shipped quantity reached");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, 120);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();
								}
							}

							else {

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
					btn_Cancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
						btnExit.setVisibility(View.VISIBLE);
						alertDialog.cancel();}

					});
					btn_Ok.setOnClickListener(new OnClickListener() {
						String resu = null;

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							String val = edt_qtyShiped.getText().toString();
							if (val.matches("")) {
								toastText
										.setText("Please Enter the Shipped Qty");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										120);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();

							} else if (Integer.parseInt(edt_qtyShiped.getText()
									.toString()) < Integer.parseInt(edt_qtyOrd
									.getText().toString())
									&& Integer.parseInt(edt_qtyShiped.getText()
											.toString()) != 0) {
								updateOrderTransTables();
								// Toast.makeText(MprShipmentEntry.this,"Data Saved Successfully ",
								// Toast.LENGTH_SHORT).show();

							} else if (Integer.parseInt(edt_qtyShiped.getText()
									.toString()) == 0) {
								toastText.setText("Shipped Qty cannot be 0 ");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										120);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
							} else if (Integer.parseInt(edt_qtyShiped.getText()
									.toString()) == Integer.parseInt(edt_qtyOrd
									.getText().toString())) {
								final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
										MprItemList.this);
								alertDialog.setTitle("Confirmation");
								alertDialog.setIcon(R.drawable.warning);
								alertDialog.setCancelable(false);
								alertDialog.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {

												updateOrderTransTables();
												/*
												 * Toast.makeText(MprShipmentEntry
												 * .this,
												 * "Data Saved Successfully ",
												 * Toast.LENGTH_SHORT).show();
												 */

											}
										});
								alertDialog.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

											}
										});
								alertDialog
										.setMessage("Do you want Save the Changes ?");

								alertDialog.show();

							} else if (!(Integer.parseInt(edt_qtyShiped
									.getText().toString()) > 0)) {
								/*
								 * Toast.makeText(getBaseContext(),
								 * "Minimum Shipped quantity reached",
								 * Toast.LENGTH_LONG).show();
								 */
								toastText
										.setText("Minimum Received quantity reached");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										120);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
							} else if (!(Integer.parseInt(edt_qtyShiped
									.getText().toString()) > 0)) {
								/*
								 * Toast.makeText(getBaseContext(),
								 * "Minimum Shipped quantity reached",
								 * Toast.LENGTH_LONG).show();
								 */
								toastText
										.setText("Minimum Received quantity reached");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										120);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
							} else {
								/*
								 * Toast.makeText(getBaseContext(),
								 * "Received Qty Exceeds Order Qty",
								 * Toast.LENGTH_LONG) .show();
								 */
								toastText
										.setText("Received Qty Exceeds Order Qty");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										120);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
							}
						}
					});
					alertDialog.show();
				} else {
					toastText.setText("No Order Quantity");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

				}

			}

			private void updateOrderTransTables() {
				String resu = "";
				MPR_OrderDetails mpr_OrderDetails = new MPR_OrderDetails();

				String itno = edt_itemNo.getText().toString();
				Integer ordqy = Integer.parseInt(edt_qtyOrd.getText()
						.toString());
				String uomval = edt_uom.getText().toString();
				Integer shQty = Integer.parseInt(edt_qtyShiped.getText()
						.toString());
				String comment = edt_comments.getText().toString();

				mpr_OrderDetails.setComments(comment);
				mpr_OrderDetails.setReceived_qty(shQty);
				mpr_OrderDetails.setItem_no(itno);
				mpr_OrderDetails.setOrdered_qty(ordqy);
				mpr_OrderDetails.setUom(uomval);
				mpr_OrderDetails.setPo_number(orderNo);

				dbhelper.getWritableDatabase();
				String result = dbhelper.updateMpr_OrderDetails(
						mpr_OrderDetails, "Ship");
				dbhelper.closeDatabase();

				if (result.equals("success")) {

					dbhelper.getReadableDatabase();
					boolean flag = dbhelper.checkMPR();
					dbhelper.getReadableDatabase();
					if (flag == true) {
						System.out.println("FOR ADD");
						MPR_OrderDetails mpr_OrderDetailsForTrans = new MPR_OrderDetails();
						dbhelper.getReadableDatabase();
						mpr_OrderDetailsForTrans = dbhelper.getMprOrderDetails(
								orderNo, itno);
						dbhelper.closeDatabase();
						dbhelper.getWritableDatabase();
						resu = dbhelper
								.addMpr_TransDetails(mpr_OrderDetailsForTrans);
						dbhelper.closeDatabase();
					} else {
						dbhelper.getReadableDatabase();
						String val = dbhelper
								.checkReceiptInTrans(orderNo, itno);

						dbhelper.closeDatabase();
						if (val.equalsIgnoreCase("Yes") /* && test == false */) {
							System.out.println("FOR ADD");
							MPR_OrderDetails mpr_OrderDetailsForTrans = new MPR_OrderDetails();
							dbhelper.getReadableDatabase();
							mpr_OrderDetailsForTrans = dbhelper
									.getMprOrderDetails(orderNo, itno);
							dbhelper.closeDatabase();
							dbhelper.getWritableDatabase();
							resu = dbhelper
									.addMpr_TransDetails(mpr_OrderDetailsForTrans);
							dbhelper.closeDatabase();
						} else {
							System.out.println("FOR UPDATE");
							dbhelper.getWritableDatabase();
							resu = dbhelper
									.updateMpr_TransDetails(mpr_OrderDetails);
							dbhelper.closeDatabase();
						}
					}
				}
				if (resu.equals("success")) {
					MprItemList.flag = "true";
					Intent i = new Intent(MprItemList.this, MprItemList.class);
					i.putExtra("OrdNo_ShipMent", orderNo);
					startActivity(i);

				}

				else {
					toastText.setText("Input not Valid");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				}

			}

		});
		btnScan.setOnClickListener(new OnClickListener() {

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
		btnScan.setOnFocusChangeListener(new OnFocusChangeListener() {
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

	public String formatDate(String gotDate) {

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String parsedDate = null;

		try {
			Date date = new SimpleDateFormat("yyyyMMdd").parse(gotDate);

			parsedDate = formatter.format(date);
			System.out.println(date);
			System.out.println(formatter.format(date));
		} catch (Exception e) {

			e.printStackTrace();
		}
		return parsedDate;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		final EditText edt_itemNo;
		final EditText edt_qtyOrd;
		final EditText edt_qtyShiped;
		final EditText edt_pickSeq;
		final EditText edt_shipVia;
		// final EditText edt_serialNo;
		final EditText edt_comments;
		final EditText edt_uom;
		final Button btn_Incr;
		final Button btn_Decr;
		final Integer count = 0;
		final Button btn_Ok;
		final Button btn_Cancel;
		final String Ordno;
		// private Button btn_Add;
		final Cursor cursor_orddetails;
		// private ListView lst_serial;

		final ArrayList<String> alst;

		LayoutInflater li = LayoutInflater.from(MprItemList.this);
		View promptsView = li.inflate(R.layout.mpr_shipment_entry, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				MprItemList.this);
		alertDialogBuilder.setView(promptsView);

		// create alert dialog

		alst = new ArrayList<String>();// initialize array list
		/*
		 * aadap = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_list_item_1, alst);
		 */
		edt_itemNo = (EditText) promptsView
				.findViewById(R.id.edt_mprshipItemNo);
		edt_qtyOrd = (EditText) promptsView
				.findViewById(R.id.edt_mprshipQtyOrd);
		edt_qtyShiped = (EditText) promptsView
				.findViewById(R.id.edt_shipShiped);
		/*
		 * pickSeq = (EditText) findViewById(R.id.edt_mseshipPickSeq); shipVia =
		 * (EditText) findViewById(R.id.edt_mseshipShipVia);
		 */
		// serialNo = (EditText) findViewById(R.id.edt_mseshipSerial);
		edt_comments = (EditText) promptsView
				.findViewById(R.id.edt_mprshipComments);
		edt_uom = (EditText) promptsView.findViewById(R.id.edt_mprshipuom);

		btn_Ok = (Button) promptsView.findViewById(R.id.btn_mprshipOk);
		btn_Cancel = (Button) promptsView.findViewById(R.id.btn_mprshipCancel);
		btn_Incr = (Button) promptsView.findViewById(R.id.btn_mprshipIncr);
		btn_Decr = (Button) promptsView.findViewById(R.id.btn_mprshipDecr);

		if (requestCode == 1) {
			// Manf No
			if (resultCode == RESULT_OK) {
				Log.i("Scan result format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				String manfCode = intent.getStringExtra("SCAN_RESULT");

				dbhelper.getReadableDatabase();
				Manf_Number01_mpr manf = dbhelper
						.getItemFromManfNum_mpr(manfCode);
				dbhelper.closeDatabase();

				if (manf != null) {
					String Itno = manf.getItemno();
					dbhelper.getReadableDatabase();
					boolean flag = dbhelper
							.checkMpr_OrderDetails(orderNo, Itno);
					dbhelper.closeDatabase();
					if (/* itemno == null && */flag == true) {
						dbhelper.getReadableDatabase();
						MPR_OrderDetails mpr_OrderDetails = new MPR_OrderDetails();
						mpr_OrderDetails = dbhelper.getMprOrderDetails(orderNo,
								manf.getItemno());
						dbhelper.closeDatabase();

						itemno = mpr_OrderDetails.getItem_no();
						qtyOrderd = mpr_OrderDetails.getOrdered_qty();
						qtyShipd = mpr_OrderDetails.getReceived_qty();
						uom = mpr_OrderDetails.getUom();
						comment = mpr_OrderDetails.getComments();
						/* if (qtyOrderd > qtyShipd) { */
						/*
						 * Intent i = new Intent(MprItemList.this,
						 * MprShipmentEntry.class);
						 * 
						 * i.putExtra("itemNo", itemno); i.putExtra("qtyOrder",
						 * qtyOrderd); i.putExtra("ordShipd", qtyShipd);
						 * i.putExtra("ordNo", orderNo); startActivity(i);
						 */

						if (qtyOrderd != 0 && qtyOrderd > 0) {
							View view = li.inflate(R.layout.mpr_itemlist, null);

							btnExit.setVisibility(view.GONE);

							final AlertDialog alertDialog = alertDialogBuilder
									.create();
							if (qtyShipd == 0) {
								qtyShipd++;
							}
							edt_itemNo.setText(itemno);
							edt_qtyOrd.setText(qtyOrderd.toString());
							edt_qtyShiped.setText(qtyShipd.toString());
							edt_uom.setText(uom);
							edt_comments.setText(comment);

							alertDialog.setOnKeyListener(new OnKeyListener() {

								@Override
								public boolean onKey(DialogInterface dialog,
										int keyCode, KeyEvent event) {
									// TODO Auto-generated method stub
									if (keyCode == KeyEvent.KEYCODE_BACK) {

										btnExit.setVisibility(View.VISIBLE);
										alertDialog.cancel();
										return true;
									} else
										return false;

								}
							});

							btn_Incr.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									String value = edt_qtyShiped.getText()
											.toString();
									if (!value.equals("")) {
										Integer val = Integer.parseInt(value);
										if (val < qtyOrderd) {
											val = val + 1;
											edt_qtyShiped.setText(val
													.toString());
										} else {
											/*
											 * Toast.makeText(getBaseContext(),
											 * "Maximum Shipped quantity reached"
											 * , Toast.LENGTH_LONG).show();
											 */

											toastText
													.setText("Maximum Shipped quantity reached");
											Toast toast = new Toast(
													getApplicationContext());
											toast.setGravity(
													Gravity.CENTER_VERTICAL, 0,
													120);
											toast.setDuration(Toast.LENGTH_SHORT);
											toast.setView(toastLayout);
											toast.show();

										}
									} else {

										toastText
												.setText("Please enter the Starting value");
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
							});
							btn_Decr.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									String value = edt_qtyShiped.getText()
											.toString();
									if (!value.equals("")) {
										Integer val = Integer.parseInt(value);
										if (val >= 2) {
											val = val - 1;

											edt_qtyShiped.setText(val
													.toString());
										} else {
											/*
											 * Toast.makeText(getBaseContext(),
											 * "Minimum Shipped quantity reached"
											 * , Toast.LENGTH_LONG).show();
											 */
											toastText
													.setText("Minimum Shipped quantity reached");
											Toast toast = new Toast(
													getApplicationContext());
											toast.setGravity(
													Gravity.CENTER_VERTICAL, 0,
													120);
											toast.setDuration(Toast.LENGTH_SHORT);
											toast.setView(toastLayout);
											toast.show();
										}
									}

									else {

										toastText
												.setText("Please enter the Starting value");
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
							});
							btn_Cancel
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											btnExit.setVisibility(View.VISIBLE);
											alertDialog.cancel();
										}

									});
							btn_Ok.setOnClickListener(new OnClickListener() {
								String resu = null;

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									String val = edt_qtyShiped.getText()
											.toString();
									if (val.matches("")) {
										toastText
												.setText("Please Enter the Shipped Qty");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 120);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();

									} else if (Integer.parseInt(edt_qtyShiped
											.getText().toString()) < Integer
											.parseInt(edt_qtyOrd.getText()
													.toString())
											&& Integer.parseInt(edt_qtyShiped
													.getText().toString()) != 0) {
										updateOrderTransTables();
										// Toast.makeText(MprShipmentEntry.this,"Data Saved Successfully ",
										// Toast.LENGTH_SHORT).show();

									} else if (Integer.parseInt(edt_qtyShiped
											.getText().toString()) == 0) {
										toastText
												.setText("Shipped Qty cannot be 0 ");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 120);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();
									} else if (Integer.parseInt(edt_qtyShiped
											.getText().toString()) == Integer
											.parseInt(edt_qtyOrd.getText()
													.toString())) {
										final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
												MprItemList.this);
										alertDialog.setTitle("Confirmation");
										alertDialog
												.setIcon(R.drawable.warning);
										alertDialog.setCancelable(false);
										alertDialog
												.setPositiveButton(
														"Yes",
														new DialogInterface.OnClickListener() {

															public void onClick(
																	DialogInterface dialog,
																	int which) {

																updateOrderTransTables();
																/*
																 * Toast.makeText
																 * (
																 * MprShipmentEntry
																 * .this,
																 * "Data Saved Successfully "
																 * , Toast.
																 * LENGTH_SHORT
																 * ).show();
																 */

															}
														});
										alertDialog
												.setNegativeButton(
														"No",
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface dialog,
																	int which) {

															}
														});
										alertDialog
												.setMessage("Do you want Save the Changes ?");

										alertDialog.show();

									} else if (!(Integer.parseInt(edt_qtyShiped
											.getText().toString()) > 0)) {
										/*
										 * Toast.makeText(getBaseContext(),
										 * "Minimum Shipped quantity reached",
										 * Toast.LENGTH_LONG).show();
										 */
										toastText
												.setText("Minimum Received quantity reached");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 120);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();
									} else if (!(Integer.parseInt(edt_qtyShiped
											.getText().toString()) > 0)) {
										/*
										 * Toast.makeText(getBaseContext(),
										 * "Minimum Shipped quantity reached",
										 * Toast.LENGTH_LONG).show();
										 */
										toastText
												.setText("Minimum Received quantity reached");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 120);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();
									} else {
										/*
										 * Toast.makeText(getBaseContext(),
										 * "Received Qty Exceeds Order Qty",
										 * Toast.LENGTH_LONG) .show();
										 */
										toastText
												.setText("Received Qty Exceeds Order Qty");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 120);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();
									}

								}

								private void updateOrderTransTables() {
									String resu = "";
									MPR_OrderDetails mpr_OrderDetails = new MPR_OrderDetails();

									String itno = edt_itemNo.getText()
											.toString();
									Integer ordqy = Integer.parseInt(edt_qtyOrd
											.getText().toString());
									String uomval = edt_uom.getText()
											.toString();
									Integer shQty = Integer
											.parseInt(edt_qtyShiped.getText()
													.toString());
									String comment = edt_comments.getText()
											.toString();

									mpr_OrderDetails.setComments(comment);
									mpr_OrderDetails.setReceived_qty(shQty);
									mpr_OrderDetails.setItem_no(itno);
									mpr_OrderDetails.setOrdered_qty(ordqy);
									mpr_OrderDetails.setUom(uomval);
									mpr_OrderDetails.setPo_number(orderNo);

									dbhelper.getWritableDatabase();
									String result = dbhelper
											.updateMpr_OrderDetails(
													mpr_OrderDetails, "Ship");
									dbhelper.closeDatabase();

									if (result.equals("success")) {

										dbhelper.getReadableDatabase();
										boolean flag = dbhelper.checkMPR();
										dbhelper.getReadableDatabase();
										if (flag == true) {
											System.out.println("FOR ADD");
											MPR_OrderDetails mpr_OrderDetailsForTrans = new MPR_OrderDetails();
											dbhelper.getReadableDatabase();
											mpr_OrderDetailsForTrans = dbhelper
													.getMprOrderDetails(
															orderNo, itno);
											dbhelper.closeDatabase();
											dbhelper.getWritableDatabase();
											resu = dbhelper
													.addMpr_TransDetails(mpr_OrderDetailsForTrans);
											dbhelper.closeDatabase();
										} else {
											dbhelper.getReadableDatabase();
											String val = dbhelper
													.checkReceiptInTrans(
															orderNo, itno);

											dbhelper.closeDatabase();
											if (val.equalsIgnoreCase("Yes") /*
																			 * &&
																			 * test
																			 * ==
																			 * false
																			 */) {
												System.out.println("FOR ADD");
												MPR_OrderDetails mpr_OrderDetailsForTrans = new MPR_OrderDetails();
												dbhelper.getReadableDatabase();
												mpr_OrderDetailsForTrans = dbhelper
														.getMprOrderDetails(
																orderNo, itno);
												dbhelper.closeDatabase();
												dbhelper.getWritableDatabase();
												resu = dbhelper
														.addMpr_TransDetails(mpr_OrderDetailsForTrans);
												dbhelper.closeDatabase();
											} else {
												System.out
														.println("FOR UPDATE");
												dbhelper.getWritableDatabase();
												resu = dbhelper
														.updateMpr_TransDetails(mpr_OrderDetails);
												dbhelper.closeDatabase();
											}
										}
									}
									if (resu.equals("success")) {
										MprItemList.flag = "true";
										Intent i = new Intent(MprItemList.this,
												MprItemList.class);
										i.putExtra("OrdNo_ShipMent", orderNo);
										startActivity(i);

									}

									else {
										toastText.setText("Input not Valid");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 410);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();
									}
								}

							});
							alertDialog.show();
						} else {
							/*
							 * Toast.makeText(getBaseContext(),
							 * "No Order Quantity", Toast.LENGTH_SHORT) .show();
							 */
							toastText.setText("No Order Quantity");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();

						}

					} else {
						/*
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
				} else {
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
		} else if (requestCode == 0) {
			// ItemNo
			if (resultCode == RESULT_OK) {
				Log.i("Scan resul format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				String itNo = intent.getStringExtra("SCAN_RESULT");

				dbhelper.getReadableDatabase();
				MPR_OrderDetails mpr_OrderDetails = new MPR_OrderDetails();
				mpr_OrderDetails = dbhelper.getMprOrderDetails(orderNo, itNo);
				dbhelper.closeDatabase();

				if (mpr_OrderDetails != null) {
					/* if (itemno == null) { */
					itemno = mpr_OrderDetails.getItem_no();
					qtyOrderd = mpr_OrderDetails.getOrdered_qty();
					qtyShipd = mpr_OrderDetails.getReceived_qty();
					uom = mpr_OrderDetails.getUom();
					comment = mpr_OrderDetails.getComments();
					/*
					 * if (qtyOrderd > qtyShipd) { Intent i = new
					 * Intent(MprItemList.this, MprShipmentEntry.class);
					 * 
					 * i.putExtra("itemNo", itemno); i.putExtra("qtyOrder",
					 * qtyOrderd); i.putExtra("ordShipd", qtyShipd);
					 * i.putExtra("ordNo", orderNo); startActivity(i);
					 */

					/*
					 * Intent i = new Intent(MprItemList.this,
					 * MprShipmentEntry.class);
					 * 
					 * i.putExtra("itemNo", itemno); i.putExtra("qtyOrder",
					 * qtyOrderd); i.putExtra("ordShipd", qtyShipd);
					 * i.putExtra("ordNo", orderNo); startActivity(i);
					 */

					if (qtyOrderd != 0 && qtyOrderd > 0) {
						View view = li.inflate(R.layout.mpr_itemlist, null);

						btnExit.setVisibility(view.GONE);

						final AlertDialog alertDialog = alertDialogBuilder
								.create();
						if (qtyShipd == 0) {
							qtyShipd++;
						}
						edt_itemNo.setText(itemno);
						edt_qtyOrd.setText(qtyOrderd.toString());
						edt_qtyShiped.setText(qtyShipd.toString());
						edt_uom.setText(uom);
						edt_comments.setText(comment);

						alertDialog.setOnKeyListener(new OnKeyListener() {

							@Override
							public boolean onKey(DialogInterface dialog,
									int keyCode, KeyEvent event) {
								// TODO Auto-generated method stub
								if (keyCode == KeyEvent.KEYCODE_BACK) {

									btnExit.setVisibility(View.VISIBLE);
									alertDialog.cancel();
									return true;
								} else
									return false;

							}
						});

						btn_Incr.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String value = edt_qtyShiped.getText()
										.toString();
								if (!value.equals("")) {
									Integer val = Integer.parseInt(value);
									if (val < qtyOrderd) {
										val = val + 1;
										edt_qtyShiped.setText(val.toString());
									} else {
										/*
										 * Toast.makeText(getBaseContext(),
										 * "Maximum Shipped quantity reached",
										 * Toast.LENGTH_LONG).show();
										 */

										toastText
												.setText("Maximum Shipped quantity reached");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 120);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();

									}
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
						btn_Decr.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String value = edt_qtyShiped.getText()
										.toString();
								if (!value.equals("")) {
									Integer val = Integer.parseInt(value);
									if (val >= 2) {
										val = val - 1;

										edt_qtyShiped.setText(val.toString());
									} else {
										/*
										 * Toast.makeText(getBaseContext(),
										 * "Minimum Shipped quantity reached",
										 * Toast.LENGTH_LONG).show();
										 */
										toastText
												.setText("Minimum Shipped quantity reached");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 120);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();
									}
								}

								else {

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
						btn_Cancel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {btnExit.setVisibility(View.VISIBLE);
							alertDialog.cancel();}

						});
						btn_Ok.setOnClickListener(new OnClickListener() {
							String resu = null;

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String val = edt_qtyShiped.getText().toString();
								if (val.matches("")) {
									toastText
											.setText("Please Enter the Shipped Qty");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, 120);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();

								} else if (Integer.parseInt(edt_qtyShiped
										.getText().toString()) < Integer
										.parseInt(edt_qtyOrd.getText()
												.toString())
										&& Integer.parseInt(edt_qtyShiped
												.getText().toString()) != 0) {
									updateOrderTransTables();
									// Toast.makeText(MprShipmentEntry.this,"Data Saved Successfully ",
									// Toast.LENGTH_SHORT).show();

								} else if (Integer.parseInt(edt_qtyShiped
										.getText().toString()) == 0) {
									toastText
											.setText("Shipped Qty cannot be 0 ");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, 120);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();
								} else if (Integer.parseInt(edt_qtyShiped
										.getText().toString()) == Integer
										.parseInt(edt_qtyOrd.getText()
												.toString())) {
									final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
											MprItemList.this);
									alertDialog.setTitle("Confirmation");
									alertDialog.setIcon(R.drawable.warning);
									alertDialog.setCancelable(false);
									alertDialog
											.setPositiveButton(
													"Yes",
													new DialogInterface.OnClickListener() {

														public void onClick(
																DialogInterface dialog,
																int which) {

															updateOrderTransTables();
															/*
															 * Toast.makeText(
															 * MprShipmentEntry
															 * .this,
															 * "Data Saved Successfully "
															 * ,
															 * Toast.LENGTH_SHORT
															 * ).show();
															 */

														}
													});
									alertDialog
											.setNegativeButton(
													"No",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {

														}
													});
									alertDialog
											.setMessage("Do you want Save the Changes ?");

									alertDialog.show();

								} else if (!(Integer.parseInt(edt_qtyShiped
										.getText().toString()) > 0)) {
									/*
									 * Toast.makeText(getBaseContext(),
									 * "Minimum Shipped quantity reached",
									 * Toast.LENGTH_LONG).show();
									 */
									toastText
											.setText("Minimum Received quantity reached");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, 120);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();
								} else if (!(Integer.parseInt(edt_qtyShiped
										.getText().toString()) > 0)) {
									/*
									 * Toast.makeText(getBaseContext(),
									 * "Minimum Shipped quantity reached",
									 * Toast.LENGTH_LONG).show();
									 */
									toastText
											.setText("Minimum Received quantity reached");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, 120);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();
								} else {
									/*
									 * Toast.makeText(getBaseContext(),
									 * "Received Qty Exceeds Order Qty",
									 * Toast.LENGTH_LONG) .show();
									 */
									toastText
											.setText("Received Qty Exceeds Order Qty");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, 120);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();
								}
							}

							private void updateOrderTransTables() {
								String resu = "";
								MPR_OrderDetails mpr_OrderDetails = new MPR_OrderDetails();

								String itno = edt_itemNo.getText().toString();
								Integer ordqy = Integer.parseInt(edt_qtyOrd
										.getText().toString());
								String uomval = edt_uom.getText().toString();
								Integer shQty = Integer.parseInt(edt_qtyShiped
										.getText().toString());
								String comment = edt_comments.getText()
										.toString();

								mpr_OrderDetails.setComments(comment);
								mpr_OrderDetails.setReceived_qty(shQty);
								mpr_OrderDetails.setItem_no(itno);
								mpr_OrderDetails.setOrdered_qty(ordqy);
								mpr_OrderDetails.setUom(uomval);
								mpr_OrderDetails.setPo_number(orderNo);

								dbhelper.getWritableDatabase();
								String result = dbhelper
										.updateMpr_OrderDetails(
												mpr_OrderDetails, "Ship");
								dbhelper.closeDatabase();

								if (result.equals("success")) {

									dbhelper.getReadableDatabase();
									boolean flag = dbhelper.checkMPR();
									dbhelper.getReadableDatabase();
									if (flag == true) {
										System.out.println("FOR ADD");
										MPR_OrderDetails mpr_OrderDetailsForTrans = new MPR_OrderDetails();
										dbhelper.getReadableDatabase();
										mpr_OrderDetailsForTrans = dbhelper
												.getMprOrderDetails(orderNo,
														itno);
										dbhelper.closeDatabase();
										dbhelper.getWritableDatabase();
										resu = dbhelper
												.addMpr_TransDetails(mpr_OrderDetailsForTrans);
										dbhelper.closeDatabase();
									} else {
										dbhelper.getReadableDatabase();
										String val = dbhelper
												.checkReceiptInTrans(orderNo,
														itno);

										dbhelper.closeDatabase();
										if (val.equalsIgnoreCase("Yes") /*
																		 * &&
																		 * test
																		 * ==
																		 * false
																		 */) {
											System.out.println("FOR ADD");
											MPR_OrderDetails mpr_OrderDetailsForTrans = new MPR_OrderDetails();
											dbhelper.getReadableDatabase();
											mpr_OrderDetailsForTrans = dbhelper
													.getMprOrderDetails(
															orderNo, itno);
											dbhelper.closeDatabase();
											dbhelper.getWritableDatabase();
											resu = dbhelper
													.addMpr_TransDetails(mpr_OrderDetailsForTrans);
											dbhelper.closeDatabase();
										} else {
											System.out.println("FOR UPDATE");
											dbhelper.getWritableDatabase();
											resu = dbhelper
													.updateMpr_TransDetails(mpr_OrderDetails);
											dbhelper.closeDatabase();
										}
									}
								}
								if (resu.equals("success")) {
									MprItemList.flag = "true";
									Intent i = new Intent(MprItemList.this,
											MprItemList.class);
									i.putExtra("OrdNo_ShipMent", orderNo);
									startActivity(i);

								}

								else {
									toastText.setText("Input not Valid");
									Toast toast = new Toast(
											getApplicationContext());
									toast.setGravity(Gravity.CENTER_VERTICAL,
											0, 410);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.setView(toastLayout);
									toast.show();
								}
							}

						});
						alertDialog.show();

					} else {
						toastText.setText("No Order Quantity");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.setView(toastLayout);
						toast.show();

					}

				} else {
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
			// UPC
			if (resultCode == RESULT_OK) {
				Log.i("Scan resul format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				String upcCode = intent.getStringExtra("SCAN_RESULT");

				dbhelper.getReadableDatabase();
				MPR_Upc Upc = dbhelper.getItemFromUpc_mpr(upcCode);// ,
																	// cmpnyNo);
				dbhelper.closeDatabase();

				if (Upc != null) {
					String Itno = Upc.getItem_number();
					dbhelper.getReadableDatabase();
					boolean flag = dbhelper
							.checkMpr_OrderDetails(orderNo, Itno);
					dbhelper.closeDatabase();
					if (/* itemno == null && */flag == true) {
						dbhelper.getReadableDatabase();
						MPR_OrderDetails mpr_OrderDetails = new MPR_OrderDetails();
						mpr_OrderDetails = dbhelper.getMprOrderDetails(orderNo,
								Upc.getItem_number());
						dbhelper.closeDatabase();

						itemno = mpr_OrderDetails.getItem_no();
						qtyOrderd = mpr_OrderDetails.getOrdered_qty();
						qtyShipd = mpr_OrderDetails.getReceived_qty();
						uom = mpr_OrderDetails.getUom();
						comment = mpr_OrderDetails.getComments();
						/*
						 * if (qtyOrderd > qtyShipd) { Intent i = new
						 * Intent(MprItemList.this, MprShipmentEntry.class);
						 * 
						 * i.putExtra("itemNo", itemno); i.putExtra("qtyOrder",
						 * qtyOrderd); i.putExtra("ordShipd", qtyShipd);
						 * 
						 * i.putExtra("ordNo", orderNo); startActivity(i);
						 */

						/*
						 * Intent i = new Intent(MprItemList.this,
						 * MprShipmentEntry.class);
						 * 
						 * i.putExtra("itemNo", itemno); i.putExtra("qtyOrder",
						 * qtyOrderd); i.putExtra("ordShipd", qtyShipd);
						 * i.putExtra("ordNo", orderNo); startActivity(i);
						 */

						if (qtyOrderd != 0 && qtyOrderd > 0) {
							View view = li.inflate(R.layout.mpr_itemlist, null);

							btnExit.setVisibility(view.GONE);

							final AlertDialog alertDialog = alertDialogBuilder
									.create();

							if (qtyShipd == 0) {
								qtyShipd++;
							}
							edt_itemNo.setText(itemno);
							edt_qtyOrd.setText(qtyOrderd.toString());
							edt_qtyShiped.setText(qtyShipd.toString());
							edt_uom.setText(uom);
							edt_comments.setText(comment);

							alertDialog.setOnKeyListener(new OnKeyListener() {

								@Override
								public boolean onKey(DialogInterface dialog,
										int keyCode, KeyEvent event) {
									// TODO Auto-generated method stub
									if (keyCode == KeyEvent.KEYCODE_BACK) {

										btnExit.setVisibility(View.VISIBLE);
										alertDialog.cancel();
										return true;
									} else
										return false;

								}
							});

							btn_Incr.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									String value = edt_qtyShiped.getText()
											.toString();
									if (!value.equals("")) {
										Integer val = Integer.parseInt(value);
										if (val < qtyOrderd) {
											val = val + 1;
											edt_qtyShiped.setText(val
													.toString());
										} else {
											/*
											 * Toast.makeText(getBaseContext(),
											 * "Maximum Shipped quantity reached"
											 * , Toast.LENGTH_LONG).show();
											 */

											toastText
													.setText("Maximum Shipped quantity reached");
											Toast toast = new Toast(
													getApplicationContext());
											toast.setGravity(
													Gravity.CENTER_VERTICAL, 0,
													120);
											toast.setDuration(Toast.LENGTH_SHORT);
											toast.setView(toastLayout);
											toast.show();

										}
									} else {

										toastText
												.setText("Please enter the Starting value");
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
							});
							btn_Decr.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									String value = edt_qtyShiped.getText()
											.toString();
									if (!value.equals("")) {
										Integer val = Integer.parseInt(value);
										if (val >= 2) {
											val = val - 1;

											edt_qtyShiped.setText(val
													.toString());
										} else {
											/*
											 * Toast.makeText(getBaseContext(),
											 * "Minimum Shipped quantity reached"
											 * , Toast.LENGTH_LONG).show();
											 */
											toastText
													.setText("Minimum Shipped quantity reached");
											Toast toast = new Toast(
													getApplicationContext());
											toast.setGravity(
													Gravity.CENTER_VERTICAL, 0,
													120);
											toast.setDuration(Toast.LENGTH_SHORT);
											toast.setView(toastLayout);
											toast.show();
										}
									}

									else {

										toastText
												.setText("Please enter the Starting value");
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
							});
							btn_Cancel
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											btnExit.setVisibility(View.VISIBLE);
										alertDialog.cancel();}

									});
							btn_Ok.setOnClickListener(new OnClickListener() {
								String resu = null;

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									String val = edt_qtyShiped.getText()
											.toString();
									if (val.matches("")) {
										toastText
												.setText("Please Enter the Shipped Qty");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 120);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();

									} else if (Integer.parseInt(edt_qtyShiped
											.getText().toString()) < Integer
											.parseInt(edt_qtyOrd.getText()
													.toString())
											&& Integer.parseInt(edt_qtyShiped
													.getText().toString()) != 0) {
										updateOrderTransTables();
										// Toast.makeText(MprShipmentEntry.this,"Data Saved Successfully ",
										// Toast.LENGTH_SHORT).show();

									} else if (Integer.parseInt(edt_qtyShiped
											.getText().toString()) == 0) {
										toastText
												.setText("Shipped Qty cannot be 0 ");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 120);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();
									} else if (Integer.parseInt(edt_qtyShiped
											.getText().toString()) == Integer
											.parseInt(edt_qtyOrd.getText()
													.toString())) {
										final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
												MprItemList.this);
										alertDialog.setTitle("Confirmation");
										alertDialog
												.setIcon(R.drawable.warning);
										alertDialog.setCancelable(false);
										alertDialog
												.setPositiveButton(
														"Yes",
														new DialogInterface.OnClickListener() {

															public void onClick(
																	DialogInterface dialog,
																	int which) {

																updateOrderTransTables();
																/*
																 * Toast.makeText
																 * (
																 * MprShipmentEntry
																 * .this,
																 * "Data Saved Successfully "
																 * , Toast.
																 * LENGTH_SHORT
																 * ).show();
																 */

															}
														});
										alertDialog
												.setNegativeButton(
														"No",
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface dialog,
																	int which) {

															}
														});
										alertDialog
												.setMessage("Do you want Save the Changes ?");

										alertDialog.show();

									} else if (!(Integer.parseInt(edt_qtyShiped
											.getText().toString()) > 0)) {
										/*
										 * Toast.makeText(getBaseContext(),
										 * "Minimum Shipped quantity reached",
										 * Toast.LENGTH_LONG).show();
										 */
										toastText
												.setText("Minimum Received quantity reached");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 120);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();
									} else if (!(Integer.parseInt(edt_qtyShiped
											.getText().toString()) > 0)) {
										/*
										 * Toast.makeText(getBaseContext(),
										 * "Minimum Shipped quantity reached",
										 * Toast.LENGTH_LONG).show();
										 */
										toastText
												.setText("Minimum Received quantity reached");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 120);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();
									} else {
										/*
										 * Toast.makeText(getBaseContext(),
										 * "Received Qty Exceeds Order Qty",
										 * Toast.LENGTH_LONG) .show();
										 */
										toastText
												.setText("Received Qty Exceeds Order Qty");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 120);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();
									}
								}

								private void updateOrderTransTables() {
									String resu = "";
									MPR_OrderDetails mpr_OrderDetails = new MPR_OrderDetails();

									String itno = edt_itemNo.getText()
											.toString();
									Integer ordqy = Integer.parseInt(edt_qtyOrd
											.getText().toString());
									String uomval = edt_uom.getText()
											.toString();
									Integer shQty = Integer
											.parseInt(edt_qtyShiped.getText()
													.toString());
									String comment = edt_comments.getText()
											.toString();

									mpr_OrderDetails.setComments(comment);
									mpr_OrderDetails.setReceived_qty(shQty);
									mpr_OrderDetails.setItem_no(itno);
									mpr_OrderDetails.setOrdered_qty(ordqy);
									mpr_OrderDetails.setUom(uomval);
									mpr_OrderDetails.setPo_number(orderNo);

									dbhelper.getWritableDatabase();
									String result = dbhelper
											.updateMpr_OrderDetails(
													mpr_OrderDetails, "Ship");
									dbhelper.closeDatabase();

									if (result.equals("success")) {

										dbhelper.getReadableDatabase();
										boolean flag = dbhelper.checkMPR();
										dbhelper.getReadableDatabase();
										if (flag == true) {
											System.out.println("FOR ADD");
											MPR_OrderDetails mpr_OrderDetailsForTrans = new MPR_OrderDetails();
											dbhelper.getReadableDatabase();
											mpr_OrderDetailsForTrans = dbhelper
													.getMprOrderDetails(
															orderNo, itno);
											dbhelper.closeDatabase();
											dbhelper.getWritableDatabase();
											resu = dbhelper
													.addMpr_TransDetails(mpr_OrderDetailsForTrans);
											dbhelper.closeDatabase();
										} else {
											dbhelper.getReadableDatabase();
											String val = dbhelper
													.checkReceiptInTrans(
															orderNo, itno);

											dbhelper.closeDatabase();
											if (val.equalsIgnoreCase("Yes") /*
																			 * &&
																			 * test
																			 * ==
																			 * false
																			 */) {
												System.out.println("FOR ADD");
												MPR_OrderDetails mpr_OrderDetailsForTrans = new MPR_OrderDetails();
												dbhelper.getReadableDatabase();
												mpr_OrderDetailsForTrans = dbhelper
														.getMprOrderDetails(
																orderNo, itno);
												dbhelper.closeDatabase();
												dbhelper.getWritableDatabase();
												resu = dbhelper
														.addMpr_TransDetails(mpr_OrderDetailsForTrans);
												dbhelper.closeDatabase();
											} else {
												System.out
														.println("FOR UPDATE");
												dbhelper.getWritableDatabase();
												resu = dbhelper
														.updateMpr_TransDetails(mpr_OrderDetails);
												dbhelper.closeDatabase();
											}
										}
									}
									if (resu.equals("success")) {
										MprItemList.flag = "true";
										Intent i = new Intent(MprItemList.this,
												MprItemList.class);
										i.putExtra("OrdNo_ShipMent", orderNo);
										startActivity(i);

									}

									else {
										toastText.setText("Input not Valid");
										Toast toast = new Toast(
												getApplicationContext());
										toast.setGravity(
												Gravity.CENTER_VERTICAL, 0, 410);
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.setView(toastLayout);
										toast.show();
									}
								}

							});

							alertDialog.show();
						} else {
							toastText.setText("No Order Quantity");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();

						}

					} else {
						toastText.setText("Item not available");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.setView(toastLayout);
						toast.show();
					}
				} else {
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

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// TODO Auto-generated method stub
			// Better to use SharedPreference to retain selected order

			Intent i = new Intent(MprItemList.this, MprOrderList.class);
			MprOrderList.returnfrm = "true";
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
