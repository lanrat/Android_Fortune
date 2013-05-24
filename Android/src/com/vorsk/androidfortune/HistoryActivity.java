package com.vorsk.androidfortune;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
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
	
	public static class HistoryFragment extends SherlockFragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.history, null);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
		
			ArrayList<Fortune> list = Client.getInstance().getSeenFortunes(); 
			Fortune[] fortunes = list.toArray(new Fortune[list.size()]);			
			
			FortuneArrayAdapter adapter = new FortuneArrayAdapter(getActivity(), fortunes);
			ListView lv = (ListView) getActivity().findViewById(R.id.history_list);
			
				
			/*
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
			        
			        timeText.setText(fortune.getSeen().toString());
			        bodyText.setText(fortune.getFortuneText(false));
			        
			        dialog.show();
				}
			});
			*/
			lv.setAdapter(adapter);	
			
		}
	}
}
