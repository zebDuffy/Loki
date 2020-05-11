package com.swamptown.bill.loki;

import android.app.WallpaperManager;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class GetImageWallpaper extends AsyncTask<String, Context, Void> {
    public Context context;
    protected Void doInBackground(String... urls) {
        try {
            URL photoURL = new URL(urls[0]);
            URLConnection imageCon = null;
            imageCon = photoURL.openConnection();


            InputStream is = imageCon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            FileOutputStream fos = new FileOutputStream(context.getFilesDir().getPath() + "desktop.jpg");
            int current = 0;
            while ((current = bis.read()) != -1) {
                fos.write(current);
            }
            WallpaperManager imageWallpapermanager = WallpaperManager.getInstance(context.getApplicationContext());
            imageWallpapermanager.setStream(new FileInputStream(context.getFilesDir().getPath() + "desktop.jpg"));
            DownloadNotification downLoad = new DownloadNotification();
            downLoad.DoNotification("loki", "Downloaded " +urls[1], context);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
