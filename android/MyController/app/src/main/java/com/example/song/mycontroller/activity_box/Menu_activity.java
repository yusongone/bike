package com.example.song.mycontroller.activity_box;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.song.mycontroller.OSC_box;
import com.example.song.mycontroller.R;
import com.example.song.mycontroller.server.Main_server;
import com.example.song.mycontroller.database.MyDatabase;
import com.example.song.mycontroller.internet.Login;


public class Menu_activity extends Activity {
    private ServiceConnection conn;
    private Main_server mainserver =null;
    private Login login_server;
    private MyDatabase myDatabase=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_activity);
        Button osc=(Button)findViewById(R.id.osc);
        Button singleRC=(Button)findViewById(R.id.singleRC);
        Button ibeacon=(Button)findViewById(R.id.ibeacon);
        Button login=(Button)findViewById(R.id.login);
        Button db=(Button)findViewById(R.id.DB);
        myDatabase=new MyDatabase(this,"df",null,1);

        osc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(Menu_activity.this,OSC_box.class);
                startActivity(intent);
            }
        });

        singleRC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(Menu_activity.this,Single_RC.class);
                startActivity(intent);
            }
        });


        ibeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Menu_activity.this,Dashboard.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //login_server.request();
                SQLiteDatabase db=myDatabase.getWritableDatabase();
                myDatabase.startRecord(db);
                String s=myDatabase.search();
                Login l=new Login();
                Log.e("ff","request s");
                l.request(s);

            }
        });
        db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] a=new float[10];
            }
        });
    }
}
