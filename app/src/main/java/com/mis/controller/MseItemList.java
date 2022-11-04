package com.mis.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.mobinventorysuit.R;
import com.mis.adapter.OrderListAdapter;
import com.mis.common.AppBaseActivity;
import com.mis.database.DatabaseHandler;
import com.mis.mse.model.MSE_MasterDetails;
import com.mis.mse.model.MSE_OrderDetails;
import com.mis.mse.model.Manf_Number01;
import com.mis.mse.model.Upc_Number;

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

public class MseItemList extends AppBaseActivity {
	private EditText ordNo;
	private EditText custNo;
	private EditText custName;
	private EditText ordDate;
	private EditText shiptoLoc;
	private RadioGroup radioScan;
	private EditText btnScan;
	private RadioButton radioUpc;
	private RadioButton radioItem;
	private RadioButton radioManf;
	TextView toastText;
	View toastLayout;

	String itemno, shipVia, pickSeq, uom, comment;
	Integer qtyOrderd, qtyShipd;

	private Button btnExit;
	private ListView lstDetails;
	Cursor cursor_orddetails;
	private SQLiteDatabase db;
	MSE_MasterDetails mse_MasterDetails = new MSE_MasterDetails();
	MSE_OrderDetails mse_OrderDetails = new MSE_OrderDetails();

	Cursor cursor_masdetails;
	DatabaseHandler dbhelper;
	private List<MSE_OrderDetails> ordList;
	String orderNo;
	public static String flag = "false";

	private ArrayAdapter<MSE_OrderDetails> adapter;

