package com.koesnu.usefulutils;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	MainUI ui;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ui = new MainUI(this);
		setContentView(ui);
	}
}
