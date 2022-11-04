package com.mis.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.mobinventorysuit.R;
import com.mis.controller.MltExport;
import com.mis.controller.MseExport;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class Chk_Adapter_mlt extends ArrayAdapter<Chk_Model_mlt> {
	Context context;
	public List<Chk_Model_mlt> Po = new ArrayList<Chk_Model_mlt>();
	SharedPreferences ordExport;
	public  int check=0;
	boolean flag = false;
	private CheckBox checkBox;
	public static Set<String> ordExpo = new HashSet<String>();
	private TextView txtCustNo;

	public Chk_Adapter_mlt(Context context, List<Chk_Model_mlt> pOList) {
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

			// To hold the state of check box
			holder.checkBox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					Chk_Model_mlt model = (Chk_Model_mlt) cb.getTag();
					Log.i("Clicked on Checkbox:" + cb.getText(),
							"is " + cb.isChecked());
					model.setSelected(cb.isChecked());

					// to know which ord to be exported
					if (model.isSelected()) {

						ordExpo.add(cb.getText().toString());

						for (int i = 0; i < Po.size(); i++) {
							// Chk_Model_mlt modval= Po.get(i);
							if (Po.get(i).isSelected()) {
								flag = true;
							} else {
								flag = false;
								break;
							}
						}

					} else {
						if (ordExpo.contains(cb.getText().toString())) {

							ordExpo.remove(cb.getText().toString());

							for (int i = 0; i < Po.size(); i++) {
								// Chk_Model_mlt modval= Po.get(i);
								if (Po.get(i).isSelected()) {
									flag = true;
								} else {
									flag = false;
									break;
								}
							}
						}
					}

					// When all Orders selected then automatically Check All
					// also get checked

					if (flag) {

						MltExport.CheckAll();
					} else {

						MltExport.UnCheckAll();
					}

				}
			});
		}
		 else {
			OrdersViewHolder viewHolder = (OrdersViewHolder) convertView
					.getTag();

		}
		// fill data

		OrdersViewHolder holder = (OrdersViewHolder) convertView.getTag();
		Chk_Model_mlt model = Po.get(position);
		holder.checkBox.setText(model.getName());
		holder.checkBox.setChecked(model.isSelected());
		if (model.isSelected()) {
			ordExpo.add(checkBox.getText().toString());

		} else {
			if (ordExpo.contains(checkBox.getText().toString())) {
				ordExpo.remove(checkBox.getText().toString());

			}
		}

		holder.checkBox.setTag(model);

		/*holder.txtCustNo.setText(model.getName());
		holder.txtCustNo.setTag(model);*/

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
		public CheckBox getCheckBox() {
			return checkBox;
		}

		public TextView getTxtCustNo() {
			return txtCustNo;
		}

		public void setCheckBox(CheckBox checkBox) {
			this.checkBox = checkBox;
		}

		public void setTxtCustNo(TextView txtCustNo) {
			this.txtCustNo = txtCustNo;
		}


	}


}
