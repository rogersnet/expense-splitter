package com.exsplit;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ViewUserActivity extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private ExSplitDbAdapter mDbHelper;
	private Cursor mUserCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list);

		mDbHelper = new ExSplitDbAdapter(this);
		mDbHelper.open();

		fillUsersData();
		registerForContextMenu(getListView());
	}

	@SuppressWarnings("deprecation")
	private void fillUsersData() {

		// fetch all users
		mUserCursor = mDbHelper.fetchAllUsers();
		startManagingCursor(mUserCursor);

		// create an array to specify the fields we want in the list
		String[] from = new String[] { ExSplitDbAdapter.USER_FNAME,
				ExSplitDbAdapter.USER_LNAME, ExSplitDbAdapter.USER_EMAIL };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.first_name, R.id.last_name, R.id.email };

		SimpleCursorAdapter users = new SimpleCursorAdapter(this,
				R.layout.user_list_item, mUserCursor, from, to);
		setListAdapter(users);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_adduser).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			addUser();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_deluser);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mDbHelper.deleteUser(info.id);
			fillUsersData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void addUser() {
		Intent i = new Intent(this, AddUserActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == ACTIVITY_CREATE && resultCode == RESULT_OK) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				String first_name = extras
						.getString(ExSplitDbAdapter.USER_FNAME);
				String last_name = extras
						.getString(ExSplitDbAdapter.USER_LNAME);
				String email = extras.getString(ExSplitDbAdapter.USER_EMAIL);
				mDbHelper.add_user(first_name, last_name, email);
				fillUsersData();
			}
		}
	}
}
