package com.koesnu.usefulutils.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.koesnu.usefulutils.R;

public class Copy2Clipboard extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		if( intent.getAction() == Intent.ACTION_SEND ){
			String subject = intent.getExtras().getString(Intent.EXTRA_SUBJECT);
			String text = intent.getExtras().getString(Intent.EXTRA_TEXT);

			ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			ClipData cd = ClipData.newPlainText("text",subject+'\n'+text);
			cm.setPrimaryClip(cd);

			Toast.makeText(this,getString(R.string.msg_link_copied),Toast.LENGTH_SHORT).show();
		}

		finish();
	}

}
