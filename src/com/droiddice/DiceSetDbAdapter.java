/* Copyright (C) 2009 Andrew Semprebon */
package com.droiddice;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DiceSetDbAdapter {

    public static final String KEY_NAME = "name";
    public static final String KEY_DICE = "dice";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "DiceSetDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private static final String[] INITIAL_DICE_SETS = new String[] {
    	"d4", "d6", "d8", "d10", "d12", "d20", "d100" };
    
    private static final String DATABASE_NAME = "data";
    public static final String DATABASE_TABLE = "dice_sets";
    private static final int DATABASE_VERSION = 5;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " (_id integer primary key autoincrement, "
                    + "name text not null, dice text not null);";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context, String database) {
            super(context, database, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, "Creating new database");
            db.execSQL(DATABASE_CREATE);
    		createInitialDiceSets(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

	public static void createInitialDiceSets(SQLiteDatabase db) {
		for (String diceSetStr : INITIAL_DICE_SETS) {
			createDiceSetInDatabase(db, new DiceSet(diceSetStr)); 
		}
	}

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public DiceSetDbAdapter(Context ctx) {
        this.mCtx = ctx;
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
    public DiceSetDbAdapter open() throws SQLException {
    	return open(DATABASE_NAME);
    }
    
    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @param database name of database
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DiceSetDbAdapter open(String database) throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx, database);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new dice set. If the dice set is successfully created return the new rowId 
     * for that dice set, otherwise return a -1 to indicate failure.
     * 
     * @param dice set
     * @return rowId or -1 if failed
     */
    public long createDiceSet(DiceSet diceSet) {
        return createDiceSetInDatabase(mDb, diceSet);
    }

    /**
     * Create a new dice set. If the dice set is successfully created return the new rowId 
     * for that dice set, otherwise return a -1 to indicate failure.
     * 
     * @param db database to create record in
     * @param dice set
     * @return rowId or -1 if failed
     */
    private static long createDiceSetInDatabase(SQLiteDatabase db, DiceSet diceSet) {
    	Log.d(TAG, "Creating " + diceSet);
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, diceSet.getName());
        initialValues.put(KEY_DICE, diceSet.toString());

        return db.insert(DATABASE_TABLE, null, initialValues);
    }
    /**
     * Delete the dice set with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteDiceSet(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all dice sets in the database
     * 
     * @return Cursor over all dice sets
     */
    public Cursor fetchAllDiceSets() {
    	deleteEmptyDiceSets();
    	String[] columns = new String[] { KEY_ROWID, KEY_NAME, KEY_DICE };
        Cursor cursor = mDb.query(DATABASE_TABLE, columns, null, null, null, null, null);
        if (cursor == null) {
        	Log.w(TAG, "No rows returned");
        }
        return cursor;
    }

    private void deleteEmptyDiceSets() {
    	Log.w(TAG, "Deleting empty dice sets");
    	mDb.delete(DATABASE_TABLE, KEY_DICE + " = ''", null);
    }

    /**
     * Return a Cursor positioned at the dice set that matches the given rowId
     * 
     * @param rowId id of record to retrieve
     * @return Cursor positioned to matching record, if found
     * @throws SQLException if record could not be found/retrieved
     */
    public Cursor fetchCursorForDiceSet(Long rowId) throws SQLException {
    	if (rowId == null) {
    		return null;
    	}
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_NAME, KEY_DICE}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public DiceSet fetchDiceSet(long rowId) throws SQLException {
    	Cursor cursor = fetchCursorForDiceSet(rowId);
    	DiceSet diceSet = null;
    	if (cursor.getCount() != 0) {
    		cursor.moveToFirst();
    		diceSet = buildDiceSetFromCursor(cursor);
    	}
    	cursor.close();
    	return diceSet;
    }

    private Long fetchLong(String column, String where) {
    	String sql = "select " + column + " from " + DATABASE_TABLE;
    	if (where != null) {
    		sql = sql + " where " + where;
    	}
    	Cursor cursor =  mDb.rawQuery(sql, null);
    	
    	Long result = null;
   		cursor.moveToFirst();
   		if (!cursor.isNull(0)) {
    		result = cursor.getLong(0);
    	}
    	cursor.close();
    	return result;
    }
    
    /**
     * Get the rowId of the next dice set after the one specified
     * @param rowId row id of dice set
     * @return rowId of dice set after the one specified (or first, in the one specified is the last)
     */
	public Long next(Long rowId) {
		Log.i(TAG, "Finding next after " + rowId);
        Long nextRowId = fetchLong("min(" + KEY_ROWID + ")", KEY_ROWID + " > " + rowId);
        if (nextRowId == null) {
    		Log.i(TAG, "  Looping to front");
            nextRowId = fetchLong("min(" + KEY_ROWID + ")", null);
        }
		Log.i(TAG, "  Found " + nextRowId);
        return nextRowId;
	}
	
	public static DiceSet buildDiceSetFromCursor(Cursor cursor) {
		String name = cursor.getString(cursor.getColumnIndexOrThrow(DiceSetDbAdapter.KEY_NAME));
    	String dice = cursor.getString(cursor.getColumnIndexOrThrow(DiceSetDbAdapter.KEY_DICE));
    	DiceSet diceSet = new DiceSet(dice);
    	diceSet.setName(name);
		return diceSet;
	}

	public static Long getRowIdFor(Cursor cursor) {
		return cursor.getLong(cursor.getColumnIndexOrThrow(DiceSetDbAdapter.KEY_ROWID));
	}
	
	
	public void moveDiceSet ( int oldPosition, int newPosition ) {
		if ( oldPosition == newPosition ) {
			return;
		}
		
	  	String[] columns = new String[] { KEY_ROWID, KEY_NAME, KEY_DICE };
        Cursor cursor = mDb.query(DATABASE_TABLE, columns, null, null, null, null, KEY_ROWID );
        if (cursor == null) {
        	Log.w(TAG, "No rows returned");
        	return;
        }
        if ( oldPosition > newPosition ) {
//        	cursor.moveToPosition(oldPosition);
//        	
//        	ContentValues args = new ContentValues();
//        	for (int i = oldPosition; i > newPosition; i--) {
//        		cursor.
//                args.put(KEY_NAME, diceSet.getName());
//                args.put(KEY_DICE, diceSet.toString());
//
//                return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
//        	}
//        	
//        	// stepping backward until new Position reached. during this update key_rowid
//
//        	
//        	int currPos = Integer.parseInt( cursor.getString(cursor.getColumnIndexOrThrow(DiceSetDbAdapter.KEY_ROWID)) );
//        	if ()
        	
        }
        
	}
	
	
    /**
     * Update the dice set using the details provided. The dice set to be updated is
     * specified using the rowId.
     * 
     * @param rowId id of note to update
     * @param diceSet value to set record to
     * @return true if the record was successfully updated, false otherwise
     */
    public boolean updateDiceSet(long rowId, DiceSet diceSet) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, diceSet.getName());
        args.put(KEY_DICE, diceSet.toString());

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Count rows
     */
    public int getCount() {
        Cursor cursor =  mDb.rawQuery("select count(*) from " + DATABASE_TABLE, null);
        int count = 0;
        if (cursor != null) {
        	cursor.moveToFirst();
        	count = cursor.getInt(0); 
        	cursor.close();
        }
        return count;
    }

    public SQLiteDatabase getDatabase() {
    	return mDb;
    }
}
