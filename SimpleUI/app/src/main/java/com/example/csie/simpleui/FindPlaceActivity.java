package com.example.csie.simpleui;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class FindPlaceActivity extends ActionBarActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_place);

//        disableStrictMode();

        textView = (TextView) findViewById(R.id.urlResult);

        String url = "http://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&sensor=false";
        asyncTask.execute(url);
    }

    private void disableStrictMode() {
        StrictMode.ThreadPolicy.Builder builder =
                new StrictMode.ThreadPolicy.Builder();

        StrictMode.ThreadPolicy threadPolicy =
                builder.permitAll().build();

        StrictMode.setThreadPolicy(threadPolicy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    AsyncTask<String, Void, String> asyncTask =
            new AsyncTask<String, Void, String>() {

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            return Utils.fetch(url);
        }

        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);
        }
    };
}
