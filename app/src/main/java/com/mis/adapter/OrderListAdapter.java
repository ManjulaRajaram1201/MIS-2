package com.mis.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.mobinventorysuit.R;
import com.mis.mse.model.MSE_OrderDetails;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import android.widget.TextView;

public class OrderListAdapter extends ArrayAdapter<MSE_OrderDetails>  implements Filterable{

	List<MSE_OrderDetails> alllst;
	List<MSE_OrderDetails> list;
	
	List<MSE_OrderDetails> filterlst;
	// = new ArrayList<MSE_OrderDetails>();
	Context context;

	TextView txtitem;
	TextView txtdesc;
	TextView txtpick;
	TextView txtordqty;
	TextView txtrecqty;
	TextView txtuom;
	TextView txtshipvia;
	TextView txtcomment;

	private ModelFilter filter;

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new ModelFilter();
		}
		return filter;
	}
	
	public OrderListAdapter(Context context, List<MSE_OrderDetails> value) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.eight_textview, value);
		this.context = context;
		this.list = value;
		this.alllst=new ArrayList<MSE_OrderDetails>(list);
		this.filterlst=new ArrayList<MSE_OrderDetails>(alllst);
		getFilter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int pos = position;

		View view = null;
		if (convertView == null) {

			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.eight_textview, parent, false);

			txtitem = (TextView) convertView.findViewById(R.id.txt_fullItemNo);
			txtdesc = (TextView) convertView.findViewById(R.id.txt_fullDesc);
			txtpick = (TextView) convertView.findViewById(R.id.txt_fullLoc);
			txtordqty = (TextView) convertView.findViewById(R.id.txt_fullord);
			txtrecqty = (TextView) convertView.findViewById(R.id.txt_fullrecqty);
			txtuom = (TextView) convertView.findViewById(R.id.txt_fullUom);
			txtshipvia = (TextView) convertView.findViewById(R.id.txt_fullshipvia);
			txtcomment = (TextView) convertView.findViewById(R.id.txt_fullComment);

			convertView.setTag(new OrdersViewHolder(txtitem, txtdesc, txtpick,
					txtordqty, txtrecqty, txtuom, txtshipvia, txtcomment));
			

		} else {
			OrdersViewHolder viewHolder = (OrdersViewHolder) convertView
					.getTag();
		}

		OrdersViewHolder holder = (OrdersViewHolder) convertView.getTag();
	
		holder.txtitem.setText(alllst.get(position).getItemNumber());
		holder.txtdesc.setText(alllst.get(position).getItemDescription());
		holder.txtpick.setText(alllst.get(position).getPickingSequence());
		holder.txtordqty.setText((alllst.get(position).getQtyOrdred()).toString());
		/*String o=(lst.get(position).getQtyShiped()).toString();
		String u=lst.get(position).getUom();*/
		holder.txtrecqty.setText((alllst.get(position).getQtyShiped()).toString());
		holder.txtuom.setText(alllst.get(position).getUom());
		holder.txtshipvia.setText(alllst.get(position).getShipViaCode());
		holder.txtcomment.setText(alllst.get(position).getComments());
		
		return convertView;

	}

	/** Holds child views for one row. */
	static class OrdersViewHolder {

		TextView txtitem;
		TextView txtdesc;
		TextView txtpick;
		TextView txtordqty;
		TextView txtrecqty;
		TextView txtuom;
		TextView txtshipvia;
		TextView txtcomment;

		public OrdersViewHolder(TextView txtitem, TextView txtdesc,
				TextView txtpick, TextView txtordqty, TextView txtrecqty,
				TextView txtuom, TextView txtshipvia, TextView txtcomment) {
			// TODO Auto-generated constructor stub
			this.txtitem = txtitem;
			this.txtdesc = txtdesc;
			this.txtpick = txtpick;
			this.txtordqty = txtordqty;
			this.txtrecqty = txtrecqty;
			this.txtuom = txtuom;
			this.txtshipvia = txtshipvia;
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

		public TextView getTxtpick() {
			return txtpick;
		}

		public void setTxtpick(TextView txtpick) {
			this.txtpick = txtpick;
		}

		public TextView getTxtshipvia() {
			return txtshipvia;
		}

		public void setTxtshipvia(TextView txtshipvia) {
			this.txtshipvia = txtshipvia;
		}
	}

	private class ModelFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<MSE_OrderDetails> filteredItems = new ArrayList<MSE_OrderDetails>();

				for (int i = 0, l = alllst.size(); i < l; i++) {
					MSE_OrderDetails customer = alllst.get(i);
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

			filterlst = (ArrayList<MSE_OrderDetails>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filterlst.size(); i < l; i++)
				add(filterlst.get(i));
			notifyDataSetInvalidated();
		}
	}

}