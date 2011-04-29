package org.kurup.yamba;

import android.content.ContentProvider;
import android.net.Uri;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentUris;

public class StatusProvider extends ContentProvider {
    public static final String TAG = "StatusProvider";
    public static final Uri CONTENT_URI = Uri
        .parse("content://org.kurup.yamba.statusprovider");
    public static final String SINGLE_RECORD_MIME_TYPE = 
        "vnd.android.cursor.item/vnd.kurup.yamba.status";
    public static final String MULTIPLE_RECORDS_MIME_TYPE = 
        "vnd.android.cursor.dir/vnd.kurup.yamba.mstatus";
    StatusData statusData;

    @Override
        public boolean onCreate() {
        statusData = new StatusData(getContext());
        return false;
    }

    @Override
        public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = statusData.dbHelper.getWritableDatabase();
        try {
            long id = db.insertOrThrow(StatusData.TABLE, null, values);
            if (id == -1) {
                throw new RuntimeException(String.format("%s: Failed to insert [%s] to [%s] for unknown reasons.", TAG, values, uri));
            } else {
                return ContentUris.withAppendedId(uri, id);
            }
        } finally {
            db.close();
        }
    }

    @Override
        public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        long id = this.getId(uri);
        SQLiteDatabase db = statusData.dbHelper.getWritableDatabase();
        try {
            if (id < 0) {
                return db.update(StatusData.TABLE, values, selection, selectionArgs);
            } else {
                return db.update(StatusData.TABLE, values,
                                 StatusData.C_ID + "=" + id, null);
            }
        } finally {
            db.close();
        }
    }

    @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
        long id = this.getId(uri);
        SQLiteDatabase db = statusData.dbHelper.getWritableDatabase();
        try {
            if (id < 0) {
                return db.delete(StatusData.TABLE, selection, selectionArgs);
            } else {
                return db.delete(StatusData.TABLE, StatusData.C_ID + "=" + id, null);
            }
        } finally {
            db.close();
        }
    }

    @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        long id = this.getId(uri);
        SQLiteDatabase db = statusData.dbHelper.getReadableDatabase();
        if (id < 0) {
            return db.query(StatusData.TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        } else {
            return db.query(StatusData.TABLE, projection, StatusData.C_ID + "=" + id, null, null, null, null);
        }
    }

    @Override
        public String getType(Uri uri) {
        return this.getId(uri) < 0 ? MULTIPLE_RECORDS_MIME_TYPE
            : SINGLE_RECORD_MIME_TYPE;
    }

    private long getId(Uri uri) {
        String lastPathSegment = uri.getLastPathSegment();
        if (lastPathSegment != null) {
            try {
                return Long.parseLong(lastPathSegment);
            } catch (NumberFormatException e) {
                // at least we tried
            }
        }
        return -1;
    }
    
    
}
