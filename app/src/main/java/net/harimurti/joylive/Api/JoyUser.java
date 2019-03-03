package net.harimurti.joylive.Api;

import net.harimurti.joylive.Classes.Converter;

import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

public class JoyUser {
    public static final String ID = "mid";
    public static final String NICKNAME = "nickname";
    public static final String PROFILEPIC = "headPic";
    public static final String ANNOUNCEMENT = "announcement";
    public static final String LINKSTREAM = "videoPlayUrl";

    private long playStartTime;
    private String sex;
    private String id;
    private String mid;
    private String nickname;
    private String headPic;
    private String bgImg;
    private long onlineNum;
    private String fansNum;
    private String announcement;
    private boolean isPlaying;
    private String videoPlayUrl;
    private int price;

    public JoyUser(String id, String nickname, String profilePic, String announcement, String linkStream) {
        this.id = id;
        this.mid = id;
        this.nickname = nickname;
        this.headPic = profilePic;
        this.announcement = announcement;
        this.videoPlayUrl = linkStream;
    }

    public static boolean isContainInList(ArrayList<JoyUser> list, JoyUser o) {
        String id = o.getId();
        for (JoyUser x : list) {
            if (x.getId().equals(id))
                return true;
        }
        return false;
    }

    public static ArrayList<JoyUser> updateFromList(ArrayList<JoyUser> list, JoyUser o) {
        ArrayList<JoyUser> tempList = new ArrayList<>();
        String id = o.getId();
        for (JoyUser x : list) {
            if (x.getId().equals(id))
                tempList.add(o);
            else
                tempList.add(x);
        }
        return tempList;
    }

    public static ArrayList<JoyUser> removeFromList(ArrayList<JoyUser> list, JoyUser o) {
        ArrayList<JoyUser> tempList = new ArrayList<>();
        String id = o.getId();
        for (JoyUser x : list) {
            if (x.getId().equals(id))
                continue;

            tempList.add(x);
        }
        return tempList;
    }

    public long getPlayStartTimeEpoch() {
        return this.playStartTime;
    }

    public String getPlayStartTime() {
        return Converter.TimestampToHumanDate(this.playStartTime);
    }

    public void setPlayStartTime(long timestamp) {
        this.playStartTime = timestamp;
    }

    public void setPlayStartTimeNow() {
        setPlayStartTime(currentTimeMillis()/1000);
    }

    public String getSex() {
        return this.sex;
    }

    public String getId() {
        return this.mid.isEmpty() ? this.id : this.mid;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void  setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfilePic() {
        return this.headPic;
    }

    public void setProfilepic(String profilepic) {
        this.headPic = profilepic;
    }

    public String getBackgroundImage() {
        return this.bgImg;
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

    public String getFansNum() {
        return this.fansNum;
    }

    public int getPrice() {
        return this.price;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public String getLinkStream() {
        return this.videoPlayUrl;
    }

    public String getLinkPlaylist() {
        return Converter.RtmpToHttp(this.videoPlayUrl);
    }
}
