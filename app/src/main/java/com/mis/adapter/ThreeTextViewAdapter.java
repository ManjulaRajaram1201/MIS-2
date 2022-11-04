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

public class ThreeTextViewAdapter extends ArrayAdapter<String> {
	Context context;
	List<String> orderNum = new ArrayList<String>();
	TextView txtOrd1, txtShip1, txtStatus;
	Cursor cursor;
	DatabaseHandler handler;
	String shipNo="";

	public ThreeTextViewAdapter(Context context, List<String> ordNo) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.threetextview, ordNo);
		this.context = context;
		this.orderNum = ordNo;
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

		viewHolder.txtOrd1.setText(orderNum.get(pos));
		try {

			handler.getReadableDatabase();
			cursor = handler.getShipNo(orderNum.get(pos));
			handler.closeDatabase();

			if (cursor.getCount() > 0) {
				shipNo = cursor.getString(0);
				try {
					//In MseExport we had set the shipno to Ordno if some 
					//order not exported thats why we r checking .equals to ordno
					if (shipNo.trim().equalsIgnoreCase(orderNum.get(pos))) {
						viewHolder.txtShip1.setText("Not Exported");
						String Fail = "Failed";
						viewHolder.txtStatus.setText(Fail);

					}
					else {

						viewHolder.txtShip1.setText(shipNo);
						String Pass = "Passed";
						viewHolder.txtStatus.setText(Pass);
					}
				} catch (NullPointerException ex) {
					System.out.println("Pc"+shipNo);
					
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
		return orderNum.size();
	}

	private static class ViewHolder {
		TextView txtOrd1, txtShip1, txtStatus;
	}

}
