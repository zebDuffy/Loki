package com.swamptown.bill.loki;

import android.app.WallpaperManager;
import android.content.Context;
import android.os.AsyncTask;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
            downLoad.DoNotification("loki", "Downloaded National Geopgraphic Photo of The Day. For more information on this picture see http://photography.nationalgeographic.com/photography/photo-of-the-day/", context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
