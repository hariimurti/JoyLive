package net.harimurti.joylive.JsonData;

public class User {
    String mid;
    String nickname;
    String sex;
    String headPic;
    String announcement;
    String videoPlayUrl;

    public User(String mid, String nickname, String sex, String headPic, String announcement, String videoPlayUrl) {
        this.mid = mid;
        this.nickname = nickname;
        this.sex = sex;
        this.headPic = headPic;
        this.announcement = announcement;
        this.videoPlayUrl = videoPlayUrl;
    }

    public String getMid() {
        return this.mid;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getSex() {
        return this.sex;
    }

    public String getHeadPic() {
        return this.headPic;
    }

    public String getAnnouncement() {
        return this.announcement;
    }

    public String getVideoPlayUrl() {
        return this.videoPlayUrl;
    }
}
