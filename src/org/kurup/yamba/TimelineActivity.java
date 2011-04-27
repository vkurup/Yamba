package org.kurup.yamba;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class TimelineActivity extends Activity {
    DbHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;
    ListView listTimeline;
    SimpleCursorAdapter adapter;
    static final String[] FROM = {DbHelper.C_
    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        
        // Find your views
        textTimeline = (TextView) findViewById(R.id.textTimeline);

        // Connect to database
        dbHelper = new DbHelper(this);
        db = dbHelper.getReadableDatabase();
    }

    @Override
        public void onDestroy() {
        super.onDestroy();

        // Close the database
        db.close();
    }

    @Override
        protected void onResume() {
        super.onResume();

        // Get the data from the database
        cursor = db.query(DbHelper.TABLE, null, null, null, null, null, DbHelper.C_CREATED_AT + " DESC");
        startManagingCursor(cursor);

        // Iterate over all the data and print it out
        String user, text, output;
        while (cursor.moveToNext()) {
            user = cursor.getString(cursor.getColumnIndex(DbHelper.C_USER));
            text = cursor.getString(cursor.getColumnIndex(DbHelper.C_TEXT));
            output = String.format("%s: %s\n", user, text);
            textTimeline.append(output);
        }
    }

    // Called the first time the user clicks the menu button
    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // Called when an options item is selected
    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.itemServiceStart:
            startService(new Intent(this, UpdaterService.class));
            break;
        case R.id.itemServiceStop:
            stopService(new Intent(this, UpdaterService.class));
            break;
        case R.id.itemPrefs:
            startActivity(new Intent(this, PrefsActivity.class));
            break;
        }
        return true;
    }

}
