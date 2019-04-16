package net.harimurti.joylive.Classes;

import android.util.Base64;

import net.harimurti.joylive.JsonData.User;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

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

    public static String GetIdFromLink(String link) {
        String pattern = "live/(\\d+)/?";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(link);

        if (m.find())
            return m.group(1);
        else
            return "0";
    }

    public static String LinkToWebpage(String link) {
        String pattern = "/(\\d+)/?";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(link);

        if (m.find())
            return "http://m.joylive.tv/" + m.group(1);
        else
            return link;
    }

    public static User LinkToUser(String link) {
        String pattern = "^https?://(.*live/(\\d+))/playlist.m3u8";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(link);

        if (m.find()) {
            String rtmp = "rtmp://" + m.group(1);
            String id = m.group(2);

            return new User(id, "NoName", "", "NoStatus", rtmp);
        }
        else {
            return new User("0", "NoName", "", "NoStatus", HttpToRtmp(link));
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

    public static String DecodeKey(String encrypted) {
        try {
            byte[] decodedValue = Base64.decode(encrypted.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = "2019032662309102".getBytes(StandardCharsets.UTF_8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            char[] password = "C0L1-T3RU5-S4MP3-P3G3L".toCharArray();
            byte[] salt = "CR4CK3R?FUCK-Y0U-KAL14N".getBytes(StandardCharsets.UTF_8);
            KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
            SecretKey tmp = factory.generateSecret(spec);
            byte[] encoded = tmp.getEncoded();
            Key key = new SecretKeySpec(encoded, "AES");
            c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] decValue = c.doFinal(decodedValue);
            return new String(decValue);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
