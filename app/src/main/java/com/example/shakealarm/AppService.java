package com.example.shakealarm;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by USER on 2017-01-17.
 */

public class AppService extends Service implements SensorEventListener{

    SensorManager sm;
    Sensor accSensor;
    long lastCalled = 0;

    private MainActivity ma;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //서비스가 시작될 때 마다 실행 시킬 것 입력
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
                        Log.i("abcd", "shake");
                        lastCalled=currentTime;
                    }
                    break;
                }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
    }


}