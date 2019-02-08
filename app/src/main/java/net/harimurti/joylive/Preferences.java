package net.harimurti.joylive;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    public static final String KEY_3RD_PLAYER = "USE_3RD_PLAYER";

    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;

    public Preferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public int getInteger(String key) {
        return preferences.getInt(key, 0);
    }

    public void setBoolean(String key, boolean value) {
        prefEditor = preferences.edit();
        prefEditor.putBoolean(key, value);
        prefEditor.apply();
    }

    public void setInteger(String key, int value) {
        prefEditor = preferences.edit();
        prefEditor.putInt(key, value);
        prefEditor.apply();
    }
}
