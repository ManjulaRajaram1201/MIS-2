package com.mis.adapter;

import java.util.ArrayList;
import java.util.List;


import com.example.mobinventorysuit.R;
import com.mis.adapter.Chk_Model;
import com.mis.database.DatabaseHandler;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ThreeTextViewAdapter_mlt extends ArrayAdapter<String> {
	Context context;
	List<String> docNum = new ArrayList<String>();
	String trfNo="";

	TextView txtOrd1, txtShip1, txtStatus;
	DatabaseHandler handler;
	Cursor cursor;
	public ThreeTextViewAdapter_mlt(Context context, List<String> docNo) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.threetextview, docNo);
		this.context = context;
		this.docNum = docNo;
		handler = new DatabaseHandler(context);
		

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final int pos = position;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.threetextview, parent, false);
			viewHolder.txtOrd1 = (TextView) convertView
					.findViewById(R.id.txtfirst_detail_expo);
			viewHolder.txtShip1 = (TextView) convertView
					.findViewById(R.id.txtsec_detail_expo);
			viewHolder.txtStatus = (TextView) convertView
					.findViewById(R.id.txtthird_detail_expo);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}

		viewHolder.txtOrd1.setText(docNum.get(pos));
		try {

			handler.getReadableDatabase();
			cursor = handler.getTrfNo(docNum.get(pos));
			handler.closeDatabase();

			if (cursor.getCount() > 0) {
				trfNo = cursor.getString(0);
				try {
					//In MseExport we had set the shipno to Ordno if some 
					//order not exported thats why we r checking .equals to ordno
					if (trfNo.trim().equalsIgnoreCase(docNum.get(pos))) {
						viewHolder.txtShip1.setText("Not Exported");
						String Fail = "Failed";
						viewHolder.txtStatus.setText(Fail);

					}
					else {

						viewHolder.txtShip1.setText(trfNo);
						String Pass = "Passed";
						viewHolder.txtStatus.setText(Pass);
					}
				} catch (NullPointerException ex) {
					System.out.println("Pc"+trfNo);
					
					String Fail = "Failed";
					viewHolder.txtShip1.setText("Not Exported");
					viewHolder.txtStatus.setText(Fail);
					cursor.close();
				}

			}

			
		} catch (Exception e) {

			cursor.close();
		}
		cursor.close();
		return convertView;
	}

	@Override
	public int getCount() {
		return docNum.size();
	}

	private static class ViewHolder {
		TextView txtOrd1, txtShip1, txtStatus;
	}

}