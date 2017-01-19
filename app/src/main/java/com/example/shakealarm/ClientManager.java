package com.example.shakealarm;

/**
 * Created by Administrator on 2017-01-17.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

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
    /*
    public int read(byte[] buffer){
        try{
            return in.read(buffer);
        }catch(IOException e){return 0;}
    }
    */
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
    public void write(byte[] b, int off, int len){
        try{
            out.write(b, off, len);
        }catch(IOException e){};
    }
    public void close(){
        try{
            in.close();
            out.close();
            socket.close();
        }catch(IOException e){}
    }

    //휴대폰 번호를 보내고 가입, 그리고 아이디를 리턴받음.
    public int sendJoin(Context context, String roomName){
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.i("abcd", manager+"");
        String phoneNumber = manager.getLine1Number();
        if (phoneNumber == null) {
            phoneNumber="nope";
        }
        PreferencesManager.setRoomName(context, roomName);
        this.writeUTF("Join");
        this.writeUTF(phoneNumber);
        this.writeUTF(roomName);

        int id = this.readInt();
        PreferencesManager.setId(context, id);
        return id;
    }

    // 방 이름 수정
    public void updateRoom(Context context, String roomName){
        PreferencesManager.setRoomName(context, roomName);
        this.writeUTF("Update");
        this.writeInt(PreferencesManager.getId(context));
        this.writeUTF(roomName);
    }

    //onCreate()에서 목록에 표시할 멤버 요청 후 리턴.
    public  ArrayList<String> askMembers(Context context){
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

    //내 폰이 흔들리는 것이 감지되면 이 메소드를 호출
    public void changeState(Context context){
        this.writeUTF("State");
        this.writeInt(PreferencesManager.getId(context));
    }

    // 내 상태를 체크함
    public int checkMyState(Context context){
        this.writeUTF("Check");
        this.writeInt(PreferencesManager.getId(context));

        String check = this.readUTF();
        if(check.equals("FALSE")){
            return 0;
       /* }else if(check.equals("VOICE")){
            int length = this.readInt();
            File file = new File("voice.3gp");
            try{
                FileOutputStream fout = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                for(;length>0;length--){
                    int size = this.read(buffer);
                    fout.write(buffer, 0, length);
                }
                fout.flush();
                fout.close();
                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(file.getPath());
                mp.prepare();
                mp.start();
            }catch(FileNotFoundException e){}
            catch (IOException e) {}
            return 2;
            */
        } else{
            return 1;
        }
    }

    // 회원 탈퇴를 위해서 id를 보내면, database에서 삭제
    public void delete(Context context){
        this.writeUTF("Delete");
        this.writeInt(PreferencesManager.getId(context));
    }

    /*
    public void upload(Context context, VoiceRecorder vr){
        File file = new File(vr.getLocation());
        try{
            FileInputStream fin = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            int data =0;
            while((length=fin.read(buffer))>0){
                data++;
            }
            fin.close();
            fin = new FileInputStream(vr.getLocation());
            this.writeUTF("File");
            this.writeInt(PreferencesManager.getId(context));
            this.writeInt(data);
            for(;data>0;data--){
                length = fin.read(buffer);
                this.write(buffer, 0, length);
            }
        }catch(FileNotFoundException e){
        }catch(IOException e){}

    }
    */

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