package com.example.song.mycontroller;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;


/**
 * Created by song on 14/12/13.
 */
public class Rocker extends View {
    private int DOM_width;
    private int DOM_height;
    private Runnable runnable;
    private Rocker_center rc;
    private Paint p;
    private float initx=0.5f;
    private float inity=0.5f;
    private OnDataChange odc=null;
    public Rocker(Context context) {
        super(context);
    }
    public Rocker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public Rocker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void onMeasure(int width,int height){
        //int a=2147483647;
        super.onMeasure(width, height);
        int _width=MeasureSpec.getSize(width);
        int _height=MeasureSpec.getSize(height);

        if(_width>680){ _width=680; }
        if(_height>680){ _height=680; }
        setMeasuredDimension(_width,_height);
        DOM_width=_width;
        DOM_height=_height;
        rc=new Rocker_center(DOM_width,DOM_height);
        p=new Paint();
        p.setAntiAlias(true);
        rc.setPointPosition(initx*DOM_width,(1-inity)*DOM_height);
       // bindDefaultEvent();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==event.ACTION_MOVE){
            rc.setPointPosition(event.getX(), event.getY());
            return true;
        }
        return true;
    }
    public void setPointPosition(float Pitch,float Roll){
        rc.setPointPosition(DOM_width/2+Pitch,DOM_height/2+Roll);
    }
    protected void onDraw(Canvas canvas){
        rc.update();
        rc.draw(p,canvas);
        super.onDraw(canvas);
    }
    public void starAnimate(){
        final Rocker r=this;
        final Handler h=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                r.invalidate();
                h.postDelayed(runnable,30);
            }
        };
        runnable.run();
    }
    public void reCenter(boolean bx,boolean by){
       rc.reCenter(bx,by);
    };
    public void initValue(float valuex,float valuey){
        initx=valuex;
        inity=valuey;
    }
    public void setRawDataChange(OnDataChange change){
        odc=change;
    }


    public static class OnDataChange{
        public void onChange(float rawX,float rawY){

        }
    }

    class Rocker_center{
        private int strokeWidth=50;
        private int size=50;
        private int max_x;
        private int max_y;
        private float centerX;
        private float centerY;
        private float pointX;
        private float pointY;
        private float outputX;
        private float outputY;
        private float valueX;
        private float valueY;
        private int whiteRectSize=150;
        private boolean dir=true;
        public Rocker_center(int width,int height){
            max_x=width;
            max_y=height;
            centerX=max_x/2;
            centerY=max_y/2;
            pointX=centerX;
            pointY=centerY;
        }
        public void update(){
            if(strokeWidth<120){
                strokeWidth+=2;
                if(strokeWidth>=120){
                    strokeWidth=50;
                }
            }
            /*
            else if((!dir)&&strokeWidth>0){
                strokeWidth-=1;
                if(strokeWidth<=50){
                    dir=true;
                }
            }
            */
        };
        private void computeValue(){
           valueX=pointX/max_x;
           valueY=1-(pointY/max_y);
        }
        public void setPointPosition(float x,float y){
            if(x>max_x){
                pointX=max_x;
            }else if(x<0){
                pointX=0;
            }else{
                pointX=x;
            }
            if(y>max_y){
                pointY=max_y;
            }else if(y<0){
                pointY=0;
            }else{
                pointY=y;
            }
            computeValue();
            if(odc!=null){
                odc.onChange(valueX,valueY);
            }
        };
        public void reCenter(boolean x,boolean y){
            if(x){
                setPointPosition(centerX,pointY);
            }
            if(y){
                setPointPosition(pointX,centerY);
            }
        };

        public void draw(Paint p,Canvas canvas){
            p.setARGB(250, 250, 0, 0);
            this.drawBorder(p, canvas);
            this.drawCenter(p, canvas);
            this.drawCenterLine(p, canvas);
            this.drawStroke(p, canvas);
            this.drawPoint(p, canvas);
            this.drawText(p, canvas);
        };
        private void drawStroke(Paint p,Canvas canvas){
            p.setARGB(255-(strokeWidth*255/120),30,240,255);
            canvas.drawCircle(pointX,pointY,strokeWidth,p);
        }
        private void drawPoint(Paint p,Canvas canvas){
            p.setColor(getResources().getColor(R.color.blue_b));
            canvas.drawCircle(pointX,pointY,size,p);
        };
        private void drawCenter(Paint p,Canvas canvas){
            p.setColor(getResources().getColor(R.color.blue_b));
            canvas.drawCircle(centerX, centerY, 20, p);
            Rect r=new Rect((int)centerX-30,(int)centerY-15,(int)centerX+30,(int)centerY+15);
            canvas.drawRect(r,p);
        };
        private void drawText(Paint p,Canvas canvas){
            p.setColor(getResources().getColor(R.color.blue_a));
            p.setTextSize(20);
            canvas.drawText("X: "+(int)(valueX*100)+"%", pointX>max_x-150?pointX-150:pointX+80,pointY<100?pointY+50:pointY-80, p);
            canvas.drawText("Y: "+(int)(valueY*100)+"%", pointX>max_x-150?pointX-150:pointX+80,pointY<100?pointY+80:pointY-50, p);
        };
        private void drawBorder(Paint p,Canvas canvas){
            p.setColor(getResources().getColor(R.color.blue_b));
            p.setStrokeWidth(20);
            RectF rf=new RectF(0,0,max_x,max_y);
            canvas.drawRoundRect(rf,0,0,p);
            p.setColor(getResources().getColor(R.color.blue_a));
            Rect r2=new Rect(5,5,max_x-5,max_y-5);
            canvas.drawRect(r2, p);
            p.setColor(getResources().getColor(R.color.black));
            Rect r3=new Rect(10,10,max_x-10,max_y-10);
            canvas.drawRect(r3, p);
            Rect r4=new Rect((int)centerX-whiteRectSize,0,(int)centerX+whiteRectSize,max_y);
            canvas.drawRect(r4, p);
            Rect r5=new Rect(0,(int)centerY-whiteRectSize,max_x,(int)centerY+whiteRectSize);
            canvas.drawRect(r5,p);
        };
        private void drawCenterLine(Paint p,Canvas canvas){
            p.setColor(getResources().getColor(R.color.blue_a));
            p.setStrokeWidth(2);
            canvas.drawLine(pointX,5,pointX,max_y-5,p);
            canvas.drawLine(5,pointY,max_x-5,pointY,p);
        };
    }

}

