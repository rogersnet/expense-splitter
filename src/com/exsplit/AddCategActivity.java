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

public class AddCategActivity extends Activity {
	public static final String PREFS_NAME = "ExSplitPref";
	private EditText mCategNameText;
	private Long mRowId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_categ);
		
		mCategNameText = (EditText) findViewById(R.id.EditText_CategName);
		
		Button confirmationButton = (Button) findViewById(R.id.Button_Confirm);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null ){
			String categ_name = extras.getString(ExSplitDbAdapter.CAT_NAME);
			mRowId            = extras.getLong(ExSplitDbAdapter.CAT_ID);
			
			if ( categ_name !=  null ){
				mCategNameText.setText(categ_name);
			}
		}
		confirmationButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				
				bundle.putString(ExSplitDbAdapter.CAT_NAME,mCategNameText.getText().toString());
				
				if ( mRowId != null ){
					bundle.putLong(ExSplitDbAdapter.CAT_ID, mRowId);
				}
				
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK,mIntent);
				finish();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();

		SharedPreferences objStateSavedInstance = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = objStateSavedInstance.edit();
		editor.putString("CATEGORY", mCategNameText.getText().toString());
		editor.commit();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences objStateSavedInstance = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String objectState = objStateSavedInstance.getString("CATEGORY","");
		mCategNameText.setText(objectState);			
	}
}
