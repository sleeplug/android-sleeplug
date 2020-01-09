package es.uvigo.teleco.sleeplug.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import es.uvigo.teleco.sleeplug.contract.SettingsContract;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SleePlug.db";
    public static final int DATABASE_VERSION = 1;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SettingsContract.SettingsEntry.TABLE_NAME + " (" + SettingsContract.SettingsEntry._ID + " INTEGER PRIMARY KEY," +
                SettingsContract.SettingsEntry.COLUMN_ENGINE_SPEED + " VARCHAR," + SettingsContract.SettingsEntry.COLUMN_ENGINE_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                SettingsContract.SettingsEntry.COLUMN_NOTIFICATIONS + "INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SettingsContract.SettingsEntry.TABLE_NAME);
        onCreate(db);
    }
}
