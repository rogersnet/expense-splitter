package com.exsplit;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ViewCategActivity extends ListActivity {

	private static final int ACTIVITY_CREATE = 0;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private ExSplitDbAdapter mDbHelper;
	private Cursor mCategCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categ_list);

		mDbHelper = new ExSplitDbAdapter(this);
		mDbHelper.open();

		fillCategData();
		registerForContextMenu(getListView());
	}

	@SuppressWarnings("deprecation")
	private void fillCategData() {

		// fetch all users
		mCategCursor = mDbHelper.fetchAllCategory();
		startManagingCursor(mCategCursor);

		// create an array to specify the fields we want in the list
		String[] from = new String[] { ExSplitDbAdapter.CAT_NAME };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.category };

		SimpleCursorAdapter categories = new SimpleCursorAdapter(this,
				R.layout.categ_list_item, mCategCursor, from, to);
		setListAdapter(categories);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_addcateg).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			addCategory();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delcat).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mDbHelper.deleteCategory(info.id);
			fillCategData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void addCategory() {
		Intent i = new Intent(this, AddCategActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == ACTIVITY_CREATE && resultCode == RESULT_OK) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				String categ_name = extras.getString(ExSplitDbAdapter.CAT_NAME);
				mDbHelper.add_category(categ_name);
				fillCategData();
			}
		}
	}
}
