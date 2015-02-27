package com.example.luchano.wildliferecording.Adapters;

/**
 * Created by Luchano on 25/02/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.luchano.wildliferecording.Databases.MarkerHandler;
import com.example.luchano.wildliferecording.ObjectClasses.MyMarkerObj;

import java.util.ArrayList;
import java.util.List;


public class MarkerAdapter {
    MarkerHandler dbhelper;
    SQLiteDatabase db;

    String[] cols = {MarkerHandler.TITLE, MarkerHandler.SNIPPET, MarkerHandler.POSITION};

    public MarkerAdapter(Context c) {
        dbhelper = new MarkerHandler(c);

    }

    public void open() throws SQLException {
        db = dbhelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public void addMarker(MyMarkerObj m) {
        ContentValues v = new ContentValues();

        v.put(MarkerHandler.TITLE, m.getTitle());
        v.put(MarkerHandler.SNIPPET, m.getSnippet());
        v.put(MarkerHandler.POSITION, m.getPosition());

        db.insert(MarkerHandler.TABLE_NAME, null, v);

    }

    public List<MyMarkerObj> getMyMarkers() {
        List<MyMarkerObj> markers = new ArrayList<MyMarkerObj>();

        Cursor cursor = db.query(MarkerHandler.TABLE_NAME, cols, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MyMarkerObj m = cursorToMarker(cursor);
            markers.add(m);
            cursor.moveToNext();
        }
        cursor.close();


        return markers;
    }

    public void deleteMarker(MyMarkerObj m) {
        db.delete(MarkerHandler.TABLE_NAME, MarkerHandler.POSITION + " = '" + m.getPosition() + "'", null);
    }


    private MyMarkerObj cursorToMarker(Cursor cursor) {
        MyMarkerObj m = new MyMarkerObj();
        m.setTitle(cursor.getString(0));
        m.setSnippet(cursor.getString(1));
        m.setPosition(cursor.getString(2));
        return m;
    }


}