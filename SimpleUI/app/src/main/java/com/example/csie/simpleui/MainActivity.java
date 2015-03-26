package com.example.csie.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_CODE_ORDER_DETAIL = 1;
    private static final int REQUEST_CODE_TAKE_PHOTO = 2;

    private List<ParseObject> orderList;
    private List<ParseObject> storeInfoList;

    private boolean hasPhoto = false;

    private ParseFile file;
    private Bitmap bitmap;
    private Button button;
    private EditText editText;
    private CheckBox checkBox;
    private Spinner spinner;
    private ListView listView;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sp.edit();

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent, View view,
                    int position, long id) {
                ParseObject orderObject = orderList.get(position);

                Intent intent = new Intent();
                intent.putExtra("objectId", orderObject.getObjectId());
                intent.setClass(MainActivity.this, OrderDetailActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ORDER_DETAIL);
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        editText = (EditText) findViewById(R.id.editText);
        editText.setHint("type something ...");
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode,
                                 KeyEvent event) {
//                Log.d("debug", "keyCode:" + keyCode);
                String currentText = editText.getText().toString();
                editor.putString("text", currentText);
                editor.commit();

                if (keyCode == KeyEvent.KEYCODE_ENTER &&
                        event.getAction() == KeyEvent.ACTION_DOWN) {
                    send();
                    return true;
                }
                return false;
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                editor.putBoolean("checkBox", isChecked);
                editor.commit();
            }
        });

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

        initSpinner();
        initValue();
        updateHistory();
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
                        MainActivity.this,
                        data, R.layout.listview_item, from , to);

                listView.setAdapter(adapter);
            }
        });


    }

    private int getDrinkNumber(JSONArray menu) {
        //TODO
        return Math.abs(new Random().nextInt()%100);
    }

    private void initValue() {
        String text = sp.getString("text", "");
        editText.setText(text);

        Boolean isChecked = sp.getBoolean("checkBox", false);
        checkBox.setChecked(isChecked);

        int position = sp.getInt("spinner", 0);
        spinner.setSelection(position);
    }

    private void send() {
        String text = editText.getText().toString();
        String name = (String) spinner.getSelectedItem();
        if (checkBox.isChecked()) {
            text = "********";
        }
        text = "to:[" + name + "] " + text + "\nmenu:" + getMenuInfo();
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();

        try {
            String status = sp.getString("status", "");

            ParseObject orderObject = new ParseObject("Order");
            orderObject.put("storeName", name);
            orderObject.put("note", editText.getText().toString());
            orderObject.put("menu", new JSONArray(status));

            if (hasPhoto) {
                file = new ParseFile("photo.png", uriToBytes(Utils.getOutputUri()));
                orderObject.put("photo", file);
            }

            orderObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d("debug", "done method called");
                }
            });


            ParsePush push = new ParsePush();
            push.setMessage(name);

            int selected = spinner.getSelectedItemPosition();
            String objectId = storeInfoList.get(selected).getObjectId();
//            push.setChannel("storeName" + objectId);
            push.sendInBackground();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateHistory();
        editText.setText("");
    }

    private String getMenuInfo() {
        String status = sp.getString("status", "");
        try {
            JSONArray array = new JSONArray(status);
            String data = "";
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String drinkName = object.getString("drinkName");
                String l = "l:" + object.getString("l") + ".";
                String m = "m:" + object.getString("m") + ".";
                String s = "s:" + object.getString("s") + ".";
                data += drinkName + " " + l + m + s + "\n";
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void initSpinner() {
//        String[] names =
//                getResources().getStringArray(R.array.stores);

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
                        MainActivity.this,
                        android.R.layout.simple_spinner_item, names);

                spinner.setAdapter(adapter);
            }
        });
    }

    public void onClick(View view) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.example.csie.simpleui.callreceiver");
        sendBroadcast(broadcastIntent);

        String storeName = (String) spinner.getSelectedItem();

        Intent intent = new Intent();
        intent.setClass(this, MenuActivity.class);
        intent.putExtra("storeName", storeName);
        startActivity(intent);
    }

    private void writeFile(String text) {
        try {
            text += "\n";
            FileOutputStream fos =
                    openFileOutput("message.txt", Context.MODE_APPEND);
            fos.write(text.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFile() {
        try {
            FileInputStream fis = openFileInput("message.txt");
            byte[] buffer = new byte[1024];
            fis.read(buffer);
            fis.close();
            return new String(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private byte[] uriToBytes(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = 0;

            while( (len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_ORDER_DETAIL) {
            switch (resultCode) {
                case OrderDetailActivity.RESULT_CODE_CANCEL:
                    Toast.makeText(this, "Cancel order.",
                            Toast.LENGTH_SHORT).show();
                    break;

                case OrderDetailActivity.RESULT_CODE_APPROVE:
                    Toast.makeText(this, "Approve order.",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                hasPhoto = true;
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageURI(Utils.getOutputUri());

                /*
                bitmap = data.getParcelableExtra("data");
                imageView.setImageBitmap(bitmap);
                file = new ParseFile("photo.png", bitmapToBytes(bitmap));
                */
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        } else if (id == R.id.action_takephoto) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getOutputUri());
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
        }

        return super.onOptionsItemSelected(item);
    }
}
