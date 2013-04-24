package com.vorsk.androidfortune;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;

import android.support.v4.app.FragmentManager;

public class HomeFragmentActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
        	HomeFragment list = new HomeFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    public static class HomeFragment extends SherlockListFragment {

        @Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

    }
}

