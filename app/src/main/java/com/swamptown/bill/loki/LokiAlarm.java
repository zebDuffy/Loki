package com.swamptown.bill.loki;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

import androidx.legacy.content.WakefulBroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by bill on 5/14/16.
 */
public class LokiAlarm extends WakefulBroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "");
        wl.acquire();
        SharedPreferences readShared;
        readShared = context.getSharedPreferences("LokiPrefs", Context.MODE_PRIVATE);
        String getSite=readShared.getString("Site", null);
        int mId=1;

        if (getSite.equals("Wikimedia Photo of the Day"))
        {
            GetWikiPage page = new GetWikiPage();
            page.context=context;
            page.execute();


        }
        else if (getSite.equals("Astronomy Picture of the Day"))
        {
            GetNASAPage page = new GetNASAPage();
            page.context=context;
            page.execute();

        }
        else if (getSite.equals("National Geographic Photo of the Day"))
        {
            GetWebPage page = new GetWebPage();
            page.context=context;
            page.execute();

        }
        else
        {
            Random rand = new Random();
            int randomNum = rand.nextInt(3) + 1;
            switch (randomNum) {
                case 1:
                    GetWebPage page = new GetWebPage();
                    page.context=context;
                    page.execute();
                    break;
                case 2:
                    GetWikiPage page2 = new GetWikiPage();
                    page2.context=context;
                    page2.execute();
                    break;
                case 3:
                    GetNASAPage page3 = new GetNASAPage();
                    page3.context=context;
                    page3.execute();
                    break;
            }

        }
        wl.release();
        Log.d("Alarm", "trigger alarm");
        String alarmHour = readShared.getString("Time",null);
        SetAlarm(context, alarmHour);
    }

    public void SetAlarm(Context context, String hour)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, LokiAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();
        int hourOfDay= Integer.parseInt((hour.substring(0,hour.indexOf(":"))));
        calSet.set(Calendar.HOUR_OF_DAY, 18);
        calSet.set(Calendar.MINUTE, 31);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);
        calSet.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
        Log.d("Alarm", "Alarm Time: " + format.format(calSet.getTime()));
        am.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pi);
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
