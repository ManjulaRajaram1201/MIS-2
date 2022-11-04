package com.mis.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.example.mobinventorysuit.R;
import com.mis.adapter.ThreeTextViewAdapter;
import com.mis.adapter.ThreeTextViewAdapter_mlt;

import com.mis.common.AppBaseActivity;
import com.mis.common.LogfileCreator;
import com.mis.database.DatabaseHandler;
import com.mis.mse.model.MSE_Trans;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MltExportResult extends AppBaseActivity {

	ListView lststatus;
	Button btn_ok;
	HashMap<String, String> TrfNo;
	ArrayList<String> docExported;
	ArrayList<String> trfNumber;
	ThreeTextViewAdapter_mlt adapter;
	DatabaseHandler db;

	DatabaseHandler dbhelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mltexportresult);
		registerBaseActivityReceiver();
		lststatus = (ListView) findViewById(R.id.lstSelOrder);
		btn_ok = (Button) findViewById(R.id.btn_mltOkResult);
		db=new DatabaseHandler(this);
		Intent intent = getIntent();
		// contains both ord and shipNo
		TrfNo = (HashMap<String, String>) intent
				.getSerializableExtra("OrdTrfNoKey");
		
		docExported = new ArrayList<String>();
		
		dbhelper = new DatabaseHandler(this);
		
		
		Set<String> keySet = TrfNo.keySet();
		Iterator it = keySet.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			docExported.add(TrfNo.get(key));

		}

		
		adapter = new ThreeTextViewAdapter_mlt(this, docExported);
		lststatus.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				db.getWritableDatabase();
				db.deleteExportedDetails(docExported);
				db.closeDatabase();
				
				Intent i = new Intent(MltExportResult.this, MltExport.class);
				startActivity(i);
			}
		});
	}
	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// TODO Auto-generated method stub

			db.getWritableDatabase();
			db.deleteExportedDetails(docExported);
			db.closeDatabase();
			
			Intent i = new Intent(MltExportResult.this, MltExport.class);
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
