package com.exsplit;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddUserActivity extends Activity {
	public static final String PREFS_NAME = "ExSplitPref";
	
	private EditText mFirstNameText;
	private EditText mLastNameText;
	private EditText mEmailText;
	private Long mRowId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_user);

		mFirstNameText = (EditText) findViewById(R.id.EditText_Firstname);
		mLastNameText  = (EditText) findViewById(R.id.EditText_Lastname);
		mEmailText     = (EditText) findViewById(R.id.EditText_Email);
 		
		Button confirmButton = (Button) findViewById(R.id.Button_Confirm);
		
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String first_name = extras.getString(ExSplitDbAdapter.USER_FNAME);
			String last_name  = extras.getString(ExSplitDbAdapter.USER_LNAME);
			String email      = extras.getString(ExSplitDbAdapter.USER_EMAIL);
		    mRowId 			  = extras.getLong(ExSplitDbAdapter.USER_ID);

		    if (first_name != null) {
		    	mFirstNameText.setText(first_name);
		    }
		    if (last_name != null) {
		    	mLastNameText.setText(last_name);
		    }		    
		    if (email != null) {
		        mEmailText.setText(email);
		    }
		}	
	
		confirmButton.setOnClickListener(new View.OnClickListener() {

	        public void onClick(View view) {
	            Bundle bundle = new Bundle();

	            bundle.putString(ExSplitDbAdapter.USER_FNAME, mFirstNameText.getText().toString());
	            bundle.putString(ExSplitDbAdapter.USER_LNAME, mLastNameText.getText().toString());
	            bundle.putString(ExSplitDbAdapter.USER_EMAIL, mEmailText.getText().toString());
	            if (mRowId != null) {
	                bundle.putLong(ExSplitDbAdapter.USER_ID, mRowId);
	            }

	            Intent mIntent = new Intent();
	            mIntent.putExtras(bundle);
	            setResult(RESULT_OK, mIntent);
	            finish();
	        }
	    });		
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		JSONObject objMap = new JSONObject();
		try {
			objMap.put(ExSplitDbAdapter.USER_FNAME,mFirstNameText.getText().toString());
			objMap.put(ExSplitDbAdapter.USER_LNAME,mLastNameText.getText().toString());
			objMap.put(ExSplitDbAdapter.USER_EMAIL,mEmailText.getText().toString());			
		} catch (JSONException e) {
			//do nothing
		}

		SharedPreferences objStateSavedInstance = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = objStateSavedInstance.edit();
		editor.putString("USER", objMap.toString());
		editor.commit();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences objStateSavedInstance = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String objectState = objStateSavedInstance.getString("USER","");
		try {
			JSONObject objMap  = new JSONObject(objectState);
			mFirstNameText.setText(objMap.get(ExSplitDbAdapter.USER_FNAME).toString());
			mLastNameText.setText(objMap.get(ExSplitDbAdapter.USER_LNAME).toString());
			mEmailText.setText(objMap.get(ExSplitDbAdapter.USER_EMAIL).toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
	}
}
