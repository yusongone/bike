package com.example.song.mycontroller;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.song.mycontroller.internet.Login;
import com.example.song.mycontroller.server.BT_Server;
import com.example.song.mycontroller.server.DB_Server;


public class Menu_activity extends Activity {
    private ServiceConnection conn;
    private BT_Server bt_server=null;
    private Login login_server;
    private DB_Server db_server=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_activity);
        Button osc=(Button)findViewById(R.id.osc);
        Button singleRC=(Button)findViewById(R.id.singleRC);
        Button ibeacon=(Button)findViewById(R.id.ibeacon);
        Button login=(Button)findViewById(R.id.login);
        Button db=(Button)findViewById(R.id.DB);

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
                Intent intent=new Intent(Menu_activity.this,BT.class);
                startActivity(intent);
            }
        });


        login_server=new Login();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_server.request();
            }
        });
        //db_server=new DB_Server(this,"",);
        db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db_server.test();
            }
        });
    }
}
