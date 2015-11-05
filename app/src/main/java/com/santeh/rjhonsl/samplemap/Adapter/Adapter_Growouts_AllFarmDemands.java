package com.santeh.rjhonsl.samplemap.Adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.santeh.rjhonsl.samplemap.Obj.CustInfoObject;
import com.santeh.rjhonsl.samplemap.R;

import java.text.DecimalFormat;
import java.util.List;

public class Adapter_Growouts_AllFarmDemands extends ArrayAdapter<CustInfoObject> {

	Context context;
	LayoutInflater inflater;
	List<CustInfoObject> objArrayList;
	int positions = 0;
	String tag = "CreateNew ArrayAdapter";
	private SparseBooleanArray mSelectedItemsIds;


	public Adapter_Growouts_AllFarmDemands(Context context, int resourceId, List<CustInfoObject> items) {
		super(context, resourceId, items);
		mSelectedItemsIds = new SparseBooleanArray();
		this.context = context;
		this.objArrayList = items;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Log.d(tag, "Adapter Context");
	}

	private class ViewHolder {
		TextView txtFamrname, txtaddress, txtreportCount, txtconsumption, txtinitial;
	}

	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		positions = position;

		if (view == null) {

			Log.d(tag, "if null");
			holder = new ViewHolder();

			view = inflater.inflate(R.layout.item_lv_weeklyreport_allfeeddemands, null);

			holder.txtFamrname = (TextView) view.findViewById(R.id.itemlv_feedconsSummary_farmname);
			holder.txtaddress = (TextView) view.findViewById(R.id.itemlv_feedconsSummary_address);
			holder.txtreportCount = (TextView) view.findViewById(R.id.itemlv_feedconsSummary_reportCount);
			holder.txtconsumption = (TextView) view.findViewById(R.id.itemlv_feedconsSummary_consumption);
			holder.txtinitial= (TextView) view.findViewById(R.id.itemlv_feedconsSummary_initials);

			view.setTag(holder);
		}
		else
		{
			Log.d(tag, "if not null");
			holder = (ViewHolder) view.getTag();
		}

//		 Capture position and set to the TextViews
		holder.txtinitial.setText(objArrayList.get(position).getFarmname().substring(0,1));
		holder.txtFamrname.setText(objArrayList.get(position).getFarmname());
		holder.txtaddress.setText("" + objArrayList.get(position).getAddress());
		holder.txtreportCount.setText("Reports: "+objArrayList.get(position).getCounter()+"");
		DecimalFormat df = new DecimalFormat("#.##");
		holder.txtconsumption.setText("Consumption: "+(df.format(objArrayList.get(position).getWeeklyConsumptionInGrams() / 1000))+" kg");


		return view;
	}

	@Override
	public void remove(CustInfoObject object) {
		objArrayList.remove(object);
		notifyDataSetChanged();
	}

	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, true);
		else
			mSelectedItemsIds.delete(position);
		notifyDataSetChanged();
	}

}
