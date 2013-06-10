package com.vorsk.androidfortune;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.vorsk.androidfortune.data.Client;
import com.vorsk.androidfortune.data.Fortune;

public class TabsFragment extends SherlockFragmentActivity {
	private static ViewPager mViewPager;
	private static TabsAdapter mTabsAdapter;
	private static TabsFragment mTabsFragment;
	private static final int defaultPosition = 1;
	
	private static final String TAB_ID_LEFT = "left";
	private static final String TAB_ID_RIGHT = "right";
	private static final String TAB_ID_HOME = "home";
	
	private final static String TAG = "Tabs Fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mTabsFragment = this;
		
		setContentView(R.layout.fragment_tabs_pager);
		
		Client.getInstance(getApplicationContext());
		
		mViewPager = (ViewPager) findViewById(R.id.pager);
		
		// This block thanks to http://stackoverflow.com/q/9790279/517561
		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayShowHomeEnabled(true);

		mTabsAdapter = new TabsAdapter(this, mViewPager);

		mTabsAdapter.addTab(TAB_ID_LEFT, "History",
				HistoryActivity.HistoryFragment.class, null);
		mTabsAdapter.addTab(TAB_ID_HOME, "Fortune",
				HomeActivity.FortuneFragment.class, null);
		mTabsAdapter.addTab(TAB_ID_RIGHT, "Submit",
				SubmitActivity.SubmitFragment.class, null);

		// 0-based so 1 is the second tab
		mViewPager.setCurrentItem(defaultPosition);
		
		
		//update Fortunes
		ArrayList<Fortune> list = Client.getInstance().getSeenFortunes();
		list.addAll(Client.getInstance().getFortunesSubmitted());
		list.add(Client.getInstance().getCurrentFortune());
		
		new UpdateFortunesTask(getApplicationContext(),list).execute();
	}
	
	//TODO document and improve
	//TODO move to home activity
	public void flagFortune(){
		Fortune f = Client.getInstance().getCurrentFortune();
		//TODO ask confirmation
		if (f != null) {
			f.flag();
			new UpdateFortuneReceiver().onReceive(this, null);
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// actionbar menu
		int pos = mViewPager.getCurrentItem();
		Log.v(TAG,"Changing menu to pos: "+pos);

		switch (pos) {
		case 0:
			getSupportMenuInflater().inflate(R.menu.default_menu, menu);
			break;
		case 1:
			getSupportMenuInflater().inflate(R.menu.home_menu, menu);
			break;
		case 2:
			getSupportMenuInflater().inflate(R.menu.submit_menu, menu);
			break;
		default:
			getSupportMenuInflater().inflate(R.menu.default_menu, menu);
			break;
		}
		return true;
	}
	
	static void updateActionBar() {
		mTabsFragment.supportInvalidateOptionsMenu();

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			case R.id.action_flag_fortune:
				flagFortune();
				return true;
			case R.id.action_new_fortune:
				new UpdateFortuneReceiver().onReceive(this, null);
				return true;
			case R.id.action_submit:
				SubmitActivity.showSubmitDiolog(this);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void onResume() {
		super.onResume();
	}	
		
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
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
			@SuppressWarnings("unused")
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
			//update actionbar
			updateActionBar();
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
