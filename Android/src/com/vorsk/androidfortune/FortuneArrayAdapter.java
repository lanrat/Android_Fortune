package com.vorsk.androidfortune;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/*
 * Adapted from http://stackoverflow.com/questions/2109271/can-any-one-provide-me-example-of-two-line-list-item-in-android
 */
public class FortuneArrayAdapter extends ArrayAdapter<Fortune> {
	
	private final Fortune[] fortunes;
	private final Context context;
	
    public FortuneArrayAdapter(Context context, Fortune[] fortunes) {
        super(context, R.layout.history_row, fortunes);
        this.fortunes = fortunes;
        this.context = context;
    }

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
    	LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.history_row, parent, false);

		TextView timeText = (TextView) rowView.findViewById(R.id.time);
		TextView bodyText = (TextView) rowView.findViewById(R.id.body);
		rowView.findViewById(R.id.up_row).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(getContext());
				dialog.setContentView(R.layout.history_vote_dialog);
				dialog.setTitle("Up Vote");
				TextView text = (TextView) dialog.findViewById(R.id.vote_dialog);
				text.setText("UPVOTE");
				dialog.setCancelable(true);
				dialog.show();
			}
			
		});
		rowView.findViewById(R.id.down_row).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(getContext());
				dialog.setContentView(R.layout.history_vote_dialog);
				dialog.setTitle("Down Vote");
				TextView text = (TextView) dialog.findViewById(R.id.vote_dialog);
				text.setText("DOWNVOTE");
				dialog.setCancelable(true);
				dialog.show();
			}
			
		});

		timeText.setText(fortunes[position].getSeen().toString());
		bodyText.setText(fortunes[position].getFortuneText(false));
		
		return rowView;
    	
    }
}