package com.vorsk.androidfortune;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class TabsFragment extends SherlockFragmentActivity {
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	Client client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_tabs_pager);

		//create the client TODO update the singleton instance ctor to model the DB
		this.client = Client.getInstance(getApplicationContext());
		
		mViewPager = (ViewPager) findViewById(R.id.pager);
		
		// This block thanks to http://stackoverflow.com/q/9790279/517561
		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayShowHomeEnabled(true);

		mTabsAdapter = new TabsAdapter(this, mViewPager);

		mTabsAdapter.addTab("left", "History",
				HistoryActivity.HistoryFragment.class, null);
		mTabsAdapter.addTab("home", "Fortune",
				FortuneActivity.FortuneFragment.class, null);
		mTabsAdapter.addTab("right", "Submit",
				SubmitActivity.SubmitFragment.class, null);

		// 0-based so 1 is the second tab
		mViewPager.setCurrentItem(1);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// actionbar menu
		getSupportMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void onResume() {
		super.onResume();
		Log.v("NotificationService","setting alarm");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Intent intent = new Intent(this, NotificationReceiver.class);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
	        intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	    am.cancel(pendingIntent);
		if ( prefs.getBoolean("pref_enable_notification",false) ) {
		    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
		       AlarmManager.INTERVAL_DAY, pendingIntent);
		}
	}

	// TODO move this function
	public void createNotification(View view) {

		Log.v("TAG", "Button pressed");

		// Prepare intent which is triggered if the
		// notification is selected
		int pendingFlag = PendingIntent.FLAG_ONE_SHOT;
		int intentFlag = Intent.FLAG_ACTIVITY_CLEAR_TOP;

		Intent intent = new Intent(this, NotificationReceiverActivity.class);
		intent.setFlags(intentFlag);
		intent.putExtra("action", "Click");
		Intent intentUp = new Intent(this, NotificationReceiverActivity.class);
		intent.setFlags(intentFlag);
		intentUp.putExtra("action", "Upvote");
		Intent intentDown = new Intent(this, NotificationReceiverActivity.class);
		intent.setFlags(intentFlag);
		intentDown.putExtra("action", "Downvote");
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
				pendingFlag);
		PendingIntent pIntentUp = PendingIntent.getActivity(this, 1, intentUp,
				pendingFlag);
		PendingIntent pIntentDown = PendingIntent.getActivity(this, 2,
				intentDown, pendingFlag);

		// Build notification
		// Actions are just fake
		Notification noti = new NotificationCompat.Builder(this)
				.setContentTitle(
						getResources().getString(R.string.notification_title))
				.setContentText(getResources().getString(R.string.fortune))
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
				.addAction(R.drawable.arrow_up, "Upvote", pIntentUp)
				.addAction(R.drawable.arrow_down, "Downvote", pIntentDown)
				.build();
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, noti);
	}
	

	// Everything below this line is part of the tabs view pager api.
	// It should not be modified.

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	public static class TabsAdapter extends FragmentPagerAdapter implements
			ViewPager.OnPageChangeListener, ActionBar.TabListener {
		private final SherlockFragmentActivity mContext;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			// tags is not currently used
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(String tag, CharSequence label, Class<?> clss,
				Bundle args) {
			ActionBar.Tab tab = mContext.getSupportActionBar().newTab();
			tab.setText(label);
			tab.setTabListener(this);
			mContext.getSupportActionBar().addTab(tab);
			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			mContext.getSupportActionBar().setSelectedNavigationItem(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.actionbarsherlock.app.ActionBar.TabListener#onTabSelected(com
		 * .actionbarsherlock.app.ActionBar.Tab,
		 * android.support.v4.app.FragmentTransaction)
		 */
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			mViewPager.setCurrentItem(mContext.getSupportActionBar()
					.getSelectedNavigationIndex());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.actionbarsherlock.app.ActionBar.TabListener#onTabUnselected(com
		 * .actionbarsherlock.app.ActionBar.Tab,
		 * android.support.v4.app.FragmentTransaction)
		 */
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.actionbarsherlock.app.ActionBar.TabListener#onTabReselected(com
		 * .actionbarsherlock.app.ActionBar.Tab,
		 * android.support.v4.app.FragmentTransaction)
		 */
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}
}
