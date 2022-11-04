package com.mis.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.mobinventorysuit.R;

import com.mis.adapter.InternalListAdapter.OrdersViewHolder;
import com.mis.internal.model.Internal_OrderDetails;

public class InternalListAdapter extends ArrayAdapter<Internal_OrderDetails>
		implements Filterable {

	List<Internal_OrderDetails> alllst;
	List<Internal_OrderDetails> list;

	List<Internal_OrderDetails> filterlst;
	// = new ArrayList<Internal_OrderDetails>();
	Context context;

	TextView txtitem;
	TextView txtdesc;
	
	TextView txtordqty;
	TextView txtrecqty;
	TextView txtuom;


	private ModelFilter filter;

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new ModelFilter();
		}
		return filter;
	}

	public InternalListAdapter(Context context,
			List<Internal_OrderDetails> value) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.fivetextview, value);
		this.context = context;
		this.list = value;
		this.alllst = new ArrayList<Internal_OrderDetails>(list);
		this.filterlst = new ArrayList<Internal_OrderDetails>(alllst);
		getFilter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int pos = position;

		View view = null;
		if (convertView == null) {

			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.fivetextview, parent, false);

			txtitem = (TextView) convertView.findViewById(R.id.txt_fullItemNo);
			txtdesc = (TextView) convertView.findViewById(R.id.txt_fullDesc);
			txtordqty = (TextView) convertView.findViewById(R.id.txt_fullord);
			txtrecqty = (TextView) convertView.findViewById(R.id.txt_fullrecqty);
			txtuom = (TextView) convertView.findViewById(R.id.txt_fullUom);

			convertView.setTag(new OrdersViewHolder(txtitem, txtdesc,txtordqty, txtrecqty, txtuom));

		} else {
			OrdersViewHolder viewHolder = (OrdersViewHolder) convertView
					.getTag();
		}

		OrdersViewHolder holder = (OrdersViewHolder) convertView.getTag();

		holder.txtitem.setText(alllst.get(position).getItemNumber());
		holder.txtdesc.setText(alllst.get(position).getItemDescription());
		
		holder.txtordqty.setText((alllst.get(position).getQtyOrdred())
				.toString());
		
		holder.txtrecqty.setText((alllst.get(position).getQtyShiped())
				.toString());
		holder.txtuom.setText(alllst.get(position).getUom());/*
															 * holder.txtshipvia.
															 * setText
															 * (alllst.get
															 * (position
															 * ).getShipViaCode
															 * ());
															 * holder.txtcomment
															 * .
															 * setText(alllst.get
															 * (
															 * position).getComments
															 * ());
															 */

		return convertView;

	}

	/** Holds child views for one row. */
	static class OrdersViewHolder {

		TextView txtitem;
		TextView txtdesc;

		TextView txtordqty;
		TextView txtrecqty;
		TextView txtuom;

		public OrdersViewHolder(TextView txtitem, TextView txtdesc,
				 TextView txtordqty, TextView txtrecqty,
				TextView txtuom) {
			// TODO Auto-generated constructor stub
			this.txtitem = txtitem;
			this.txtdesc = txtdesc;

			this.txtordqty = txtordqty;
			this.txtrecqty = txtrecqty;
			this.txtuom = txtuom;

		}

		public TextView getTxtitem() {
			return txtitem;
		}

		public void setTxtitem(TextView txtitem) {
			this.txtitem = txtitem;
		}

		public TextView getTxtdesc() {
			return txtdesc;
		}

		public void setTxtdesc(TextView txtdesc) {
			this.txtdesc = txtdesc;
		}

		public TextView getTxtordqty() {
			return txtordqty;
		}

		public void setTxtordqty(TextView txtordqty) {
			this.txtordqty = txtordqty;
		}

		public TextView getTxtrecqty() {
			return txtrecqty;
		}

		public void setTxtrecqty(TextView txtrecqty) {
			this.txtrecqty = txtrecqty;
		}

		public TextView getTxtuom() {
			return txtuom;
		}

		public void setTxtuom(TextView txtuom) {
			this.txtuom = txtuom;
		}

	}

	private class ModelFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<Internal_OrderDetails> filteredItems = new ArrayList<Internal_OrderDetails>();

				for (int i = 0, l = alllst.size(); i < l; i++) {
					Internal_OrderDetails customer = alllst.get(i);
					String strNum = customer.getItemNumber();// tHhCustomer_number();

					if (strNum.toLowerCase().contains(constraint))// ||
																	// strName.toLowerCase().contains(constraint))
						filteredItems.add(customer);
				}
				result.count = filteredItems.size();
				result.values = filteredItems;
			} else {
				synchronized (this) {
					result.values = alllst;// ModelItemsArray;
					result.count = alllst.size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			filterlst = (ArrayList<Internal_OrderDetails>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filterlst.size(); i < l; i++)
				add(filterlst.get(i));
			notifyDataSetInvalidated();
		}
	}

}
