package com.example.song.mycontroller.internet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.ViewFlipper;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by song on 14/12/14.
 */
public class GetDataFromInternet {
    public Bitmap getImageBitmap(String url){
        Bitmap bitmap=null;
        InputStream in=getInputStream(url);
        try {
            BufferedInputStream bis = new BufferedInputStream(in);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            bitmap = BitmapFactory.decodeByteArray(baf.toByteArray(), 0, baf.toByteArray().length);
        }catch(OutOfMemoryError e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public JSONObject getPageData(){
        Log.d("debug","request page data.");
        JSONObject jo=null;
        InputStream in=getInputStream("http://cn.nytguide.com/json");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            byte[] data = new byte[300];
            int count = -1;
            while((count = in.read(data,0,300)) != -1) {
                outStream.write(data, 0, count);
            }
            data = null;
            String read=new String(outStream.toByteArray(),"UTF-8");
                jo=new JSONObject(read);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jo;
    }
    public void doIt(final ListView lv,final ViewFlipper vf){
        Handler h=new Handler();

        //h.post(new Runnable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
            }
        }).start();
        //});
    }
    private InputStream getInputStream(String _url){
        InputStream in=null;
        try {
            URL url=new URL(_url);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            in=conn.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return in;
        }
    }
}
