package net.harimurti.joylive.Classes;

import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;

public class Checker extends AsyncTask<Void, Void, Boolean> {
    private ImageButton imageButton;
    private TextView textView;
    private String url;
    private String checking = "Checking...";
    private String online = "Online";
    private String offline = "Offline";

    public Checker() {}

    public Checker link(String url) {
        this.url = url;
        return this;
    }

    public Checker checkingText(String checking) {
        this.checking = checking;
        return this;
    }

    public Checker onlineText(String online) {
        this.online = online;
        return this;
    }

    public Checker offlineText(String offline) {
        this.offline = offline;
        return this;
    }

    public Checker into(ImageButton imageButton) {
        this.imageButton = imageButton;
        return this;
    }

    public Checker into(TextView textView) {
        this.textView = textView;
        return this;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            //con.setRequestMethod("HEAD");
            con.setConnectTimeout(500);
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPreExecute() {
        if (imageButton != null)
            imageButton.setEnabled(false);

        if (textView != null)
            textView.setText(checking);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        boolean bResponse = result;

        if (imageButton != null)
            imageButton.setEnabled(bResponse);

        if (textView != null)
            textView.setText(bResponse ? online : offline);
    }
}
