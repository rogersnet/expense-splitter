package com.exsplit;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutExSplitActivity extends Activity {
	private WebView webView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_exsplit);
		
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("file:///android_res/raw/help.html");
	}

}
