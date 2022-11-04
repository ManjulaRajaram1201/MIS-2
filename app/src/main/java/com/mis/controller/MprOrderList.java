package com.mis.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.mobinventorysuit.R;
import com.mis.adapter.Order_CustAdapter_mpr;
import com.mis.common.Login;
import com.mis.common.AppBaseActivity;
import com.mis.common.MainActivity;
import com.mis.database.DatabaseHandler;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MprOrderList extends AppBaseActivity {

	private Button mbtn_import;
	private EditText edtSearch;
	private Button mbtn_export;

	private Button mbtn_exit;
	private RadioButton mradio_Ord;
	private RadioButton mradio_CustNo;
	private RadioGroup OrdorCust;
	private ListView lstSel;
	public Order_CustAdapter_mpr adapter;
	public static String returnfrm = "false";
	public static String order_Details = "";
	TextView toastText;
	View toastLayout;
	public String CompanyID;
	public String F_URL;
	Cursor cursor;
	int path;
	String arr[];// =new String[10];
	public TelephonyManager telephonemanager;
	private DatabaseHandler handler;
	public static List<String> rec_selected_Po;

	@SuppressLint("WorldWriteableFiles")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mpr_orderlist);

		/*
		 * InputMethodManager imm =
		 * (InputMethodManager)this.getSystemService(Context
		 * .INPUT_METHOD_SERVICE);
		 */
		registerBaseActivityReceiver();

		LayoutInflater inflater = getLayoutInflater();
		toastLayout = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));
		toastText = (TextView) toastLayout.findViewById(R.id.text);
		lstSel = (ListView) findViewById(R.id.lstSelOrder);
		mbtn_import = (Button) findViewById(R.id.btn_OrderLstImport);
		mbtn_export = (Button) findViewById(R.id.btn_OrderLstExport);
		mbtn_exit = (Button) findViewById(R.id.btn_OrderLstExit);
		OrdorCust = (RadioGroup) findViewById(R.id.radioGroup1);
		mradio_Ord = (RadioButton) findViewById(R.id.radio_OrdListOrdNo);
		mradio_CustNo = (RadioButton) findViewById(R.id.radio_OrdListCustNo);
		edtSearch = (EditText) findViewById(R.id.edt_OrderLst_Search);

		mradio_Ord.setChecked(true);
		order_Details = "ON";

		edtSearch.setInputType(InputType.TYPE_CLASS_TEXT);

		path = getIntent().getIntExtra("Came", 0);
		handler = new DatabaseHandler(this);

		handler.getWritableDatabase();
		String pendingexpo = handler.checkMprTransPendingExpo();
		handler.closeDatabase();

		if (pendingexpo.equals("true") && path == 1) {
			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					MprOrderList.this);
			alertDialog.setTitle("Confirmation");
			alertDialog.setIcon(R.drawable.warning);
			alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							Intent i = new Intent(MprOrderList.this,
									MprExport.class);
							startActivity(i);

						}
					});
			alertDialog.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Set<String> ordset = new HashSet<String>();

							handler.getReadableDatabase();
							rec_selected_Po = handler.getTempPo();
							handler.closeDatabase();

							if (rec_selected_Po != null) {
								inflateList(getApplicationContext(),
										rec_selected_Po);

								returnfrm = "false";
								MprExportResult.notyetExpo = true;

							}
							/* clearPreference(); */
							/*
							 * Editor sel =
							 * getSharedPreferences("Selected_Order",
							 * Context.MODE_PRIVATE).edit(); sel.clear();
							 * sel.commit();
							 * 
							 * Editor po = getSharedPreferences("OrdToExpo",
							 * Context.MODE_PRIVATE) .edit(); po.clear();
							 * po.commit();
							 */

						}
					});
			alertDialog.setMessage("Do you want to Export Pending Order?");

			alertDialog.show();

		} else if (path == 2 || (pendingexpo.equals("false") && path == 1)) {

			handler.getReadableDatabase();
			rec_selected_Po = handler.getTempPo();
			handler.closeDatabase();

			if (rec_selected_Po != null) {
				inflateList(this, rec_selected_Po);

				returnfrm = "false";
				MprExportResult.notyetExpo = true;

			}

		}
		try {
			if (returnfrm.equals("true")
					&& MprExportResult.notyetExpo.equals(true)) {

				handler.getReadableDatabase();
				rec_selected_Po = handler.getTempPo();
				handler.closeDatabase();

				if (rec_selected_Po != null) {
					inflateList(this, rec_selected_Po);

					returnfrm = "false";
					MprExportResult.notyetExpo = true;

				}
			} else if (returnfrm.equals("true")
					&& MprExportResult.notyetExpo.equals(false)) {
				System.out.println("Came to OrederList try ELSE IF");

				returnfrm = "false";
				MprExportResult.notyetExpo = true;

				List<String> lst = new ArrayList<String>();

				handler.getReadableDatabase();
				lst = handler.getTempExportedPo();
				handler.closeDatabase();
				
				if(lst!=null)
				{
					for (int i = 0; i < lst.size(); i++) {
						// Deleting the Order details once Shipped successfully
						handler.getWritableDatabase();
						handler.deleteFromOrderTable_mpr(lst.get(i));
						handler.closeDatabase();

						// Deleting the Master details once Shipped successfully
						handler.getWritableDatabase();
						handler.deleteFromMasterTable_mpr(lst.get(i));
						handler.closeDatabase();

						// Deleting the Trans details once Shipped successfully
						handler.getWritableDatabase();
						handler.deleteFromTransTable_mpr(lst.get(i));
						handler.closeDatabase();

						// Deleting the Temp details once Shipped successfully
						handler.getWritableDatabase();
						handler.deleteTempPo(lst.get(i));
						handler.closeDatabase();
					}
				}
				

				handler.getReadableDatabase();
				lst = handler.getTempPo();
				handler.closeDatabase();

				rec_selected_Po = lst;

				if (lst != null) {
					inflateList(this, lst);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {

				if (!(rec_selected_Po == null || rec_selected_Po.isEmpty())) {
					int radioButtonID = OrdorCust.getCheckedRadioButtonId();
					View radioClicked = OrdorCust.findViewById(radioButtonID);
					int idx = OrdorCust.indexOfChild(radioClicked);
					if (idx == 0) {// Order
						adapter.getFilter().filter(cs.toString());
					} else {// Cust

						System.out.println("CS" + cs.toString());
						adapter.getFilter().filter(cs.toString());
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable edtxt) {
				// TODO Auto-generated method stub
			}

		});

		mradio_CustNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				order_Details = "CN";
				edtSearch.setText("");
				/*
				 * edtSearch.setInputType(InputType.TYPE_CLASS_NUMBER);
				 */
				inflateList(MprOrderList.this, rec_selected_Po);

			}
		});

		mradio_Ord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				order_Details = "ON";
				edtSearch.setInputType(InputType.TYPE_CLASS_TEXT);
				edtSearch.setText("");

				inflateList(MprOrderList.this, rec_selected_Po);

			}
		});

		mbtn_import.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					if (rec_selected_Po == null || rec_selected_Po.isEmpty()) {
						if (order_Details.equals("ON")) {
							Intent i = new Intent(MprOrderList.this,
									MprImportData.class);
							startActivity(i);
						} else {
							Intent i = new Intent(MprOrderList.this,
									Mpr_Customer.class);

							startActivity(i);
						}
					} else {
						final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
								MprOrderList.this);
						alertDialog.setTitle("Confirmation");
						alertDialog.setIcon(R.drawable.warning);
						alertDialog.setCancelable(false);
						alertDialog.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

										if (order_Details.equals("ON")) {
											Intent i = new Intent(
													MprOrderList.this,
													MprImportData.class);
											startActivity(i);
										} else {
											Intent i = new Intent(
													MprOrderList.this,
													Mpr_Customer.class);

											startActivity(i);
										}

									}
								});
						alertDialog.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});
						alertDialog
								.setMessage("Do you want to OverWrite the Existing Order?");

						alertDialog.show();

					}

				}

				catch (Exception ex) {
					Toast.makeText(MprOrderList.this, ex.getLocalizedMessage(),
							Toast.LENGTH_LONG).show();
					Log.e("ERROR", ex.getLocalizedMessage());

				}

			}
		});
		mbtn_export.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent i = new Intent(getBaseContext(), MprExport.class);
				startActivity(i);
			}
		});

		mbtn_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i2 = new Intent(MprOrderList.this, Login.class);
				startActivity(i2);

			}
		});

		lstSel.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				TextView c = (TextView) view.findViewById(R.id.txtfirst_detail);
				String OrderNo = c.getText().toString();

				Intent i = new Intent(MprOrderList.this, MprItemList.class);
				i.putExtra("ordNo_OrderList", OrderNo);
				startActivity(i);
			}
		});
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
			// TODO Auto-generated method stub
			try {
				if (rec_selected_Po == null || rec_selected_Po.isEmpty()) {
					if (order_Details.equals("ON")) {
						Intent i = new Intent(MprOrderList.this,
								MprImportData.class);
						startActivity(i);
					} else {
						Intent i = new Intent(MprOrderList.this,
								Mpr_Customer.class);

						startActivity(i);
					}
				} else {
					final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							MprOrderList.this);
					alertDialog.setTitle("Confirmation");
					alertDialog.setIcon(R.drawable.warning);
					alertDialog.setCancelable(false);
					alertDialog.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

									if (order_Details.equals("ON")) {
										Intent i = new Intent(
												MprOrderList.this,
												MprImportData.class);
										startActivity(i);
									} else {
										Intent i = new Intent(
												MprOrderList.this,
												Mpr_Customer.class);

										startActivity(i);
									}

								}
							});
					alertDialog.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
					alertDialog
							.setMessage("Do you want to OverWrite the Existing Order?");

					alertDialog.show();

				}

			}

			catch (Exception ex) {
				Toast.makeText(MprOrderList.this, ex.getLocalizedMessage(),
						Toast.LENGTH_LONG).show();
				Log.e("ERROR", ex.getLocalizedMessage());

			}

		} else if (id == R.id.main_mnu_Export) {
			// TODO Auto-generated method stub
			handler.getWritableDatabase();
			String pendingexpo = handler.checkMprTransPendingExpo();
			handler.closeDatabase();

			if (pendingexpo.equals("true")) {
				Intent i = new Intent(getBaseContext(), MprExport.class);
				startActivity(i);
			} else {
				toastText.setText("Transaction not Available for Export");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(toastLayout);
				toast.show();

			}

		} else {
			// TODO Auto-generated method stub
			Intent i2 = new Intent(MprOrderList.this, Login.class);
			startActivity(i2);

		}
		return super.onOptionsItemSelected(item);
	}

	private void inflateList(Context context, List<String> rec_selected_ord) {
		// TODO Auto-generated method stub
		try {
			List<String> custNum = new ArrayList<String>();
			if (!(rec_selected_ord == null)) {

				System.out.println("CAme TO INFLATE");
				// Getting All CustNo in List
				for (int i = 0; i < rec_selected_ord.size(); i++) {
					String ordNum = rec_selected_ord.get(i);
					handler.getReadableDatabase();
					Cursor cursor = handler.getMPRCustomerData(ordNum);
					if (cursor.getCount() > 0) {
						custNum.add(cursor.getString(3).toString());
					} else {
						custNum.add("PRANESAH");
					}
				}
				if (custNum.size() > 0) {
					adapter = new Order_CustAdapter_mpr(context,
							rec_selected_ord, custNum);

					lstSel.setAdapter(adapter);
				}
			}
			// adapter.notifyDataSetChanged();
		} catch (Exception ex) {
			Log.e("Failed", ex.getLocalizedMessage());
		}
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// TODO Auto-generated method stub
			Intent i2 = new Intent(MprOrderList.this, Login.class);
			startActivity(i2);

		}
		return super.onKeyDown(keyCode, event);
	}

	private void exportCancelMethod() {
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MprOrderList.this);
		alertDialog.setTitle("Confirmation");
		alertDialog.setIcon(R.drawable.warning);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(MprOrderList.this, Login.class);
						startActivity(i);

					}
				});

		alertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.setMessage("Do you want to cancel ?");
		alertDialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unRegisterBaseActivityReceiver();
	}

}
