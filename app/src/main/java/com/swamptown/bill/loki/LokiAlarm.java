package com.swamptown.bill.loki;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.content.res.Resources;

import java.util.Calendar;

/**
 * Created by bill on 5/14/16.
 */
public class LokiAlarm extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        SharedPreferences readShared;
        readShared = context.getSharedPreferences("LokiPrefs", Context.MODE_PRIVATE);
        String getSite=readShared.getString("Site", null);
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
        wl.release();
        Log.d("Alarm", "trigger alarm");
    }

    public void SetAlarm(Context context, String hour)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, LokiAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();
        int hourOfDay= Integer.parseInt((hour.substring(0,hour.indexOf(":"))));
        calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calSet.set(Calendar.MINUTE, 0);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if(calSet.compareTo(calNow) <= 0) {
            //Today Set time passed, count to tomorrow
            calSet.add(Calendar.DATE, 1);
        }
        am.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pi);
        Log.d("Alarm", "set alarm");

    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, LokiAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
