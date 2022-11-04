package com.mis.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.example.mobinventorysuit.R;
import com.mis.common.AppBaseActivity;
import com.mis.database.DatabaseHandler;
import com.mis.controller.*;
import com.mis.controller.InventoryCount.InflateList;



import com.mis.mlt.model.MLT_Details;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MltUpdate extends AppBaseActivity {
	String froml;
	private Button Ok,Cancel;
	private EditText Qty;
	private Spinner From,To,UOM;
	String[] uomm;
	String[] ConvUOM;
	View toastLayout;
	String[]ConvFact;
	String StockUnit;
	TextView toastText;
	
	Boolean tblMLT;
	int qtyCounted;
	public static final String DATABASE_NAME = "mis";
	public static final String TABLE_MLT5="MLT_LOCATION";
	SQLiteDatabase db;
	public String ItemNo;
	public String Description;
	public String Desc;
	public String Loc;
	DatabaseHandler handler;
	int ID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mltupdate);
		
		registerBaseActivityReceiver();
		
		handler = new DatabaseHandler(this);
		// Toast
		LayoutInflater inflater = getLayoutInflater();
		toastLayout = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		toastText = (TextView) toastLayout.findViewById(R.id.text);
		Ok=(Button)findViewById(R.id.btn_mltshipOk);
		Cancel=(Button)findViewById(R.id.btn_mltshipCancel);
		From=(Spinner)findViewById(R.id.spn_frmLoc);
		To=(Spinner)findViewById(R.id.spn_ToLoc);
		Qty=(EditText)findViewById(R.id.edtmltCost);
		UOM=(Spinner)findViewById(R.id.spn_Uom);
		ItemNo = getIntent().getStringExtra("ItemNumber");
		Desc = getIntent().getStringExtra("Description");
		
		getDataFromDB();
		getDataToDB();
		getUomdata();
		From.setOnItemSelectedListener(new OnItemSelectedListener() {

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
		To.setOnItemSelectedListener(new OnItemSelectedListener() {

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
		UOM.setOnItemSelectedListener(new OnItemSelectedListener() {

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
				
					if(From.getItemAtPosition(From.getSelectedItemPosition()).
						toString().equals("1") && To.getItemAtPosition
						(To.getSelectedItemPosition()).toString().equals("1"))
					{
					Toast.makeText(MltUpdate.this,"Select different location ",
							Toast.LENGTH_SHORT).show();
					
				}else if(From.getItemAtPosition(From.getSelectedItemPosition()).toString().
						equals("2")
						&& To.getItemAtPosition
								(To.getSelectedItemPosition()).toString().equals("2"))
				{
					Toast.makeText(MltUpdate.this,"Select different location ",
							Toast.LENGTH_SHORT).show();
				}
				else if(From.getItemAtPosition(From.getSelectedItemPosition()).toString().
						equals("3")
						&& To.getItemAtPosition
								(To.getSelectedItemPosition()).toString().equals("3"))
				{
					Toast.makeText(MltUpdate.this,"Select different location ",
							Toast.LENGTH_SHORT).show();
				}
				else if(From.getItemAtPosition(From.getSelectedItemPosition()).toString().
						equals("4")
						&& To.getItemAtPosition
								(To.getSelectedItemPosition()).toString().equals("4"))
				{
					Toast.makeText(MltUpdate.this,"Select different location ",
							Toast.LENGTH_SHORT).show();
				}
				else if(From.getItemAtPosition(From.getSelectedItemPosition()).toString().
						equals("TRANS")
						&& To.getItemAtPosition
								(To.getSelectedItemPosition()).toString().equals("TRANS"))
				{
					Toast.makeText(MltUpdate.this,"Select different location ",
							Toast.LENGTH_SHORT).show();
				}
				else
				{
					froml=From.getItemAtPosition(From.getSelectedItemPosition()).toString();
					String tol=To.getItemAtPosition(To.getSelectedItemPosition()).toString();
					String uoml=UOM.getItemAtPosition(UOM.getSelectedItemPosition()).toString();
					String qyt=Qty.getText().toString();
			
					MLT_Details mlt_details=new MLT_Details();
					mlt_details.setMlt_itemno(ItemNo);
					mlt_details.setMlt_description(Desc);
					mlt_details.setMlt_from(froml);
					mlt_details.setMlt_to(tol);
					mlt_details.setMlt_qty(qyt);
					mlt_details.setMlt_uom(uoml);
					
					handler.getReadableDatabase();
					boolean flag=handler.checkMLT_Details(ItemNo);
					handler.closeDatabase();
					
					if(flag==false)
					{
					handler.getWritableDatabase();
					handler.addMLT_Temp_Table(mlt_details);
					handler.closeDatabase();
					}
					else
					{

						handler.getWritableDatabase();
						handler.updateMLT_Temp_Table(mlt_details);
						handler.closeDatabase();
					}
					
					Intent i=new Intent(MltUpdate.this,MltMain.class);
					i.putExtra("flag", 1);
					startActivity(i);
				}
			}
		});
		Cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent i= new Intent(getApplicationContext(),MltMain.class);
			/*	handler.getWritableDatabase();
				handler.deleteMltTempData();
				handler.closeDatabase();*/
				i.putExtra("flag", 1);
				startActivity(i);			
				}
		});
		
	}
    private void getUomdata() {
		// TODO Auto-generated method stub
    
    	handler.getWritableDatabase();
    	List<String> l=handler.getMltAllUomDetails(ItemNo);
    	handler.closeDatabase();
    	   
        // Creating adapter for spinner  
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, l);  
   
        // Drop down layout style - list view with radio button  
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
   
        // attaching data adapter to spinner  
        UOM.setAdapter(dataAdapter);  
		
	}
    public void onItemSelected11(AdapterView<?> parent, View view, int position,  
            long id) {  
        // On selecting a spinner item  
        String label = parent.getItemAtPosition(position).toString();  
   
        // Showing selected spinner item  
        Toast.makeText(parent.getContext(), "You selected: " + label,  
                Toast.LENGTH_LONG).show();  
   
    }  
    public void onNothingSelected11(AdapterView<?> arg0) {  
        // TODO Auto-generated method stub  
   
    }
	private void getDataToDB() {
		// TODO Auto-generated method stub
    	// TODO Auto-generated method stub
		handler.getReadableDatabase();
	List<String> l=handler.getMltAllToDetails();
	handler.closeDatabase();
    	   
        // Creating adapter for spinner  
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, l);  
   
        // Drop down layout style - list view with radio button  
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
   
        // attaching data adapter to spinner  
        To.setAdapter(dataAdapter);  
    }  
   
    public void onItemSelected1(AdapterView<?> parent, View view, int position,  
            long id) {  
        // On selecting a spinner item  
        String label = parent.getItemAtPosition(position).toString();  
   
        // Showing selected spinner item  
        Toast.makeText(parent.getContext(), "You selected: " + label,  
                Toast.LENGTH_LONG).show();  
   
    }  
    public void onNothingSelected1(AdapterView<?> arg0) {  
        // TODO Auto-generated method stub  
   
    }
		
	
	private void getDataFromDB() {
 		// TODO Auto-generated method stub
		handler.getReadableDatabase();
    	List<String> l=handler.getMltAllToDetails();
    	handler.closeDatabase();
    	   
        // Creating adapter for spinner  
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, l);  
   
        // Drop down layout style - list view with radio button  
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
   
        // attaching data adapter to spinner  
        From.setAdapter(dataAdapter);  
    }  
   
    
    public void onItemSelected(AdapterView<?> parent, View view, int position,  
            long id) {  
        // On selecting a spinner item  
        String label = parent.getItemAtPosition(position).toString();  
   
        // Showing selected spinner item  
        Toast.makeText(parent.getContext(), "You selected: " + label,  
                Toast.LENGTH_LONG).show();  
   
    }  
   
    
    public void onNothingSelected(AdapterView<?> arg0) {  
        // TODO Auto-generated method stub  
   
    }
 	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	
		unRegisterBaseActivityReceiver();
	}
 
}

		

       
 
      
    
	