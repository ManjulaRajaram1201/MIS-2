package com.mis.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobinventorysuit.R;

import com.mis.controller.InventoryCount;

import com.mis.controller.IntOrderList;
import com.mis.controller.MltMain;
import com.mis.controller.MprOrderList;
import com.mis.controller.MseOrderList;
import com.mis.database.DatabaseHandler;

public class Login extends AppBaseActivity{
	private boolean doubleBackToExitPressedOnce=false;	
	private Button mbtn_shipment;
	private Button mbtn_purchase;
	private Button mbtn_location;
	private Button mbtn_internal;
	
	DatabaseHandler dbhandler;
	private Button mbtn_inventory;
	private Button mbtn_settings;
	private Button mbtn_themes;
	private TextView mmis;
	private TextView mtxtview_mic;
	private TextView mtxtview_mse;
	
	private TextView mtxtview_mpr;
	private TextView mtxtview_mlt;
	private TextView mtxtview_internal;
	
	private TextView mtxtview_setting;
	
	private Typeface mt;
	int flag = 0;
	String result = "";
	 String devId;
	 View toastLayout;
		TextView toastText;
		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
		    if(doubleBackToExitPressedOnce)
		    {
		    	super.onBackPressed();
		    	closeAllActivities();
		    
		    }
		    this.doubleBackToExitPressedOnce=true;
		    Toast.makeText(Login.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
		    
		    new Handler().postDelayed(new Runnable()
		    {
		    	public void run()
		    	{
		    		doubleBackToExitPressedOnce=false;
		    	}
		    }, 5000);
		}
		@Override
		protected void onCreate(Bundle savedInstanceState)
		{

			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			/*setContentView(R.layout.login);*/

		
			LayoutInflater li = LayoutInflater.from(Login.this);
			
			View promptsView = li
					.inflate(R.layout.login, null);

			registerBaseActivityReceiver();
			// Toast
					LayoutInflater inflater = getLayoutInflater();
					toastLayout = inflater.inflate(R.layout.toast,
							(ViewGroup) findViewById(R.id.toast_layout_root));

					toastText = (TextView) toastLayout.findViewById(R.id.text);

			mbtn_shipment=(Button)promptsView.findViewById(R.id.btn_mis_mse);
			mbtn_settings=(Button)promptsView.findViewById(R.id.btn_mis_settings);
			mbtn_purchase=(Button)promptsView.findViewById(R.id.btn_mis_mpr);
			mbtn_location=(Button)promptsView.findViewById(R.id.btn_mis_mlt);
			mbtn_inventory=(Button)promptsView.findViewById(R.id.btn_mis_mic);
			/*mbtn_internal=(Button)promptsView.findViewById(R.id.btn_mis_internal);*/
			
			mtxtview_setting=(TextView)promptsView.findViewById(R.id.txtview_setting);
			mtxtview_mic=(TextView)promptsView.findViewById(R.id.txtview_mic);
			mtxtview_mse=(TextView)promptsView.findViewById(R.id.txtview_mse);
			mtxtview_mpr=(TextView)promptsView.findViewById(R.id.txtview_mpr);
			mtxtview_mlt=(TextView)promptsView.findViewById(R.id.txtview_mlt);
			/*mtxtview_internal=(TextView)promptsView.findViewById(R.id.txtview_internal);
			
			mbtn_internal.setVisibility(View.GONE);
			mtxtview_internal.setVisibility(View.GONE);*/
			
			/*mbtn_purchase.setVisibility(View.GONE);
			mtxtview_mpr.setVisibility(View.GONE);
			
			mbtn_location.setVisibility(View.GONE);
			mtxtview_mlt.setVisibility(View.GONE);
		
			mbtn_internal.setVisibility(View.GONE);
			mtxtview_internal.setVisibility(View.GONE);*/
			
			
			mmis=(TextView)promptsView.findViewById(R.id.textView2);
			setContentView(promptsView);
			
			/*mt=Typeface.createFromAsset(getAssets(), "Fabrica.otf");
			mmis.setTypeface(mt);*/
			
			dbhandler=new DatabaseHandler(this);
			
	
			
			
			mtxtview_setting.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub


					/*	Intent i2 = new Intent(MainActivity.this, Configuration.class);
						startActivity(i2);*/
						final WebView mwebview;
						 final EditText mipEditText;
						
						 final	 String URl = "http://";
						 final String F_Url = "/MisWCFService/Service.svc";
						 final ImageView mb1_tc;
						 final Button mb2_save;
					
					/*	private Button mbtn_settings;
						private Button mbtn_clear;*/
						final String Ipaddress = "";
						 TelephonyManager telephonyManager;
					
						
					
						
						LayoutInflater li = LayoutInflater.from(Login.this);
						View promptsView = li
								.inflate(R.layout.configuration, null);

						final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								Login.this);
						alertDialogBuilder.setView(promptsView);

