package com.vorsk.androidfortune;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.vorsk.androidfortune.HistoryActivity.HistoryFragment;
import com.vorsk.androidfortune.HomeActivity.FortuneFragment;
import com.vorsk.androidfortune.SubmitActivity.SubmitFragment;
import com.vorsk.androidfortune.data.Client;
import com.vorsk.androidfortune.data.Fortune;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/*
 * Adapted from http://stackoverflow.com/questions/2109271/can-any-one-provide-me-example-of-two-line-list-item-in-android
 */
public class FortuneArrayAdapter extends ArrayAdapter<Fortune> {
	
	private final Fortune[] fortunes;
	private final Context context;
	
    public FortuneArrayAdapter(Context context, Fortune[] fortunes) {
        super(context, R.layout.fortune_layout, fortunes);
        
        this.fortunes = fortunes;
        this.context = context;
    }

    @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
    	LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.fortune_layout, parent, false);

		TextView timeText = (TextView) rowView.findViewById(R.id.fortune_time);
		TextView bodyText = (TextView) rowView.findViewById(R.id.fortune_text);
		
		TextView upvoteCountText = (TextView) rowView.findViewById(R.id.upvote_count);
		TextView downvoteCountText = (TextView) rowView.findViewById(R.id.downvote_count);
		
		rowView.findViewById(R.id.fortune_body).setOnClickListener(new OnClickListener(){

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
		
		rowView.findViewById(R.id.upvote_info).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				Fortune fortune = fortunes[position];
				String dialogTitle = "";
				String dialogText = "";
				
				// check return value of voting
				
				if(fortune.hasVoted()) {
					dialogTitle = "Already Voted";
					dialogText = "You have already voted for this fortune";
				}
				else {
					fortune.upvote();
					Fortune f = Client.getInstance(context.getApplicationContext()).getCurrentFortune();
					HistoryFragment.refreshHistory();
					if(fortune.getOwner()) {
						SubmitFragment.refreshSubmitted();
					}
					if(fortune.equals(f)) {
						FortuneFragment.displayFortune(f);
					}
					dialogTitle = "Up Vote";
					dialogText = "You have successfully up voted";
				}
					
				
				
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				View rowView = inflater.inflate(R.layout.fortune_layout, null);
				
				TextView upvoteCountText = (TextView) rowView.findViewById(R.id.upvote_count);
				upvoteCountText.setText(Integer.toString(fortune.getUpvotes()));
				
				// create dialog 
				final Dialog dialog = new Dialog(getContext());
				dialog.setContentView(R.layout.history_vote_dialog);
				dialog.setTitle(dialogTitle);
				TextView text = (TextView) dialog.findViewById(R.id.vote_dialog);
				text.setText(dialogText);
				dialog.setCancelable(true);
				dialog.show();
			}
			
		});
		rowView.findViewById(R.id.downvote_info).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				Fortune fortune = fortunes[position];
				String dialogTitle = "";
				String dialogText = "";
				
				if(fortune.hasVoted()) {
					dialogTitle = "Already Voted";
					dialogText = "You have already voted for this fortune";
				}
				else {
					fortune.downvote();
					HistoryFragment.refreshHistory();
					if(fortune.getOwner()) {
						SubmitFragment.refreshSubmitted();
					}
					dialogTitle = "Down Vote";
					dialogText = "You have successfully downvoted";
				}
				//TODO check the return value of downvote to see if we need to refresh the list
				
				// TODO need to get downvote_count text view from here to update count
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				View rowView = inflater.inflate(R.layout.fortune_layout, null);
				
				TextView downvoteCountText = (TextView) rowView.findViewById(R.id.downvote_count);
				if(downvoteCountText != null)
					downvoteCountText.setText(Integer.toString(fortune.getDownvotes()));
				
					
				
				// create dialog
				final Dialog dialog = new Dialog(getContext());
				dialog.setContentView(R.layout.history_vote_dialog);
				dialog.setTitle(dialogTitle);
				TextView text = (TextView) dialog.findViewById(R.id.vote_dialog);
				text.setText(dialogText);
				dialog.setCancelable(true);
				dialog.show();
			}
			
		});
		
		// SimpleDateFormat => Wed May 30 10:24 PM 2013
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd hh:mm a",Locale.US);
		timeText.setText(formatter.format(fortunes[position].getSeen()));
		bodyText.setText(fortunes[position].getFortuneText(false));
		upvoteCountText.setText(Integer.toString(fortunes[position].getUpvotes()));
		downvoteCountText.setText(Integer.toString(fortunes[position].getDownvotes()));
		
		if (fortunes[position].getUpvoted()) {
			((ImageButton)rowView.findViewById(R.id.upvote_button)).setImageResource(R.drawable.arrow_up_dark);
		}else {
			((ImageButton)rowView.findViewById(R.id.upvote_button)).setImageResource(R.drawable.arrow_up);
		}
		if (fortunes[position].getDownvoted()) {
			((ImageButton)rowView.findViewById(R.id.downvote_button)).setImageResource(R.drawable.arrow_down_dark);
		}else {
			((ImageButton)rowView.findViewById(R.id.downvote_button)).setImageResource(R.drawable.arrow_down);
		}
		
		return rowView;
    	
    }
}