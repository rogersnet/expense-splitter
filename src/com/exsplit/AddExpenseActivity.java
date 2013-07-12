package com.exsplit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddExpenseActivity extends Activity {
	public static final String PREFS_NAME = "ExSplitPref";	
	static final int DATE_DIALOG_ID = 0;

	private Spinner mExpCategory;
	private Spinner mSpentBy;
	private TextView mDate;
	private EditText mExpAmount;
	private Button mPickDate;
	private Button mDone;

	private ExSplitDbAdapter mDbHelper;
	private Cursor mUserCursor;
	private Cursor mCategCursor;
	
	private String mSelCategory;
	private String mSelUser;
	
	private int mSelCatPos;
	private int mSelUserPos;
	
	private SparseIntArray mCatMap;
	private SparseIntArray mUsrMap;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_expense);

		mDbHelper = new ExSplitDbAdapter(this);
		mDbHelper.open();

		// get the expense category spinner control
		mExpCategory = (Spinner) findViewById(R.id.Spinner_Category);
		mCatMap = new SparseIntArray();
		mUsrMap = new SparseIntArray();

		// get the users spinner control
		mSpentBy = (Spinner) findViewById(R.id.Spinner_Spentby);

		loadSpinnerData();

		// get the date picker control
		initDateEntry();
		
		// get the amount edit text control
		mExpAmount = (EditText) findViewById(R.id.EditText_Amount);
		
		// get the done button control
		mDone = (Button) findViewById(R.id.Button_Done);
		mDone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				TextView doe = (TextView) findViewById(R.id.TextView_Date_Info);
				String date = doe.getText().toString();
				
				Bundle bundle = new Bundle();
				bundle.putString(ExSplitDbAdapter.EXP_USER_ID, String.valueOf(mSelUserPos));
				bundle.putString(ExSplitDbAdapter.EXP_CAT_ID, String.valueOf(mSelCatPos));
				bundle.putString(ExSplitDbAdapter.EXP_DATE, date);
				bundle.putString(ExSplitDbAdapter.EXP_AMOUNT, mExpAmount.getText().toString());

	            Intent mIntent = new Intent();
	            mIntent.putExtras(bundle);
	            setResult(RESULT_OK, mIntent);
	            finish();
			}
		});
	}

	private void loadSpinnerData() {
		
		int index = 0;

		// fill the category spinner data
		mCategCursor = mDbHelper.fetchAllCategory();

		List<String> cat_labels = new ArrayList<String>();

		if (mCategCursor.moveToFirst()) {
			do {
				cat_labels.add(mCategCursor.getString(1));
				mCatMap.put(index,mCategCursor.getInt(0));
				index++;
			} while (mCategCursor.moveToNext());
		}

		// Creating adapter for category spinner
		ArrayAdapter<String> dataAdapter01 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, cat_labels);
		dataAdapter01
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mExpCategory.setAdapter(dataAdapter01);
		
		//set onitem selected listener
		mExpCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mSelCategory = parent.getItemAtPosition(position).toString();
				mSelCatPos   = mCatMap.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		index = 0;
		
		// fill the users spinner data
		mUserCursor = mDbHelper.fetchAllUsers();

		List<String> user_labels = new ArrayList<String>();	
		if (mUserCursor.moveToFirst()) {
			do {
				String userLabel = mUserCursor.getString(1).concat(" ")
						.concat(mUserCursor.getString(2));
				user_labels.add(userLabel);
				mUsrMap.put(index, mUserCursor.getInt(0));
				index++;
			} while (mUserCursor.moveToNext());
		}
		mUserCursor.close();

		ArrayAdapter<String> dataAdapter02 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, user_labels);
		dataAdapter02
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpentBy.setAdapter(dataAdapter02);

		//set listener for users
		mSpentBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mSelUser    = parent.getItemAtPosition(position).toString();	
				mSelUserPos = mUsrMap.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

	}

	@Deprecated
	private void initDateEntry() {
		// Handle date picking dialog
		mPickDate = (Button) findViewById(R.id.Button_Date);
		mPickDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
		if (id == DATE_DIALOG_ID) {
			final TextView doe = (TextView) findViewById(R.id.TextView_Date_Info);
			DatePickerDialog dateDialog = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							Time dateOfEntry = new Time();
							dateOfEntry.set(dayOfMonth, monthOfYear, year);
							long dtDoe = dateOfEntry.toMillis(true);
							doe.setText(DateFormat.format("MMMM dd, yyyy",
									dtDoe));
						}
					}, 0, 0, 0);
			return dateDialog;
		}
		return null;
	}

	@Override
	@Deprecated
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if (id == DATE_DIALOG_ID) {
			// Handle any DatePickerDialog initialization here
			DatePickerDialog dateDialog = (DatePickerDialog) dialog;
			int iDay, iMonth, iYear;

			Calendar cal = Calendar.getInstance();
			
			// Today's date fields
			iDay = cal.get(Calendar.DAY_OF_MONTH);
			iMonth = cal.get(Calendar.MONTH);
			iYear = cal.get(Calendar.YEAR);
			
			// Set the date in the DatePicker to the date of birth OR to the
			// current date
			dateDialog.updateDate(iYear, iMonth, iDay);
			return;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		JSONObject objMap = new JSONObject();
		try {
			TextView doe = (TextView) findViewById(R.id.TextView_Date_Info);
			String date = doe.getText().toString();
			objMap.put(ExSplitDbAdapter.EXP_USER_ID, mSelUserPos);
			objMap.put(ExSplitDbAdapter.EXP_CAT_ID, mSelCatPos);
			objMap.put(ExSplitDbAdapter.EXP_DATE, date);
			objMap.put(ExSplitDbAdapter.EXP_AMOUNT, mExpAmount.getText().toString());
		} catch (JSONException e) {
			//do nothing
		}

		SharedPreferences objStateSavedInstance = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = objStateSavedInstance.edit();
		editor.putString("EXPENSE", objMap.toString());
		editor.commit();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences objStateSavedInstance = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String objectState = objStateSavedInstance.getString("EXPENSE","");
		try {
			JSONObject objMap  = new JSONObject(objectState);
			mSelUserPos = objMap.getInt(ExSplitDbAdapter.EXP_USER_ID);
			mSelCatPos  = objMap.getInt(ExSplitDbAdapter.EXP_CAT_ID);
			mDate.setText(objMap.getString(ExSplitDbAdapter.EXP_DATE));
			mExpAmount.setText(objMap.getString(ExSplitDbAdapter.EXP_AMOUNT));
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
	}	
}
