package com.example.shakealarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 2017-01-17.
 */

public class TheThread extends Thread{
    public static final int MODE_JOIN = 1;
    public static final int MODE_REQUEST = 2;

    int mode;
    Context c;
    String r;
    public TheThread(int mode, Context context,String room_name){
        this.mode=mode;
        c=context;
        r=room_name;
    }

    public void run(){
        Log.i("abcd", mode+"");
        switch (mode){
            case MODE_JOIN:
                ClientManager cm = new ClientManager(PreferencesManager.IP, PreferencesManager.port);
                Log.i("abcd", cm+"");
                cm.sendJoin(c, r);
                Intent intent=new Intent(c,MainActivity.class);
                c.startActivity(intent);
                ((Activity)c).finish();
                break;
        }
    }
}