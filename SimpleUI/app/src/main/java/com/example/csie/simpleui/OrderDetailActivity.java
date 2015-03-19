package com.example.csie.simpleui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class OrderDetailActivity extends ActionBarActivity {

    public static final int RESULT_CODE_CANCEL = 0;
    public static final int RESULT_CODE_APPROVE = 1;

    private ParseObject orderObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        String objectId = getIntent().getStringExtra("objectId");
        ParseQuery<ParseObject> query =
                new ParseQuery<ParseObject>("Order");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                orderObject = parseObject;
            }
        });
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.cancel) {
            setResult(RESULT_CODE_CANCEL);
            orderObject.put("status", "cancel");
            orderObject.saveInBackground();
            finish();
        } else if (id == R.id.approve) {
            setResult(RESULT_CODE_APPROVE);
            orderObject.put("status", "approve");
            orderObject.saveInBackground();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
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
