package com.exsplit;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ViewExpenseActivity extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private ExSplitDbAdapter mDbHelper;
	private Cursor mExpCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exp_list);

		mDbHelper = new ExSplitDbAdapter(this);
		mDbHelper.open();

		fillExpenseData();
		registerForContextMenu(getListView());
	}

	@SuppressWarnings("deprecation")
	private void fillExpenseData() {

		// fetch all users
		mExpCursor = mDbHelper.fetchAllExpenses();
		startManagingCursor(mExpCursor);

		ExpenseCursorAdapter expAdapter = new ExpenseCursorAdapter(this,
				mExpCursor, mDbHelper);
		setListAdapter(expAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.add_exp_item).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			addExpense();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delexp);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mDbHelper.deleteExpensItem(info.id);
			fillExpenseData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void addExpense() {
		Intent i = new Intent(this, AddExpenseActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == ACTIVITY_CREATE && resultCode == RESULT_OK) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				String user_id = extras.getString(ExSplitDbAdapter.EXP_USER_ID);
				String cat_id = extras.getString(ExSplitDbAdapter.EXP_CAT_ID);
				String exp_date = extras.getString(ExSplitDbAdapter.EXP_DATE);
				String exp_amount = extras
						.getString(ExSplitDbAdapter.EXP_AMOUNT);
				mDbHelper.create_expense_item(Integer.parseInt(user_id),
						Integer.parseInt(cat_id), exp_date,
						Float.parseFloat(exp_amount));
				fillExpenseData();
			}
		}
	}

	private class ExpenseCursorAdapter extends CursorAdapter {
		ExSplitDbAdapter mExDb;

		public ExpenseCursorAdapter(Context ctx, Cursor c, ExSplitDbAdapter mDb) {
			super(ctx, c);
			mExDb = mDb;
		}

		@Override
		public void bindView(View view, Context ctx, Cursor cursor) {
			String user_id = cursor.getString(1);
			String cat_id = cursor.getString(2);
			String amount = cursor.getString(4);

			Cursor userCursor = mExDb.fetchUser(Long.parseLong(user_id));

			TextView view_fname = (TextView) view.findViewById(R.id.first_name);
			view_fname.setText(userCursor.getString(1));

			TextView view_lname = (TextView) view.findViewById(R.id.last_name);
			view_lname.setText(userCursor.getString(2));

			Cursor categCursor = mExDb.fetchCateg(Long.parseLong(cat_id));

			TextView view_categ = (TextView) view.findViewById(R.id.category);
			view_categ.setText(categCursor.getString(1));

			TextView view_amount = (TextView) view.findViewById(R.id.amount);
			view_amount.setText(amount);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			return inflater.inflate(R.layout.exp_list_item, parent, false);
		}
	}

}
