package ca.ericbannatyne.colourdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Singleton implementation of a collection of markers, backed by an SQLite
 * database for persistent storage.
 */
public class MarkerDB {
	
	
	private static MarkerDB instance = null;
	
	private static final String[] fullProjection = {
		MarkerDBContract.Marker.COL_ID,
		MarkerDBContract.Marker.COL_CODE,
		MarkerDBContract.Marker.COL_FAMILY,
		MarkerDBContract.Marker.COL_NAME,
		MarkerDBContract.Marker.COL_COLOR,
		MarkerDBContract.Marker.COL_WANT_IT,
		MarkerDBContract.Marker.COL_HAVE_IT,
		MarkerDBContract.Marker.COL_NEEDS_REFILL,
	};

	private MarkerDBHelper dbHelper;
	private SQLiteDatabase db;
	
	/**
	 * Initialize a new instance of MarkerDB.
	 * 
	 * @param context context
	 */
	private MarkerDB(Context context) {
		dbHelper = new MarkerDBHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * Creates the MarkerDB instance using a workaround for use when there is an
	 * existing reference to the database exists,to avoid issues regarding locks.
	 * 
	 * @param context context
	 * @param db existing database reference
	 */
	private MarkerDB(Context context, SQLiteDatabase db) {
		dbHelper = new MarkerDBHelper(context);
		this.db = db;
	}
	
	/**
	 * Return a reference to the unique instance of MarkerDB, creating it if it
	 * does not already exist.
	 * 
	 * @param context context
	 * @return reference to the unique instance of MarkerDB
	 */
	public static MarkerDB getInstance(Context context) {
		if (instance == null) {
			instance = new MarkerDB(context.getApplicationContext());
		}
		
		return instance;
	}
	
	/**
	 * Return a reference to the unique instance of MarkerDB, creating it if it
	 * does not already exist, using the workaround to avoid locks when a
	 * reference to the database is already present.
	 * 
	 * @param context context
	 * @param db existing database reference
	 * @return reference to the unique instance of MarkerDB
	 */
	public static MarkerDB getInstance(Context context, SQLiteDatabase db) {
		if (instance == null) {
			instance = new MarkerDB(context.getApplicationContext(), db);
		}
		
		return instance;
	}
	
	/**
	 * 
	 * @param where
	 * @param whereArgs
	 * @return
	 */
	public Marker[] queryMarkers(String where, String[] whereArgs) {
		Cursor c = db.query(MarkerDBContract.Marker.TABLE_NAME, fullProjection,
				where, whereArgs, null, null, MarkerDBContract.Marker.COL_ID + " ASC");
		Marker[] result = new Marker[c.getCount()];
		
		int i = 0;
		while (c.moveToNext()) {
			result[i] = new Marker(this,
					c.getInt(c.getColumnIndex(MarkerDBContract.Marker.COL_ID)),
					c.getString(c.getColumnIndex(MarkerDBContract.Marker.COL_CODE)),
					c.getInt(c.getColumnIndex(MarkerDBContract.Marker.COL_FAMILY)),
					c.getString(c.getColumnIndex(MarkerDBContract.Marker.COL_NAME)),
					c.getInt(c.getColumnIndex(MarkerDBContract.Marker.COL_COLOR)),
					c.getInt(c.getColumnIndex(MarkerDBContract.Marker.COL_WANT_IT)) > 0,
					c.getInt(c.getColumnIndex(MarkerDBContract.Marker.COL_HAVE_IT)) > 0,
					c.getInt(c.getColumnIndex(MarkerDBContract.Marker.COL_NEEDS_REFILL)) > 0
					);
			i++;
		}
		
		return result;
	}
	
	public boolean setHaveIt(int id, boolean haveIt) {
		ContentValues values = new ContentValues();
		values.put(MarkerDBContract.Marker.COL_HAVE_IT, haveIt ? 1 : 0);
		return db.update(MarkerDBContract.Marker.TABLE_NAME, values,
				MarkerDBContract.Marker.COL_ID + "=?", new String[] { "" + id }) > 0;
	}
	
	public boolean setNeedsRefill(int id, boolean needsRefill) {
		ContentValues values = new ContentValues();
		values.put(MarkerDBContract.Marker.COL_NEEDS_REFILL, needsRefill ? 1 : 0);
		return db.update(MarkerDBContract.Marker.TABLE_NAME, values,
				MarkerDBContract.Marker.COL_ID + "=?", new String[] { "" + id }) > 0;
	}

}
