package com.mis.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.mobinventorysuit.R;
import com.mis.controller.MseOrderList;
import com.mis.database.DatabaseHandler;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class Order_CustAdapter extends BaseAdapter implements Filterable {
	Context context;
	List<String> lst_Selected;// =new ArrayList<String>();
	List<String> lst_Selected_filter;// =new ArrayList<String>();
	List<String> lst_customer;// =new ArrayList<String>();
	List<String> lst_Customer_filter;
	TextView txtord, txtcust;
	DatabaseHandler handler;
	ModelFilter filter;

	public Order_CustAdapter(Context context, List<String> selected_ord,
			List<String> cust) {
		// TODO Auto-generated constructor stub
		// super(context, R.layout.mse_orderlist, selected_ord);
		this.context = context;
		this.lst_Selected = new ArrayList<String>(selected_ord);
		this.lst_Selected_filter = new ArrayList<String>(lst_Selected);
		this.lst_customer = new ArrayList<String>(cust);
		this.lst_Customer_filter = new ArrayList<String>(lst_customer);

		handler = new DatabaseHandler(context);
		getFilter();
	}

	@Override
	public Filter getFilter() {
		if (filter == null)
			filter = new ModelFilter();

		return filter;
	}

	private class ModelFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			//FilterResults results2 = new FilterResults();
			// We implement here the filter logic
			if (constraint == null || constraint.length() == 0) {
				// No filter implemented we return all the list
				if (MseOrderList.order_Details.equals("ON")) {
					ArrayList<String> ordlist = new ArrayList<String>(
							lst_Selected);
					results.values = ordlist;
					results.count = ordlist.size();
					
				} else {
					ArrayList<String> custlist = new ArrayList<String>(
							lst_customer);
					results.values = custlist;
					results.count = custlist.size();
					
				}
			} else {
				// We perform filtering operation
				ArrayList<String> filteredList = new ArrayList<String>();

				for (int i = 0; i < lst_Selected.size(); i++) {
					if (MseOrderList.order_Details.equals("ON")) {
						String ordNum = lst_Selected.get(i);

						if (ordNum.toLowerCase().contains(
								constraint.toString().toLowerCase()))
							filteredList.add(ordNum);
					} else {
						String custNum = lst_customer.get(i);

						if (custNum.toLowerCase().contains(constraint.toString().toLowerCase()))
							filteredList.add(custNum);
						/*System.out.println("+"+filteredList);
						*/
					}

				}
				results.values = filteredList;
				results.count = filteredList.size();
			}
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// TODO Auto-generated method stub
			if (results.count == 0)
				notifyDataSetInvalidated();
			else {
				if (MseOrderList.order_Details.equals("ON"))
					lst_Selected_filter = (List<String>) results.values;
				else
					lst_Customer_filter = (List<String>) results.values;

				Order_CustAdapter.this.notifyDataSetChanged();
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int pos = position;
		if (convertView == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = mInflater.inflate(R.layout.two_textview_custom, null);

			txtord = (TextView) convertView.findViewById(R.id.txtfirst_detail);
			txtcust = (TextView) convertView.findViewById(R.id.txtlast_detail);
			convertView.setTag(new OrdersAndVendorViewHolder(txtord, txtcust));
			OrdersAndVendorViewHolder holder = new OrdersAndVendorViewHolder(
					txtord, txtcust);

		} else {
			OrdersAndVendorViewHolder viewHolder = (OrdersAndVendorViewHolder) convertView
					.getTag();

		}
		OrdersAndVendorViewHolder holder = (OrdersAndVendorViewHolder) convertView
				.getTag();

		holder.txtcust.setText(lst_Customer_filter.get(pos));
		holder.txtord.setText(lst_Selected_filter.get(pos));

		return convertView;
	}

	private static class OrdersAndVendorViewHolder {

		TextView txtord, txtcust;

		public OrdersAndVendorViewHolder(TextView txtOrd, TextView txtCust) {

			this.txtord = txtOrd;
			this.txtcust = txtCust;

		}

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (MseOrderList.order_Details.equals("ON"))
			return lst_Selected_filter.size();
		else
			return lst_Customer_filter.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (MseOrderList.order_Details.equals("ON"))
			return lst_Selected_filter.get(position);
		else
			return lst_Customer_filter.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;

	}

}