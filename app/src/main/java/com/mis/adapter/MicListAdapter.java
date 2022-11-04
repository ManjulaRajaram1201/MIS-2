package com.mis.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.mobinventorysuit.R;
import com.mis.mic.model.MIC_OrderDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import android.widget.TextView;

public class MicListAdapter extends ArrayAdapter<MIC_OrderDetails>  implements Filterable{

	List<MIC_OrderDetails> alllst;
	List<MIC_OrderDetails> list;
	
	List<MIC_OrderDetails> filterlst;
	// = new ArrayList<MIC_OrderDetails>();
	Context context;

	TextView txtitem;
	TextView txtdesc;
	TextView txtpick;
	TextView txtQtyonHand;
	TextView txtQtyCounted;
	TextView txtuom;
/*	TextView txtstatus;*/

	private ModelFilter filter;

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new ModelFilter();
		}
		return filter;
	}
	
	public MicListAdapter(Context context, List<MIC_OrderDetails> value) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.six_textview, value);
		this.context = context;
		this.list = value;
		this.alllst=new ArrayList<MIC_OrderDetails>(list);
		this.filterlst=new ArrayList<MIC_OrderDetails>(alllst);
		getFilter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int pos = position;

		View view = null;
		if (convertView == null) {

			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.six_textview, parent, false);

			txtitem = (TextView) convertView.findViewById(R.id.txt_fullItemNo);
			txtdesc = (TextView) convertView.findViewById(R.id.txt_fullDesc);
			txtpick = (TextView) convertView.findViewById(R.id.txt_fullLoc);
			txtQtyonHand = (TextView) convertView.findViewById(R.id.txt_fullord);
			txtQtyCounted = (TextView) convertView.findViewById(R.id.txt_fullrecqty);
			txtuom = (TextView) convertView.findViewById(R.id.txt_fullUom);
			convertView.setTag(new OrdersViewHolder(txtitem, txtdesc, txtpick,
					txtQtyonHand, txtQtyCounted, txtuom));
			

		} else {
			OrdersViewHolder viewHolder = (OrdersViewHolder) convertView
					.getTag();
		}

		OrdersViewHolder holder = (OrdersViewHolder) convertView.getTag();
	
		holder.txtitem.setText(alllst.get(position).getItemNumber());
		holder.txtdesc.setText(alllst.get(position).getItemDescription());
		holder.txtpick.setText(alllst.get(position).getPickSeq());
		holder.txtQtyonHand.setText((alllst.get(position).getQtyonHand()));
		/*String o=(lst.get(position).getQtyShiped()).toString();
		String u=lst.get(position).getUom();*/
		holder.txtQtyCounted.setText((alllst.get(position).getQtyCount()));
		holder.txtuom.setText(alllst.get(position).getUnit());
	/*	holder.txtstatus.setText(alllst.get(position).getStatus());*/

		
		return convertView;

	}

	/** Holds child views for one row. */
	static class OrdersViewHolder {

		TextView txtitem;
		TextView txtdesc;
		TextView txtpick;
		TextView txtQtyonHand;
		TextView txtQtyCounted;
		TextView txtuom;
/*		TextView txtstatus;*/

		public OrdersViewHolder(TextView txtitem, TextView txtdesc,
				TextView txtpick, TextView txtQtyonHand, TextView txtQtyCounted,
				TextView txtuom) {
			// TODO Auto-generated constructor stub
			this.txtitem = txtitem;
			this.txtdesc = txtdesc;
			this.txtpick = txtpick;
			this.txtQtyonHand = txtQtyonHand;
			this.txtQtyCounted = txtQtyCounted;
			this.txtuom = txtuom;
			/*this.txtstatus=txtstatus;*/

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

		

		public TextView getTxtuom() {
			return txtuom;
		}

		public void setTxtuom(TextView txtuom) {
			this.txtuom = txtuom;
		}

		public TextView getTxtpick() {
			return txtpick;
		}

		public void setTxtpick(TextView txtpick) {
			this.txtpick = txtpick;
		}


		public TextView getTxtQtyonHand() {
			return txtQtyonHand;
		}


		public void setTxtQtyonHand(TextView txtQtyonHand) {
			this.txtQtyonHand = txtQtyonHand;
		}


		public TextView getTxtQtyCounted() {
			return txtQtyCounted;
		}


		public void setTxtQtyCounted(TextView txtQtyCounted) {
			this.txtQtyCounted = txtQtyCounted;
		}

		/*public TextView getTxtstatus() {
			return txtstatus;
		}

		public void setTxtstatus(TextView txtshipvia) {
			this.txtstatus = txtshipvia;
		}*/
	}

	private class ModelFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<MIC_OrderDetails> filteredItems = new ArrayList<MIC_OrderDetails>();

				for (int i = 0, l = alllst.size(); i < l; i++) {
					MIC_OrderDetails customer = alllst.get(i);
					String strNum = customer.getItemNumber();//tHhCustomer_number();
					
					if (strNum.toLowerCase().contains(constraint))//|| strName.toLowerCase().contains(constraint))
						filteredItems.add(customer);
				}
				result.count = filteredItems.size();
				result.values = filteredItems;
			} else {
				synchronized (this) {
					result.values = alllst;//ModelItemsArray;
					result.count = alllst.size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			filterlst = (ArrayList<MIC_OrderDetails>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filterlst.size(); i < l; i++)
				add(filterlst.get(i));
			notifyDataSetInvalidated();
		}
	}

}