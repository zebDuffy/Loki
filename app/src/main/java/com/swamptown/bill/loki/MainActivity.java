package com.swamptown.bill.loki;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Random;

public class MainActivity extends
        AppCompatActivity {
    public static final String CHANNEL_ID ="1" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final Resources res = getResources();

        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        final Button downloadButton = findViewById(R.id.btnTest);
        final Button saveButton = findViewById(R.id.btnSave);
        final Button cancelButton = findViewById(R.id.btnCancel);

        final Spinner dropdown = findViewById(R.id.spinner);
        String[] items = res.getStringArray(R.array.sites);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        final TimePicker simpleTimePicker = (TimePicker) findViewById(R.id.timePicker1);
        simpleTimePicker.setIs24HourView(true);

        /*read current settings*/
        SharedPreferences readShared;
        readShared = getSharedPreferences("LokiPrefs", Context.MODE_PRIVATE);
        String getSite=readShared.getString("Site", null);
        if (getSite != null )
        {
            for(int i=0; i < adapter.getCount(); i++) {
                if(adapter.getItem(i).equals(getSite.trim())){
                    dropdown.setSelection(i);
                    break;
                }
            }
           int getHour=readShared.getInt("Hour", -1);
           if (getHour !=-1) simpleTimePicker.setHour(getHour);
           int getMin=readShared.getInt("Min", -1);
           if (getMin !=-1) simpleTimePicker.setMinute(getMin);

        }
        createNotificationChannel();
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
                Spinner dropdown = findViewById(R.id.spinner);
                TimePicker simpleTimePicker;
                simpleTimePicker= findViewById(R.id.timePicker1);
                Toast.makeText(MainActivity.this,
                        "Scheduled wallaper update from " + dropdown.getSelectedItem(),
                        Toast.LENGTH_SHORT).show();
                SharedPreferences sharedpreferences = getSharedPreferences("LokiPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("Site", String.valueOf(dropdown.getSelectedItem()));
                editor.putInt("Hour", simpleTimePicker.getHour());
                editor.putInt("Min", simpleTimePicker.getMinute());
                editor.apply();
                LokiAlarm alarm = new LokiAlarm();
                alarm.SetAlarm(getApplicationContext(),simpleTimePicker.getHour(), simpleTimePicker.getMinute());



            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    LokiAlarm alarm= new LokiAlarm();
                    alarm.CancelAlarm(getApplicationContext());
                    Toast.makeText(MainActivity.this,
                            "Cancelled All Scheduled Updates",
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
            int id = item.getItemId();
            if (id == R.id.action_settings) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
                builder1.setMessage("Write your message here.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    private void createNotificationChannel() {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


    }


