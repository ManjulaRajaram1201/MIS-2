package com.mis.adapter;

import java.util.ArrayList;

import java.util.List;

import com.example.mobinventorysuit.R;
import com.mis.mpr.model.MPR_OrderDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;

import android.widget.TextView;

public class OrderListAdapter_mpr extends ArrayAdapter<MPR_OrderDetails>
		implements Filterable {

	List<MPR_OrderDetails> alllst;
	List<MPR_OrderDetails> list;

	List<MPR_OrderDetails> filterlst;
	// = new ArrayList<MSE_OrderDetails>();
	Context context;

	TextView txtitem;
	TextView txtdesc;
	TextView txtordqty;
	TextView txtrecqty;
	TextView txtuom;
	TextView txtfullloc;
	TextView txtcomment;

	public OrderListAdapter_mpr(Context context, List<MPR_OrderDetails> value) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.seven_textview, value);
		this.context = context;
		this.list = value;
		this.alllst = new ArrayList<MPR_OrderDetails>(list);
		this.filterlst = new ArrayList<MPR_OrderDetails>(alllst);
		getFilter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int pos = position;

		View view = null;
		if (convertView == null) {

			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.seven_textview, parent, false);

			txtitem = (TextView) convertView.findViewById(R.id.txt_fullItemNo_mpr);
			txtdesc = (TextView) convertView.findViewById(R.id.txt_fullDesc_mpr);
			txtordqty = (TextView) convertView.findViewById(R.id.txt_fullord_mpr);
			txtrecqty = (TextView) convertView
					.findViewById(R.id.txt_fullrecqty_mpr);
			txtuom = (TextView) convertView.findViewById(R.id.txt_fullUom_mpr);
			txtfullloc = (TextView) convertView
					.findViewById(R.id.txt_fullLoc_mpr);
			txtcomment = (TextView) convertView
					.findViewById(R.id.txt_fullComment_mpr);

			convertView.setTag(new OrdersViewHolder(txtitem, txtdesc,
					txtordqty, txtrecqty, txtuom, txtfullloc, txtcomment));

		} else {
			OrdersViewHolder viewHolder = (OrdersViewHolder) convertView
					.getTag();
		}

		OrdersViewHolder holder = (OrdersViewHolder) convertView.getTag();

		holder.txtitem.setText(alllst.get(position).getItem_no());
		holder.txtdesc.setText(alllst.get(position).getDesc());
		holder.txtfullloc.setText(alllst.get(position).getLoc_code());

		holder.txtordqty.setText((alllst.get(position).getOrdered_qty())
				.toString());
		holder.txtrecqty.setText((alllst.get(position).getReceived_qty())
				.toString());
		holder.txtuom.setText(alllst.get(position).getUom());
		holder.txtcomment.setText(alllst.get(position).getComments());

		return convertView;

	}

	/** Holds child views for one row. */
	static class OrdersViewHolder {

		TextView txtitem;
		TextView txtdesc;

		TextView txtordqty;
		TextView txtrecqty;
		TextView txtuom;
		TextView txtfullloc;
		TextView txtcomment;

		public OrdersViewHolder(TextView txtitem, TextView txtdesc,
				TextView txtordqty, TextView txtrecqty, TextView txtuom,
				TextView txtfullloc, TextView txtcomment) {
			// TODO Auto-generated constructor stub
			this.txtitem = txtitem;
			this.txtdesc = txtdesc;

			this.txtordqty = txtordqty;
			this.txtrecqty = txtrecqty;
			this.txtuom = txtuom;
			this.txtfullloc = txtfullloc;
			this.txtcomment = txtcomment;

		}

		public TextView getTxtcomment() {
			return txtcomment;
		}

		public void setTxtcomment(TextView txtcomment) {
			this.txtcomment = txtcomment;
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

		public TextView getTxtshipvia() {
			return txtfullloc;
		}

		public void setTxtshipvia(TextView txtshipvia) {
			this.txtfullloc = txtshipvia;
		}
	}

}