package net.harimurti.joylive;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import net.harimurti.joylive.JsonData.JoyLive;
import net.harimurti.joylive.JsonData.User;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class JoyLiveApi {
    private static final String TAG = "API";
    private static Context context;
    private static int currentpage = 0;
    private static boolean working = false;

    public static void GetMoreUsers() {
        if (working) return;

        currentpage++;
        GetUsers(currentpage);
    }

    public static void GetUsers(int page) {
        if (working) return;
        if (page == 0) page = 1;

        working = true;
        context = MainActivity.getContext();
        currentpage = page;

        String spage = Integer.toString(page);
        Log.d("API", "Loading page = " + spage);
        Notification.Toast("Loading page " + spage + " ...");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("page", spage)
                .build();
        Request request = new Request.Builder()
                .url("http://m.joylive.tv/index/getRoomInfo")
                .post(body)
                .addHeader("Host","m.joylive.tv")
                .addHeader("Connection","keep-alive")
                .addHeader("Accept","application/json")
                .addHeader("Origin","http://m.joylive.tv")
                .addHeader("X-Requested-With","XMLHttpRequest")
                .addHeader("User-Agent","Mozilla/5.0 (Linux; Android 8.0.0; Pixel) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.80 Mobile Safari/537.36")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("Referer","http://m.joylive.tv/")
                .addHeader("Accept-Encoding","gzip, deflate")
                .addHeader("Accept-Language","en,id;q=0.9")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Request Failure", e);
                //Notification.Toast("Something went wrong! Try again later.");

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
                    JoyLive joyObject = gson.fromJson(responseBody.string(), JoyLive.class);
                    User[] users = joyObject.getData().getRooms();

                    int count = 0;
                    for (User user: users) {
                        if (!user.getSex().equals("2")) continue;

                        count++;
                        UserInfo userInfo = new UserInfo(user.getHeadPic(), user.getNickname(), user.getAnnouncement(), user.getVideoPlayUrl());
                        MainActivity.AddUser(userInfo);
                    }

                    Log.d(TAG, "Found " + Integer.toString(count) + " new users");

                    LocalBroadcastManager.getInstance(MainActivity.getContext())
                            .sendBroadcast(new Intent("RefreshAdapter"));

                    //Notification.Toast("Found " + Integer.toString(count) + " new users");
                }
                catch (IOException e) {
                    Log.e(TAG, "Response Error", e);
                    //Notification.Toast("Something went wrong! Try again later.");
                }

                working = false;
            }
        });
    }
}
