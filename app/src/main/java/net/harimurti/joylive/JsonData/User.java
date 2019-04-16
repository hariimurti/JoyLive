package net.harimurti.joylive.JsonData;

import net.harimurti.joylive.Classes.Converter;

import static java.lang.System.currentTimeMillis;

public class User {
    public static final String ID = "id";
    public static final String NICKNAME = "nickname";
    public static final String HEADPIC = "picture";
    public static final String ANNOUNCEMENT = "announcement";
    public static final String VIDEOPLAYURL = "videoPlayUrl";

    private String announcement;
    public String bgImg;
    public String birthday;
    public String fansNum;
    public String headPic;
    private String id;
    public boolean isPlaying;
    public String nickname;
    private String mid;
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

    public void setId(String id) {
        this.id = id;
        this.mid = id;
    }

    public String getId() {
        return this.mid == null ? this.id : this.mid;
    }
    public String getViewer() {
        return Long.toString(this.onlineNum);
    }

    public String getViewerSimple() {
        return Converter.LongNumberToHumanReadable(this.onlineNum);
    }

    public String getAnnouncement() {
        if (this.announcement != null && !this.announcement.isEmpty())
            return this.announcement;
        if (this.signature != null && !this.signature.isEmpty())
            return this.signature;
        return "Hey, come and check my show now!";
    }

    public String getPlaylistUrl() {
        return Converter.RtmpToHttp(this.videoPlayUrl);
    }

    public String getPlayStartTime() {
        return Converter.TimestampToHumanDate(this.playStartTime);
    }
}
