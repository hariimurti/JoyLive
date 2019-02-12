package net.harimurti.joylive.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import net.harimurti.joylive.Api.JoyUser;
import net.harimurti.joylive.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Preferences {

    public static final String KEY_3RD_PLAYER = "USE_3RD_PLAYER";
    public static final String FAVORITE_LIST = "FAVORITE_LIST";

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

    public ArrayList<JoyUser> getFavorite() {
        String json = getString(FAVORITE_LIST);
        if (json.isEmpty())
            return new ArrayList<>();

        Gson gson = new Gson();
        JoyUser[] joyUsers = gson.fromJson(json, JoyUser[].class);
        List<JoyUser> users = Arrays.asList(joyUsers);
        return new ArrayList<>(users);
    }

    public void setFavorite(ArrayList<JoyUser> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        setString(FAVORITE_LIST, json);
    }

    public boolean isFavorite(JoyUser user) {
        ArrayList<JoyUser> list = getFavorite();
        return JoyUser.isContainInList(list, user);
    }

    public boolean addFavorite(JoyUser user) {
        if (isFavorite(user))
            return false;

        ArrayList<JoyUser> list = getFavorite();
        list.add(user);
        setFavorite(list);
        return true;
    }

    public void addOrUpdateFavorite(JoyUser user) {
        if (!isFavorite(user)) {
            addFavorite(user);
            return;
        }

        ArrayList<JoyUser> list = getFavorite();
        ArrayList<JoyUser> newList = JoyUser.updateFromList(list, user);
        setFavorite(newList);
    }

    public boolean remFavorite(JoyUser user) {
        if (!isFavorite(user))
            return false;

        ArrayList<JoyUser> list = getFavorite();
        ArrayList<JoyUser> newList = JoyUser.removeFromList(list, user);
        setFavorite(newList);
        return true;
    }
}
