package com.example.csie.simpleui;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        textView = (TextView) findViewById(R.id.storeName);

        String storeName = getIntent().getStringExtra("storeName");
        textView.setText(storeName);
    }

    public void onClick(View view) {
        Button button = (Button) view;
        String text = button.getText().toString();
        int cnt = Integer.parseInt(text) + 1;
        button.setText(String.valueOf(cnt));

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
