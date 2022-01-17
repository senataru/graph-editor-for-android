package com.example.graph_editor.draw;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

    public static void setStylus(Context context, boolean checked) {
        SharedPreferences sharedPref = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("settingsStylus", checked);
        editor.apply();
    }
    public static void setButtons(Context context, boolean checked) {
        SharedPreferences sharedPref = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("settingsButtons", checked);
        editor.apply();
    }
    public static boolean getStylus(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("settingsStylus", false);
    }
    public static boolean getButtons(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("settingsButtons", false);
    }
}
