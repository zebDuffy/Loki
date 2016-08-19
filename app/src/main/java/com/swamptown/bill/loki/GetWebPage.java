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
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.BroadcastReceiver;


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

/**
 * Created by bill on 5/15/16.
 */
public class GetWebPage extends AsyncTask<String, Context, Void> {
    public Context context;

    protected Void doInBackground(String... urls) {
        try {

            Document document = Jsoup.connect("http://photography.nationalgeographic.com/photography/photo-of-the-day/").get();
            Elements metalinks = document.select("meta[name=twitter:image:src]");
            String media = metalinks.attr("content");
            URL photoURL=new URL(media);
            URLConnection imageCon=photoURL.openConnection();

            InputStream is = imageCon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            FileOutputStream fos = new FileOutputStream(context.getFilesDir().getPath()+"desktop.jpg");
            int current = 0;
            while ((current = bis.read()) != -1) {
                fos.write(current);
            }
            WallpaperManager imageWallpapermanager = WallpaperManager.getInstance(context.getApplicationContext());
            imageWallpapermanager.setStream(new FileInputStream(context.getFilesDir().getPath()+"desktop.jpg"));
            DownloadNotification downLoad=new DownloadNotification();
            downLoad.DoNotification("loki", "Downloaded NG Photo", context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
