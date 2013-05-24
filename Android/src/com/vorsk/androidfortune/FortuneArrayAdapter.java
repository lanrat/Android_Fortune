package com.vorsk.androidfortune;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
    	LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.history_row, parent, false);

		TextView timeText = (TextView) rowView.findViewById(R.id.time);
		TextView bodyText = (TextView) rowView.findViewById(R.id.body);
		
		TextView upvoteCountText = (TextView) rowView.findViewById(R.id.upvote_count);
		TextView downvoteCountText = (TextView) rowView.findViewById(R.id.downvote_count);
		
		rowView.findViewById(R.id.row_text).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Fortune fortune = fortunes[position];
				
				final Dialog dialog = new Dialog(getContext());
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
		
		rowView.findViewById(R.id.up_row).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				fortunes[position].upvote();
				
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				View rowView = inflater.inflate(R.layout.history_row, null);
				
				TextView upvoteCountText = (TextView) rowView.findViewById(R.id.upvote_count);
				upvoteCountText.setText(Integer.toString(fortunes[position].getUpvotes()));
				
				// create dialog 
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
				fortunes[position].downvote();
				
				// TODO need to get downvote_count text view from here to update count
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				View rowView = inflater.inflate(R.layout.history_row, null);
				
				TextView downvoteCountText = (TextView) rowView.findViewById(R.id.downvote_count);
				if(downvoteCountText != null)
					downvoteCountText.setText(Integer.toString(fortunes[position].getDownvotes()));
				
					
				
				// create dialog
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
		upvoteCountText.setText(Integer.toString(fortunes[position].getUpvotes()));
		downvoteCountText.setText(Integer.toString(fortunes[position].getDownvotes()));
		
		return rowView;
    	
    }
}