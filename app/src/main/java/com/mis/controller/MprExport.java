package com.mis.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import com.mis.adapter.Chk_Adapter_For_Export_mpr;
import com.mis.adapter.Chk_Model;
import com.mis.common.MIS_Setting;
import com.mis.common.AppBaseActivity;
import com.mis.controller.MseExport.DeleteOrder;
import com.mis.database.DatabaseHandler;
import com.mis.mpr.model.MPR_Trans;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MprExport extends AppBaseActivity {
	private Export mTask;
	public String JSONERROR = "";
	private ListView lstExpo;
	public static CheckBox chkAll;
	private DatabaseHandler handler;
	private List<MPR_Trans> ordList;
	ArrayList<Chk_Model> oList_Model;
	ArrayList<String> ord_Expo;
	ArrayList<String> cust;
	TextView toastText;
	View toastLayout;
	ArrayList<String> onlyShipNo;
	public String ShipNo;
	public HashMap<String, String> arrShipNo;
	public JSONObject putMprData;
	public JSONObject exportData;
	public JSONArray valueHeader;
	public JSONArray valueHeaderDetail;
	public String companyId;
	private String companyName = "";
	private Button btn_export;
	private Button btn_cancel;

	private EditText edtCompanyName;
	MPR_Trans mpr_trans;
	Chk_Adapter_For_Export_mpr adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mpr_export);

		// Toast
		LayoutInflater inflater = getLayoutInflater();
		toastLayout = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		toastText = (TextView) toastLayout.findViewById(R.id.text);

		registerBaseActivityReceiver();
		edtCompanyName = (EditText) findViewById(R.id.edt_mprCompanyName);
		lstExpo = (ListView) findViewById(R.id.lstSelPo);
		chkAll = (CheckBox) findViewById(R.id.chk_expAll);
		btn_cancel = (Button) findViewById(R.id.btn_mprExportCancel);
		btn_export = (Button) findViewById(R.id.btn_mprExport);
		handler = new DatabaseHandler(this);

		mpr_trans = new MPR_Trans();
		ordList = new ArrayList<MPR_Trans>();

		// hint
		handler.getReadableDatabase();
		companyName = handler.getCompanyName();
		handler.closeDatabase();
		edtCompanyName.setText(companyName);
		// btn_cancel=(Button)find
		displayOrdertoExport();
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				MprOrderList.returnfrm = "true";

				Intent i = new Intent(MprExport.this, MprOrderList.class);
				startActivity(i);

			}
		});

		chkAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isChecked = chkAll.isChecked();

				if (isChecked) {
					for (int i = 0; i < oList_Model.size(); i++) {
						Chk_Model model = oList_Model.get(i);
						model.setSelected(true);
					}
					// Chk_Adapter_For_Export_mpr.check+=oList_Model.size();
					adapter = new Chk_Adapter_For_Export_mpr(MprExport.this,
							oList_Model);
					lstExpo.setAdapter(adapter);
					handler.closeDatabase();

				} else {
					for (int i = 0; i < oList_Model.size(); i++) {
						Chk_Model model = oList_Model.get(i);
						model.setSelected(false);
					}
					adapter = new Chk_Adapter_For_Export_mpr(MprExport.this,
							oList_Model);
					lstExpo.setAdapter(adapter);
					handler.closeDatabase();

				}
				if (isChecked == false)
					for (int i = 0; i < lstExpo.getChildCount(); i++) {
						LinearLayout listviewlayout = (LinearLayout) lstExpo
								.getChildAt(i);
						CheckBox cb = (CheckBox) listviewlayout
								.findViewById(R.id.export_chk);
						cb.setChecked(true);

					}

			}

		});

		btn_export.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				ord_Expo = new ArrayList<String>();
				for (int i = 0; i < oList_Model.size(); i++) {
					Chk_Model model = oList_Model.get(i);
					if (model.isSelected()) {

						ord_Expo.add(model.getName());

					}
				}
				new Export(ord_Expo).execute();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mprexport, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.mprexport_mnu_order_exp) {

			// TODO Auto-generated method stub

			ord_Expo = new ArrayList<String>();
			for (int i = 0; i < oList_Model.size(); i++) {
				Chk_Model model = oList_Model.get(i);
				if (model.isSelected()) {

					ord_Expo.add(model.getName());

				}
			}
			if (ord_Expo.size() > 0) {
				new Export(ord_Expo).execute();
			} else {
				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						MprExport.this);
				alertDialog.setTitle("Error");
				alertDialog.setIcon(R.drawable.error);
				alertDialog.setCancelable(false);
				alertDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// Check if SHNO not in MSETRANS then do this
								// else if SHNO avail for order add one column
								// with same order but diff SHNO
								Log.i("Export failed", "Export failed.");

							}
						});
				alertDialog
						.setMessage("Please select the Transactions before exporting");

				alertDialog.show();

			}

		} else if (id == R.id.mprexport_mnu_del) {
			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					MprExport.this);
			alertDialog.setTitle("Confirmation");
			alertDialog.setIcon(R.drawable.warning);
			alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							ord_Expo = new ArrayList<String>();
							for (int i = 0; i < oList_Model.size(); i++) {
								Chk_Model model = oList_Model.get(i);
								if (model.isSelected()) {

									ord_Expo.add(model.getName());

								}
							}
							new DeleteOrder(ord_Expo).execute();
						}
					});

			alertDialog.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			alertDialog.setMessage("Do you want to delete?");
			alertDialog.show();

		} else {

			MprOrderList.returnfrm = "true";

			Intent i = new Intent(MprExport.this, MprOrderList.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}

	public static void CheckAll() {
		chkAll.setChecked(true);
	}

	public static void UnCheckAll() {
		chkAll.setChecked(false);
	}

	class DeleteOrder extends AsyncTask<String, String, String> {

		String result = "";
		ProgressDialog dialog;
		ArrayList<String> orderToDelete = new ArrayList<String>();

		public DeleteOrder(ArrayList<String> ord_Expo) {

			dialog = new ProgressDialog(MprExport.this);
			dialog.setCancelable(false);
			this.orderToDelete = ord_Expo;

		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Please Wait while Deleting Transactions...");
			dialog.show();
		}

		protected String doInBackground(String... results) {

			// Deleting order details from Mse Tables
			handler.getWritableDatabase();
			result = handler.deleteTrans(orderToDelete);
			handler.closeDatabase();

			return result;
		}

		protected void onProgressUpdate(String... progress) {
			super.onProgressUpdate(progress);
			// pd.setProgress(Integer.parseInt(progress[0]));
		}

		protected void onPostExecute(String result) {
			if (chkAll.isChecked())
				chkAll.setChecked(false);
			if (result.equals("success")) {

				// hint

				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						MprExport.this);
				alertDialog.setTitle("Info");
				alertDialog.setIcon(R.drawable.rsz_ok1);
				alertDialog.setCancelable(false);
				alertDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// Check if SHNO not in MSETRANS then do this
								// else if SHNO avail for order add one column
								// with same order but diff SHNO
								Log.i("Deletion success", "Deletion success.");

							}
						});
				alertDialog.setMessage("Transactions deleted Successfully");

				alertDialog.show();
				dialog.dismiss();
				displayOrdertoExport();

			} else {

				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						MprExport.this);
				alertDialog.setTitle("Error");
				alertDialog.setIcon(R.drawable.error);
				alertDialog.setCancelable(false);
				alertDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// Check if SHNO not in MSETRANS then do this
								// else if SHNO avail for order add one column
								// with same order but diff SHNO
								Log.i("Deletion failed", "Deletion failed.");

							}
						});
				alertDialog
						.setMessage("Please try again to delete the Transactions");

				alertDialog.show();
				dialog.dismiss();
				displayOrdertoExport();

			}

		}
	}

	class Export extends AsyncTask<String, String, String> {

		ProgressDialog dialog;
		ArrayList<String> orderToExport = new ArrayList<String>();

		public Export(ArrayList<String> ord_Expo) {

			dialog = new ProgressDialog(MprExport.this);
			dialog.setCancelable(false);
			this.orderToExport = ord_Expo;

		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Please Wait while Exporting Data...");
			dialog.show();
		}

		protected String doInBackground(String... results) {
			String result = "";
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			MIS_Setting mis_setting = new MIS_Setting();
			handler.getReadableDatabase();
			mis_setting = handler.getSetting();
			handler.closeDatabase();
			String deviceId = mis_setting.getDeviceId();
			String ipAddress = mis_setting.getIpAddress();
			handler.getReadableDatabase();
			companyId = handler.LOAD_COMPANYID(deviceId);
			handler.closeDatabase();
			String F_URL = "http://" + ipAddress
					+ "/MISWCFService/Service.svc/PutMPR";
			try {
				arrShipNo = new HashMap<String, String>();

				onlyShipNo = new ArrayList<String>();
				System.out.println("Received in expo backgrd" + orderToExport);

				for (int i = 0, l = orderToExport.size(); i < l; i++) {
					ExportData(orderToExport.get(i));

					String total;
					HttpResponse response = null;

					final HttpClient httpclient = new DefaultHttpClient();
					final HttpPost httppost = new HttpPost(F_URL);
					System.out.println("URL...." + F_URL);

					httppost.setHeader("Accept", "application/json");
					httppost.setHeader("Content-Type", "application/json");

					JSONStringer jsonStringer = new JSONStringer().object()
							.key("putmprdata").object().key("CompanyID")
							.value(companyId).key("ValueHeader")
							.value(valueHeaderDetail).key("ValueHeaderDetail")
							.value(valueHeader).endObject();

					StringEntity entity = new StringEntity(
							jsonStringer.toString());

					System.out.println("String....." + jsonStringer.toString());

					entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
							"application/json"));
					httppost.setEntity(entity);

					if (isConnected(getApplicationContext())) {

						HttpGet httpget = new HttpGet(F_URL);
						try {
							HttpParams httpParameters = httpget.getParams();
							// Set the timeout in milliseconds until a
							// connection is
							// established.
							int timeoutConnection = 120000;
							HttpConnectionParams.setConnectionTimeout(
									httpParameters, timeoutConnection);
							// Set the default socket timeout (SO_TIMEOUT)
							// in milliseconds which is the timeout for waiting
							// for data.
							int timeoutSocket = 120000;
							HttpConnectionParams.setSoTimeout(httpParameters,
									timeoutSocket);
							response = httpclient.execute(httppost);
							handler.closeDatabase();

							StatusLine statusLine = response.getStatusLine();

							System.out.println("StatusLine=" + statusLine);
							int statusCode = statusLine.getStatusCode();

							System.out.println("StatusCode=" + statusCode);
							System.out.println("Response=" + response);

							if (response != null && statusCode == 200) {
								HttpEntity httpEntity = response.getEntity();
								total = EntityUtils.toString(httpEntity);

								JSONObject jobject = new JSONObject(total);
								try {
									JSONObject jobject1 = jobject
											.getJSONObject("PutMPRResult");

									if (jobject1 != null) {
										JSONArray jarray = jobject1
												.getJSONArray("PoReceipt");
										if (jarray != null) {
											JSONObject jobject2 = jarray
													.getJSONObject(0);
											String str = jobject2
													.getString("RCPNUMBER");
											ShipNo = str;
										}
									}
									handler.getWritableDatabase();
									handler.Update_MPRTrans(ShipNo,
											orderToExport.get(i));
									handler.closeDatabase();

									handler.getWritableDatabase();
									handler.UpdateTempPo(orderToExport.get(i));
									handler.closeDatabase();

									arrShipNo.put(ShipNo, orderToExport.get(i));
									System.out.println("HashMap" + arrShipNo);
									onlyShipNo.add(ShipNo);
									System.out
											.println("ArrayList" + onlyShipNo);

									if (!result.equals("jsonError"))
										result = "success";

								} catch (JSONException ex) {/*
															 * handler.
															 * getWritableDatabase
															 * (); handler.
															 * Update_MPRTrans_Fail
															 * (
															 * orderToExport.get
															 * (i),
															 * orderToExport
															 * .get(i));
															 * handler.
															 * closeDatabase();
															 * dialog.dismiss();
															 * // key value pair
															 * is getting same
															 * if we hard //
															 * coded the first
															 * vale // thats why
															 * we were putting
															 * key value also //
															 * as // ord no
															 * arrShipNo
															 * .put(orderToExport
															 * .get(i),
															 * orderToExport
															 * .get(i));
															 * onlyShipNo
															 * .add(ShipNo);
															 * result =
															 * "jsonError";
															 * ex.printStackTrace
															 * ();
															 */
									handler.getWritableDatabase();
									handler.Update_MSETrans_Fail(
											orderToExport.get(i),
											orderToExport.get(i));
									handler.closeDatabase();
									// key value pair is getting same if we hard
									// coded the first vale
									// thats why we were putting key value also
									// as
									// ord no
									// dialog.dismiss();
									arrShipNo.put(orderToExport.get(i),
											orderToExport.get(i));
									onlyShipNo.add(ShipNo);

									JSONObject jobject1 = jobject
											.getJSONObject("PutMSE1Result");

									System.out.println("JSONOBJECT" + jobject1);

									if (jobject1 != null) {
										JSONArray jarray = jobject1
												.getJSONArray("ErrorInfo");

										if (jarray != null) {
											JSONObject jobject2 = jarray
													.getJSONObject(0);
											JSONERROR = jobject2
													.getString("ERRORMESSAGE");

										}
									}

									result = "jsonError";
									ex.printStackTrace();

								}

							}

						} catch (HttpHostConnectException e) {
							handler.getWritableDatabase();
							handler.Update_MPRTrans_Fail(orderToExport.get(i),
									orderToExport.get(i));
							handler.closeDatabase();
							//dialog.dismiss();
							// key value pair is getting same if we hard coded
							// the
							// first vale
							// thats why we were putting key value also as ord
							// no
							arrShipNo.put(orderToExport.get(i),
									orderToExport.get(i));
							onlyShipNo.add(ShipNo);
							result = "hostconnError";
						} catch (ClientProtocolException cpe) {
							handler.getWritableDatabase();
							handler.Update_MPRTrans_Fail(orderToExport.get(i),
									orderToExport.get(i));
							handler.closeDatabase();
						//	dialog.dismiss();
							// key value pair is getting same if we hard coded
							// the
							// first vale
							// thats why we were putting key value also as ord
							// no
							arrShipNo.put(orderToExport.get(i),
									orderToExport.get(i));
							onlyShipNo.add(ShipNo);
							result = "clientProt";

						} catch (ParseException pe) {
							handler.getWritableDatabase();
							handler.Update_MPRTrans_Fail(orderToExport.get(i),
									orderToExport.get(i));
							handler.closeDatabase();
							//dialog.dismiss();
							// key value pair is getting same if we hard coded
							// the
							// first vale
							// thats why we were putting key value also as ord
							// no
							arrShipNo.put(orderToExport.get(i),
									orderToExport.get(i));
							onlyShipNo.add(ShipNo);
							result = "parseExcep";
						} catch (IOException io) {
							handler.getWritableDatabase();
							handler.Update_MPRTrans_Fail(orderToExport.get(i),
									orderToExport.get(i));
							handler.closeDatabase();
						//	dialog.dismiss();
							// key value pair is getting same if we hard coded
							// the
							// first vale
							// thats why we were putting key value also as ord
							// no
							arrShipNo.put(orderToExport.get(i),
									orderToExport.get(i));
							onlyShipNo.add(ShipNo);
							result = "io";
						} catch (Exception ex) {
						//	dialog.dismiss();
							result = "error";
							ex.printStackTrace();
						}
					} else {
						handler.getWritableDatabase();
						handler.Update_MSETrans_Fail(orderToExport.get(i),
								orderToExport.get(i));
						handler.closeDatabase();

						// key value pair is getting same if we hard coded
						// the
						// first vale
						// thats why we were putting key value also as ord
						// no
						arrShipNo.put(orderToExport.get(i),
								orderToExport.get(i));
						onlyShipNo.add(ShipNo);
						result = "internetprob";

					}
				}

			}

			catch (Exception e) {
				//dialog.dismiss();
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
			if (chkAll.isChecked())
				chkAll.setChecked(false);
			dialog.dismiss();
			if (result.equals("success")) {

				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						MprExport.this);
				alertDialog.setTitle("Info");
				alertDialog.setIcon(R.drawable.rsz_ok1);
				alertDialog.setCancelable(false);
				alertDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// Check if SHNO not in MSETRANS then do this
								// else if SHNO avail for order add one column
								// with same order but diff SHNO
								Log.i("Transaction success",
										"Transaction success.");
								/*
								 * adapter.clear();
								 * adapter.notifyDataSetChanged();
								 * lstExpo.setAdapter(adapter);
								 */
								Intent i = new Intent(MprExport.this,
										MprExportResult.class);
								i.putExtra("OrdShipNoKey", arrShipNo);
								i.putExtra("OnlyShipNoKey", onlyShipNo);
								startActivity(i);

							}
						});
				alertDialog.setMessage("Data Exported Successfully");

				alertDialog.show();

			}

			else if (!result.equals("") && !result.equals("success")) {

				if (result.contains("socTimeError".toString())
						|| result.contains("connTimeError".toString())) {
					result = "time out";

				} else if (result.contains("clientsideError".toString())
						|| result.contains("hostconnError".toString())) {

					result = "Connection prob";

				} else if (result.contains("parseExcep".toString())
						|| result.contains("io".toString())
						|| result.contains("jsonError".toString())
						|| result.contains("encoError".toString())) {
					result = "Data prob";

				}

				else if (result.contains("internetprob".toString())) {
					result = "Internet Prob";
				} else {
					result = "error";
				}

				if (result.contains("time out".toString())) {
					/*
					 * Toast.makeText(MseExport.this,
					 * "Time Out!    Try Again!!", Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Time Out! Please check the Server Path and try again!!");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				}

				else if (result.contains("Internet Prob".toString())) {
					/*
					 * Toast.makeText( MseExport.this,
					 * "Check your Internet Connectivity",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Check your Internet Connectivity");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else if (result.contains("Connection prob".toString())) {
					/*
					 * Toast.makeText( MseExport.this,
					 * "Problem while establishing connection with Server while Exporting"
					 * , Toast.LENGTH_LONG).show();
					 */
					toastText
							.setText("Problem while establishing connection with Server while Exporting");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				} else if (result.contains("Data prob".toString())) {/*
																	 * 
																	 * Toast.
																	 * makeText(
																	 * MseExport
																	 * .this,
																	 * "Format of Data doesn't matched for Some Order while Exporting"
																	 * , Toast.
																	 * LENGTH_LONG
																	 * ).show();
																	 * 
																	 * toastText
																	 * .setText(
																	 * "Problem while establishing connection with Server while Exporting"
																	 * ); Toast
																	 * toast =
																	 * new
																	 * Toast(
																	 * getApplicationContext
																	 * ());
																	 * toast
																	 * .setGravity
																	 * (Gravity.
																	 * CENTER_VERTICAL
																	 * , 0,
																	 * 410);
																	 * toast
																	 * .setDuration
																	 * (Toast.
																	 * LENGTH_SHORT
																	 * );
																	 * toast.setView
																	 * (
																	 * toastLayout
																	 * );
																	 * toast.show
																	 * ();
																	 * 
																	 * dialog.
																	 * dismiss
																	 * ();
																	 */
					if (!JSONERROR.equals("")) {
						toastText.setText(JSONERROR);
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
						toast.setDuration(Toast.LENGTH_LONG);
						toast.setView(toastLayout);
						toast.show();

						dialog.dismiss();
					} else {
						toastText
								.setText("Format of Data doesn't matched for Some Order while Exporting");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.setView(toastLayout);
						toast.show();

						dialog.dismiss();
					}

				} else {
					/*
					 * Toast.makeText(MseExport.this,
					 * "Order Not Exported Successfully",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Order Not Exported Successfully");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					dialog.dismiss();
				}

				Intent i = new Intent(MprExport.this, MprExportResult.class);
				i.putExtra("OrdShipNoKey", arrShipNo);
				i.putExtra("OnlyShipNoKey", onlyShipNo);
				startActivity(i);

				/*
				 * final AlertDialog.Builder alertDialog = new
				 * AlertDialog.Builder( MseExport.this);
				 * alertDialog.setTitle("Confirmation");
				 * alertDialog.setIcon(R.drawable.tick);
				 * alertDialog.setCancelable(false);
				 * alertDialog.setPositiveButton("Yes", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * public void onClick(DialogInterface dialog, int which) {
				 * 
				 * // Check if SHNO not in MSETRANS then do this // else if SHNO
				 * avail for order add one column // with same order but diff
				 * SHNO Log.i("Transaction success", "Transaction success.");
				 * 
				 * adapter.clear(); adapter.notifyDataSetChanged();
				 * lstExpo.setAdapter(adapter);
				 * 
				 * Intent i = new Intent(MseExport.this, MseExportResult.class);
				 * i.putExtra("OrdShipNoKey", arrShipNo);
				 * i.putExtra("OnlyShipNoKey", onlyShipNo); startActivity(i); }
				 * }); alertDialog.setNegativeButton("No", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * public void onClick(DialogInterface dialog, int which) { //
				 * Check if SHNO not in MSETRANS then do this // else if SHNO
				 * avail for order add one column // with same order but diff
				 * SHNO Log.i("Transaction success", "Transaction success.");
				 * 
				 * adapter.clear(); adapter.notifyDataSetChanged();
				 * lstExpo.setAdapter(adapter);
				 * 
				 * 
				 * } }); alertDialog .setMessage(
				 * "Some Orders was Not Exported Do you want to See the Status?"
				 * );
				 * 
				 * alertDialog.show();
				 */

			}
			/*
			 * else if(result.equals("hostconnError") || result.equals("io")||
			 * result.equals("parseExcep") || result.equals("clientProt")||
			 * result.equals("error")|| result.equals("jsonError")) {
			 * 
			 * Toast.makeText(MprExport.this, "Failure in posting some data.",
			 * Toast.LENGTH_LONG).show();
			 * 
			 * toastText.setText("Failure in Posting Some Data."); Toast toast =
			 * new Toast(getApplicationContext());
			 * toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
			 * toast.setDuration(Toast.LENGTH_SHORT);
			 * toast.setView(toastLayout); toast.show();
			 * 
			 * final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
			 * MprExport.this); alertDialog.setTitle("Info"); //
			 * alertDialog.setIcon(R.drawable.tick);
			 * alertDialog.setCancelable(false);
			 * alertDialog.setPositiveButton("Yes", new
			 * DialogInterface.OnClickListener() {
			 * 
			 * public void onClick(DialogInterface dialog, int which) {
			 * 
			 * // Check if SHNO not in MSETRANS then do this // else if SHNO
			 * avail for order add one column // with same order but diff SHNO
			 * Log.i("Transaction success", "Transaction success.");
			 * 
			 * adapter.clear(); adapter.notifyDataSetChanged();
			 * lstExpo.setAdapter(adapter);
			 * 
			 * Intent i = new Intent(MprExport.this, MprExportResult.class);
			 * i.putExtra("OrdShipNoKey", arrShipNo);
			 * i.putExtra("OnlyShipNoKey", onlyShipNo); startActivity(i); } });
			 * alertDialog.setNegativeButton("No", new
			 * DialogInterface.OnClickListener() {
			 * 
			 * public void onClick(DialogInterface dialog, int which) { // Check
			 * if SHNO not in MSETRANS then do this // else if SHNO avail for
			 * order add one column // with same order but diff SHNO
			 * Log.i("Transaction success", "Transaction success.");
			 * 
			 * adapter.clear(); adapter.notifyDataSetChanged();
			 * lstExpo.setAdapter(adapter);
			 * 
			 * 
			 * } }); alertDialog .setMessage(
			 * "Some Orders was Not Exported Do you want to See the Status?");
			 * 
			 * alertDialog.show();
			 * 
			 * } else { Toast.makeText(MprExport.this,
			 * "Failure in Exporting Order", Toast.LENGTH_SHORT).show();
			 * toastText.setText("Failure in Exporting Orders"); Toast toast =
			 * new Toast(getApplicationContext());
			 * toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
			 * toast.setDuration(Toast.LENGTH_SHORT);
			 * toast.setView(toastLayout); toast.show(); }
			 */
			dialog.dismiss();
			displayOrdertoExport();

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

	public void displayOrdertoExport() {
		try {
			handler.getReadableDatabase();
			ordList = handler.getDistinctOrderMprTrans();
			handler.closeDatabase();

			oList_Model = new ArrayList<Chk_Model>();

			for (int i = 0; i < ordList.size(); i++) {
				MPR_Trans obj = ordList.get(i);
				String ordno = obj.getPoNumber();
				String custno = obj.getVendorId();

				Chk_Model pp = new Chk_Model(ordno, custno, false);

				oList_Model.add(pp);

			}

			adapter = new Chk_Adapter_For_Export_mpr(this, oList_Model);
			lstExpo.setAdapter(adapter);
			handler.closeDatabase();
		} catch (Exception ex) {
			Log.e("Error", ex.getMessage());
		}

	}

	public void ExportData(String ordToExport) {
		putMprData = new JSONObject();
		exportData = new JSONObject();

		String poNumber = null, location = null, lineNumber = null, itemNo = null, Uom = null, comments = null;
		int qtyShipped = 0;

		valueHeader = new JSONArray();
		valueHeaderDetail = new JSONArray();

		try {
			handler.getReadableDatabase();
			Cursor trans_Cursor = handler.getMprTransData(ordToExport);
			handler.closeDatabase();
			JSONArray jarray1 = null;
			JSONArray jarray2 = null;

			if (trans_Cursor != null && trans_Cursor.moveToFirst() == true) {
				trans_Cursor.moveToFirst();
				while (!trans_Cursor.isAfterLast()) {

					poNumber = trans_Cursor.getString(0);// getOrdNumber();
					lineNumber = trans_Cursor.getString(10);// .getLineNumber();
					itemNo = trans_Cursor.getString(2);
					location = trans_Cursor.getString(8);
					Uom = trans_Cursor.getString(4);
					qtyShipped = trans_Cursor.getInt(6);
					comments = trans_Cursor.getString(7);// .getComments();

					// listview.setDivider(null);
					/*
					 * jarray1 = new JSONArray(); jarray1.put(0, poNumber);
					 * jarray1.put(1, "32"); jarray1.put(2, "A13200");
					 * jarray1.put(3, "2"); jarray1.put(4, "Ea.");
					 * jarray1.put(5, 5); jarray1.put(6, comments);
					 */

					jarray1 = new JSONArray();
					jarray1.put(0, poNumber);
					jarray1.put(1, lineNumber);
					jarray1.put(2, itemNo);
					jarray1.put(3, location);
					jarray1.put(4, Uom);
					jarray1.put(5, qtyShipped);
					jarray1.put(6, comments);

					valueHeader.put(jarray1);
					// ////////////////////////////////////////////////////////////////////////////////
					/*
					 * jarray2 = new JSONArray(); jarray2.put(0, ordNumber);
					 * jarray2.put(1, lineNumber); jarray2.put(2, itemNo);
					 * jarray2.put(3, "");
					 * 
					 * valueHeaderDetail.put(jarray2);
					 */
					// ////////////////////////////////////////////////////////////////////////////////
					trans_Cursor.moveToNext();

				}
				// Outside Since duplicate data will not be in valueHeaderDetail
				jarray2 = new JSONArray();
				jarray2.put(0, poNumber);
				valueHeaderDetail.put(jarray2);

			}
		} catch (Exception e) {
			Log.e("Failed", e.getLocalizedMessage());
		}

		try {
			putMprData.put("CompanyID", companyId);
			putMprData.put("ValueHeader", valueHeaderDetail);
			putMprData.put("ValueHeaderDetail", valueHeader);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("Failed", e.getLocalizedMessage());
		}

	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			MprOrderList.returnfrm = "true";

			Intent i = new Intent(MprExport.this, MprOrderList.class);
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
