package com.swamptown.bill.loki;


import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class MainActivity extends
        AppCompatActivity {
    public static String lokiPrefs="Loli.cfg";
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final Resources res = getResources();

        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        final
        Button downloadButton = (Button) findViewById(R.id.btnTest);
        final Button saveButton = (Button) findViewById(R.id.btnSave);
        final Button cancelButton = (Button) findViewById(R.id.btnCancel);

        final Spinner dropdown = (Spinner)findViewById(R.id.spinner);
        String[] items = res.getStringArray(R.array.sites);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Spinner dropdown2 = (Spinner)findViewById(R.id.spinner2);
        String[] items2 = res.getStringArray(R.array.hours);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);

        /*read current settings*/
        SharedPreferences readShared;
        readShared = getSharedPreferences("LokiPrefs", Context.MODE_PRIVATE);
        String getSite=readShared.getString("Site", null);
        if (getSite != null)
        {
            for(int i=0; i < adapter.getCount(); i++) {
                if(getSite.trim().equals(adapter.getItem(i).toString())){
                    dropdown.setSelection(i);
                    break;
                }
            }

            String getTime=readShared.getString("Time", null);
            for(int i=0; i < adapter2.getCount(); i++) {
                if (getTime.trim().equals(adapter2.getItem(i).toString())) {
                    dropdown2.setSelection(i);
                    break;
                }
            }
        }

        downloadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String photoURL = null;
                    if (String.valueOf(dropdown.getSelectedItem()).equals("Wikimedia Photo of the Day"))
                    {
                        photoURL="https://s3-us-west-2.amazonaws.com/com.screenscraper.images/wiki.jpg";

                    }
                    else if (String.valueOf(dropdown.getSelectedItem()).equals("Astronomy Picture of the Day"))
                    {
                        photoURL="https://s3-us-west-2.amazonaws.com/com.screenscraper.images/astronomy.jpg";
                    }
                    else if (String.valueOf(dropdown.getSelectedItem()).equals("National Geographic Photo of the Day"))
                    {
                        photoURL="https://s3-us-west-2.amazonaws.com/com.screenscraper.images/geo.jpg";
                    }
                    else
                    {
                        Random rand = new Random();
                        int randomNum = rand.nextInt(3) + 1;
                        switch (randomNum) {
                            case 1:
                                photoURL="https://s3-us-west-2.amazonaws.com/com.screenscraper.images/wiki.jpg";
                                break;
                            case 2:
                                photoURL="https://s3-us-west-2.amazonaws.com/com.screenscraper.images/geo.jpg";
                                break;
                            case 3:
                                photoURL="https://s3-us-west-2.amazonaws.com/com.screenscraper.images/astronomy.jpg";
                                break;
                        }
                    }
                    Toast.makeText(MainActivity.this, "Getting "+ dropdown.getSelectedItem().toString() , Toast.LENGTH_SHORT).show();
                    GetImageWallpaper page = new GetImageWallpaper();
                    page.context=getApplicationContext();
                    page.execute(photoURL, dropdown.getSelectedItem().toString() );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Spinner dropdown = (Spinner)findViewById(R.id.spinner);
                Spinner dropdown2 = (Spinner)findViewById(R.id.spinner2);
                SharedPreferences sharedpreferences =null;
                Toast.makeText(MainActivity.this,
                        res.getString(R.string.action_settings)+
                                "\n"+res.getString(R.string.site) + ": "+ String.valueOf(dropdown.getSelectedItem()) +
                                "\n"+res.getString(R.string.hour) + ": "+ String.valueOf(dropdown2.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
                sharedpreferences = getSharedPreferences("LokiPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("Site", String.valueOf(dropdown.getSelectedItem()));
                editor.putString("Time", String.valueOf(dropdown2.getSelectedItem()));
                editor.commit();
                LokiAlarm alarm = new LokiAlarm();
                alarm.SetAlarm(getApplicationContext(),String.valueOf(dropdown2.getSelectedItem()));



            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    LokiAlarm alarm= new LokiAlarm();
                    alarm.CancelAlarm(getApplicationContext());
                    Toast.makeText(MainActivity.this,
                            res.getString(R.string.cancel),
                            Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
  }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
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


