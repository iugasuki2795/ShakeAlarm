package com.example.shakealarm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by USER on 2017-01-17.
 */

public class AppService extends Service implements SensorEventListener{
    private static ClientManager cm;
    SensorManager sm;
    Sensor accSensor;
    long lastCalled = 0;

    private MainActivity ma;

    public static void setClientManager(ClientManager cmm){
        cm=cmm;
    }
    public static ClientManager getClientManager(){
        return cm;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //서비스가 시작될 때 마다 실행 시킬 것 입력
        if(PreferencesManager.isEnabled(this)){
            CheckThread thread = new CheckThread(this);
            thread.start();
        }
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate(){
        //서비스 처음 실행 될때 생성할 것 객체 생성 후, setLooping(false);로 바꿔주기,
        super.onCreate();
        sm =(SensorManager)getSystemService(SENSOR_SERVICE);
        accSensor=sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onDestroy() {
        cm=null;
        //서비스 중지되면 중지 할 것 입력 후, ".stop();"
        super.onDestroy();
        sm.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                long currentTime = System.currentTimeMillis();
                if(currentTime-lastCalled>400){
                    float average = (float)Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
                    if(average>5){
                        cm.changeState(this);
                        lastCalled=currentTime;
                    }
                    break;
                }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
    }

    class CheckThread extends Thread{
        private Context c;
        private long last =0;
        private Vibrator m_vibrator;
        private TextToSpeech tts;
        public CheckThread(Context c){
            this.c=c;
            m_vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            tts = new TextToSpeech(c, new TextToSpeech.OnInitListener(){
                @Override
                public void onInit(int status){

                }
            });
        }

        public void run(){
            while(PreferencesManager.isEnabled(c)){
                if(cm==null)continue;
                Log.i("abcd", "check");
                int state = cm.checkMyState(c);
                if(state==1){
                    if(PreferencesManager.getRoomName(c).charAt(0)>='a'&&PreferencesManager.getRoomName(c).charAt(0)<='z')
                        tts.setLanguage(Locale.ENGLISH);
                    else
                        tts.setLanguage(Locale.KOREAN);
                    tts.speak(PreferencesManager.getRoomName(c), TextToSpeech.QUEUE_FLUSH, null);
                    m_vibrator.vibrate(300);
                    try{
                        Thread.sleep(300);
                    }catch(InterruptedException e){}
                }else if(state==2){

                }
            }
            tts.stop();
            tts.shutdown();
        }
    }


}