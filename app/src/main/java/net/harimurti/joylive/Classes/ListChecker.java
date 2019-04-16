package net.harimurti.joylive.Classes;

import net.harimurti.joylive.JsonData.User;

import java.util.ArrayList;

public class ListChecker {
    private ArrayList<User> list;

    public ListChecker(ArrayList<User> list) {
        this.list = list;
    }

    public boolean isContain(User o) {
        String id = o.getId();
        for (User x : list) {
            if (x.getId().equals(id))
                return true;
        }
        return false;
    }
}
