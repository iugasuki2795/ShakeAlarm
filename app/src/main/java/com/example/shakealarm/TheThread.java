package com.example.shakealarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-01-17.
 */

public class TheThread extends Thread{
    public enum mode {JOIN, REQUEST, UPDATE, DELETE, FILE};

    mode mode;
    Context c;
    String r;
    VoiceRecorder vr;

    public TheThread(mode mode, Context context,String room_name){
        this.mode=mode;
        c=context;
        r=room_name;
    }
    public TheThread(mode mode, Context context){
        this.mode=mode;
        this.c=context;
    }
    public TheThread(mode mode, Context context, VoiceRecorder vr){
        this.mode=mode;
        this.c=context;
        this.vr=vr;
    }

    public void run(){
        ClientManager cm = AppService.getClientManager();
        if(cm==null)
            cm = new ClientManager(PreferencesManager.IP, PreferencesManager.port);
        Log.i("abcd", cm+"");
        switch (mode){
            case JOIN:
                cm.sendJoin(c, r);
            case REQUEST:
                AppService.setClientManager(cm);
                ArrayList<String> list =  cm.askMembers(c);
                Intent intent=new Intent(c,MainActivity.class);
                intent.putExtra("list", list);
                c.startActivity(intent);
                ((Activity)c).finish();
                break;
            case UPDATE:
                cm.updateRoom(c, r);
                Toast.makeText(c,"알람 코드가 바뀌었습니다",Toast.LENGTH_SHORT).show();
                break;
            case DELETE:
                cm.delete(c);
                PreferencesManager.setId(c, -1);
                break;
            case FILE:
                cm.upload(c, vr);
                break;
        }
    }
}