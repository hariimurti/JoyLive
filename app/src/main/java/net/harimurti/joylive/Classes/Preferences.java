package net.harimurti.joylive.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.harimurti.joylive.App;

import java.util.UUID;

public class Preferences {
    public static final String DEVICE_SERIAL = "DEVICE_SERIAL";
    public static final String APP_KEY = "APP_KEY";

    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;

    public Preferences() {
        Context context = App.getContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public void setBoolean(String key, boolean value) {
        prefEditor = preferences.edit();
        prefEditor.putBoolean(key, value);
        prefEditor.apply();
    }

    public int getInteger(String key) {
        return preferences.getInt(key, 0);
    }

    public void setInteger(String key, int value) {
        prefEditor = preferences.edit();
        prefEditor.putInt(key, value);
        prefEditor.apply();
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public void setString(String key, String value) {
        prefEditor = preferences.edit();
        prefEditor.putString(key, value);
        prefEditor.apply();
    }

    public String getDeviceSerial() {
        String serial = getString(DEVICE_SERIAL);

        if (serial.isEmpty()) {
            serial = UUID.randomUUID().toString();
            setString(DEVICE_SERIAL, serial);
        }

        return serial;
    }

    public boolean isRegistered() {
        String key = getString(APP_KEY);
        if (key.isEmpty()) return false;

        String serial = App.getSerialNumber();
        return Converter.DecodeKey(key).equals(serial);
    }

    public boolean Register(String key) {
        if (key.isEmpty()) return false;

        String serial = App.getSerialNumber();
        if (Converter.DecodeKey(key).equals(serial)) {
            setString(APP_KEY, key);
            return true;
        }
        return false;
    }
}
