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
    public  static  final int MODE_UPDATE =3;

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
                ArrayList<String> list =  cm.askMembers(c);
                Intent intent=new Intent(c,MainActivity.class);
                intent.putExtra("list", list);
                c.startActivity(intent);
                ((Activity)c).finish();

                break;
            case MODE_UPDATE:
                cm.updateRoom(c, r);
        }
    }
}