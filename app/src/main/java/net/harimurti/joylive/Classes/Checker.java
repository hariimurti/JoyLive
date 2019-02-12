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
    private String text;

    public void link(String url) {
        this.url = url;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void into(ImageButton imageButton) {
        this.imageButton = imageButton;
    }

    public void into(TextView textView) {
        this.textView = textView;
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
            textView.setText(text);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        boolean bResponse = result;

        if (imageButton != null)
            imageButton.setEnabled(bResponse);

        if (textView != null)
            textView.setText(bResponse ? "Online" : "Offline");
    }
}
