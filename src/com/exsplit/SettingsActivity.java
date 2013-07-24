package com.exsplit;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsActivity extends Activity {
	public static final String PREFS_NAME = "ExSplitPref";
	SharedPreferences mApplSettings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		// Populate Spinner control with genders
		mApplSettings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		final Spinner spinner = (Spinner) findViewById(R.id.Spinner_Currency);
		ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this,
				R.array.currency, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		if (mApplSettings.contains("CURRENCY")){
			spinner.setSelection(mApplSettings.getInt("CURRENCY", 0));
		}
		
		// Handle spinner selections
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent,
					View itemSelected, int selectedItemPosition, long selectedId) {
				Editor editor = mApplSettings.edit();
				editor.putInt("CURRENCY", selectedItemPosition);
				editor.commit();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});		
	}
	
}
