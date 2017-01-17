package com.example.shakealarm;

/**
 * Created by Administrator on 2017-01-17.
 */


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import static android.util.Log.i;


public class ClientManager {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientManager(String ip, int port){
        try{
            socket = new Socket(ip, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }catch(IOException e){}
    }
    public int readInt(){
        try{
            return in.readInt();
        }catch(IOException e){ return -1; }
    }
    public String readUTF(){
        try{
            return in.readUTF();
        }catch(IOException e){ return null; }
    }
    public void writeInt(int i){
        try{
            out.writeInt(i);
        }catch(IOException e){}
    }
    public void writeUTF(String s){
        try{
            out.writeUTF(s);
        }catch(IOException e){}
    }
    public void close(){
        try{
            in.close();
            out.close();
            socket.close();
        }catch(IOException e){}
    }

    public int sendJoin(Context context, String roomName){//휴대폰 번호를 보내고 가입, 그리고 아이디를 리턴받음.
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = manager.getLine1Number();
        PreferencesManager.setRoomName(context, roomName);
        this.writeUTF("Join");
        this.writeUTF(phoneNumber);
        this.writeUTF(roomName);

        int id = this.readInt();
        PreferencesManager.setId(context, id);
        return id;
    }

    public void updateRoom(Context context, String roomName){//방 이름 수정
        PreferencesManager.setRoomName(context, roomName);
        this.writeUTF("Update");
        this.writeInt(PreferencesManager.getId(context));
        this.writeUTF(roomName);
    }

    public ArrayList<String> askMembers(Context context){//onCreate()에서 목록에 표시할 멤버 요청 후 리턴.
        this.writeUTF("AskMember");
        this.writeInt(PreferencesManager.getId(context));

        ArrayList<String> members = new ArrayList<>();
        int count = this.readInt();
        for(int i=0;i<count;i++){
            String number = this.readUTF();
            members.add(getNameFromNumber(context, number));
        }
        return members;
    }

    public void changeState(Context context){//내 폰이 흔들리는 것이 감지되면 이 메소드를 호출
        this.writeUTF("State");
        this.writeInt(PreferencesManager.getId(context));
    }


    public boolean checkMyState(Context context){
        this.writeUTF("Check");
        this.writeInt(PreferencesManager.getId(context));


        String check = this.readUTF();

        if(check.equals("FALSE")){
            return false;
        }else{
            return true;
        }
    }

    private String getNameFromNumber(Context context, String number){
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null, null);
        if(cursor.moveToNext()){
            return cursor.getString(0);
        }
        return number;
    }

}