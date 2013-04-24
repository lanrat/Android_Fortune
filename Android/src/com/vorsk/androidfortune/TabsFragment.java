package com.vorsk.androidfortune;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;


public class TabsFragment extends SherlockFragmentActivity {
    ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_tabs_pager);

        mViewPager = (ViewPager)findViewById(R.id.pager);
        

        // This block thanks to http://stackoverflow.com/q/9790279/517561
        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        //

        mTabsAdapter = new TabsAdapter(this, mViewPager);

        mTabsAdapter.addTab("left", "History", 
        		HomeFragmentActivity.HomeFragment.class, null);
        mTabsAdapter.addTab("home", "Fortune",
        		HomeFragmentActivity.HomeFragment.class, null);
        mTabsAdapter.addTab("right", "Submit",
        		HomeFragmentActivity.HomeFragment.class, null);
        
        //0-based so 1 is the second tab
        mViewPager.setCurrentItem(1);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//TODO possibly move this to an actionbar item
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
        	Log.v("Tabs","Settings selected");
        	//TODO Menu Activity
            //startActivity(new Intent(this, SetPreference.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }

    }

    
    
    
    
    //TODO move this function
	  public void createNotification(View view) {
		  
		  Log.v("TAG","Button pressed");
		  
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
	    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, pendingFlag);
	    PendingIntent pIntentUp = PendingIntent.getActivity(this, 1, intentUp, pendingFlag);
	    PendingIntent pIntentDown = PendingIntent.getActivity(this, 2, intentDown, pendingFlag);
	    
	    
	    // Build notification
	    // Actions are just fake
	    Notification noti = new Notification.Builder(this)
	        .setContentTitle(getResources().getString(R.string.notification_title))
	        .setContentText(getResources().getString(R.string.fortune)).setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent)
	        .addAction(R.drawable.up, "Upvote", pIntentUp)
	        .addAction(R.drawable.down, "Downvote", pIntentDown).build();
	    NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
	    // Hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;

	    notificationManager.notify(0, noti);

	  }
    
    
    
    
    
    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between pages.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct paged in the ViewPager whenever the selected
     * tab changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter
            implements ViewPager.OnPageChangeListener, ActionBar.TabListener {
        private final SherlockFragmentActivity mContext;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
        	//tags is not currently used
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
        
        public void addTab(String tag, CharSequence label, Class<?> clss, Bundle args) {
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
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mContext.getSupportActionBar().setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        /* (non-Javadoc)
         * @see com.actionbarsherlock.app.ActionBar.TabListener#onTabSelected(com.actionbarsherlock.app.ActionBar.Tab, android.support.v4.app.FragmentTransaction)
         */
        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
          mViewPager.setCurrentItem(mContext.getSupportActionBar().getSelectedNavigationIndex());
        }

        /* (non-Javadoc)
         * @see com.actionbarsherlock.app.ActionBar.TabListener#onTabUnselected(com.actionbarsherlock.app.ActionBar.Tab, android.support.v4.app.FragmentTransaction)
         */
        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {          
        }

        /* (non-Javadoc)
         * @see com.actionbarsherlock.app.ActionBar.TabListener#onTabReselected(com.actionbarsherlock.app.ActionBar.Tab, android.support.v4.app.FragmentTransaction)
         */
        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {          
        }
    }
}
