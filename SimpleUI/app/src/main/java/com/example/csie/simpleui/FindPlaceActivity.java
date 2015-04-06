package com.example.csie.simpleui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class FindPlaceActivity extends ActionBarActivity {

    private TextView textView;
    private WebView webView;
    private Spinner spinner;
    private List<ParseObject> storeInfoList;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private MapFragment mapFragment;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_place);

//        disableStrictMode();


        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sp.edit();

        spinner = (Spinner) findViewById(R.id.storeListSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                editor.putInt("spinner", position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textView = (TextView) findViewById(R.id.urlResult);
        webView = (WebView) findViewById(R.id.webView);

        FragmentManager fragmentManager = getFragmentManager();
        mapFragment =
                (MapFragment) fragmentManager.findFragmentById(R.id.map);
        map = mapFragment.getMap();
        
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

        initSpinner();
    }


    private void initSpinner() {

        ParseQuery<ParseObject> query =
                new ParseQuery<ParseObject>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects,
                             ParseException e) {

                storeInfoList = parseObjects;

                List<String> names = new ArrayList<>();
                for(ParseObject object:parseObjects) {
                    String name = object.getString("name");
                    String address = object.getString("address");
                    names.add(name + "," + address);
                }
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(
                                FindPlaceActivity.this,
                                android.R.layout.simple_spinner_item, names);

                spinner.setAdapter(adapter);
            }
        });
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
                "http://maps.googleapis.com/maps/api/staticmap?" +
                "center=%s,%s&zoom=%s&size=500x500&sensor=false", lat, lng, zoom);
        return url;
    }

    private String getMapsUrl(String lat, String lng, String zoom) {

        String url =
                String.format(
                        "https://www.google.com.tw/maps/@%s,%s,%s",
                        lat, lng, zoom);
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
                        textView.setText(formattedAddress + "," + lat + "," + lng);

                        String imageUrl = getMapsImageUrl(
                                String.valueOf(lat),
                                String.valueOf(lng), "15");

                        String mapsUrl = getMapsUrl(
                                String.valueOf(lat),
                                String.valueOf(lng), "17z");

                        webView.loadUrl(imageUrl);

                        CameraUpdate cameraUpdate
                                = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15);


                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(formattedAddress);

                        map.addMarker(markerOptions);
                        map.moveCamera(cameraUpdate);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            };
}
