package net.harimurti.joylive.JsonData;

import net.harimurti.joylive.Classes.Converter;

public class JoyUser {
    private String rid;
    private long playStartTime;
    private String sex;
    private String mid;
    private String nickname;
    private String headPic;
    private long onlineNum;
    private String announcement;
    private String level;
    private String moderatorLevel;
    private String videoPlayUrl;

    public JoyUser(String mid, String nickname, String headPic, String videoPlayUrl) {
        this.mid = mid;
        this.nickname = nickname;
        this.headPic = headPic;
        this.videoPlayUrl = videoPlayUrl;
    }

    public String getRid() {
        return this.rid;
    }

    public String getPlayStartTime() {
        return Converter.TimestampToHumanDate(this.playStartTime);
    }

    public String getSex() {
        return this.sex;
    }

    public String getMid() {
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

    public String getLevel() {
        return this.level;
    }

    public String getModeratorLevel() {
        return this.moderatorLevel;
    }

    public String getLinkStream() {
        return this.videoPlayUrl;
    }

    public String getLinkPlaylist() {
        return Converter.RtmpToHttp(this.videoPlayUrl);
    }
}
