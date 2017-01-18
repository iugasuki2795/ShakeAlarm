package com.example.shakealarm;

/**
 * Created by Administrator on 2017-01-17.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private int readInt(){
        try{
            return in.readInt();
        }catch(IOException e){ return -1; }
    }
    private String readUTF(){
        try{
            return in.readUTF();
        }catch(IOException e){ return null; }
    }
    private int read(byte[] buffer){
        try{
            return in.read(buffer);
        }catch(IOException e){return 0;}
    }
    private void writeInt(int i){
        try{
            Log.i("abcd", "write/"+i);
            out.writeInt(i);
        }catch(IOException e){}
    }
    private void writeUTF(String s){
        try{
            Log.i("abcd", "write/"+s);
            out.writeUTF(s);
        }catch(IOException e){}
    }
    private void write(byte[] b, int off, int len){
        try{
            Log.i("abcd", "write"+b.toString());
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
        int id;
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = manager.getLine1Number();
        if (phoneNumber == null) {
            phoneNumber="nope";
        }
        PreferencesManager.setRoomName(context, roomName);
        synchronized (this){
            this.writeUTF("Join");
            this.writeUTF(phoneNumber);
            this.writeUTF(roomName);

            id = this.readInt();
        }
        PreferencesManager.setId(context, id);
        return id;
    }

    // 방 이름 수정
    public void updateRoom(Context context, String roomName){
        PreferencesManager.setRoomName(context, roomName);
        synchronized (this){
            this.writeUTF("Update");
            this.writeInt(PreferencesManager.getId(context));
            this.writeUTF(roomName);
        }
    }

    //onCreate()에서 목록에 표시할 멤버 요청 후 리턴.
    public ArrayList<String> askMembers(Context context){
        ArrayList<String> members = new ArrayList<>();
        synchronized (this){
            this.writeUTF("AskMember");
            this.writeInt(PreferencesManager.getId(context));

            int count = this.readInt();
            for(int i=0;i<count;i++){
                String number = this.readUTF();
                members.add(getNameFromNumber(context, number));
            }
        }
        return members;
    }

    //내 폰이 흔들리는 것이 감지되면 이 메소드를 호출
    public synchronized void changeState(Context context){
        this.writeUTF("State");
        this.writeInt(PreferencesManager.getId(context));
    }

    // 내 상태를 체크함
    public synchronized int checkMyState(Context context){
        Log.i("abcd", "checkSyncStart");
        this.writeUTF("Check");
        Log.i("abcd", "1");
        this.writeInt(PreferencesManager.getId(context));
        Log.i("abcd", PreferencesManager.getId(context)+"/2");

        String check = this.readUTF();
        Log.i("abcd", check+"/3");
        if(check.equals("FALSE")){
            Log.i("abcd", "check1SyncEnd");
            return 0;
        }else if(check.equals("VOICE")){
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
            Log.i("abcd", "check2SyncEnd");
            return 2;
        } else{
            return 1;
        }

    }

    // 회원 탈퇴를 위해서 id를 보내면, database에서 삭제
    public synchronized void delete(Context context){
        this.writeUTF("Delete");
        this.writeInt(PreferencesManager.getId(context));

        // 이렇게 하면 database에서 클라이언트 정보가 삭제 됩니다
        // 어플은 클라이언트가 알아서 지우겠죠?
    }

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
            synchronized (this){
                this.writeUTF("File");
                this.writeInt(PreferencesManager.getId(context));
                this.writeInt(data);
                for(;data>0;data--){
                    length = fin.read(buffer);
                    this.write(buffer, 0, length);
                }
            }
        }catch(FileNotFoundException e){
        }catch(IOException e){}
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