	public static ArrayList<String> selected_ord = new ArrayList<String>();;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mse_itemlist);

		registerBaseActivityReceiver();
		ordNo = (EditText) findViewById(R.id.edt_mseOrderNo);
		custNo = (EditText) findViewById(R.id.edt_mseCustNo);
		custName = (EditText) findViewById(R.id.mse_edtCustName);
		ordDate = (EditText) findViewById(R.id.edt_mseOrderDate);
		shiptoLoc = (EditText) findViewById(R.id.edt_mseShipLoc);
		btnScan = (EditText) findViewById(R.id.edt_mseSearch);
		btnExit = (Button) findViewById(R.id.btn_mseExit);
		radioScan = (RadioGroup) findViewById(R.id.radioScanBasedOn);
		lstDetails = (ListView) findViewById(R.id.lst_msefull);

		radioUpc = (RadioButton) findViewById(R.id.radioUpc);
		radioItem = (RadioButton) findViewById(R.id.radioInum);
		radioManf = (RadioButton) findViewById(R.id.radioNum);

		// Toast
		LayoutInflater inflater = getLayoutInflater();
		toastLayout = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		toastText = (TextView) toastLayout.findViewById(R.id.text);

		dbhelper = new DatabaseHandler(this);
		ordList = new ArrayList<MSE_OrderDetails>();

		if (flag == "false") {
			orderNo = getIntent().getStringExtra("ordNo_OrderList");

		} else {
			orderNo = getIntent().getStringExtra("OrdNo_ShipMent");

			flag = "false";

		}

		try {
			dbhelper.getReadableDatabase();
			mse_MasterDetails = dbhelper.getMseMasterDetails(orderNo);
			dbhelper.closeDatabase();

			dbhelper.getReadableDatabase();
			mse_OrderDetails = dbhelper.getMseOrderDetails(orderNo);
			dbhelper.closeDatabase();

			ordNo.setText(mse_OrderDetails.getOrdNumber());
			custNo.setText(mse_OrderDetails.getCustNumber());
			custName.setText(mse_MasterDetails.getBillName());
			// String dispDate=formatDate(mse_MasterDetails.getOrdDate());
			ordDate.setText(formatDate(mse_MasterDetails.getOrdDate()));
			shiptoLoc.setText(mse_MasterDetails.getShiptoLocCode());

			dbhelper.getReadableDatabase();
			ordList = dbhelper.getMseDataMSE_OrderDetails(orderNo);
			dbhelper.closeDatabase();

			adapter = new OrderListAdapter(this, ordList);
			lstDetails.setAdapter(adapter);

		} catch (Exception ex) {
			Log.e("Error", ex.getMessage());

		}
		btnExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MseItemList.this, MseOrderList.class);
				MseOrderList.returnfrm = "true";
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
			ArrayList<String> alst;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				MSE_OrderDetails list_obj = new MSE_OrderDetails();
				list_obj = (MSE_OrderDetails) lstDetails
						.getItemAtPosition(position);

				itemno = list_obj.getItemNumber();
				qtyOrderd = list_obj.getQtyOrdred();
				qtyShipd = list_obj.getQtyShiped();
				pickSeq = list_obj.getPickingSequence();
				shipVia = list_obj.getShipViaCode();
				uom = list_obj.getUom();
				comment = list_obj.getComments();

				LayoutInflater li = LayoutInflater.from(MseItemList.this);
				View promptsView = li
						.inflate(R.layout.mse_shipment_entry, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MseItemList.this);
				alertDialogBuilder.setView(promptsView);

				if (qtyOrderd != 0 && qtyOrderd > 0) {
					btnExit.setVisibility(view.GONE);

					// create alert dialog
					final AlertDialog alertDialog = alertDialogBuilder.create();
					
					/*
					 * i.putExtra("itemNo", itemno); i.putExtra("qtyOrder",
					 * qtyOrderd); i.putExtra("ordShipd", qtyShipd);
					 * i.putExtra("pickSeq", pickSeq); i.putExtra("shipVia",
					 * shipVia); i.putExtra("uom", uom); i.putExtra("ordNo",
					 * orderNo); i.putExtra("comment", comment);
					 * startActivity(i);
					 */

					// dbhelper = new DatabaseHandler(MprItemList.this);

					alst = new ArrayList<String>();// initialize array list

					edt_itemNo = (EditText) promptsView
							.findViewById(R.id.edt_mseshipItemNo);
					edt_qtyOrd = (EditText) promptsView
							.findViewById(R.id.edt_mseshipQtyOrd);
					edt_qtyShiped = (EditText) promptsView
							.findViewById(R.id.edt_shipShiped);
					edt_pickSeq = (EditText) promptsView
							.findViewById(R.id.edt_mseshipPickSeq);
					edt_shipVia = (EditText) promptsView
							.findViewById(R.id.edt_mseshipShipVia);
					// serialNo = (EditText)
					// findViewById(R.id.edt_mseshipSerial);
					edt_comments = (EditText) promptsView
							.findViewById(R.id.edt_mseshipComments);
					edt_uom = (EditText) promptsView
							.findViewById(R.id.edt_mseshipuom);

					btn_Ok = (Button) promptsView
							.findViewById(R.id.btn_mseshipOk);
					btn_Cancel = (Button) promptsView
							.findViewById(R.id.btn_mseshipCancel);
					btn_Incr = (Button) promptsView
							.findViewById(R.id.btn_mseshipIncr);
					btn_Decr = (Button) promptsView
							.findViewById(R.id.btn_mseshipDecr);

					if (qtyShipd == 0) {
						qtyShipd++;
					}

					edt_itemNo.setText(itemno);
					edt_qtyOrd.setText(qtyOrderd.toString());
					edt_qtyShiped.setText(qtyShipd.toString());
					edt_pickSeq.setText(pickSeq);
					edt_shipVia.setText(shipVia);
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

							String val = edt_qtyShiped.getText().toString();
							if (val.matches("")) {
								/*
								 * Toast.makeText(MseItemList.this,
								 * "Please Enter the Shipped Qty ",
								 * Toast.LENGTH_SHORT).show();
								 */
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
								// Toast.makeText(MseShipmentEntry.this,
								// "Data Saved Successfully ",
								// Toast.LENGTH_SHORT).show();

							} else if (Integer.parseInt(edt_qtyShiped.getText()
									.toString()) == 0) {
								/*
								 * Toast.makeText(MseItemList.this,
								 * "Shipped Qty cannot be 0 ",
								 * Toast.LENGTH_SHORT).show();
								 */
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
										MseItemList.this);
								alertDialog.setTitle("Confirmation");
								alertDialog.setIcon(R.drawable.warning);
								alertDialog.setCancelable(false);
								alertDialog.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {

												updateOrderTransTables();

												// Toast.makeText(MseShipmentEntry.this,
												// "Data Saved Successfully ",
												// Toast.LENGTH_SHORT).show();

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
										.setText("Minimum Shipped quantity reached");
								Toast toast = new Toast(getApplicationContext());
								toast.setGravity(Gravity.CENTER_VERTICAL, 0,
										120);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.setView(toastLayout);
								toast.show();
							} else {
								/*
								 * Toast.makeText(getBaseContext(),
								 * "Shipped Qty Exceeds Order Qty",
								 * Toast.LENGTH_LONG).show();
								 */
								toastText
										.setText("Shipped Qty Exceeds Order Qty");
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
					/*
					 * Toast.makeText(getBaseContext(), "No Order Quantity",
					 * Toast.LENGTH_SHORT).show();
					 */
					toastText.setText("No Order Quantity");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

				}

			}

			private void updateOrderTransTables() {
				// TODO Auto-generated method stub
				String resu = "";
				MSE_OrderDetails mse_OrderDetails = new MSE_OrderDetails();

				String itno = edt_itemNo.getText().toString();
				Integer ordqy = Integer.parseInt(edt_qtyOrd.getText()
						.toString());
				String picseq = edt_pickSeq.getText().toString();
				String uom_ = edt_uom.getText().toString();
				Integer shQty = Integer.parseInt(edt_qtyShiped.getText()
						.toString());
				String comment = edt_comments.getText().toString();

				mse_OrderDetails.setComments(comment);
				mse_OrderDetails.setQtyShiped(shQty);
				mse_OrderDetails.setItemNumber(itno);
				mse_OrderDetails.setQtyOrdred(ordqy);
				mse_OrderDetails.setPickingSequence(picseq);
				mse_OrderDetails.setUom(uom_);
				mse_OrderDetails.setOrdNumber(orderNo);
				dbhelper.getWritableDatabase();
				String result = dbhelper.updateMse_OrderDetails(
						mse_OrderDetails, "Ship");
				dbhelper.closeDatabase();

				if (result.equals("success")) {

					dbhelper.getReadableDatabase();
					boolean flag = dbhelper.checkMSE();
					dbhelper.getReadableDatabase();
					if (flag == true) {
						MSE_OrderDetails mse_OrderDetailsForTrans = new MSE_OrderDetails();
						dbhelper.getReadableDatabase();
						mse_OrderDetailsForTrans = dbhelper.getMseOrderDetails(
								orderNo, itno);
						dbhelper.closeDatabase();
						dbhelper.getWritableDatabase();
						resu = dbhelper
								.addMse_TransDetails(mse_OrderDetailsForTrans);
						dbhelper.closeDatabase();
					} else {
						dbhelper.getReadableDatabase();
						String val = dbhelper.checkShipInTrans1(orderNo, itno);
						dbhelper.closeDatabase();
						if (val.equalsIgnoreCase("Yes")/* || test == false */) {
							MSE_OrderDetails mse_OrderDetailsForTrans = new MSE_OrderDetails();
							dbhelper.getReadableDatabase();
							mse_OrderDetailsForTrans = dbhelper
									.getMseOrderDetails(orderNo, itno);
							dbhelper.closeDatabase();
							dbhelper.getWritableDatabase();
							resu = dbhelper
									.addMse_TransDetails(mse_OrderDetailsForTrans);
							dbhelper.closeDatabase();
						} else {
							dbhelper.getWritableDatabase();
							resu = dbhelper
									.updateMse_TransDetails(mse_OrderDetails);
							dbhelper.closeDatabase();
						}
					}
				}
				if (resu.equals("success")) {
					MseItemList.flag = "true";
					Intent i = new Intent(MseItemList.this, MseItemList.class);
					i.putExtra("OrdNo_ShipMent", orderNo);
					startActivity(i);

				}

				else {
					/*
					 * Toast.makeText(getBaseContext(), "Input not Valid",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Input not Valid");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
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

		LayoutInflater li = LayoutInflater.from(MseItemList.this);
		View promptsView = li.inflate(R.layout.mse_shipment_entry, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				MseItemList.this);
		alertDialogBuilder.setView(promptsView);

		// create alert dialog

		alst = new ArrayList<String>();// initialize array list
		/*
		 * aadap = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_list_item_1, alst);
		 */
		edt_itemNo = (EditText) promptsView
				.findViewById(R.id.edt_mseshipItemNo);
		edt_qtyOrd = (EditText) promptsView
				.findViewById(R.id.edt_mseshipQtyOrd);
		edt_qtyShiped = (EditText) promptsView
				.findViewById(R.id.edt_shipShiped);
		edt_pickSeq = (EditText) promptsView
				.findViewById(R.id.edt_mseshipPickSeq);
		edt_shipVia = (EditText) promptsView
				.findViewById(R.id.edt_mseshipShipVia);
		// serialNo = (EditText)
		// findViewById(R.id.edt_mseshipSerial);
		edt_comments = (EditText) promptsView
				.findViewById(R.id.edt_mseshipComments);
		edt_uom = (EditText) promptsView.findViewById(R.id.edt_mseshipuom);

		btn_Ok = (Button) promptsView.findViewById(R.id.btn_mseshipOk);
		btn_Cancel = (Button) promptsView.findViewById(R.id.btn_mseshipCancel);
		btn_Incr = (Button) promptsView.findViewById(R.id.btn_mseshipIncr);
		btn_Decr = (Button) promptsView.findViewById(R.id.btn_mseshipDecr);

		if (requestCode == 1) {
			// Scan Result for Manf
			if (resultCode == RESULT_OK) {
				Log.i("Scan result format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				String manfCode = intent.getStringExtra("SCAN_RESULT");

				dbhelper.getReadableDatabase();
				Manf_Number01 manf = dbhelper.getItemFromManfNum(manfCode);
				dbhelper.closeDatabase();

				if (manf != null) {
					String Itno = manf.getManuf_itemno();
					dbhelper.getReadableDatabase();
					boolean flag = dbhelper
							.checkMse_OrderDetails(orderNo, Itno);
					dbhelper.closeDatabase();

					if (/* itemno == null && */flag == true) {
						dbhelper.getReadableDatabase();
						MSE_OrderDetails mse_OrderDetails = dbhelper
								.getMseOrderDetails(orderNo,
										manf.getManuf_itemno());
						dbhelper.closeDatabase();

						itemno = mse_OrderDetails.getItemNumber();
						qtyOrderd = mse_OrderDetails.getQtyOrdred();
						qtyShipd = mse_OrderDetails.getQtyShiped();
						pickSeq = mse_OrderDetails.getPickingSequence();
						shipVia = mse_OrderDetails.getShipViaCode();
						uom = mse_OrderDetails.getUom();
						comment = mse_OrderDetails.getComments();
						/*
						 * Intent i = new Intent(MseItemList.this,
						 * MseShipmentEntry.class);
						 * 
						 * i.putExtra("itemNo", itemno); i.putExtra("qtyOrder",
						 * qtyOrderd); i.putExtra("ordShipd", qtyShipd);
						 * i.putExtra("pickSeq", pickSeq); i.putExtra("shipVia",
						 * shipVia); i.putExtra("uom", uom); i.putExtra("ordNo",
						 * orderNo); startActivity(i);
						 */
						if (qtyOrderd != 0 && qtyOrderd > 0) {
							View view = li.inflate(R.layout.mse_itemlist, null);

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
							edt_pickSeq.setText(pickSeq);
							edt_shipVia.setText(shipVia);

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
										/*
										 * Toast.makeText(MseItemList.this,
										 * "Please Enter the Shipped Qty ",
										 * Toast.LENGTH_SHORT).show();
										 */
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
										// Toast.makeText(MseShipmentEntry.this,
										// "Data Saved Successfully ",
										// Toast.LENGTH_SHORT).show();

									} else if (Integer.parseInt(edt_qtyShiped
											.getText().toString()) == 0) {
										/*
										 * Toast.makeText(MseItemList.this,
										 * "Shipped Qty cannot be 0 ",
										 * Toast.LENGTH_SHORT).show();
										 */
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
												MseItemList.this);
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

																// Toast.makeText(MseShipmentEntry.this,
																// "Data Saved Successfully ",
																// Toast.LENGTH_SHORT).show();

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
												.setText("Minimum Shipped quantity reached");
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
										 * "Shipped Qty Exceeds Order Qty",
										 * Toast.LENGTH_LONG).show();
										 */
										toastText
												.setText("Shipped Qty Exceeds Order Qty");
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
									// TODO Auto-generated method stub
									String resu = "";
									MSE_OrderDetails mse_OrderDetails = new MSE_OrderDetails();

									String itno = edt_itemNo.getText()
											.toString();
									Integer ordqy = Integer.parseInt(edt_qtyOrd
											.getText().toString());
									String picseq = edt_pickSeq.getText()
											.toString();
									String uom_ = edt_uom.getText().toString();
									Integer shQty = Integer
											.parseInt(edt_qtyShiped.getText()
													.toString());
									String comment = edt_comments.getText()
											.toString();

									mse_OrderDetails.setComments(comment);
									mse_OrderDetails.setQtyShiped(shQty);
									mse_OrderDetails.setItemNumber(itno);
									mse_OrderDetails.setQtyOrdred(ordqy);
									mse_OrderDetails.setPickingSequence(picseq);
									mse_OrderDetails.setUom(uom_);
									mse_OrderDetails.setOrdNumber(orderNo);
									dbhelper.getWritableDatabase();
									String result = dbhelper
											.updateMse_OrderDetails(
													mse_OrderDetails, "Ship");
									dbhelper.closeDatabase();

									if (result.equals("success")) {

										dbhelper.getReadableDatabase();
										boolean flag = dbhelper.checkMSE();
										dbhelper.getReadableDatabase();
										if (flag == true) {
											MSE_OrderDetails mse_OrderDetailsForTrans = new MSE_OrderDetails();
											dbhelper.getReadableDatabase();
											mse_OrderDetailsForTrans = dbhelper
													.getMseOrderDetails(
															orderNo, itno);
											dbhelper.closeDatabase();
											dbhelper.getWritableDatabase();
											resu = dbhelper
													.addMse_TransDetails(mse_OrderDetailsForTrans);
											dbhelper.closeDatabase();
										} else {
											dbhelper.getReadableDatabase();
											String val = dbhelper
													.checkShipInTrans1(orderNo,
															itno);
											dbhelper.closeDatabase();
											if (val.equalsIgnoreCase("Yes")/*
																			 * ||
																			 * test
																			 * ==
																			 * false
																			 */) {
												MSE_OrderDetails mse_OrderDetailsForTrans = new MSE_OrderDetails();
												dbhelper.getReadableDatabase();
												mse_OrderDetailsForTrans = dbhelper
														.getMseOrderDetails(
																orderNo, itno);
												dbhelper.closeDatabase();
												dbhelper.getWritableDatabase();
												resu = dbhelper
														.addMse_TransDetails(mse_OrderDetailsForTrans);
												dbhelper.closeDatabase();
											} else {
												dbhelper.getWritableDatabase();
												resu = dbhelper
														.updateMse_TransDetails(mse_OrderDetails);
												dbhelper.closeDatabase();
											}
										}
									}
									if (resu.equals("success")) {
										MseItemList.flag = "true";
										Intent i = new Intent(MseItemList.this,
												MseItemList.class);
										i.putExtra("OrdNo_ShipMent", orderNo);
										startActivity(i);

									}

									else {
										/*
										 * Toast.makeText(getBaseContext(),
										 * "Input not Valid",
										 * Toast.LENGTH_LONG).show();
										 */
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
							 * Toast.makeText(this, "No Order Quantity",
							 * Toast.LENGTH_SHORT).show();
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
					/*
					 * Toast.makeText(this, "Item not Available",
					 * Toast.LENGTH_SHORT).show();
					 */toastText.setText("Item not available");
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
			// ItemNumber

			if (resultCode == RESULT_OK) {
				Log.i("Scan resul format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				String itNo = intent.getStringExtra("SCAN_RESULT");

				dbhelper.getReadableDatabase();
				MSE_OrderDetails mse_OrderDetails = dbhelper
						.getMseOrderDetails(orderNo, itNo);
				dbhelper.closeDatabase();

				if (mse_OrderDetails != null) {
					/* if (itemno == null) { */
					itemno = mse_OrderDetails.getItemNumber();
					qtyOrderd = mse_OrderDetails.getQtyOrdred();
					qtyShipd = mse_OrderDetails.getQtyShiped();
					pickSeq = mse_OrderDetails.getPickingSequence();
					shipVia = mse_OrderDetails.getShipViaCode();
					uom = mse_OrderDetails.getUom();
					comment = mse_OrderDetails.getComments();
					/*
					 * Intent i = new Intent(MseItemList.this,
					 * MseShipmentEntry.class);
					 * 
					 * i.putExtra("itemNo", itemno); i.putExtra("qtyOrder",
					 * qtyOrderd); i.putExtra("ordShipd", qtyShipd);
					 * i.putExtra("pickSeq", pickSeq); i.putExtra("shipVia",
					 * shipVia); i.putExtra("uom", uom); i.putExtra("ordNo",
					 * orderNo); startActivity(i);
					 */
					if (qtyOrderd != 0 && qtyOrderd > 0) {
						View view = li.inflate(R.layout.mse_itemlist, null);

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
						edt_pickSeq.setText(pickSeq);
						edt_shipVia.setText(shipVia);
						edt_comments.setText(comment);

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

								String val = edt_qtyShiped.getText().toString();
								if (val.matches("")) {
									/*
									 * Toast.makeText(MseItemList.this,
									 * "Please Enter the Shipped Qty ",
									 * Toast.LENGTH_SHORT).show();
									 */
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
									// Toast.makeText(MseShipmentEntry.this,
									// "Data Saved Successfully ",
									// Toast.LENGTH_SHORT).show();

								} else if (Integer.parseInt(edt_qtyShiped
										.getText().toString()) == 0) {
									/*
									 * Toast.makeText(MseItemList.this,
									 * "Shipped Qty cannot be 0 ",
									 * Toast.LENGTH_SHORT).show();
									 */
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
											MseItemList.this);
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

															// Toast.makeText(MseShipmentEntry.this,
															// "Data Saved Successfully ",
															// Toast.LENGTH_SHORT).show();

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
											.setText("Minimum Shipped quantity reached");
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
									 * "Shipped Qty Exceeds Order Qty",
									 * Toast.LENGTH_LONG).show();
									 */
									toastText
											.setText("Shipped Qty Exceeds Order Qty");
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
								// TODO Auto-generated method stub
								String resu = "";
								MSE_OrderDetails mse_OrderDetails = new MSE_OrderDetails();

								String itno = edt_itemNo.getText().toString();
								Integer ordqy = Integer.parseInt(edt_qtyOrd
										.getText().toString());
								String picseq = edt_pickSeq.getText()
										.toString();
								String uom_ = edt_uom.getText().toString();
								Integer shQty = Integer.parseInt(edt_qtyShiped
										.getText().toString());
								String comment = edt_comments.getText()
										.toString();

								mse_OrderDetails.setComments(comment);
								mse_OrderDetails.setQtyShiped(shQty);
								mse_OrderDetails.setItemNumber(itno);
								mse_OrderDetails.setQtyOrdred(ordqy);
								mse_OrderDetails.setPickingSequence(picseq);
								mse_OrderDetails.setUom(uom_);
								mse_OrderDetails.setOrdNumber(orderNo);
								dbhelper.getWritableDatabase();
								String result = dbhelper
										.updateMse_OrderDetails(
												mse_OrderDetails, "Ship");
								dbhelper.closeDatabase();

								if (result.equals("success")) {

									dbhelper.getReadableDatabase();
									boolean flag = dbhelper.checkMSE();
									dbhelper.getReadableDatabase();
									if (flag == true) {
										MSE_OrderDetails mse_OrderDetailsForTrans = new MSE_OrderDetails();
										dbhelper.getReadableDatabase();
										mse_OrderDetailsForTrans = dbhelper
												.getMseOrderDetails(orderNo,
														itno);
										dbhelper.closeDatabase();
										dbhelper.getWritableDatabase();
										resu = dbhelper
												.addMse_TransDetails(mse_OrderDetailsForTrans);
										dbhelper.closeDatabase();
									} else {
										dbhelper.getReadableDatabase();
										String val = dbhelper
												.checkShipInTrans1(orderNo,
														itno);
										dbhelper.closeDatabase();
										if (val.equalsIgnoreCase("Yes")/*
																		 * ||
																		 * test
																		 * ==
																		 * false
																		 */) {
											MSE_OrderDetails mse_OrderDetailsForTrans = new MSE_OrderDetails();
											dbhelper.getReadableDatabase();
											mse_OrderDetailsForTrans = dbhelper
													.getMseOrderDetails(
															orderNo, itno);
											dbhelper.closeDatabase();
											dbhelper.getWritableDatabase();
											resu = dbhelper
													.addMse_TransDetails(mse_OrderDetailsForTrans);
											dbhelper.closeDatabase();
										} else {
											dbhelper.getWritableDatabase();
											resu = dbhelper
													.updateMse_TransDetails(mse_OrderDetails);
											dbhelper.closeDatabase();
										}
									}
								}
								if (resu.equals("success")) {
									MseItemList.flag = "true";
									Intent i = new Intent(MseItemList.this,
											MseItemList.class);
									i.putExtra("OrdNo_ShipMent", orderNo);
									startActivity(i);

								}

								else {
									/*
									 * Toast.makeText(getBaseContext(),
									 * "Input not Valid",
									 * Toast.LENGTH_LONG).show();
									 */
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
						/*
						 * Toast.makeText(this, "No Order Quantity",
						 * Toast.LENGTH_SHORT).show();
						 */
						toastText.setText("No Order Quantity");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.setView(toastLayout);
						toast.show();
					}

					/* } */

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
				Toast.makeText(this, "Scanning Canceled", Toast.LENGTH_LONG)
						.show();

				Log.i("Scanning cancelled ", "Scanning cancelled");

			}

		}
		// ////////////////////////////////////
		else if (requestCode == 2) {
			// Scan Result for UPC
			if (resultCode == RESULT_OK) {
				Log.i("Scan resul format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				String upcCode = intent.getStringExtra("SCAN_RESULT");

				dbhelper.getReadableDatabase();
				Upc_Number Upc = dbhelper.getItemFromUpc(upcCode);// , cmpnyNo);
				dbhelper.closeDatabase();

				if (Upc != null) {
					String Itno = Upc.getItemno();
					dbhelper.getReadableDatabase();
					boolean flag = dbhelper
							.checkMse_OrderDetails(orderNo, Itno);
					dbhelper.closeDatabase();

					if (/* itemno == null && */flag == true) {

						dbhelper.getReadableDatabase();
						MSE_OrderDetails mse_OrderDetails = dbhelper
								.getMseOrderDetails(orderNo, Upc.getItemno());
						dbhelper.closeDatabase();

						itemno = mse_OrderDetails.getItemNumber();
						qtyOrderd = mse_OrderDetails.getQtyOrdred();
						qtyShipd = mse_OrderDetails.getQtyShiped();
						pickSeq = mse_OrderDetails.getPickingSequence();
						shipVia = mse_OrderDetails.getShipViaCode();
						uom = mse_OrderDetails.getUom();
						comment = mse_OrderDetails.getComments();
						if (qtyOrderd != 0 && qtyOrderd > 0) {
							View view = li.inflate(R.layout.mse_itemlist, null);

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
							edt_pickSeq.setText(pickSeq);
							edt_shipVia.setText(shipVia);

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
										/*
										 * Toast.makeText(MseItemList.this,
										 * "Please Enter the Shipped Qty ",
										 * Toast.LENGTH_SHORT).show();
										 */
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
										// Toast.makeText(MseShipmentEntry.this,
										// "Data Saved Successfully ",
										// Toast.LENGTH_SHORT).show();

									} else if (Integer.parseInt(edt_qtyShiped
											.getText().toString()) == 0) {
										/*
										 * Toast.makeText(MseItemList.this,
										 * "Shipped Qty cannot be 0 ",
										 * Toast.LENGTH_SHORT).show();
										 */
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
												MseItemList.this);
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

																// Toast.makeText(MseShipmentEntry.this,
																// "Data Saved Successfully ",
																// Toast.LENGTH_SHORT).show();

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
												.setText("Minimum Shipped quantity reached");
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
										 * "Shipped Qty Exceeds Order Qty",
										 * Toast.LENGTH_LONG).show();
										 */
										toastText
												.setText("Shipped Qty Exceeds Order Qty");
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
									// TODO Auto-generated method stub
									String resu = "";
									MSE_OrderDetails mse_OrderDetails = new MSE_OrderDetails();

									String itno = edt_itemNo.getText()
											.toString();
									Integer ordqy = Integer.parseInt(edt_qtyOrd
											.getText().toString());
									String picseq = edt_pickSeq.getText()
											.toString();
									String uom_ = edt_uom.getText().toString();
									Integer shQty = Integer
											.parseInt(edt_qtyShiped.getText()
													.toString());
									String comment = edt_comments.getText()
											.toString();

									mse_OrderDetails.setComments(comment);
									mse_OrderDetails.setQtyShiped(shQty);
									mse_OrderDetails.setItemNumber(itno);
									mse_OrderDetails.setQtyOrdred(ordqy);
									mse_OrderDetails.setPickingSequence(picseq);
									mse_OrderDetails.setUom(uom_);
									mse_OrderDetails.setOrdNumber(orderNo);
									dbhelper.getWritableDatabase();
									String result = dbhelper
											.updateMse_OrderDetails(
													mse_OrderDetails, "Ship");
									dbhelper.closeDatabase();

									if (result.equals("success")) {

										dbhelper.getReadableDatabase();
										boolean flag = dbhelper.checkMSE();
										dbhelper.getReadableDatabase();
										if (flag == true) {
											MSE_OrderDetails mse_OrderDetailsForTrans = new MSE_OrderDetails();
											dbhelper.getReadableDatabase();
											mse_OrderDetailsForTrans = dbhelper
													.getMseOrderDetails(
															orderNo, itno);
											dbhelper.closeDatabase();
											dbhelper.getWritableDatabase();
											resu = dbhelper
													.addMse_TransDetails(mse_OrderDetailsForTrans);
											dbhelper.closeDatabase();
										} else {
											dbhelper.getReadableDatabase();
											String val = dbhelper
													.checkShipInTrans1(orderNo,
															itno);
											dbhelper.closeDatabase();
											if (val.equalsIgnoreCase("Yes")/*
																			 * ||
																			 * test
																			 * ==
																			 * false
																			 */) {
												MSE_OrderDetails mse_OrderDetailsForTrans = new MSE_OrderDetails();
												dbhelper.getReadableDatabase();
												mse_OrderDetailsForTrans = dbhelper
														.getMseOrderDetails(
																orderNo, itno);
												dbhelper.closeDatabase();
												dbhelper.getWritableDatabase();
												resu = dbhelper
														.addMse_TransDetails(mse_OrderDetailsForTrans);
												dbhelper.closeDatabase();
											} else {
												dbhelper.getWritableDatabase();
												resu = dbhelper
														.updateMse_TransDetails(mse_OrderDetails);
												dbhelper.closeDatabase();
											}
										}
									}
									if (resu.equals("success")) {
										MseItemList.flag = "true";
										Intent i = new Intent(MseItemList.this,
												MseItemList.class);
										i.putExtra("OrdNo_ShipMent", orderNo);
										startActivity(i);

									}

									else {
										/*
										 * Toast.makeText(getBaseContext(),
										 * "Input not Valid",
										 * Toast.LENGTH_LONG).show();
										 */
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
							 * Toast.makeText(this, "No Order Quantity",
							 * Toast.LENGTH_SHORT).show();
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

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent i = new Intent(MseItemList.this, MseOrderList.class);
			MseOrderList.returnfrm = "true";
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
