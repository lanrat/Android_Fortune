package com.vorsk.androidfortune;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vorsk.androidfortune.data.Fortune;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
			public void onClick(View view) {
				Fortune fortune = fortunes[position];
				
				final Dialog dialog = new Dialog(getContext());
		        dialog.setContentView(R.layout.history_info_dialog);
		        dialog.setTitle("Fortune Information");
		        dialog.setCancelable(true);
		        
		        // Get views
		        TextView timeText = (TextView) dialog.findViewById(R.id.dialogTime);
		        TextView bodyText = (TextView) dialog.findViewById(R.id.dialogBody);
		        TextView voteText = (TextView) dialog.findViewById(R.id.dialogVote);
		        TextView upvotesText = (TextView) dialog.findViewById(R.id.dialogUpvotes);
		        TextView downvotesText = (TextView) dialog.findViewById(R.id.dialogDownvotes);
		        
		        
		        //SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd hh:mm yyyy");
		        
		        // Set fortune information
		        timeText.setText(fortune.getSeen().toString());
		        bodyText.setText(fortune.getFortuneText(false));
		        if(fortune.getUpvoted()) {
		        	voteText.setText("Upvoted");
		        	voteText.setTextColor(Color.parseColor("#f46b27"));
		        }
		        else if (fortune.getDownvoted()) {
		        	voteText.setText("Downvoted");	
		        	voteText.setTextColor(Color.parseColor("#0075cd"));
		        }
		        else
		        	voteText.setText("You have not voted on this fortune");
		        
		        upvotesText.setText("Upvotes: " + fortune.getUpvotes());
		        downvotesText.setText("Downvotes: " + fortune.getDownvotes());
		        
		        
		        dialog.show();
			}
			
		});
		
		rowView.findViewById(R.id.up_row).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				Fortune fortune = fortunes[position];
				fortune.upvote();
				
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				View rowView = inflater.inflate(R.layout.history_row, null);
				
				TextView upvoteCountText = (TextView) rowView.findViewById(R.id.upvote_count);
				upvoteCountText.setText(Integer.toString(fortune.getUpvotes()));
				
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
				Fortune fortune = fortunes[position];
				fortune.downvote();
				
				// TODO need to get downvote_count text view from here to update count
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				View rowView = inflater.inflate(R.layout.history_row, null);
				
				TextView downvoteCountText = (TextView) rowView.findViewById(R.id.downvote_count);
				if(downvoteCountText != null)
					downvoteCountText.setText(Integer.toString(fortune.getDownvotes()));
				
					
				
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
		
		// SimpleDateFormat => Wed May 30 10:24 PM 2013
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd hh:mm a yyyy");
		timeText.setText(formatter.format(fortunes[position].getSeen()));
		bodyText.setText(fortunes[position].getFortuneText(false));
		upvoteCountText.setText(Integer.toString(fortunes[position].getUpvotes()));
		downvoteCountText.setText(Integer.toString(fortunes[position].getDownvotes()));
		
		return rowView;
    	
    }
}