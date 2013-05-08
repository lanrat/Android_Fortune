package com.vorsk.androidfortune;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class HistoryActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if (fm.findFragmentById(android.R.id.content) == null) {
			HistoryFragment fragment = new HistoryFragment();
			fm.beginTransaction().add(android.R.id.content, fragment).commit();
		}
		
	}
	
	public static class HistoryFragment extends SherlockFragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.history, null);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			/*
			//TODO fix deprecation
			// Get all of the notes from the database and create the item list
	        Cursor c = FortuneDbAdapter.getInstance(null).fetchAllFortunes();
	        getActivity().startManagingCursor(c);

	        String[] from = new String[] { FortuneDbAdapter.KEY_BODY };
	        int[] to = new int[] { android.R.id.text1 };
	        
	        // Now create an array adapter and set it to display using our row
	        SimpleCursorAdapter fortunes =
	            new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, c, from, to);
	        ListView lv = (ListView) getActivity().findViewById(R.id.history_list);
			lv.setAdapter(fortunes);
			*/
			
			Fortune[] fortunes = new Fortune[10];
			for(int i = 0; i < 10; i++) fortunes[i] = (new Fortune("Fortune " + (i + 1)));
			
			FortuneArrayAdapter adapter = new FortuneArrayAdapter(getActivity(), fortunes);
			ListView lv = (ListView) getActivity().findViewById(R.id.history_list);
			lv.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> list, View view, int position, long id) {
					// TODO Auto-generated method stub
					Fortune fortune = (Fortune)list.getAdapter().getItem(position);
					
					final Dialog dialog = new Dialog(getActivity());
			        dialog.setContentView(R.layout.history_info_dialog);
			        dialog.setTitle("Fortune Information");
			        dialog.setCancelable(true);
			        //set up text
			        TextView timeText = (TextView) dialog.findViewById(R.id.dialogTime);
			        TextView bodyText = (TextView) dialog.findViewById(R.id.dialogBody);
			        
			        timeText.setText(fortune.getDate().toString());
			        bodyText.setText(fortune.getFortune());
			        
			        dialog.show();
				}
			});
			lv.setAdapter(adapter);
			
			
			
		}
	}
}
