package net.harimurti.joylive.JsonClass;

import net.harimurti.joylive.Classes.Converter;

import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

public class User {
    public static final String MID = "mid";
    public static final String NICKNAME = "nickname";
    public static final String PROFILEPIC = "headPic";
    public static final String ANNOUNCEMENT = "announcement";
    public static final String LINKSTREAM = "videoPlayUrl";

    public String announcement;
    public String bgImg;
    public String birthday;
    public String fansNum;
    public String headPic;
    public String id;
    public boolean isPlaying;
    public String nickname;
    public String mid;
    public long onlineNum;
    public long playStartTime;
    public int price;
    public String rid;
    public String sex;
    public String signature;
    public String videoPlayUrl;

    public User(String id, String nickname, String profilePic, String announcement, String linkStream) {
        this.id = id;
        this.mid = id;
        this.nickname = nickname;
        this.headPic = profilePic;
        this.announcement = announcement;
        this.videoPlayUrl = linkStream;
    }

    public static boolean isContainInList(ArrayList<User> list, User o) {
        String id = o.getId();
        for (User x : list) {
            if (x.getId().equals(id))
                return true;
        }
        return false;
    }

    public static ArrayList<User> updateFromList(ArrayList<User> list, User o) {
        ArrayList<User> tempList = new ArrayList<>();
        String id = o.getId();
        for (User x : list) {
            if (x.getId().equals(id))
                tempList.add(o);
            else
                tempList.add(x);
        }
        return tempList;
    }

    public static ArrayList<User> removeFromList(ArrayList<User> list, User o) {
        ArrayList<User> tempList = new ArrayList<>();
        String id = o.getId();
        for (User x : list) {
            if (x.getId().equals(id))
                continue;

            tempList.add(x);
        }
        return tempList;
    }

    public String getPlayStartTime() {
        return Converter.TimestampToHumanDate(this.playStartTime);
    }

    public void setPlayStartTimeNow() {
        this.playStartTime = currentTimeMillis()/1000;
    }

    public String getId() {
        return this.mid.isEmpty() ? this.id : this.mid;
    }

    public String getViewerHumanReadable() {
        return Converter.LongNumberToHumanReadable(this.onlineNum);
    }

    public String getViewer() {
        return Long.toString(this.onlineNum);
    }

    public String getAnnouncement() {
        if (this.announcement.isEmpty())
            return "Hey, come and check my show now!";

        return this.announcement;
    }

    public String getSignature() {
        if (this.signature.isEmpty())
            return "Hey, come and check my show now!";

        return this.signature;
    }

    public String getLinkPlaylist() {
        return Converter.RtmpToHttp(this.videoPlayUrl);
    }
}
