package com.vorsk.androidfortune;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//Yanked from http://developer.android.com/training/notepad/notepad-ex1.html
// and http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html

public class FortuneDbAdapter {
	private static FortuneDbAdapter mInstance = null;
	
	public static final String KEY_ID = "_id";
	
	public static final String KEY_TEXT = "fortunetext";
	public static final String KEY_UPVOTES = "upvotes";
	public static final String KEY_DOWNVOTES = "downvotes";
	public static final String KEY_UPVOTED = "upvoted";
	public static final String KEY_DOWNVOTED = "downvoted";
	
	public static final String KEY_SUBMITDATE = "submitdate";
	public static final String KEY_VIEWDATE = "viewdate";
	public static final String KEY_FLAG = "flag";
	public static final String KEY_OWNER = "owner";
	
	private static final String TAG = "FortuneDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE =
			"create table fortunes" +
	        "(" + KEY_ID + " integer primary key autoincrement, " +
			      KEY_TEXT + " text not null, " +
			      KEY_UPVOTES + " integer, " +
			      KEY_DOWNVOTES + " integer, " +
			      KEY_UPVOTED +  " integer, " +
			      KEY_DOWNVOTED + " integer, " +
			      KEY_SUBMITDATE + " integer not null, " +
			      KEY_VIEWDATE + " integer, " +
			      KEY_FLAG + " integer, " +
			      KEY_OWNER + " integer);";

	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "fortunes";
	private static final int DATABASE_VERSION = 2;

	private final Context mContext;

	public static FortuneDbAdapter getInstance(Context ctx) {
		
		// Use the application context, which will ensure that you 
	    // don't accidentally leak an Activity's context.
	    // See this article for more information: http://bit.ly/6LRzfx
	    if (mInstance == null) {
	      mInstance = new FortuneDbAdapter(ctx.getApplicationContext());
	      Log.v(TAG,"FortuneDbAdapter instance has been instantialized.");
	    }
	    return mInstance;
	}
	
	//overloaded method. need to initialize using the other first though
	public static FortuneDbAdapter getInstance(){
	    if (mInstance == null) {
	    	Log.v(TAG,"FortuneDbAdapter instance has not been initialized.");
	    }
	    return mInstance;
	}
	
