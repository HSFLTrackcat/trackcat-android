package de.mobcom.group3.gotrack.Database.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static de.mobcom.group3.gotrack.Database.DAO.DbContract.*;

/**
 * Helper class to provide database operations
 */
public class DbHelper extends SQLiteOpenHelper {

    /**
     * default constructor to init database
     * @param context of type context
     */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates tables to store user and route information in the database
     * @param db of type SQLiteDatabase
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ROUTE_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    /**
     * Method to upgrade to a newer version of the database
     * @param db of type SQLiteDatabase
     * @param oldVersion of type integer
     * @param newVersion of type integer
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DbHelper.class.getName(), "Upgrading database from version " + oldVersion +
                " to " + newVersion + ". Old data will be destroyed");
        db.execSQL(SQL_DELETE_ROUTE_TABLE);
        db.execSQL(SQL_DELETE_USER_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DbHelper.class.getName(), "Upgrading database from version " + newVersion +
                " to " + oldVersion + ". Old data will be destroyed");
        db.execSQL(SQL_DELETE_ROUTE_TABLE);
        db.execSQL(SQL_DELETE_USER_TABLE);
        onCreate(db);
    }
}
