package net.harimurti.joylive.JsonData;

import net.harimurti.joylive.Classes.Converter;

import static java.lang.System.currentTimeMillis;

public class UserFav {
    public String id;
    public String nickname;
    public String picture;
    public long lastSeen;

    public UserFav convertFrom(User user) {
        id = user.getId();
        nickname = user.nickname;
        picture = user.headPic;
        lastSeen = user.playStartTime;
        return this;
    }

    public String getLastSeen() {
        return Converter.TimestampToHumanDate(this.lastSeen);
    }

    public String setLastSeenNow() {
        this.lastSeen = currentTimeMillis()/1000;
        return Converter.TimestampToHumanDate(this.lastSeen);
    }
}
