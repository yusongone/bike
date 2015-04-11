package com.example.song.mycontroller.internet;

import android.os.Handler;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

/**
 * Created by song on 15/3/24.
 */
public class Login{
    private URL url;
    public  String cookie=null;
    Thread get;
    public Login(){

    }
    public void request(String s){
        get=new get(s);
        get.start();
    }

    private class get extends Thread{
        private String s;
        public get(String s){
            this.s=s;
        }
        public void run(){
            //HttpPost hp=new HttpPost("http://www.makejs.com/login");
            HttpPost hp=new HttpPost("http://192.168.1.106:3420/put");
            //HttpPost hp=new HttpPost("http://192.168.100.132:4400/login");
            HttpClient httpClient=new DefaultHttpClient();
            LinkedList p=new LinkedList();
            p.add(new BasicNameValuePair("text",s));
            p.add(new BasicNameValuePair("id","song"));
            p.add(new BasicNameValuePair("pass","song"));
            try {
                hp.addHeader("Cookie",cookie);
                if(cookie!=null){
//                hp.addHeader("Cookie",cookie);
                }
                hp.setEntity(new UrlEncodedFormEntity(p, "utf-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                Log.e("Login", "connecting...");
                HttpResponse res=httpClient.execute(hp);
                int d=res.getStatusLine().getStatusCode();
                Log.e("Login", EntityUtils.toString(res.getEntity(),"utf-8")+"");
                Header[] headers = res.getHeaders("Set-Cookie");
                if(headers.length>0){
                    String cookieval=headers[0].getValue();
                    cookie=cookieval+"ssid=123;";//cookieval.substring(0, cookieval.indexOf(";")); ;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

