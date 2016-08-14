package com.swamptown.bill.loki;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.content.Intent;
import android.content.BroadcastReceiver;
import java.util.Random;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/* TODO INtegrate github and commit*/
/* TODO Icon, Title, help, about etc
/* TODO get description of page */
/* TODO finish statusbar notifications*/
public class MainActivity extends Activity {
    public static String lokiPrefs="Loli.cfg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final Resources res = getResources();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        final Button downloadButton = (Button) findViewById(R.id.btnTest);
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
                    if (String.valueOf(dropdown.getSelectedItem()).equals("Wikimedia Photo of the Day"))
                    {
                        GetWikiPage page = new GetWikiPage();
                        page.context=getApplicationContext();
                        page.execute();
                        /*LokiAlarm alarm = new LokiAlarm();
                         alarm.SetAlarm(getApplicationContext());*/
                        Toast.makeText(MainActivity.this, "Getting Wiki Page", Toast.LENGTH_SHORT).show();
                    }
                    else if (String.valueOf(dropdown.getSelectedItem()).equals("Astronomy Picture of the Day"))
                    {
                        GetNASAPage page = new GetNASAPage();
                        page.context=getApplicationContext();
                        page.execute();
                        /*LokiAlarm alarm = new LokiAlarm();
                         alarm.SetAlarm(getApplicationContext());*/
                        Toast.makeText(MainActivity.this, "Getting NASA Page", Toast.LENGTH_SHORT).show();
                    }
                    else if (String.valueOf(dropdown.getSelectedItem()).equals("National Geographic Photo of the Day"))
                    {
                        GetWebPage page = new GetWebPage();
                        page.context=getApplicationContext();
                        page.execute();
                        Toast.makeText(MainActivity.this, "Getting National Geographic Page", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Random rand = new Random();
                        int randomNum = rand.nextInt(3) + 1;
                        switch (randomNum) {
                            case 1:
                                GetWebPage page = new GetWebPage();
                                page.context=getApplicationContext();
                                page.execute();
                                break;
                            case 2:
                                GetWikiPage page2 = new GetWikiPage();
                                page2.context=getApplicationContext();
                                page2.execute();
                                break;
                            case 3:
                                GetNASAPage page3 = new GetNASAPage();
                                page3.context=getApplicationContext();
                                page3.execute();
                                break;
                        }
                        Toast.makeText(MainActivity.this, "Getting Random Page", Toast.LENGTH_SHORT).show();

                    }

                    Log.d("Test", String.valueOf(dropdown.getSelectedItem()));


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


