package com.example.shakealarm;

import android.content.Context;

/**
 * Created by Administrator on 2017-01-17.
 */

public class TheThread extends Thread{
    public static final int MODE_JOIN = 1;

    int mode;
    Context c;
    String r;
    public TheThread(int mode, Context context,String room_name){
        this.mode=mode;
        c=context;
        r=room_name;
    }

    public void run(){
        switch (mode){
            case MODE_JOIN:
                ClientManager cm = new ClientManager(PreferencesManager.IP, PreferencesManager.port);
                cm.sendJoin(c, r);
                break;
        }
    }
}