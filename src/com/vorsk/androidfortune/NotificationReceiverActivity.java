package com.vorsk.androidfortune;

import com.vorsk.androidfortune.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NotificationReceiverActivity extends Activity {
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		Log.v("TAG","Got notification activity");
	    setContentView(R.layout.result);

	    Log.v("TAG","Got new intent");
		  
	    Intent intent = getIntent();
	    
		    TextView text = (TextView) findViewById(R.id.textView2);
		    
		    if (intent == null)
		    {
		    	Log.v("TAG","Intent null");
		    	return;
		    }
		    //Log.v("Action",getIntent().getExtras().getString("action"));

		    if (text == null)
		    {
		    	Log.v("TAG","text null");
		    	return;
		    }
		    
		    Bundle b = intent.getExtras();
		    
		    if (b == null)
		    {
		    	Log.v("TAG","bundle null");
		    	return;
		    }
		    
		    String s = b.getString("action");
		    
		    if (s == null)
		    {
		    	Log.v("TAG","string null");
		    	return;
		    }
		    
		    text.setText(s);
	  }
	
	
}
