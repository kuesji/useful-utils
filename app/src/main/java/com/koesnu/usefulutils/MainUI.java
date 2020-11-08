package com.koesnu.usefulutils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.koesnu.usefulutils.utils.Copy2Clipboard;
import com.koesnu.usefulutils.utils.UrlRouter;

import java.util.ArrayList;
import java.util.List;

public class MainUI extends RelativeLayout {

	public ScrollView scroller;
	public LinearLayout content;

	public MainUI(Context ctx){
		super(ctx);

		setBackgroundColor(0xff000000);

		scroller = new ScrollView(ctx);
		scroller.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		scroller.setFillViewport(true);
		scroller.setOverScrollMode(OVER_SCROLL_NEVER);
		scroller.setVerticalFadingEdgeEnabled(false);
		scroller.setHorizontalFadingEdgeEnabled(false);
		scroller.setVerticalScrollBarEnabled(false);
		scroller.setHorizontalScrollBarEnabled(false);
		addView(scroller);

		content = new LinearLayout(ctx);
		content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		content.setOrientation(LinearLayout.VERTICAL);
		scroller.addView(content);

		load_content();
	}

	public void load_content(){
		content.removeAllViews();

		String disable = getContext().getString(R.string.state_disable);
		String enable = getContext().getString(R.string.state_enable);

		for(final UtilEntry entry : get_entries()){
			entry_view view = new entry_view(getContext());
			view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			view.label.setText("\n"+entry.label);
			view.description.setText(entry.description+"\n");
			view.state.setText( entry.active ? disable : enable);
			view.state.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if( entry.active ){
						getContext().getPackageManager().setComponentEnabledSetting(entry.component,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
					} else {
						getContext().getPackageManager().setComponentEnabledSetting(entry.component,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
					}
					load_content();
				}
			});
			content.addView(view);

		}
	}

	public List<UtilEntry> get_entries (){
		List<UtilEntry> result = new ArrayList<>();

		UtilEntry entry;

		entry = new UtilEntry();
		entry.label = getContext().getString(R.string.name_copy2clipboard);
		entry.description = getContext().getString(R.string.description_copy2clipboard);
		entry.component = new ComponentName(getContext().getPackageName(), Copy2Clipboard.class.getName());
		entry.active = getContext().getPackageManager().getComponentEnabledSetting(entry.component) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ? true : false;
		result.add(entry);

		entry = new UtilEntry();
		entry.label = getContext().getString(R.string.name_url_router);
		entry.description = getContext().getString(R.string.description_url_router);
		entry.component = new ComponentName(getContext().getPackageName(), UrlRouter.class.getName());
		entry.active = getContext().getPackageManager().getComponentEnabledSetting(entry.component) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ? true : false;
		result.add(entry);


		return result;
	}


	class entry_view extends LinearLayout {

		TextView label;
		TextView description;
		Button state;

		public entry_view(Context ctx){
			super(ctx);

			setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
			setOrientation(VERTICAL);
			setBackgroundColor(0xff000000);

			label = new TextView(ctx);
			label.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			label.setTextColor(0xffeeeeee);
			label.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
			addView(label);

			description = new TextView(ctx);
			description.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			description.setTextColor(0xffdddddd);
			description.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
			addView(description);

			state = new Button(ctx);
			state.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			state.setBackgroundColor(0xffeeeeee);
			state.setTextColor(0xff000000);
			state.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
			state.setText("enable/disable");
			addView(state);
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			super.onLayout(changed, l, t, r, b);

			if(changed){
				LayoutParams lp;

				lp = (LayoutParams) state.getLayoutParams();
				lp.width = getWidth() / 10 * 6;
				state.setLayoutParams(lp);

				int padding = getWidth() / 20;

				setLeft(padding);
			}
		}
	}

	class UtilEntry {
		ComponentName component;
		String label;
		String description;
		boolean active;

		public UtilEntry(){}
		public UtilEntry(ComponentName component, String description){
			this.component = component;
			this.description = description;
			this.active = getContext().getPackageManager().getComponentEnabledSetting(component) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED  ? true : false;
		}
	}
}
