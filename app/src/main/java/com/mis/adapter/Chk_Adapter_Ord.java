package com.mis.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.mobinventorysuit.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class Chk_Adapter_Ord extends ArrayAdapter<Chk_Model> {
	Context context;
	public List<Chk_Model> Po = new ArrayList<Chk_Model>();
	

	private CheckBox checkBox;
	public static Set<String> ordExpo = new HashSet<String>();
	private TextView txtCustNo;

	public Chk_Adapter_Ord(Context context, List<Chk_Model> pOList) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.po_custom, pOList);
		this.context = context;
		this.Po.addAll(pOList);

	
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int pos = position;
		if (convertView == null) {

			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.po_custom, parent, false);
			checkBox = (CheckBox) convertView.findViewById(R.id.Chk_po);
			txtCustNo = (TextView) convertView.findViewById(R.id.Txt_Po);
			convertView.setTag(new OrdersViewHolder(checkBox, txtCustNo));
			OrdersViewHolder holder = new OrdersViewHolder(checkBox, txtCustNo);

			// To hold the state of checkbox
			holder.checkBox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					Chk_Model model = (Chk_Model) cb.getTag();
				
					model.setSelected(cb.isChecked());
	}
			});

		} else {
			OrdersViewHolder viewHolder = (OrdersViewHolder) convertView
					.getTag();

		}
		// fill data

		OrdersViewHolder holder = (OrdersViewHolder) convertView.getTag();
		Chk_Model model = Po.get(position);
		holder.checkBox.setText(model.getName());
		holder.checkBox.setChecked(model.isSelected());
	

		holder.checkBox.setTag(model);

		holder.txtCustNo.setText(model.getName1());
		holder.txtCustNo.setTag(model);

		return convertView;
	}

	/** Holds child views for one row. */
	private static class OrdersViewHolder {
		private CheckBox checkBox;

		private TextView txtCustNo;

		public OrdersViewHolder(CheckBox checkBox, TextView textOrdNo) {
			this.checkBox = checkBox;
			this.txtCustNo = textOrdNo;

		}

	}


}
