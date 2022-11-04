package com.mis.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mobinventorysuit.R;

import com.mis.database.DatabaseHandler;

public class ThreeInternalTextViewAdapter extends ArrayAdapter<String> {
	Context context;
	List<String> orderNum = new ArrayList<String>();
	TextView txtOrd1, txtShip1, txtStatus;
	Cursor cursor;
	DatabaseHandler handler;
	String shipNo = "";

	public ThreeInternalTextViewAdapter(Context context, List<String> ordNo) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.two_textview, ordNo);
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
					R.layout.two_textview, parent, false);
			viewHolder.txtOrd1 = (TextView) convertView
					.findViewById(R.id.txtfirst_detail);
			/*
			 * viewHolder.txtShip1 = (TextView) convertView
			 * .findViewById(R.id.txtsec_detail_expo);
			 */
			viewHolder.txtStatus = (TextView) convertView
					.findViewById(R.id.txtlast_detail);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}

	

		/*
		 * handler.getReadableDatabase(); cursor =
		 * handler.getShipNoforII(orderNum.get(pos)); handler.closeDatabase();
		 * 
		 * if (cursor.getCount() > 0) { shipNo = cursor.getString(0); try { //In
		 * MseExport we had set the shipno to Ordno if some //order not exported
		 * thats why we r checking .equals to ordno if
		 * (shipNo.trim().equalsIgnoreCase(orderNum.get(pos))) {
		 * viewHolder.txtShip1.setText("Not Exported"); String Fail = "Failed";
		 * viewHolder.txtStatus.setText(Fail);
		 * 
		 * } else {
		 * 
		 * viewHolder.txtShip1.setText(shipNo); String Pass = "Passed";
		 * viewHolder.txtStatus.setText(Pass); } } catch (NullPointerException
		 * ex) { System.out.println("Pc"+shipNo);
		 * 
		 * String Fail = "Failed"; viewHolder.txtShip1.setText("Not Exported");
		 * viewHolder.txtStatus.setText(Fail); cursor.close(); }
		 * 
		 * }
		 */
		
		viewHolder.txtOrd1.setText(orderNum.get(pos));
		
		handler.getReadableDatabase();
		boolean status = handler.checkStatusforII(orderNum.get(pos));
		handler.closeDatabase();

		if (status) {
			String Pass = "Passed";
			viewHolder.txtStatus.setText(Pass);

		} else {
			String Fail = "Failed";
			viewHolder.txtStatus.setText(Fail);

		}

		return convertView;
	}

	@Override
	public int getCount() {
		return orderNum.size();
	}

	private static class ViewHolder {
		TextView txtOrd1, txtStatus;
	}

}