						final AlertDialog alertDialog = alertDialogBuilder.create();
						
						registerBaseActivityReceiver();
						
						mwebview = (WebView) promptsView.findViewById(R.id.webView1);
						mipEditText = (EditText)promptsView.findViewById(R.id.edtIp);
						mb1_tc = (ImageView) promptsView.findViewById(R.id.imgtc);
						mb2_save = (Button) promptsView.findViewById(R.id.btnConfSave);
				/*		mbtn_settings = (Button) findViewById(R.id.button1);
						mbtn_clear = (Button) findViewById(R.id.button2);*/
						/*telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						devId = telephonyManager.getDeviceId();*/
					String android_id = Settings.Secure.getString(Login.this.getContentResolver(), Settings.Secure.ANDROID_ID);
					devId = android_id;
						
						if (devId == null) {
							devId = Build.SERIAL;
							if (devId == null) {
								devId = "1234";
							}
						}
						mwebview.setBackgroundColor(Color.parseColor("#a9a9a9"));
						

						MIS_Setting mis_setting = new MIS_Setting();
					
						dbhandler.getReadableDatabase();
						mis_setting = dbhandler.getSetting();
						dbhandler.closeDatabase();
						
						if (mis_setting != null) {
							String ipAddress = mis_setting.getIpAddress();
							mipEditText.setText(ipAddress);

						} 
						mb1_tc.setOnClickListener(new OnClickListener() {String Ipaddress = "";

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
								String deviceId = devId;

								SharedPreferences shared=getSharedPreferences("SettingDetails",MODE_PRIVATE);
								Editor ed =shared.edit();
								ed.putString("Dev",deviceId);
								ed.putString("Ip",ipAddress );
								ed.commit();
								System.out.println("DEV"+deviceId);
								MIS_Setting mis_setting = new MIS_Setting();
								mis_setting.setIpAddress(ipAddress);
								mis_setting.setDeviceId(deviceId);
								
								dbhandler.getReadableDatabase();
								boolean val=dbhandler.checkMISSetting();
								dbhandler.closeDatabase();
								if(val){
									dbhandler.getWritableDatabase();
									dbhandler.updateSettings(mis_setting);
									dbhandler.closeDatabase();	
								}
								else
								{
									dbhandler.getWritableDatabase();
									dbhandler.addSettings(mis_setting);
									dbhandler.closeDatabase();
								}
								
								
								Intent i = new Intent(Login.this, MainActivity.class);
								i.putExtra("flag",1);
								startActivity(i);
								
							}
						});
						

						alertDialog.show();
						
					
				}
			});
			mbtn_settings.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

					/*	Intent i2 = new Intent(MainActivity.this, Configuration.class);
						startActivity(i2);*/
						final WebView mwebview;
						 final EditText mipEditText;
						
						 final	 String URl = "http://";
						 final String F_Url = "/MisWCFService/Service.svc";
						 final ImageView mb1_tc;
						 final Button mb2_save;
					
					/*	private Button mbtn_settings;
						private Button mbtn_clear;*/
						final String Ipaddress = "";
						 TelephonyManager telephonyManager;
					
						
						
						
						LayoutInflater li = LayoutInflater.from(Login.this);
						View promptsView = li
								.inflate(R.layout.configuration, null);

						final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								Login.this);
						alertDialogBuilder.setView(promptsView);

						final AlertDialog alertDialog = alertDialogBuilder.create();
						
						registerBaseActivityReceiver();
						
						mwebview = (WebView) promptsView.findViewById(R.id.webView1);
						mipEditText = (EditText)promptsView.findViewById(R.id.edtIp);
						mb1_tc = (ImageView) promptsView.findViewById(R.id.imgtc);
						mb2_save = (Button) promptsView.findViewById(R.id.btnConfSave);
				/*		mbtn_settings = (Button) findViewById(R.id.button1);
						mbtn_clear = (Button) findViewById(R.id.button2);*/
						/*telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						devId = telephonyManager.getDeviceId();*/
					String android_id = Settings.Secure.getString(Login.this.getContentResolver(), Settings.Secure.ANDROID_ID);
					devId = android_id;
						
						if (devId == null) {
							devId = Build.SERIAL;
							if (devId == null) {
								devId = "1234";
							}
						}
						mwebview.setBackgroundColor(Color.parseColor("#a9a9a9"));
						
						
						MIS_Setting mis_setting = new MIS_Setting();
					
						dbhandler.getReadableDatabase();
						mis_setting = dbhandler.getSetting();
						dbhandler.closeDatabase();
						
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
								String deviceId = devId;

								SharedPreferences shared=getSharedPreferences("SettingDetails",MODE_PRIVATE);
								Editor ed =shared.edit();
								ed.putString("Dev",deviceId);
								ed.putString("Ip",ipAddress );
								ed.commit();
								System.out.println("DEV"+deviceId);
								MIS_Setting mis_setting = new MIS_Setting();
								mis_setting.setIpAddress(ipAddress);
								mis_setting.setDeviceId(deviceId);
								
								dbhandler.getReadableDatabase();
								boolean val=dbhandler.checkMISSetting();
								dbhandler.closeDatabase();
								if(val){
									dbhandler.getWritableDatabase();
									dbhandler.updateSettings(mis_setting);
									dbhandler.closeDatabase();	
								}
								else
								{
									dbhandler.getWritableDatabase();
									dbhandler.addSettings(mis_setting);
									dbhandler.closeDatabase();
								}
								
								
								Intent i = new Intent(Login.this, MainActivity.class);
								i.putExtra("flag",1);
								startActivity(i);
								
							}
						});
						

						alertDialog.show();
						
					}
			});
			mtxtview_mse.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Editor editor = getSharedPreferences("Selected_Order", Context.MODE_PRIVATE).edit();
					editor.clear();
					editor.commit();
					
					Intent i=new Intent(Login.this,MseOrderList.class);
					i.putExtra("Came", 1);
					startActivity(i);
				}
			});
			mbtn_shipment.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					Editor editor = getSharedPreferences("Selected_Order", Context.MODE_PRIVATE).edit();
					editor.clear();
					editor.commit();
					
					Intent i=new Intent(Login.this,MseOrderList.class);
					i.putExtra("Came", 1);
					startActivity(i);
				}
			});
			mtxtview_mpr.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					/*Editor editor = getSharedPreferences("Selected_Order", Context.MODE_PRIVATE).edit();
					editor.clear();
					editor.commit();*/
					
					Intent i=new Intent(Login.this,MprOrderList.class);
					i.putExtra("Came", 1);
					startActivity(i);
				}
			});
			mbtn_purchase.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					/*Editor editor = getSharedPreferences("Selected_Order", Context.MODE_PRIVATE).edit();
					editor.clear();
					editor.commit();*/
					
					Intent i=new Intent(Login.this,MprOrderList.class);
					i.putExtra("Came", 1);
					startActivity(i);
				}
			});
mtxtview_mic.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					   Intent i=new Intent(Login.this,InventoryCount.class);
					   startActivity(i);
					   
					}
			});
			mbtn_inventory.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
				   Intent i=new Intent(Login.this,InventoryCount.class);
				   startActivity(i);
				   
				}
			});
mtxtview_mlt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent i= new Intent(getApplicationContext(),MltMain.class);
				
					i.putExtra("flag", 1);
					startActivity(i);	
				   
				}
			});
			mbtn_location.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					Intent i= new Intent(getApplicationContext(),MltMain.class);
				
					i.putExtra("Came", 1);
					startActivity(i);	
				   
				}
			});

			/*mbtn_internal.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i=new Intent(Login.this,IntOrderList.class);
					i.putExtra("Came", 1);
					startActivity(i);
				}
			});
			mtxtview_internal.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i=new Intent(Login.this,IntOrderList.class);
					i.putExtra("Came", 1);
					startActivity(i);
				}
			});*/
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