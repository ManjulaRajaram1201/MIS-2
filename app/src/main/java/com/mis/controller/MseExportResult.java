package com.mis.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.example.mobinventorysuit.R;
import com.mis.adapter.ThreeTextViewAdapter;
import com.mis.common.AppBaseActivity;
import com.mis.database.DatabaseHandler;
import com.mis.mse.model.MSE_Trans;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MseExportResult extends AppBaseActivity {

	ListView lststatus;
	Button btn_ok;
	HashMap<String, String> ordShipNo;
	ArrayList<String> ordExported;
	
	ThreeTextViewAdapter adapter;
	
	List<String> rec_selected_Po;
	public static Boolean notyetExpo = true;

	DatabaseHandler dbhelper;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mseexportresult);
		registerBaseActivityReceiver();
		lststatus = (ListView) findViewById(R.id.lstSelOrder);
		btn_ok = (Button) findViewById(R.id.btn_mseOkResult);

		Intent intent = getIntent();
		// contains both ord and shipNo
		ordShipNo = (HashMap<String, String>) intent
				.getSerializableExtra("OrdShipNoKey");

		ordExported = new ArrayList<String>();
		dbhelper = new DatabaseHandler(this);
		
		// getting all orde that exported
		Set<String> keySet = ordShipNo.keySet();
		Iterator it = keySet.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			ordExported.add(ordShipNo.get(key));

		}
		
		adapter = new ThreeTextViewAdapter(this, ordExported);
		lststatus.setAdapter(adapter);
		

		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				notyetExpo = false;
				Intent i = new Intent(MseExportResult.this, MseExport.class);
				startActivity(i);
			
			
			}
		});

	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			notyetExpo = false;
			Intent i = new Intent(MseExportResult.this, MseExport.class);
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
