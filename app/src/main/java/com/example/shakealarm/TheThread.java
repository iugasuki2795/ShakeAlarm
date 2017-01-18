package com.example.shakealarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

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
    public TheThread(int mode, Context context){
        this.mode=mode;
        this.c=context;
    }

    public void run(){
        ClientManager cm = AppService.getClientManager();
        if(cm==null)
            cm = new ClientManager(PreferencesManager.IP, PreferencesManager.port);
        Log.i("abcd", cm+"");
        switch (mode){
            case MODE_JOIN:
                cm.sendJoin(c, r);
            case MODE_REQUEST:
                AppService.setClientManager(cm);
                Log.i("abcd", "1");
                ArrayList<String> list =  cm.askMembers(c);
                Log.i("abcd", "2");
                Intent intent=new Intent(c,MainActivity.class);
                Log.i("abcd", "3");
                intent.putExtra("list", list);
                Log.i("abcd", "4");
                c.startActivity(intent);
                Log.i("abcd", "5");
                ((Activity)c).finish();
                break;
        }
    }
}