package org.kurup.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends BaseActivity implements OnClickListener, TextWatcher, LocationListener {
    private static final String TAG = "StatusActivity";
    private static final long LOCATION_MIN_TIME = 3600000; // One hour
    private static final long LOCATION_MIN_DISTANCE = 1000; // One kilometer
    EditText editText;
    Button updateButton;
    TextView textCount;
    LocationManager locationManager;
    Location location;
    String provider;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);

        // Find views
        editText = (EditText) findViewById(R.id.editText);
        updateButton = (Button) findViewById(R.id.buttonUpdate);
        updateButton.setOnClickListener(this);

        textCount = (TextView) findViewById(R.id.textCount);
        textCount.setText(Integer.toString(140));
        textCount.setTextColor(Color.GREEN);
        editText.addTextChangedListener(this);
    }

    @Override
        protected void onResume() {
        super.onResume();
        
        // Setup location information
        provider = yamba.getProvider();
        if (!YambaApplication.LOCATION_PROVIDER_NONE.equals(provider)) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        } else {
            locationManager = null;
            location = null;
        }

        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
        }
    }

    @Override
        protected void onPause() {
        super.onPause();
        
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
            
    // Called when button is clicked
    public void onClick(View v) {
        String status = editText.getText().toString();
        new PostToTwitter().execute(status);
        Log.d(TAG, "onClicked");
    }

    // Asyncronously posts to twitter
    class PostToTwitter extends AsyncTask<String, Integer, String> {
        // Called to initiate background activity
        @Override
            protected String doInBackground(String... statuses) {
            try {
                // Check if we have the location
                if (location != null) {
                    double latlong[] = {location.getLatitude(), location.getLongitude()};
                    yamba.getTwitter().setMyLocation(latlong);
                }
                Twitter.Status status = yamba.getTwitter().updateStatus(statuses[0]);
                return "Message posted.";
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to connect to Twitter service", e);
                return "Failed to post.";
            }
        }

        // Called when there's a status to be updated
        @Override
            protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Not used in this case
        }

        // Called once the background activity has completed
        @Override
            protected void onPostExecute(String result) {
            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }

    // TextWatcher methods
    public void afterTextChanged(Editable statusText) {
        int count = 140 - statusText.length();
        textCount.setText(Integer.toString(count));
        textCount.setTextColor(Color.GREEN);
        if (count < 10)
            textCount.setTextColor(Color.YELLOW);
        if (count < 0)
            textCount.setTextColor(Color.RED);
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    // LocationLiistener methods
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    public void onProviderDisabled(String provider) {
        if (this.provider.equals(provider))
            locationManager.removeUpdates(this);
    }

    public void onProviderEnabled(String provider) {
        if (this.provider.equals(provider))
            locationManager.requestLocationUpdates(this.provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
