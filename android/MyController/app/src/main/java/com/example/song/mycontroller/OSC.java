package com.example.song.mycontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by song on 15/1/20.
 */
public class OSC extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder sfh;
    private ArrayList<Line> al=new ArrayList<Line>();
    private Canvas sfh_canvas;

    public OSC(Context context) {
        super(context);
        sfh=this.getHolder();
        sfh.addCallback(this);
        this.setKeepScreenOn(true);
    }

    public OSC(Context context, AttributeSet attrs) {
        super(context, attrs);
        sfh=this.getHolder();
        sfh.addCallback(this);
        this.setKeepScreenOn(true);
    }

    public OSC(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        sfh=this.getHolder();
        sfh.addCallback(this);
        this.setKeepScreenOn(true);
    }
    public sensorData getSensorData(){
       return new sensorData();
    };
    public Line getLine(int color){
        Line l=new Line(color);
        al.add(l);
        return l;
    }

    public void draw(){
        int d=al.size();
        sfh_canvas = sfh.lockCanvas();
        sfh_canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for(int i = 0;i<d;i++){
            al.get(i).draw(sfh_canvas);
        }
        sfh.unlockCanvasAndPost(sfh_canvas);
    }

    class sensorData{
        private boolean fill=false;
        private float Data=0;
        private float dt;
        public void setData(float _data){
            Data=_data;
            fill=true;
        }
        public void setDt(float _dt){
            dt=0.0f;
            dt=_dt;
        }
        public float getDt(){
           return  dt;
        }
        public float getData(){
           return Data;
        }
        public boolean isFill(){
            return fill;
        }
        public void clear(){
            fill=false;
            Data=0;
        }
    }

    class Line{
        private int pointList[]=new int[500];
        private int _color;
        private Paint p;
        public Line(int color){
           _color=color;
            p=new Paint();
            p.setColor(_color);
        }
        public void pushPoint(int x){
            int lgth=pointList.length;
            pointList[lgth-1]=x;
            for(int i=0;i<lgth-1;i++){
                pointList[i]=pointList[i+1];
            }
        }

        public void draw(Canvas canvas){
            int lgth=pointList.length;
            for(int i=0;i<lgth-1;i++){
                canvas.drawLine(i, pointList[i] + 200, (i + 1), pointList[i + 1] + 200, p);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Canvas canvas=sfh.lockCanvas();
        //myThread mt=new myThread();
        //mt.start();
        //this.setBackgroundColor(Color.argb(255,255,255,255));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    class myThread extends Thread{
        private Canvas canvas;
        private Paint p;
        int i=0;
        Line line1;
        Line line2;
        public myThread(){
            line1=new Line(Color.argb(250,250,0,0));
            line2=new Line(Color.argb(250,0,250,0));
            p=new Paint();
            p.setARGB(255,250,250,250);
        }

        //Override
        public void run(){
            try {
                while (!(Thread.currentThread().isInterrupted())) {
                    int max=200;
                    int min=10;
                    Random random = new Random();
                    int s1 = random.nextInt(max)%(max-min+1) + min;
                    Log.e("a", s1 + "");
                    line1.pushPoint(s1);
                    sleep(16);
                }
            }catch(Exception e){
              e.printStackTrace();
            }
        }
    }
}
