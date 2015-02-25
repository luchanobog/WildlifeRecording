package com.example.luchano.wildliferecording;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luchano on 13/02/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "species",
            TABLE_SPECIES = "species",
            KEY_ID = "id",
            KEY_NAME = "name",
            KEY_NUMBER = "number",
            KEY_LOCATION = "location",
            KEY_COMMENTS = "comments",
            KEY_IMAGEURI = "imageUri";

    //CONSTRUCTOR
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SPECIES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_NUMBER + " TEXT," + KEY_LOCATION + " TEXT," + KEY_COMMENTS + " TEXT," + KEY_IMAGEURI + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPECIES);

        onCreate(db);
    }

    public void createLog(Log log) {
        SQLiteDatabase db = getWritableDatabase();//Allow me to write to this database

        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, log.get_name());
        cv.put(KEY_NUMBER, log.get_number());
        cv.put(KEY_LOCATION, log.get_location());
        cv.put(KEY_COMMENTS, log.get_comments());
        cv.put(KEY_IMAGEURI, log.get_imageURI().toString());

        db.insert(TABLE_SPECIES, null, cv);

        db.close();
    }

    public Log getLog(int id) {
        SQLiteDatabase db = getReadableDatabase();
        //Cursor
        Cursor cursor = db.query(TABLE_SPECIES, new String[]{KEY_ID, KEY_NAME, KEY_NUMBER, KEY_LOCATION, KEY_COMMENTS, KEY_IMAGEURI}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        //make sure cursor is not null
        if (cursor != null)
            cursor.moveToFirst();
        //
        Log log = new Log(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5)));
        db.close();
        cursor.close();
        return log;
    }

    //Deleting species
    public void deleteLog(Log log) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SPECIES, KEY_ID + "=?", new String[]{String.valueOf(log.getId())});
        db.close();
    }

    //Raw sql commands,get a certain specie
    public int getLogCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SPECIES, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

    public int updateLog(Log log) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, log.get_name());
        cv.put(KEY_NUMBER, log.get_number());
        cv.put(KEY_LOCATION, log.get_location());
        cv.put(KEY_COMMENTS, log.get_comments());
        cv.put(KEY_IMAGEURI, log.get_imageURI().toString());

        //Optimizing the code
        int rowsAffected = db.update(TABLE_SPECIES, cv, KEY_ID + "=?", new String[]{String.valueOf(log.getId())});
        db.close();
//        return db.update(TABLE_SPECIES,cv,KEY_ID + "=?", new String[]{ String.valueOf(log.getId())});
        return rowsAffected;
    }


    public List<Log> getAllLogs() {
        List<Log> logs = new ArrayList<Log>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SPECIES, null);

        if (cursor.moveToFirst()) {

            do {
                logs.add(new Log(Integer.parseInt(
                        cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        Uri.parse(cursor.getString(5))));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return logs;
    }

}
