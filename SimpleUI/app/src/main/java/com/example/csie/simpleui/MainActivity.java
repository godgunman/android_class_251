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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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

    private static final int REQUEST_CODE_TAKE_PHOTO = 2;
    private static final int REQUEST_CODE_FIND_PLACE = 3;

    private boolean hasPhoto = false;

    private ParseFile file;
    private String storeId;
    private String storeName;

    private LoginButton loginButton;
    private Bitmap bitmap;
    private Button button;
    private EditText editText;
    private CheckBox checkBox;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sp.edit();

        checkBox = (CheckBox) findViewById(R.id.checkBox);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("fb", loginResult.getAccessToken().getToken());
                        Log.d("fb", loginResult.getAccessToken().getUserId());
//                        Log.d("fb", Profile.getCurrentProfile().getFirstName());
//                        Log.d("fb", Profile.getCurrentProfile().getName());

                        GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject,
                                                    GraphResponse graphResponse) {

                            }
                        });

                        GraphRequest.newGraphPathRequest(
                                loginResult.getAccessToken(),
                                "me?fields=albums",
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse graphResponse) {

                                    }
                                });
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException e) {

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


        initValue();
    }

    private void initValue() {
        String text = sp.getString("text", "");
        editText.setText(text);

        Boolean isChecked = sp.getBoolean("checkBox", false);
        checkBox.setChecked(isChecked);

        storeName = "尚未選擇商店";
    }

    private void send() {
        String text = editText.getText().toString();

        if (checkBox.isChecked()) {
            text = "********";
        }
        text = "to:[" + storeName + "] " + text + "\nmenu:" + getMenuInfo();
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();

        try {
            String status = sp.getString("status", "");

            ParseObject orderObject = new ParseObject("Order");
            orderObject.put("storeName", storeName);
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
            push.setMessage(storeName);

//            if (storeId != null) {
//            push.setChannel("storeName" + objectId);
//            }
            push.sendInBackground();

        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    public void clickFillMenu(View view) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.example.csie.simpleui.callreceiver");
        sendBroadcast(broadcastIntent);

        Intent intent = new Intent();
        intent.setClass(this, MenuActivity.class);
        intent.putExtra("storeName", storeName);
        startActivity(intent);
    }

    public void clickFindStore(View view) {
        Intent intent = new Intent();
        intent.setClass(this, FindPlaceActivity.class);
        intent.putExtra("storeName", storeName);
        startActivityForResult(intent, REQUEST_CODE_FIND_PLACE);
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
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
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
        } else if (requestCode == REQUEST_CODE_FIND_PLACE) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("storeName")) {
                    storeName = data.getStringExtra("storeName");
                }
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
        } else if (id == R.id.action_history) {
            Intent intent = new Intent();
            intent.setClass(this, HistoryActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

}
