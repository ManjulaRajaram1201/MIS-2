package com.mis.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobinventorysuit.R;
import com.mis.database.DatabaseHandler;

public class MainActivity extends AppBaseActivity {
	private AutoCompleteTextView mtxt_UserID;
	private EditText mtxt_Password;

	TelephonyManager tm;
	JSONArray jarray = null;
	private boolean doubleBackToExitPressedOnce = false;
	public String sUserID;
	public String sPassword;
	private Button mbtn_login;
	private Button mbtn_settings;
	private Button mbtn_exit;

	int flag = 0;
	private ImageView mimageview;
	private TextView mtxt_mis;
	private LayoutInflater mlayoutinflater;
	private View mview;
	private Typeface mtypeface;
	private boolean mres = false;
	private File file;
	View toastLayout;
	TextView toastText;
	String devId="0000";

	private DatabaseHandler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Toast
		LayoutInflater inflater = getLayoutInflater();
		toastLayout = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		toastText = (TextView) toastLayout.findViewById(R.id.text);

		registerBaseActivityReceiver();
		mtxt_UserID = (AutoCompleteTextView) findViewById(R.id.autotxtViewSPCode);
		mtxt_Password = (EditText) findViewById(R.id.txtViewpassword);

		mbtn_login = (Button) findViewById(R.id.btnLogin);
		mbtn_settings = (Button) findViewById(R.id.btnSetting);
		mbtn_exit = (Button) findViewById(R.id.btnExit);

		LogfileCreator logfile = new LogfileCreator();

		if (LogfileCreator.isSdPresent()) {

			long size = checkMemorySpace();

			if (size < 50) {
				callExpiredAlert("Your Device have low memory space. Please delete unwanted files.");
			}
			logfile.createCommonPath();
			handler = new DatabaseHandler(this);

			flag = getIntent().getIntExtra("flag", 0);
			handler.getReadableDatabase();
			boolean check = handler.checkMISSetting();
			handler.closeDatabase();
			System.out.println("check" + check);
			if (flag == 0 && check == false) {
				Editor sel = getSharedPreferences("SettingDetails",
						Context.MODE_PRIVATE).edit();
				sel.clear();
				sel.commit();
			}
			mtxt_UserID.requestFocus();

			tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		} else {
			exitAlert("SD card is required for this application");
		}

		/*
		 * mlayoutinflater = getLayoutInflater(); mview =
		 * mlayoutinflater.inflate(R.layout.toast, (ViewGroup)
		 * findViewById(R.id.toast_root)); mtypeface =
		 * Typeface.createFromAsset(getAssets(), "Fabrica.otf");
		 * mtxt_mis.setTypeface(mtypeface);
		 */

		/*
		 * mimageview.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub Intent i = new Intent(MainActivity.this, About.class);
		 * startActivity(i); } });
		 */