	/**
	 * Constructor - takes the context to allow the database to be opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public FortuneDbAdapter(Context ctx) {
		this.mContext = ctx;
	}
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.v(TAG, "Creating database of version " + DATABASE_VERSION);
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS fortunes");
			onCreate(db);
		}
	}

	/**
	 * Open the database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public FortuneDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mContext);
		mDb = mDbHelper.getWritableDatabase();
		
		Log.v(TAG, "DB version "+ mDb.getVersion()+ " opened.");
		return this;
	}

	public void close() {
		mDbHelper.close();
	}


	/**
	 * Create a new fortune from user
	 * 
	 * @param body the body of the fortune
	 * @return rowId or -1 if failed
	 */
	public long createFortune(String text) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TEXT, text);
		initialValues.put(KEY_SUBMITDATE, System.currentTimeMillis() );
		
		initialValues.put(KEY_UPVOTES, 0);
		initialValues.put(KEY_DOWNVOTES, 0);
		initialValues.put(KEY_UPVOTED, 0);
		initialValues.put(KEY_DOWNVOTED, 0);
		initialValues.put(KEY_VIEWDATE, -1);
		initialValues.put(KEY_FLAG, 0);
		initialValues.put(KEY_OWNER, 1);
		
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	/**
	 * Take a JSON string and inserts it into the database
	 * 
	 * @param json String to parse as JSON to insert into database
	 * @return id or -1 if failed
	 * @throws JSONException 
	 */
	public long createFortuneFromJson(String json) throws JSONException {

		ContentValues initialValues = new ContentValues();
		try { 
			JSONObject data = new JSONObject(json);
			Log.v(TAG, "Adding into database fortuneID:" + data.getInt("fortuneID"));
			initialValues.put(KEY_ID, data.getInt("fortuneID"));
			initialValues.put(KEY_TEXT, data.getString("text"));
			initialValues.put(KEY_UPVOTES, data.getInt("upvote"));
			initialValues.put(KEY_DOWNVOTES, data.getInt("downvote"));
			initialValues.put(KEY_UPVOTED, 0);
			initialValues.put(KEY_DOWNVOTED, 0);
			initialValues.put(KEY_SUBMITDATE, data.getInt("uploadDate"));
			initialValues.put(KEY_VIEWDATE, -1);
			initialValues.put(KEY_FLAG, 0);
			initialValues.put(KEY_OWNER, data.getInt("uploaders"));
		} catch ( JSONException e) {
			Log.v(TAG, "Failed to parse: " + json);
		}
		
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	/**
	 * Returns the JSON string representation of row
	 * 
	 * @param id row to create Json of
	 * @return String JSON string
	 */
	public String createFortuneJson(int id) {
		Cursor c = 
				mDb.query(true, DATABASE_TABLE, null, KEY_ID + "=" + id, null,
						null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		
		JSONObject json = new JSONObject();
		try {
			json.put("fortuneID", c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
			json.put("text", c.getString(c.getColumnIndexOrThrow(KEY_TEXT)));
			json.put("uploadDate", c.getInt(c.getColumnIndexOrThrow(KEY_SUBMITDATE)));
		}
		catch (Exception e) {
			Log.v(TAG,e.getMessage());
		}
		
		String s = json.toString();
		return s;
	}

	/**
	 * Delete the fortune with the given rowId
	 * 
	 * @param rowId id of fortune to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteFortune(long rowId) {

		return mDb.delete(DATABASE_TABLE, KEY_ID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a list of all fortunes that matches the where clause.
	 * 
	 * @return ArrayList of all fortunes
	 */
	public ArrayList<Fortune> fetchAllBy(String where) {
		ArrayList<Fortune> fortunes = new ArrayList<Fortune>();
		Cursor c = mDb.query(DATABASE_TABLE, null, where, null, null, null, null);
		
		if (c == null) {
			return null;
		}
		
		c.moveToFirst();
		while ( !c.isAfterLast() ) {
			fortunes.add(createFortuneFromCursor(c));
			c.moveToNext();
		}
		return fortunes;
	}
	
	/**
	 * Creates a Fortune from a Cursor by calling Fortune ctor
	 * @param c query result
	 * @return Fortune object version of row
	 */
	public Fortune createFortuneFromCursor(Cursor c) {
		int id, upvotes, downvotes;
		String text;
		boolean upvoted,downvoted,flag,owner;
		Date seen, submitted;
		try {
			id = c.getInt(c.getColumnIndexOrThrow(KEY_ID));
			text = c.getString(c.getColumnIndexOrThrow(KEY_TEXT));
			upvotes = c.getInt(c.getColumnIndexOrThrow(KEY_UPVOTES));
		    downvotes = c.getInt(c.getColumnIndexOrThrow(KEY_DOWNVOTES));
			upvoted = c.getInt(c.getColumnIndexOrThrow(KEY_UPVOTED))!=0;
			downvoted = c.getInt(c.getColumnIndexOrThrow(KEY_DOWNVOTED))!=0;
			flag = c.getInt(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_FLAG))!=0;
			owner =c.getInt(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_OWNER))!=0;
			seen = null;
			long tempTime = c.getLong(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_VIEWDATE));
			if ( tempTime > 0 ) {
				seen = new Date(tempTime*1000);
			}	
		    submitted = new Date( c.getLong(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_SUBMITDATE))*1000);
		} catch ( IllegalStateException e) {
			Log.v(TAG, e.getMessage());
			return null;
		} catch ( Exception e ) {
			Log.v(TAG, "Unable to create Fortune object from local db");
			return null;
		}
					
		return new Fortune(id,text,upvotes,downvotes,upvoted,downvoted,flag,owner,seen,submitted);
	}
	
	/**
	 * Return a list of all fortunes
	 * 
	 * @return ArrayList of all fortunes
	 */
	public ArrayList<Fortune> fetchAllFortunes() {
		return fetchAllBy(null);
	}
	
	/**
	 * Return a list of all the fortunes by the current user
	 * 
	 * @return ArrayList of all fortunes
	 */
	public ArrayList<Fortune> fetchAllByUser() {
		return fetchAllBy(KEY_OWNER+"="+1);
	}

	/**
	 * Return a Cursor positioned at the fortune that matches the given rowId
	 * 
	 * @param fortuneId id of fortune to retrieve
	 * @return Fortune object 
	 */
	public Fortune fetchFortune(long fortuneId) {
		Cursor mCursor = 
				mDb.query(true, DATABASE_TABLE, null, KEY_ID + "=" + fortuneId, null,
						null, null, null, null);
		if ( mCursor == null || mCursor.getCount() == 0 ) {
			return null;
		}
		
		mCursor.moveToFirst();
		return createFortuneFromCursor(mCursor);
	}
	
	/**
	 * Add Fortune object into local database.
	 * 
	 * @param f fortune object to put into database.
	 * @return rowId of new entry
	 */
	public long insertFortune(Fortune f) {

		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_ID, f.getFortuneID());
		initialValues.put(KEY_TEXT, f.getFortuneText(false));
		initialValues.put(KEY_UPVOTES, f.getUpvotes());
		initialValues.put(KEY_DOWNVOTES, f.getDownvotes());
		initialValues.put(KEY_UPVOTED, f.getUpvoted() ? 1 : 0);
		initialValues.put(KEY_DOWNVOTED, f.getDownvoted() ? 1 : 0);
		initialValues.put(KEY_SUBMITDATE, f.getSubmitted().getTime()/1000);
		if ( f.getSeen()!= null )
			initialValues.put(KEY_VIEWDATE, f.getSeen().getTime()/1000);
		initialValues.put(KEY_FLAG, f.getFlagged() ? 1 : 0);
		initialValues.put(KEY_OWNER, f.getOwner()? 1 : 0 );
		
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	
	/**
	 * UPDATE ALL THE THINGS!!! (updates all fields of fortune in the db whether you need to or not)
	 * 
	 * @param fortune row/object to update.
	 * @return true if the fortune was successfully updated, false otherwise
	 */
	public boolean updateFortune(Fortune f) {
		ContentValues args = new ContentValues();
		args.put(KEY_TEXT, f.getFortuneText(false));
		args.put(KEY_UPVOTES, f.getUpvotes());
		args.put(KEY_DOWNVOTES, f.getDownvotes());
		args.put(KEY_UPVOTED, f.getUpvoted() ? 1 : 0);
		args.put(KEY_DOWNVOTED, f.getDownvoted() ? 1 : 0);
		//don't update submit date
		if ( f.getSeen()!= null )
			args.put(KEY_VIEWDATE, f.getSeen().getTime()/1000);
		args.put(KEY_FLAG, f.getFlagged() ? 1 : 0);
		//args.put(KEY_OWNER, f.getOwner()? 1 : 0 ); //probably shouldn't update
		return mDb.update(DATABASE_TABLE, args, KEY_ID + "=" + f.getFortuneID(), null) > 0;
	}
	
	/**
	 * Update the fortune using the details provided. Overloaded.
	 * 
	 * @param id id of fortune to update
	 * @param columnName column to update
	 * @param value new value to update column to
	 * @return true if the fortune was successfully updated, false otherwise
	 */
	public boolean updateFortuneCol(int id, String columnName, String value) {
		ContentValues args = new ContentValues();
		args.put(columnName, value);
		return mDb.update(DATABASE_TABLE, args, KEY_ID + "=" + id, null) > 0;
	}
	
	/**
	 * Update the fortune using the details provided. Overloaded.
	 * 
	 * @param id id of fortune to update
	 * @param columnName column to update
	 * @param value new value to update column to
	 * @return true if the fortune was successfully updated, false otherwise
	 */
	public boolean updateFortuneCol(int id, String columnName, long value) {
		ContentValues args = new ContentValues();
		args.put(columnName, value);
		return mDb.update(DATABASE_TABLE, args, KEY_ID + "=" + id, null) > 0;
	}
	
	/**
	 * Update the fortune using the details provided. Overloaded.
	 * 
	 * @param id id of fortune to update
	 * @param columnName column to update
	 * @param value new value to update column to
	 * @return true if the fortune was successfully updated, false otherwise
	 */
	public boolean updateFortuneCol(int id, String columnName, boolean value) {
		return updateFortuneCol(id, columnName, value ? 1 : 0);
	}
	
	/**
	 * Update the fortune using the details provided. Overloaded.
	 * 
	 * @param id id of fortune to update
	 * @param columnName column to update
	 * @param value new value to update column to
	 * @return true if the fortune was successfully updated, false otherwise
	 */
	public boolean updateFortuneCol(int id, String columnName, Date value) {
		return updateFortuneCol(id, columnName, value.getTime()/1000);
	}
	
	/**
	 * Remove all users and groups from database. Probably should remove. Or hide.
	 */
	public void removeAll()
	{
		Log.v(TAG, "Clearing database");	
	    mDb.delete(DATABASE_TABLE, null, null);
	}
}
