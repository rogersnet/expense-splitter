package com.exsplit;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

public class GenerateReportActivity extends Activity {
	private ExpandableListView expList;
	private SummaryListAdapter expListAdapter;
	
	private ExSplitDbAdapter mDbHelper;
	private Resources res;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gen_report);
		mDbHelper = new ExSplitDbAdapter(this);
		mDbHelper.open();		
		
		expList = (ExpandableListView) findViewById(R.id.expandableList);
		
		expListAdapter = new SummaryListAdapter(getApplicationContext());	
		
		//removes the standard group state indicator
		expList.setGroupIndicator(null);
		
		ArrayList<SummaryItem> totalExp  = new ArrayList<SummaryItem>();
		ArrayList<SummaryItem> totalRecv = new ArrayList<SummaryItem>();
		
		fillSummaryData(totalExp,totalRecv);
		
		expListAdapter.setUpExp(totalExp, totalRecv);
		expList.setAdapter(expListAdapter);

		if(expListAdapter.getChildrenCount(0) >= 1){
			expList.expandGroup(0);
		}
		
		/*
		 * override the onGroupClick method to make sure the
		 * first group (future travel plans) does not collapse
		 * or expand.
		 */
		expList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if(groupPosition == 0){
					return true;
				} else {
					return false;
				}
			}
		});
	}
	
	public void fillSummaryData(ArrayList<SummaryItem> exp, ArrayList<SummaryItem> recv){
		
		Cursor expCursor = mDbHelper.fetchAllExpenses();
		HashMap<String,String> amtMap = new HashMap<String,String>();
		SummaryItem item;
		
		float total_exp = 0;
		int size = 0;
		float total_amount = 0;
		if (expCursor.moveToFirst()){
			do {
				String user_id = expCursor.getString(1);
				String amount  = expCursor.getString(4);

				total_exp = total_exp + Float.parseFloat(amount);
				String curr_amt = amtMap.get(user_id);
				size += 1;
				try {
					total_amount = 0;
					total_amount = Float.parseFloat(amount) + Float.parseFloat(curr_amt);
				} catch (Exception e) {
					total_amount = Float.parseFloat(amount);
				}

				amtMap.put(user_id, String.valueOf(total_amount));
											
			} while (expCursor.moveToNext());
		}
		
		float avg_exp = (total_exp/size);
		
		//fill total expenditure list
		for(Entry<String,String> entry: amtMap.entrySet() ){
			String user_id = entry.getKey();
			String amount  = entry.getValue();
			
			Cursor userCursor = mDbHelper.fetchUser(Long.parseLong(user_id));
			item = new SummaryItem(userCursor.getString(1),userCursor.getString(2),amount);
			
			exp.add(item);
			
			if ( avg_exp < Float.parseFloat(amount) ){
				float pers_recv = Float.parseFloat(amount) - avg_exp;
				item = new SummaryItem(userCursor.getString(1),userCursor.getString(2),String.valueOf(pers_recv));
				recv.add(item);
			}
		}
		
	}

}
