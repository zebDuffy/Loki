package com.swamptown.bill.loki;

import android.app.WallpaperManager;
import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by bill on 7/18/16.
 */
public class GetNASAPage extends AsyncTask<String, Void, Void> {
    public Context context;
    protected Void doInBackground(String... urls) {
        try {
            Document document = Jsoup.connect("http://apod.nasa.gov/apod/astropix.html").get();
            Elements photo= document.getElementsByTag("IMG");
            Elements media = photo.select("[src]");
            URL photoURL=new URL( media.first().attr("abs:src"));
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
            downLoad.DoNotification("loki", "Downloaded Astronomy Picture of the Day. For more information see http://apod.nasa.gov/apod/astropix.html/", context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
