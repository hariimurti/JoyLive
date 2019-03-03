package net.harimurti.joylive.Api;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import net.harimurti.joylive.App;
import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.MainActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class JoyLive {
    private static final String TAG = "API";
    private static final String Host = "m.joylive.tv";
    public static final String Website = "http://" + Host;
    public static final String UserAgent = "Mozilla/5.0 (Linux; Android 8.0.0; Pixel) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.80 Mobile Safari/537.36";
    private static int currentPage = 0;
    private static boolean working = false;

    public static void GetMoreUsers() {
        if (working) return;

        currentPage++;
        GetUsers(currentPage);
    }

    public static void GetUsers(int page) {
        if (working) return;
        if (page == 0) page = 1;

        working = true;
        currentPage = page;

        String sPage = Integer.toString(page);
        Log.d("API", "Loading page = " + sPage);
        Notification.Toast("Loading page " + sPage + " ...");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("page", sPage)
                .build();
        Request request = new Request.Builder()
                .url(Website + "/index/getRoomInfo")
                .post(body)
                .addHeader("Host", Host)
                .addHeader("Connection","keep-alive")
                .addHeader("Accept", "application/json")
                .addHeader("Origin", Website)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("User-Agent", UserAgent)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Referer", Website + "/")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "en,id;q=0.9")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Request Failure", e);
                Notification.Toast("Something went wrong! Try again later.");

                working = false;
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        Log.e(TAG,"Unexpected code " + response);
                        Notification.Toast("Unexpected code " + response);
                        return;
                    }

                    Gson gson = new Gson();
                    JsonRoom joyObject = gson.fromJson(responseBody.string(), JsonRoom.class);
                    JoyUser[] users = joyObject.getData().getRooms();

                    int count = 0;
                    for (JoyUser user: users) {
                        // only girls
                        if (!user.getSex().equals("2")) continue;

                        if (MainActivity.AddUser(user)) count++;
                    }

                    Log.d(TAG, "Found " + Integer.toString(count) + " new girls");

                    LocalBroadcastManager.getInstance(App.getContext())
                            .sendBroadcast(new Intent("RefreshAdapter"));

                    Notification.Toast("Found " + Integer.toString(count) + " new girls");
                }
                catch (IOException e) {
                    Log.e(TAG, "Response Error", e);
                    Notification.Toast("Something went wrong! Try again later.");
                }

                working = false;
            }
        });
    }
}
