package net.harimurti.joylive.Classes;

import com.google.gson.Gson;

import net.harimurti.joylive.JsonData.UserFav;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Favorite {
    private static final String FAVORITE_LIST = "FAVORITE_LIST";
    private Preferences pref;
    private ArrayList<UserFav> list;

    public Favorite() {
        pref = new Preferences();
        list = get();
    }

    public void set(ArrayList<UserFav> list) {
        this.list = list;
        Gson gson = new Gson();
        String json = gson.toJson(list);
        pref.setString(FAVORITE_LIST, json);
    }

    public ArrayList<UserFav> get() {
        String json = pref.getString(FAVORITE_LIST);
        if (json.isEmpty())
            return new ArrayList<>();

        Gson gson = new Gson();
        UserFav[] joyUsers = gson.fromJson(json, UserFav[].class);
        List<UserFav> users = Arrays.asList(joyUsers);
        list = new ArrayList<>(users);
        return list;
    }

    public boolean exist(UserFav newMember) {
        for (UserFav member : list) {
            if (member.id.equals(newMember.id))
                return true;
        }
        return false;
    }

    public boolean add(UserFav user) {
        if (exist(user))
            return false;

        list.add(user);
        set(list);
        return true;
    }

    public void addOrUpdate(UserFav newMember) {
        if (exist(newMember)) {
            ArrayList<UserFav> newList = new ArrayList<>();
            for (UserFav member : list) {
                if (member.id.equals(newMember.id))
                    newList.add(newMember);
                else
                    newList.add(member);
            }
            set(newList);
        }
        else {
            list.add(newMember);
            set(list);
        }
    }

    public boolean remove(UserFav newMember) {
        if (exist(newMember)) {
            ArrayList<UserFav> newList = new ArrayList<>();
            for (UserFav member : list) {
                if (member.id.equals(newMember.id))
                    continue;

                newList.add(member);
            }
            set(newList);
            return true;
        }

        return false;
    }
}
