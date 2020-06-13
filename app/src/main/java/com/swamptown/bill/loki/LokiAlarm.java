package com.swamptown.bill.loki;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;


public class LokiAlarm extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences readShared;
        readShared = context.getSharedPreferences("LokiPrefs", Context.MODE_PRIVATE);
        String getSite=readShared.getString("Site", null);


        String photoURL = null;
        if (getSite.equals("Wikimedia Photo of the Day"))
        {
            photoURL="https://s3-us-west-2.amazonaws.com/com.screenscraper.images/wiki.jpg";
       }
        else if (getSite.equals("Astronomy Picture of the Day"))
        {
            photoURL="https://s3-us-west-2.amazonaws.com/com.screenscraper.images/astronomy.jpg";
        }
        else if (getSite.equals("National Geographic Photo of the Day"))
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
                    photoURL="https://s3-us-west-2.amazonaws.com/com.screenscraper.images/astronomy.jpg";
                    break;
                case 3:
                    photoURL="https://s3-us-west-2.amazonaws.com/com.screenscraper.images/geo.jpg";
                    break;
            }

        }
        GetImageWallpaper page = new GetImageWallpaper();
        page.context= context.getApplicationContext();
        page.execute(photoURL, getSite );
        Log.d("Alarm", "trigger alarm");
    }

    public void SetAlarm(Context context, int hour, int minute)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, LokiAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calNow = Calendar.getInstance();
        calNow.set(Calendar.HOUR_OF_DAY, hour);
        calNow.set(Calendar.MINUTE, minute);
        calNow.set(Calendar.SECOND, 0);
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
        Log.d("Alarm", "Alarm Time: " + format.format(calNow.getTime()));
        am.setRepeating(AlarmManager.RTC_WAKEUP, calNow.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
        Log.d("Alarm", "set alarm");
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, LokiAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Log.d("Alarm", "cancel alarm");
    }
}