		mbtn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sUserID = mtxt_UserID.getText().toString();
				sPassword = mtxt_Password.getText().toString();
				SharedPreferences shared = getSharedPreferences(
						"SettingDetails", MODE_PRIVATE);
				String dev = shared.getString("Dev", null);
				String ip = shared.getString("Ip", null);
				if (dev == null && ip == null) {
					handler.getReadableDatabase();
					MIS_Setting mis_Setting = handler.getSetting();
					handler.closeDatabase();
					if (mis_Setting != null) {
						dev = mis_Setting.getDeviceId();
						ip = mis_Setting.getIpAddress();
					}
				}
				handler.getReadableDatabase();
				if (sUserID.matches("")) {
					mtxt_UserID.setError("Please Enter Username");
				} else if (sPassword.matches("")) {
					mtxt_Password.setError("Please Enter Password");
				} else if (dev == null || ip == null) {
					/*
					 * Toast.makeText(MainActivity.this,
					 * "Enter Server Path in Setting", Toast.LENGTH_LONG)
					 * .show();
					 */
					toastText.setText("Enter Server Path in Setting");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				} else if (handler.checkGetData2ContainsData()) {
					mres = handler.GetData2(sUserID, sPassword);
					handler.closeDatabase();
					if (mres == true) {

						// For MSE
						/*
						 * Intent i=new
						 * Intent(MainActivity.this,MseOrderList.class);
						 * i.putExtra("Came", 1); startActivity(i);
						 */

						Intent i = new Intent(MainActivity.this, Login.class);
						i.putExtra("Came", 1);
						startActivity(i);

						mtxt_UserID.setText("");
						mtxt_Password.setText("");
					} else {
						/*
						 * Toast.makeText(MainActivity.this,
						 * "Invalid Login Details", Toast.LENGTH_LONG) .show();
						 */
						toastText.setText("Invalid Login Details");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.setView(toastLayout);
						toast.show();
					}
				} else {
					toastText.setText("Please import the Company Details");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				}

			}
		});

		mbtn_settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				/*
				 * Intent i2 = new Intent(MainActivity.this,
				 * Configuration.class); startActivity(i2);
				 */
				final WebView mwebview;
				final EditText mipEditText;
				final String URl = "http://";
				final String F_Url = "/MisWCFService/Service.svc";
				final ImageView mb1_tc;
				final Button mb2_save;
				/*
				 * private Button mbtn_settings; private Button mbtn_clear;
				 */
				final String Ipaddress = "";
				TelephonyManager telephonyManager;

				DatabaseHandler dbhandler;
				/*final IPAddressValidator iav;*/

				LayoutInflater li = LayoutInflater.from(MainActivity.this);
				View promptsView = li.inflate(R.layout.configuration, null);

				final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MainActivity.this);
				alertDialogBuilder.setView(promptsView);

				final AlertDialog alertDialog = alertDialogBuilder.create();

				registerBaseActivityReceiver();

				mwebview = (WebView) promptsView.findViewById(R.id.webView1);
				mipEditText = (EditText) promptsView.findViewById(R.id.edtIp);
				mb1_tc = (ImageView) promptsView.findViewById(R.id.imgtc);
				mb2_save = (Button) promptsView.findViewById(R.id.btnConfSave);
				/*
				 * mbtn_settings = (Button) findViewById(R.id.button1);
				 * mbtn_clear = (Button) findViewById(R.id.button2);
				 */
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				devId = telephonyManager.getDeviceId();

				if (devId == null) {
					devId = Build.SERIAL;
					if (devId == null) {
						devId = "0000";
					}
				}

				/*iav = new IPAddressValidator();*/

				MIS_Setting mis_setting = new MIS_Setting();

				handler.getReadableDatabase();
				mis_setting = handler.getSetting();
				handler.closeDatabase();

				if (mis_setting != null) {
					String ipAddress = mis_setting.getIpAddress();
					mipEditText.setText(ipAddress);

				}
				mb1_tc.setOnClickListener(new OnClickListener() {
					
					String Ipaddress = "";

					@Override
					public void onClick(View v) {

						Ipaddress = mipEditText.getText().toString();
						String strIp = checkIp(Ipaddress);
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), 0);

						if (strIp.equals("Success")) {
							String path = mipEditText.getText().toString();
							mwebview.getSettings().setJavaScriptEnabled(true);
							mwebview.loadUrl(URl + Ipaddress + F_Url);
						} else {
							toastText.setText("Enter Valid IP Address...");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.setView(toastLayout);
							toast.show();
						}
					}
					});

				mb2_save.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						String ipAddress = mipEditText.getText().toString();
						String deviceId = "0000";

						SharedPreferences shared = getSharedPreferences(
								"SettingDetails", MODE_PRIVATE);
						Editor ed = shared.edit();
						ed.putString("Dev", deviceId);
						ed.putString("Ip", ipAddress);
						ed.commit();
						System.out.println("DEV" + deviceId);
						MIS_Setting mis_setting = new MIS_Setting();
						mis_setting.setIpAddress(ipAddress);
						mis_setting.setDeviceId(deviceId);

						handler.getReadableDatabase();
						boolean val = handler.checkMISSetting();
						handler.closeDatabase();
						if (val) {
							handler.getWritableDatabase();
							handler.updateSettings(mis_setting);
							handler.closeDatabase();
						} else {
							handler.getWritableDatabase();
							handler.addSettings(mis_setting);
							handler.closeDatabase();
						}

						Intent i = new Intent(MainActivity.this,
								MainActivity.class);
						i.putExtra("flag", 1);
						startActivity(i);

					}
				});

				alertDialog.show();

			}

		});

		mbtn_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog.Builder ad = new AlertDialog.Builder(
						MainActivity.this);
				ad.setTitle("Confirmation");
				ad.setIcon(R.drawable.warning);
				ad.setMessage("Are you sure you want to exit the application?");
				ad.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								closeAllActivities();
							}
						});
				ad.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						});
				ad.show();
			}
		});

	}

	private void exitAlert(String msg) {
		AlertDialog.Builder alertMemory = new AlertDialog.Builder(this);
		alertMemory.setTitle("Warning");
		alertMemory.setIcon(R.drawable.warning);
		alertMemory.setCancelable(false);
		alertMemory.setMessage(msg);
		alertMemory.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						closeAllActivities();
					}
				});
		alertMemory.show();
	}

	private void callExpiredAlert(String message) {
		AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
		alertUser.setTitle("Warning");
		alertUser.setIcon(R.drawable.warning);
		alertUser.setCancelable(false);
		alertUser.setMessage(message);
		alertUser.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertUser.show();
	}

	private long checkMemorySpace() {
		final long SIZE_KB = 1024L;
		final long SIZE_MB = SIZE_KB * SIZE_KB;
		long availableSpace = -1L;
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		availableSpace = (long) stat.getAvailableBlocks()
				* (long) stat.getBlockSize();
		long size = availableSpace / SIZE_MB;
		Log.i("Available Space in sdcard:", "Size in MB: " + size);

		return size;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.login_mnu_imports) {
			sUserID = mtxt_UserID.getText().toString();
			sPassword = mtxt_Password.getText().toString();
			SharedPreferences shared = getSharedPreferences("SettingDetails",
					MODE_PRIVATE);
			String dev = shared.getString("Dev", null);
			String ip = shared.getString("Ip", null);
			if (dev == null && ip == null) {
				handler.getReadableDatabase();
				MIS_Setting mis_Setting = handler.getSetting();
				handler.closeDatabase();
				if (mis_Setting != null) {
					dev = mis_Setting.getDeviceId();
					ip = mis_Setting.getIpAddress();
				}
			}
			handler.getReadableDatabase();
			if (sUserID.matches("")) {
				mtxt_UserID.setError("Please Enter Username");
			} else if (sPassword.matches("")) {
				mtxt_Password.setError("Please Enter Password");
			} else if (dev == null || ip == null) {
				/*
				 * Toast.makeText(MainActivity.this,
				 * "Enter Server Path in Setting", Toast.LENGTH_LONG) .show();
				 */
				toastText.setText("Enter Server Path in Setting");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(toastLayout);
				toast.show();
			} else if (handler.checkGetData2ContainsData()) {
				mres = handler.GetData2(sUserID, sPassword);
				handler.closeDatabase();
				if (mres == true) {

					// For MSE
					/*
					 * Intent i=new
					 * Intent(MainActivity.this,MseOrderList.class);
					 * i.putExtra("Came", 1); startActivity(i);
					 */
					// hint
					/*
					 * Intent i = new Intent(MainActivity.this, Login.class);
					 * i.putExtra("Came", 1); startActivity(i);
					 */
					toastText.setText("Company Details Already Exist.");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();

					mtxt_UserID.setText("");
					mtxt_Password.setText("");
				} else {
					/*
					 * Toast.makeText(MainActivity.this,
					 * "Invalid Login Details", Toast.LENGTH_LONG) .show();
					 */
					toastText.setText("Invalid Login Details");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
				}
			} else {
				new ImportData().execute();
			}
		} else if (id == R.id.login_mnu_settings) {

			/*
			 * Intent i2 = new Intent(MainActivity.this, Configuration.class);
			 * startActivity(i2);
			 */
			final WebView mwebview;
			final EditText mipEditText;
			final String URl = "http://";
			final String F_Url = "/MisWCFService/Service.svc";
			final ImageView mb1_tc;
			final Button mb2_save;
			/*
			 * private Button mbtn_settings; private Button mbtn_clear;
			 */
			final String Ipaddress = "";
			TelephonyManager telephonyManager;

			DatabaseHandler dbhandler;
			/*final IPAddressValidator iav;*/

			LayoutInflater li = LayoutInflater.from(MainActivity.this);
			View promptsView = li.inflate(R.layout.configuration, null);

			final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					MainActivity.this);
			alertDialogBuilder.setView(promptsView);

			final AlertDialog alertDialog = alertDialogBuilder.create();

			registerBaseActivityReceiver();

			mwebview = (WebView) promptsView.findViewById(R.id.webView1);
			mipEditText = (EditText) promptsView.findViewById(R.id.edtIp);
			mb1_tc = (ImageView) promptsView.findViewById(R.id.imgtc);
			mb2_save = (Button) promptsView.findViewById(R.id.btnConfSave);
			/*
			 * mbtn_settings = (Button) findViewById(R.id.button1); mbtn_clear =
			 * (Button) findViewById(R.id.button2);
			 */
			mwebview.setBackgroundColor(Color.parseColor("#a9a9a9"));
			/*telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			devId = telephonyManager.getDeviceId();*/
			String android_id = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
			devId = android_id;

			if (devId == null) {
				devId = Build.SERIAL;
				if (devId == null) {
					devId = "0000";
				}
			}

			/*iav = new IPAddressValidator();*/

			MIS_Setting mis_setting = new MIS_Setting();

			handler.getReadableDatabase();
			mis_setting = handler.getSetting();
			handler.closeDatabase();

			if (mis_setting != null) {
				String ipAddress = mis_setting.getIpAddress();
				mipEditText.setText(ipAddress);

			}
			mb1_tc.setOnClickListener(new OnClickListener() {
				
				String Ipaddress = "";

				@Override
				public void onClick(View v) {

					Ipaddress = mipEditText.getText().toString();
					String strIp = checkIp(Ipaddress);
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(), 0);

					if (strIp.equals("Success")) {
						String path = mipEditText.getText().toString();
						mwebview.getSettings().setJavaScriptEnabled(true);
						mwebview.loadUrl(URl + Ipaddress + F_Url);
					} else {
						toastText.setText("Enter Valid IP Address...");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.setView(toastLayout);
						toast.show();
					}
				}
				});

			mb2_save.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					String ipAddress = mipEditText.getText().toString();
					String deviceId = "0000";

					SharedPreferences shared = getSharedPreferences(
							"SettingDetails", MODE_PRIVATE);
					Editor ed = shared.edit();
					ed.putString("Dev", deviceId);
					ed.putString("Ip", ipAddress);
					ed.commit();
					System.out.println("DEV" + deviceId);
					MIS_Setting mis_setting = new MIS_Setting();
					mis_setting.setIpAddress(ipAddress);
					mis_setting.setDeviceId(deviceId);

					handler.getReadableDatabase();
					boolean val = handler.checkMISSetting();
					handler.closeDatabase();
					if (val) {
						handler.getWritableDatabase();
						handler.updateSettings(mis_setting);
						handler.closeDatabase();
					} else {
						handler.getWritableDatabase();
						handler.addSettings(mis_setting);
						handler.closeDatabase();
					}

					Intent i = new Intent(MainActivity.this, MainActivity.class);
					i.putExtra("flag", 1);
					startActivity(i);

				}
			});

			alertDialog.show();

		}/* else if (id == R.id.login_mnu_dev) {
			// hint
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			devId = telephonyManager.getDeviceId();
			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					MainActivity.this);
			alertDialog.setTitle("Info");
			alertDialog.setIcon(R.drawable.rsz_ok1);
			alertDialog.setMessage("Device Id : " + devId);
			alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});
			alertDialog.show();
		} */else {
			// TODO Auto-generated method stub
			AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
			ad.setTitle("Confirmation");
			ad.setIcon(R.drawable.warning);
			ad.setMessage("Are you sure you want to exit the application?");
			ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					closeAllActivities();
				}
			});
			ad.setNegativeButton("No", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			});
			ad.show();
		}
		return super.onOptionsItemSelected(item);
	}

	public String CompanyDetails() {
		// subscriberID=tm.getDeviceId();
		String result = "";
		String status = "";
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		MIS_Setting mis_setting = new MIS_Setting();
		handler.getReadableDatabase();
		mis_setting = handler.getSetting();
		handler.closeDatabase();

		String deviceId = mis_setting.getDeviceId();
		String ipAddress = mis_setting.getIpAddress();

		String URL = "http://" + ipAddress + "/MISWCFService/Service.svc";
		String fulURL = URL + "/GetLoginDetails/"
				+ mtxt_UserID.getText().toString() + "/"
				+ mtxt_Password.getText().toString() + "/0000" ;
		JSONObject jobject = doResponse(fulURL);

		if (jobject != null) {
			try {
				jarray = jobject.getJSONArray("GetCompanyInfo");
				status = jarray.getString(0);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (status.equals("internetprob") || status.equals("encoError")
					|| status.equals("clientsideError")
					|| status.equals("socTimeError")
					|| status.equals("connTimeError")
					|| status.equals("hostconnError")
					|| status.equals("parseExcep")
					|| status.equals("jsonError") || status.equals("io"))

			{

				if (status.equals("socTimeError".toString())
						|| status.equals("connTimeError".toString())) {
					result = "time out";

				} else if (status.equals("clientsideError".toString())
						|| status.equals("hostconnError".toString())) {

					result = "Connection prob";

				} else if (status.equals("parseExcep".toString())
						|| status.equals("io".toString())
						|| status.equals("jsonError".toString())
						|| status.equals("encoError".toString())) {
					result = "Data prob";

				} else if (status.equals("internetprob".toString())) {
					result = "Internet Prob";

				} else {
					result = "error";

				}

			} else {

				handler.getWritableDatabase();
				try {
					result = handler.addMis_company(new MIS_Company(jarray
							.getString(0), jarray.getString(1), jarray
							.getString(2), jarray.getString(3), jarray
							.getString(4), jarray.getString(5), jarray
							.getString(6), jarray.getString(7), jarray
							.getString(8)));

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					result = "error";
					e.printStackTrace();
				}
				handler.closeDatabase();

			}

		}

		return result;

	}

	private JSONObject doResponse(String URL) {

		System.out.println("URL_" + URL);
		JSONObject outerObject = new JSONObject();
		JSONArray outerArray = new JSONArray();
		JSONObject innerObject = new JSONObject();
		String result = "";
		JSONObject jobject = null;

		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;

		if (isConnected(this)) {

			HttpGet httpget = new HttpGet(URL);
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
					jobject = jobject.getJSONObject("GetLoginDetailsResult");

				}

			}

			catch (UnsupportedEncodingException e) {
				result = "encoError";

				try {
					outerArray.put(result);
					outerObject.put("GetCompanyInfo", outerArray);
					jobject = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} catch (ClientProtocolException e) {
				result = "clientsideError";
				try {
					outerArray.put(result);
					outerObject.put("GetCompanyInfo", outerArray);
					jobject = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (SocketTimeoutException e) {
				result = "socTimeError";
				try {
					outerArray.put(result);
					outerObject.put("GetCompanyInfo", outerArray);
					jobject = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ConnectTimeoutException e) {
				result = "connTimeError";
				try {
					outerArray.put(result);
					outerObject.put("GetCompanyInfo", outerArray);
					jobject = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (JSONException ex) {
				result = "jsonError";
				try {
					outerArray.put(result);
					outerObject.put("GetCompanyInfo", outerArray);
					jobject = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (HttpHostConnectException e) {
				result = "hostconnError";
				try {
					outerArray.put(result);
					outerObject.put("GetCompanyInfo", outerArray);
					jobject = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ParseException pe) {
				result = "parseExcep";
				try {
					outerArray.put(result);
					outerObject.put("GetCompanyInfo", outerArray);
					jobject = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException io) {
				result = "io";
				try {
					outerArray.put(result);
					outerObject.put("GetCompanyInfo", outerArray);
					jobject = outerObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else {
			result = "internetprob";
			try {
				outerArray.put(result);
				outerObject.put("GetCompanyInfo", outerArray);
				jobject = outerObject;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return jobject;
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

	public String UserDetails() {
		// subscriberID=tm.getDeviceId();
		String result = "";
		String status = "";
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		MIS_Setting mis_setting = new MIS_Setting();

		handler.getReadableDatabase();
		mis_setting = handler.getSetting();
		handler.closeDatabase();

		String deviceId = mis_setting.getDeviceId();
		String ipAddress = mis_setting.getIpAddress();

		String URL = "http://" + ipAddress + "/MISWCFService/Service.svc";
		String fulURL = URL + "/GetLoginDetails/"
				+ mtxt_UserID.getText().toString() + "/"
				+ mtxt_Password.getText().toString() + "/0000" ;
		JSONObject jobject = doResponse(fulURL);
		if (jobject != null) {
			try {
				jarray = jobject.getJSONArray("GetUserInfo");
				status = jarray.getString(0);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (status.equals("internetprob") || status.equals("encoError")
					|| status.equals("clientsideError")
					|| status.equals("socTimeError")
					|| status.equals("connTimeError")
					|| status.equals("hostconnError")
					|| status.equals("parseExcep")
					|| status.equals("jsonError") || status.equals("io"))

			{

				if (status.equals("socTimeError".toString())
						|| status.equals("connTimeError".toString())) {
					result = "time out";

				} else if (status.equals("clientsideError".toString())
						|| status.equals("hostconnError".toString())) {

					result = "Connection prob";

				} else if (status.equals("parseExcep".toString())
						|| status.equals("io".toString())
						|| status.equals("jsonError".toString())
						|| status.equals("encoError".toString())) {
					result = "Data prob";

				} else if (status.equals("internetprob".toString())) {
					result = "Internet Prob";

				} else {
					result = "error";

				}

			} else {

				handler.getWritableDatabase();
				try {
					result = handler
							.addMis_User(new MIS_User(mtxt_UserID.getText()
									.toString(), mtxt_Password.getText()
									.toString(), deviceId, jarray.getString(3),
									Integer.parseInt(jarray.getString(4)),
									jarray.getString(5)));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.closeDatabase();

			}

		}

		return result;

	}

	class ImportData extends AsyncTask<String, String, String> {

		ProgressDialog pd;

		public ImportData() {

			pd = new ProgressDialog(MainActivity.this);
			pd.setCancelable(false);

		}

		protected void onPreExecute() {
			super.onPreExecute();
			pd.setMessage("Importing Company Details..");
			pd.show();
		}

		protected String doInBackground(String... results) {

			String result = "";

			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						0);
				System.out.println("Compnay");
				result = CompanyDetails();
				System.out.println("user");
				if (result.equals("success"))
					result = UserDetails();
				

			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				result = "error";
			}
			return result;
		}

		

		protected void onProgressUpdate(String... progress) {
			super.onProgressUpdate(progress);
		}

		protected void onPostExecute(String result) {
			if (result.equals("success")) {
				pd.dismiss();/*
							 * Toast toast = new Toast(getApplicationContext());
							 * toast.setDuration(Toast.LENGTH_LONG);
							 * toast.setView(mview); toast.show();
							 */

				// For MSE
				/*
				 * Intent i=new Intent(MainActivity.this,MseOrderList.class);
				 * i.putExtra("Came", 1); startActivity(i);
				 */

				/*
				 * Intent i = new Intent(MainActivity.this, Login.class);
				 * i.putExtra("Came", 1); startActivity(i);
				 */
				toastText.setText("Company Details Loaded Successfully");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(toastLayout);
				toast.show();
				mtxt_UserID.setText("");
				mtxt_Password.setText("");

				/* MainActivity.this.finish(); */

			}

			else {

				if (result.equals("time out".toString())) {
					/*
					 * Toast.makeText(MseImportData.this, "Time Out Try Again",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Time Out Try Again");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
					pd.dismiss();
				} else if (result.equals("Connection prob".toString())) {
					/*
					 * Toast.makeText( MseImportData.this,
					 * "Problem while establishing connection with Server",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText
							.setText("Problem While Establishing Connection with Server");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
					pd.dismiss();
				} else if (result.equals("Internet Prob".toString())) {
					/*
					 * Toast.makeText( MseImportData.this,
					 * "Problem while establishing connection with Server",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Check Your Internet Connectivity");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
					pd.dismiss();
				} else if (result.equals("Data prob".toString())) {
					/*
					 * Toast.makeText(MseImportData.this,
					 * "Improper Format of Data", Toast.LENGTH_LONG) .show();
					 */
					toastText
							.setText("Problem with SalesPerson Device Id in Server, Improper Format of Data");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
					toast.setDuration(Toast.LENGTH_LONG);
					toast.setView(toastLayout);
					toast.show();
					pd.dismiss();
				} else {
					/*
					 * Toast.makeText(MseImportData.this,
					 * "Order Not Imported Successfully",
					 * Toast.LENGTH_LONG).show();
					 */
					toastText.setText("Problem in Importing Login Details");
					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(toastLayout);
					toast.show();
					pd.dismiss();
				}

			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			closeAllActivities();

		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(MainActivity.this, "Please click BACK again to exit",
				Toast.LENGTH_SHORT).show();

		/*
		 * toastText.setText("Please Click BACK Again to Exit"); Toast toast =
		 * new Toast(getApplicationContext());
		 * toast.setGravity(Gravity.CENTER_VERTICAL, 0, 310);
		 * toast.setDuration(Toast.LENGTH_SHORT); toast.setView(toastLayout);
		 * toast.show();
		 */new Handler().postDelayed(new Runnable() {
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 5000);
	}

	protected String checkIp(String path) {
		String result = "";
		// String path = edtsetServerPath.getText().toString();
		final String PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(path);
		boolean IPcheck = matcher.matches();

		final String PATTERN2 = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\:" + "([0-9]+)";

		Pattern pattern2 = Pattern.compile(PATTERN2);
		Matcher matcher2 = pattern2.matcher(path);
		boolean IPcheck2 = matcher2.matches();

		if (IPcheck) {
			result = "Success";
		} else if (IPcheck2) {
			result = "Success";
		} else {
			result = "Fail";
		}

		return result;

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();

		unRegisterBaseActivityReceiver();
	}

	/*class IPAddressValidator {

		Pattern pattern;
		Matcher matcher;

		String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

		public IPAddressValidator() {
			pattern = Pattern.compile(IPADDRESS_PATTERN);
		}

		public boolean validate(final String ip) {
			matcher = pattern.matcher(ip);
			return matcher.matches();

		}
	}*/
}
