package org.kurup.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.graphics.Color;
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

public class StatusActivity extends BaseActivity implements OnClickListener,
                                                        TextWatcher {
    private static final String TAG = "StatusActivity";
    EditText editText;
    Button updateButton;
    TextView textCount;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);

        // Find views
        editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(this);

        updateButton = (Button) findViewById(R.id.buttonUpdate);
        updateButton.setOnClickListener(this);

        textCount = (TextView) findViewById(R.id.textCount);
        textCount.setText(Integer.toString(140));
        textCount.setTextColor(Color.GREEN);
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
                Twitter.Status status = yamba.getTwitter().updateStatus(statuses[0]);
                return "message posted";
            } catch (TwitterException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                return "failed to post";
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

}
