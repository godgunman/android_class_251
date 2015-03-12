package com.example.csie.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends ActionBarActivity {

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
        String history = readFile();
        String[] historyArray = history.split("\n");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, historyArray);

        listView.setAdapter(adapter);
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
        text = "to:[" + name + "] " + text;
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        editText.setText("");
        writeFile(text);
        updateHistory();
    }

    private void initSpinner() {
        String[] names =
                getResources().getStringArray(R.array.stores);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, names);

        spinner.setAdapter(adapter);
    }

    public void onClick(View view) {

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
        }

        return super.onOptionsItemSelected(item);
    }
}
