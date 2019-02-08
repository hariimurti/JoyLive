package net.harimurti.joylive;
import android.util.Log;

import java.util.regex.*;

public class UserInfo {

    private String Image;
    private String Nickname;
    private String Status;
    private String Link;
    private boolean Favorite;
    private int Viewer;

    public UserInfo(String Image, String Nickname, String Status, String Link) {
        this.Image = Image;
        this.Nickname = Nickname;
        this.Status = Status;
        this.Link = Link.replace("\\/", "/");
        this.Viewer = 0;
    }

    public UserInfo(String Image, String Nickname, String Status, String Link, int Viewer) {
        this.Image = Image;
        this.Nickname = Nickname;
        this.Status = Status;
        this.Link = Link.replace("\\/", "/");
        this.Viewer = Viewer;
    }

    public UserInfo(String Image, String Nickname, String Status, String Link, int Viewer, boolean Favorite) {
        this.Image = Image;
        this.Nickname = Nickname;
        this.Status = Status;
        this.Link = Link.replace("\\/", "/");
        this.Viewer = Viewer;
        this.Favorite = Favorite;
    }

    public String getImage() {
        return this.Image;
    }

    public String getNickname() {
        if (this.Nickname.isEmpty())
            return "NoName";

        return this.Nickname;
    }

    public String getStatus() {
        if (this.Nickname.isEmpty())
            return "NoStatus";

        return this.Status;
    }

    public String getLinkStream() {
        return this.Link;
    }

    public String getLinkShare() {

        Log.d("LINK", this.Link);
        String pattern = "^rtmp://(.*)";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(this.Link);

        if (m.find()) {
            return "http://" + m.group(1) + "/playlist.m3u8";
        }else {
            return this.Link;
        }
    }

    public boolean isFavorite() {
        return this.Favorite;
    }

    public String getViewer() {
        if (this.Viewer < 1000)
            return Integer.toString(this.Viewer);

        int exp = (int) (Math.log(this.Viewer) / Math.log(1000));
        String pre = ("KMGTPE").charAt(exp-1) + "";
        return String.format("%.1f%s", this.Viewer / Math.pow(1000, exp), pre);
    }
}
