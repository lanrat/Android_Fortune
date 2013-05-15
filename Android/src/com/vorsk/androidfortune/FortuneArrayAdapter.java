package com.vorsk.androidfortune;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	public View getView(int position, View convertView, ViewGroup parent) {
    	LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.history_row, parent, false);

		TextView timeText = (TextView) rowView.findViewById(R.id.time);
		TextView bodyText = (TextView) rowView.findViewById(R.id.body);

		timeText.setText(fortunes[position].getSeen().toString());
		bodyText.setText(fortunes[position].getFortuneText(false));
		
		return rowView;
    	
    }
}