package net.harimurti.joylive.Classes;

import net.harimurti.joylive.Api.JoyLive;
import net.harimurti.joylive.Api.JoyUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {

    public static String LongNumberToHumanReadable(long number) {
        if (number < 1000)
            return Long.toString(number);

        int exp = (int) (Math.log(number) / Math.log(1000));
        double _number = number / Math.pow(1000, exp);
        String pre = Character.toString(("KMGTPE").charAt(exp-1));
        return String.format(Locale.getDefault(), "%.1f%s", _number, pre);
    }

    public static String RtmpToHttp(String link) {
        String pattern = "^rtmp://(.*)";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(link);

        if (m.find())
            return "http://" + m.group(1) + "/playlist.m3u8";
        else
            return link;
    }

    public static String LinkToWebpage(String link) {
        String pattern = "(/\\d+)/?";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(link);

        if (m.find())
            return JoyLive.Website + m.group(1);
        else
            return link;
    }

    public static JoyUser LinkToUser(String link) {
        String pattern = "^https?://(.*/(\\d+))/playlist.m3u8";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(link);

        if (m.find()) {
            String rtmp = "rtmp://" + m.group(1);
            String id = m.group(2);

            return new JoyUser(id, "NoName", "", "NoStatus", rtmp);
        }
        else {
            return new JoyUser("0", "NoName", "", "NoStatus", HttpToRtmp(link));
        }
    }

    public static String HttpToRtmp(String link) {
        String pattern = "^https?://(.*)/playlist.m3u8";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(link);

        if (m.find())
            return "rtmp://" + m.group(1);
        else
            return link;
    }

    public static String TimestampToHumanDate(long epoch) {
        return new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
                .format(new Date(epoch*1000));
    }
}
