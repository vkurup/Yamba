package org.kurup.yamba;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.text.format.DateUtils;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class TimelineActivity extends Activity {
    DbHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;
    ListView listTimeline;
    SimpleCursorAdapter adapter;
    static final String[] FROM = { DbHelper.C_CREATED_AT, DbHelper.C_USER,
                                   DbHelper.C_TEXT };
    static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);

        // Check whether preferences have been set
        YambaApplication yamba = ((YambaApplication) getApplication());
        if (yamba.prefs.getString("username",null) == null) {
            startActivity(new Intent(this, PrefsActivity.class));
            Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
        }
      
        // Find your views
        listTimeline = (ListView) findViewById(R.id.listTimeline);

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
        cursor = db.query(DbHelper.TABLE, null, null, null, null, null, 
                          DbHelper.C_CREATED_AT + " DESC");
        startManagingCursor(cursor);

        // Set up the adapter
        adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);

        // Set up custom view binder
        adapter.setViewBinder(VIEW_BINDER);

        listTimeline.setAdapter(adapter);
    }

    // View binder constant to inject business logic converting
    // timestamp to relative time string
    static final ViewBinder VIEW_BINDER = new ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() != R.id.textCreatedAt)
                    return false;
                        
                // Update the created at text to relative time
                long timestamp = cursor.getLong(columnIndex);
                CharSequence relTime = DateUtils.getRelativeTimeSpanString(timestamp);
                ((TextView) view).setText(relTime);

                return true;
            }
        };
    
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
