package cs.hm.edu.sisy.chat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "pubKeyManager";
 
    // PubKey table name
    private static final String TABLE_PUBKEY = "pubKey";
 
    // PubKey Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FRIEND_ID = "friendid";
    private static final String KEY_PUB_KEY = "pubkey";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PUBKEY_TABLE = "CREATE TABLE " + TABLE_PUBKEY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FRIEND_ID + " INTEGER,"
                + KEY_PUB_KEY + " INTEGER" + ")";
        db.execSQL(CREATE_PUBKEY_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PUBKEY);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new pubKey
    void addPubKey(PubKey pub, DatabaseHandler dbh) {
        SQLiteDatabase db = dbh.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_FRIEND_ID, pub.getFriendId());
        values.put(KEY_PUB_KEY, pub.getPubKey());
 
        // Inserting Row
        db.insert(TABLE_PUBKEY, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single pubKey
    public PubKey getPubKey(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_PUBKEY, new String[] { KEY_ID,
                KEY_FRIEND_ID, KEY_PUB_KEY }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        PubKey pub = new PubKey(Integer.parseInt(cursor.getString(0)),
            Integer.parseInt(cursor.getString(1)), cursor.getString(2));
        
        return pub;
    }
 
    // Updating single pubKey - unused atm
    public int updatePubKey(PubKey pub) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_FRIEND_ID, pub.getFriendId());
        values.put(KEY_PUB_KEY, pub.getPubKey());
 
        // updating row
        return db.update(TABLE_PUBKEY, values, KEY_ID + " = ?",
                new String[] { String.valueOf(pub.getId()) });
    }
 
    // Deleting single pubKey - unused atm
    public void deletePubKey(PubKey pub) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PUBKEY, KEY_ID + " = ?",
                new String[] { String.valueOf(pub.getId()) });
        db.close();
    }
 
}