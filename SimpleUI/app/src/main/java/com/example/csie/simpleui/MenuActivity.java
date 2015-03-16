package com.example.csie.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MenuActivity extends ActionBarActivity {

    private TextView textView;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        textView = (TextView) findViewById(R.id.storeName);

        String storeName = getIntent().getStringExtra("storeName");
        textView.setText(storeName);
        loadStatus();
    }

    public void onClick(View view) {
        Button button = (Button) view;
        String text = button.getText().toString();
        int cnt = Integer.parseInt(text) + 1;
        button.setText(String.valueOf(cnt));

        storeStatus();
        loadStatus();
    }

    private void loadStatus() {
        String status = sp.getString("status", "");
        try {
            JSONArray array = new JSONArray(status);
            JSONObject blackTeaObject = array.getJSONObject(0);
            JSONObject greenTeaObject = array.getJSONObject(1);

            int l = blackTeaObject.getInt("l");
            int m = blackTeaObject.getInt("m");
            int s = blackTeaObject.getInt("s");

            ((Button)findViewById(R.id.button1_l)).setText(String.valueOf(l));
            ((Button)findViewById(R.id.button1_m)).setText(String.valueOf(m));
            ((Button)findViewById(R.id.button1_s)).setText(String.valueOf(s));

            int l2 = greenTeaObject.getInt("l");
            int m2 = greenTeaObject.getInt("m");
            int s2 = greenTeaObject.getInt("s");

            ((Button)findViewById(R.id.button2_l)).setText(String.valueOf(l2));
            ((Button)findViewById(R.id.button2_m)).setText(String.valueOf(m2));
            ((Button)findViewById(R.id.button2_s)).setText(String.valueOf(s2));



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void storeStatus() {
        JSONArray array = new JSONArray();
        JSONObject blackTeaObject = new JSONObject();
        JSONObject greenTeaObject = new JSONObject();

        String blackTeaL =
                ((Button)findViewById(R.id.button1_l)).getText().toString();
        String blackTeaM =
                ((Button)findViewById(R.id.button1_m)).getText().toString();
        String blackTeaS =
                ((Button)findViewById(R.id.button1_s)).getText().toString();

        try {
            blackTeaObject.put("l", Integer.valueOf(blackTeaL));
            blackTeaObject.put("m", Integer.valueOf(blackTeaM));
            blackTeaObject.put("s", Integer.valueOf(blackTeaS));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String greenTeaL =
                ((Button)findViewById(R.id.button2_l)).getText().toString();
        String greenTeaM =
                ((Button)findViewById(R.id.button2_m)).getText().toString();
        String greenTeaS =
                ((Button)findViewById(R.id.button2_s)).getText().toString();

        try {
            greenTeaObject.put("l", Integer.valueOf(greenTeaL));
            greenTeaObject.put("m", Integer.valueOf(greenTeaM));
            greenTeaObject.put("s", Integer.valueOf(greenTeaS));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        array.put(blackTeaObject);
        array.put(greenTeaObject);

        Log.d("debug", array.toString());


        SharedPreferences.Editor editor = sp.edit();

        editor.putString("status", array.toString());
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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
}
