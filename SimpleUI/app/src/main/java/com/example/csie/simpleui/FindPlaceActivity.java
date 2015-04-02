package com.example.csie.simpleui;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class FindPlaceActivity extends ActionBarActivity {

    private TextView textView;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_place);

//        disableStrictMode();

        textView = (TextView) findViewById(R.id.urlResult);
        webView = (WebView) findViewById(R.id.webView);

        String address = "台北市中華路2段313巷2號";
        try {
            address = URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url =
                "http://maps.googleapis.com/maps/api/geocode/" +
                "json?sensor=false&address=" + address;

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

    private String getMapsImageUrl(
            String lat, String lng, String zoom) {

        String url = String.format(
                "http://maps.googleapis.com/maps/api/staticmap" +
                "?center=%s,%s&zoom=%s&size=500x500&sensor=false", lat, lng, zoom);
        return url;
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
            try {
                JSONObject object = new JSONObject(result);
                object = object.getJSONArray("results")
                      .getJSONObject(0);

                String formattedAddress =
                        object.getString("formatted_address");

                JSONObject geometry =
                        object.getJSONObject("geometry");

                double lat
                        = geometry.getJSONObject("location")
                                  .getDouble("lat");

                double lng
                        = geometry.getJSONObject("location")
                                  .getDouble("lng");
                textView.setText(formattedAddress+","+lat+","+lng);

                String imageUrl = getMapsImageUrl(
                        String.valueOf(lat),
                        String.valueOf(lng), "15");

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
}
