package com.vorsk.androidfortune;

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
		
		Log.v(TAG, "Db version: "+ mDb.getVersion());
		return this;
	}

	public void close() {
		mDbHelper.close();
	}


	/**
	 * Create a new fortune
	 * 
	 * @param body the body of the fortune
	 * @return rowId or -1 if failed
	 */
	public long createFortune(String text) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TEXT, text);
		initialValues.put(KEY_SUBMITDATE, System.currentTimeMillis() );
		return mDb.insert(DATABASE_TABLE, null, initialValues);
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
	 * Return a Cursor over the list of all fortunes in the database
	 * 
	 * @return Cursor over all fortunes
	 */
	public Cursor fetchAllFortunes() {

		return mDb.query(DATABASE_TABLE, new String[] {KEY_ID,
				KEY_TEXT, KEY_SUBMITDATE}, null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the fortune that matches the given rowId
	 * 
	 * @param rowId id of fortune to retrieve
	 * @return Cursor positioned to matching fortune, if found
	 * @throws SQLException if fortune could not be found/retrieved
	 */
	public Cursor fetchFortune(long rowId) throws SQLException {

		Cursor mCursor =

				mDb.query(true, DATABASE_TABLE, new String[] {KEY_ID,
						KEY_TEXT}, KEY_ID + "=" + rowId, null,
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Update the fortune using the details provided.
	 * 
	 * @param rowId id of fortune to update
	 * @param body value to set fortune body to
	 * @return true if the fortune was successfully updated, false otherwise
	 */
	public boolean updatefortune(long rowId, String title, String body) {
		ContentValues args = new ContentValues();
		args.put(KEY_TEXT, body);

		return mDb.update(DATABASE_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
	}
	
	/**
	 * Remove all users and groups from database.
	 */
	public void removeAll()
	{
	    mDb.delete(DATABASE_TABLE, null, null);
	}
}
