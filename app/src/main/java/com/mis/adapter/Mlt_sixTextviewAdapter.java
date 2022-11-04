package com.mis.adapter;

import java.util.ArrayList;
import java.util.List;


import com.example.mobinventorysuit.R;
import com.mis.mlt.model.MLT_Details;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class Mlt_sixTextviewAdapter extends ArrayAdapter<MLT_Details> implements
		Filterable {

	private final List<MLT_Details> list;
	private final Activity context;

	private ModelFilter filter;
	private LayoutInflater inflator;
	private List<MLT_Details> allModelItemsArray;
	private List<MLT_Details> filteredModelItemsArray;

	static class ViewHolder {
		TextView txtitem;
		TextView txtdesc;
		TextView txtfrom;
		TextView txtto;
		TextView txtrecqty;
		TextView txtuom;
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new ModelFilter();
		}
		return filter;
	}

	public int getCount() {
		return list.size();
	}

	public long getItemId(int position) {
		return position;
	}

	public Mlt_sixTextviewAdapter(Activity context, List<MLT_Details> list) {
		super(context, R.layout.mlt_six_textview, list);
		this.context = context;
		this.list = list;
		allModelItemsArray = new ArrayList<MLT_Details>(list);
		filteredModelItemsArray = new ArrayList<MLT_Details>(allModelItemsArray);
		inflator = context.getLayoutInflater();
		getFilter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.mlt_six_textview, null);
			final ViewHolder viewHolder = new ViewHolder();


			viewHolder.txtitem = (TextView) view.findViewById(R.id.mlt_txt_fullItemNo);
			viewHolder.txtdesc = (TextView) view.findViewById(R.id.mlt_txt_fullDesc);
			viewHolder.txtfrom = (TextView) view.findViewById(R.id.mlt_txt_fullLoc);
			viewHolder.txtto = (TextView) view.findViewById(R.id.mlt_txt_fullord);
			viewHolder.txtrecqty = (TextView) view.findViewById(R.id.mlt_txt_fullrecqty);
			viewHolder.txtuom = (TextView) view.findViewById(R.id.mlt_txt_fullUom);

			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		final ViewHolder holder = (ViewHolder) view.getTag();

		holder.txtitem.setText(list.get(position).getMlt_itemno());
		holder.txtdesc.setText(list.get(position).getMlt_description());
		holder.txtfrom.setText(list.get(position).getMlt_from());
		holder.txtto.setText((list.get(position).getMlt_to()));
		holder.txtrecqty.setText((list.get(position).getMlt_qty()));
		holder.txtuom.setText(list.get(position).getMlt_uom());

		return view;
	}

	private class ModelFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<MLT_Details> filteredItems = new ArrayList<MLT_Details>();

				for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
					MLT_Details reciptData = allModelItemsArray.get(i);

					String strChkNo = reciptData.getMlt_description();// getHhReceipt_receiptnumber();
					String strCustNo = reciptData.getMlt_itemno();// getHhReceipt_customernumber();

					if (strChkNo.toLowerCase().contains(constraint)
							|| strCustNo.toLowerCase().contains(constraint))
						filteredItems.add(reciptData);
				}
				result.count = filteredItems.size();
				result.values = filteredItems;
			} else {
				synchronized (this) {
					result.values = allModelItemsArray;
					result.count = allModelItemsArray.size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			filteredModelItemsArray = (ArrayList<MLT_Details>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
				add(filteredModelItemsArray.get(i));
			notifyDataSetInvalidated();
		}
	}

}
