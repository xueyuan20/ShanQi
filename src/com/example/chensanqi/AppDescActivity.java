package com.example.chensanqi;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AppDescActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		WebView wv = new WebView(this);
		setContentView(wv);
		wv.loadUrl("file:///android_asset/rules.html");
	}
}
