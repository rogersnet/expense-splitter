package com.exsplit;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class MenuActivity extends Activity {
	private GridViewAdapter mAdapter;
    private ArrayList<Integer> listMenuItem;
    private ArrayList<Integer> listImage;	
	
    private GridView gridView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		prepareList();

		mAdapter = new GridViewAdapter(this,listMenuItem, listImage);
		 
        // Set custom adapter to gridview
        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);
        
        gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Integer menuItemSelected = (Integer) mAdapter.getItem(arg2);
				switch(menuItemSelected){
				case R.string.item_view_user:
					startActivity(new Intent(MenuActivity.this,
							ViewUserActivity.class));
					break;
				case R.string.item_view_categ:
					startActivity(new Intent(MenuActivity.this,
							ViewCategActivity.class));
					break;
				case R.string.item_view_exp:
					startActivity(new Intent(MenuActivity.this,
							ViewExpenseActivity.class));
					break;
				case R.string.item_gen_rep:
					startActivity(new Intent(MenuActivity.this,
							GenerateReportActivity.class));
					break;
				case R.string.item_settings:
					startActivity(new Intent(MenuActivity.this,
							SettingsActivity.class));
					break;
				case R.string.item_about:
					startActivity(new Intent(MenuActivity.this,
							AboutExSplitActivity.class));
					break;
				}
			}
        	
		});        
	}
	
	private void prepareList()
	{
		listMenuItem = new ArrayList<Integer>();
		listImage    = new ArrayList<Integer>();
		
		
		listMenuItem.add(R.string.item_view_user);
		listMenuItem.add(R.string.item_view_categ);
		listMenuItem.add(R.string.item_view_exp);
		listMenuItem.add(R.string.item_gen_rep);
		listMenuItem.add(R.string.item_settings);
		listMenuItem.add(R.string.item_about);
		
		listImage.add(R.drawable.user);
		listImage.add(R.drawable.category);
		listImage.add(R.drawable.expenses);
		listImage.add(R.drawable.reports);
		listImage.add(R.drawable.settings);
		listImage.add(R.drawable.about);
	}

	private class GridViewAdapter extends BaseAdapter{
		private ArrayList<Integer> listMenuItem;
	    private ArrayList<Integer> listImage;
	    private Activity activity;
	    
		public GridViewAdapter(Activity activity, ArrayList<Integer> menuItem, ArrayList<Integer> image){
			super();
			this.activity 	  = activity;
			this.listMenuItem = menuItem;
			this.listImage    = image;
		}

		@Override
		public int getCount() {			
			return listMenuItem.size();
		}

		@Override
		public Object getItem(int position) {
			return listMenuItem.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	        LayoutInflater inflator = activity.getLayoutInflater();
	        ViewHolder view;

	        if(convertView==null)
	        {
	        	view = new ViewHolder();
	            convertView = inflator.inflate(R.layout.menu_item, null);
	 
	            view.txtViewTitle = (TextView) convertView.findViewById(R.id.textView_menuItem);
	            view.imgViewIcon  = (ImageView) convertView.findViewById(R.id.imageView01);
	 
	            convertView.setTag(view);
	        }
	        else
	        {
	            view = (ViewHolder) convertView.getTag();
	        }
	 
	        view.txtViewTitle.setText(getString(listMenuItem.get(position)));
	        view.imgViewIcon.setImageResource(listImage.get(position));
	 
	        return convertView;
		}
	}
	
	public static class ViewHolder{
        TextView txtViewTitle;
        ImageView imgViewIcon;		
	}

}
