package com.example.csie.simpleui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class HistoryActivity extends ActionBarActivity {

    private static final int REQUEST_CODE_ORDER_DETAIL = 1;

    private ListView historyListView;
    private List<ParseObject> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = (ListView) findViewById(R.id.historyListView);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent, View view,
                    int position, long id) {
                goToOrderDetailActivity(position);
            }
        });

        updateHistory();
    }

    private void goToOrderDetailActivity(int position) {
        ParseObject orderObject = orderList.get(position);

        Intent intent = new Intent();
        intent.putExtra("objectId", orderObject.getObjectId());
        intent.setClass(this, OrderDetailActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ORDER_DETAIL);
    }

    private void updateHistory() {
        final List<Map<String, String>> data =
                new ArrayList<Map<String, String>>();

        ParseQuery<ParseObject> query =
                new ParseQuery<ParseObject>("Order");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects,
                             ParseException e) {

                orderList = parseObjects;

                for(ParseObject object : parseObjects) {
                    Log.d("debug", object.getString("storeName"));

                    String storeName = object.getString("storeName");
                    String note = object.getString("note");
                    JSONArray menu = object.getJSONArray("menu");

                    Map<String, String> item =
                            new HashMap<String, String>();

                    item.put("storeName", storeName);
                    item.put("note", "Note:" + note);
                    item.put("drinkNumber", "Total:" + getDrinkNumber(menu));

                    data.add(item);
                }

                String[] from =
                        new String[]{"storeName", "note", "drinkNumber"};

                int[] to =
                        new int[]{R.id.storeName, R.id.note, R.id.drinkNumber};

                SimpleAdapter adapter = new SimpleAdapter(
                        HistoryActivity.this,
                        data, R.layout.listview_item, from , to);

                historyListView.setAdapter(adapter);
            }
        });
    }

    private int getDrinkNumber(JSONArray menu) {
        //TODO
        return Math.abs(new Random().nextInt()%100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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
