package com.koesnu.usefulutils.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.koesnu.usefulutils.R;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class UrlRouter extends Activity {

	UI ui;

	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);

		ui = new UI(this);
		setContentView(ui);


		Intent intent = getIntent();
		if (intent.getAction() == Intent.ACTION_VIEW) {
			final String url = intent.getDataString();
			ui.target.setText(url);

			Intent search = new Intent();
			search.setAction(Intent.ACTION_VIEW);
			search.setData(Uri.parse(url));

			List<ResolveInfo> resolves = getPackageManager().queryIntentActivities(search, PackageManager.MATCH_ALL | PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS);

			final Collator collator = Collator.getInstance(Locale.getDefault());
			Collections.sort(resolves, new Comparator<ResolveInfo>() {
				@Override
				public int compare(ResolveInfo o1, ResolveInfo o2) {
					return collator.compare(
					o1.activityInfo.loadLabel(getPackageManager()).toString().toLowerCase(Locale.getDefault()),
					o2.activityInfo.loadLabel(getPackageManager()).toString().toLowerCase(Locale.getDefault())
					);
				}
			});

			for (final ResolveInfo resolve : resolves) {
				if (resolve.activityInfo.packageName.equals(getPackageName()))
					continue;

				if (!resolve.activityInfo.exported)
					continue;

				ui.addItem(
				resolve.loadIcon(getPackageManager()),
				resolve.loadLabel(getPackageManager()).toString(),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent();
						i.setAction(Intent.ACTION_VIEW);
						i.setData(Uri.parse(url));
						i.setClassName(resolve.activityInfo.packageName, resolve.activityInfo.name);
						i.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
						startActivity(i);
						finish();
					}
				}
				);
			}
		} else {
			finish();
		}
	}

	public class UI extends LinearLayout {

		public TextView target;
		public ScrollView scroller;
		public LinearLayout scroller_content;

		public UI(Context ctx) {
			super(ctx);

			setOrientation(VERTICAL);
			setGravity(Gravity.BOTTOM | Gravity.LEFT);

			target = new TextView(ctx);
			target.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			target.setText("url");
			target.setTextSize(16);
			target.setTextColor(0xffeeeeee);
			target.setGravity(Gravity.CENTER);
			target.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData data = ClipData.newPlainText("text", target.getText().toString());
					cm.setPrimaryClip(data);
					Toast.makeText(getContext(), getString(R.string.msg_link_copied), Toast.LENGTH_LONG).show();
				}
			});
			addView(target);

			scroller = new ScrollView(ctx);
			scroller.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			scroller.setFillViewport(true);
			scroller.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
			scroller.setHorizontalFadingEdgeEnabled(false);
			scroller.setVerticalFadingEdgeEnabled(false);
			addView(scroller);

			scroller_content = new LinearLayout(ctx);
			scroller_content.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			scroller_content.setOrientation(VERTICAL);
			scroller_content.setGravity(Gravity.BOTTOM | Gravity.LEFT);
			scroller.addView(scroller_content);
		}

		public boolean onTouchEvent(MotionEvent event) {
			super.onTouchEvent(event);
			return true;
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			super.onLayout(changed, l, t, r, b);

			int w = r - l;
			int h = b - t;
			int pad = (h > w) ? w / 100 * 5 : h / 100 * 5;

			if (changed) {
				setPadding(pad, pad, pad, pad);
				target.setMaxWidth(w - pad * 2);

				LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(w, h / 8);
				LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ilp.height, ilp.height);
				for (int i = 0; i < scroller_content.getChildCount(); i++) {
					Item item = (Item) scroller_content.getChildAt(i);
					item.setLayoutParams(ilp);
					item.logo.setLayoutParams(llp);
					item.logo.setPadding(pad, pad, pad, pad);
				}
			}
		}

		public void addItem(Drawable logo, String label, OnClickListener listener) {
			Item item = new Item(getContext());
			item.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			item.setOnClickListener(listener);
			item.logo.setImageDrawable(logo);
			item.label.setText(label);
			scroller_content.addView(item);
		}

		public void clearItems() {
			scroller_content.removeAllViews();
		}

		public class Item extends LinearLayout {

			public ImageView logo;
			public TextView label;

			public Item(Context ctx) {
				super(ctx);

				setOrientation(HORIZONTAL);
				setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

				logo = new ImageView(ctx);
				logo.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
				logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				addView(logo);

				label = new TextView(ctx);
				label.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				label.setTextSize(16);
				label.setTextColor(0xffeeeeee);
				label.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				addView(label);
			}

		}
	}
}
