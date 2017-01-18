package com.example.shakealarm;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    public static final String IP= "172.17.192.81";
    public static final int port = 12345;


    public static void setId(Context c, int id){
        SharedPreferences pref = c.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("id", id).commit();
    }
    public static int getId(Context c){
        SharedPreferences pref = c.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getInt("id", -1);
    }
    public static void setRoomName(Context c, String name){
        SharedPreferences pref = c.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name", name).commit();
    }
    public static String getRoomName(Context c){
        SharedPreferences pref = c.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString("name", "");
    }
    public static void setEnabled(Context c, boolean enabled){
        SharedPreferences pref = c.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("enabled", enabled).commit();
    }
    public static boolean isEnabled(Context c){
        SharedPreferences pref = c.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getBoolean("enabled", true);
    }
}