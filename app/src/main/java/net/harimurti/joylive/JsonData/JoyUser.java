package net.harimurti.joylive.JsonData;

import net.harimurti.joylive.Classes.Converter;

public class JoyUser {
    public static final String ID = "mid";
    public static final String NICKNAME = "nickname";
    public static final String PROFILEPIC = "headPic";
    public static final String ANNOUNCEMENT = "announcement";
    public static final String LINKSTREAM = "videoPlayUrl";

    private long playStartTime;
    private String sex;
    private String mid;
    private String nickname;
    private String headPic;
    private long onlineNum;
    private String announcement;
    private String videoPlayUrl;

    public JoyUser(String id, String nickname, String profilePic, String announcement, String linkStream) {
        this.mid = id;
        this.nickname = nickname;
        this.headPic = profilePic;
        this.announcement = announcement;
        this.videoPlayUrl = linkStream;
    }

    public String getPlayStartTime() {
        return Converter.TimestampToHumanDate(this.playStartTime);
    }

    public String getSex() {
        return this.sex;
    }

    public String getId() {
        return this.mid;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getProfilePic() {
        return this.headPic;
    }

    public String getViewer() {
        return Converter.LongNumberToHumanReadable(this.onlineNum);
    }

    public String getAnnouncement() {
        return this.announcement;
    }

    public String getLinkStream() {
        return this.videoPlayUrl;
    }

    public String getLinkPlaylist() {
        return Converter.RtmpToHttp(this.videoPlayUrl);
    }
}
