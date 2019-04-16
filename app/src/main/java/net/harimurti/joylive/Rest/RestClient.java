package net.harimurti.joylive.Rest;

import android.util.Log;

import com.google.gson.Gson;

import net.harimurti.joylive.App;
import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.JsonData.JoyBase;
import net.harimurti.joylive.JsonData.JoyRoom;
import net.harimurti.joylive.JsonData.JoyUser;
import net.harimurti.joylive.JsonData.JoyRooms;
import net.harimurti.joylive.JsonData.Room;
import net.harimurti.joylive.JsonData.User;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RestClient {
    private OkHttpClient client = RestBuilder.client;
    public static boolean isLoggedIn = false;
    private static boolean inProgress = false;
    private static int currentPage = 0;

    private OnMyInfoListener onMyInfoListener = null;
    private OnUserInfoListener onUserInfoListener = null;
    private OnRoomListener onRoomListener = null;
    private OnRoomsListener onRoomsListener = null;

    public interface OnMyInfoListener {
        default void onError(String status){}
        default void onLoginRequired(){}
        default void onProgress(String status){}
        default void onSuccess(){}
    }

    public interface OnUserInfoListener {
        default void onError(){}
        default void onSuccess(User result){}
    }

    public interface OnRoomListener {
        default void onError(){}
        default void onLoginRequired(){}
        default void onSuccess(Room result){}
    }

    public interface OnRoomsListener {
        default void onError(){}
        default void onLoginRequired(){}
        default void onSuccess(User[] result){}
    }

    public void setOnMyInfoListener(OnMyInfoListener onMyInfoListener) {
        this.onMyInfoListener = onMyInfoListener;
    }

    public void setOnUserInfoListener(OnUserInfoListener onUserInfoListener) {
        this.onUserInfoListener = onUserInfoListener;
    }

    public void setOnRoomListener(OnRoomListener onRoomListener) {
        this.onRoomListener = onRoomListener;
    }

    public void setOnRoomsListener(OnRoomsListener onRoomsListener) {
        this.onRoomsListener = onRoomsListener;
    }

    public void login() {
        final String TAG = "UserLogin";
        onMyInfoListener.onProgress("Try Login...");

        RequestBody body = RestBuilder.body
                .addFormDataPart("remember", "true")
                .addFormDataPart("username", App.GogoUsername)
                .addFormDataPart("password", App.GogoPassword)
                .build();
        Request request = RestBuilder.request
                .url("http://app.joylive.tv/user/login")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage());
                onMyInfoListener.onError("Something wrong!");
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String jsonRaw = response.body().string();
                    Log.i(TAG, jsonRaw);

                    Gson gson = new Gson();
                    JoyBase obj = gson.fromJson(jsonRaw, JoyBase.class);
                    if (obj.errno == 0) {
                        isLoggedIn = true;
                        Log.i(TAG, "Status: Logged In");
                        onMyInfoListener.onSuccess();
                    }
                    else {
                        Log.e(TAG, obj.msg);
                        onMyInfoListener.onError("Can't login! Try again later.");
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    onMyInfoListener.onError("Something wrong!");
                }
            }
        });
    }

    public void getMyInfo() {
        final String TAG = "GetMyInfo";
        onMyInfoListener.onProgress("Gathering information. Please wait...");

        RequestBody body = RestBuilder.body.build();
        Request request = RestBuilder.request
                .url("http://app.joylive.tv/user/getMyInfo")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage());
                onMyInfoListener.onError("Something wrong!");
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String jsonRaw = response.body().string();
                    Log.i(TAG, jsonRaw);

                    Gson gson = new Gson();
                    JoyBase obj = gson.fromJson(jsonRaw, JoyBase.class);
                    if (obj.errno == 0) {
                        isLoggedIn = true;
                        Log.i(TAG, "Status: Logged In");
                        onMyInfoListener.onSuccess();
                    }
                    else {
                        Log.e(TAG, obj.msg);
                        onMyInfoListener.onLoginRequired();
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    onMyInfoListener.onError("Something wrong!");
                }
            }
        });
    }

    public void getUserInfo(String id) {
        final String TAG = "UserLogin";
        RequestBody body = RestBuilder.body.build();
        Request request = RestBuilder.request
                .url("http://app.joylive.tv/user/GetUserInfo?uid=" + id)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage());
                onUserInfoListener.onError();
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String jsonRaw = response.body().string();
                    Log.i(TAG, jsonRaw);

                    Gson gson = new Gson();
                    JoyUser obj = gson.fromJson(jsonRaw, JoyUser.class);
                    if (obj.errno == 0) {
                        onUserInfoListener.onSuccess(obj.data);
                        return;
                    }

                    Log.e(TAG, obj.msg);
                    onUserInfoListener.onError();
                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    onUserInfoListener.onError();
                }
            }
        });
    }

    public void getRoomInfo(String rid) {
        final String TAG = "GetRoomInfo";

        RequestBody body = RestBuilder.body.build();
        Request request = RestBuilder.request
                .url("http://app.joylive.tv/room/GetInfo?rid=" + rid)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage());
                onRoomListener.onError();
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String jsonRaw = response.body().string();
                    Log.i(TAG, jsonRaw);

                    Gson gson = new Gson();
                    JoyRoom obj = gson.fromJson(jsonRaw, JoyRoom.class);
                    if (obj.errno == 0) {
                        onRoomListener.onSuccess(obj.data);
                        return;
                    }

                    Log.e(TAG, obj.msg);
                    onRoomListener.onLoginRequired();
                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    onRoomListener.onError();
                }
            }
        });
    }

    public void getRooms(boolean reset) {
        final String TAG = "GetRooms";
        if (inProgress) return;
        inProgress = true;

        if (reset) currentPage = 0;

        String targetUrl = "http://app.joylive.tv/room/getRooms?currentPage=" + currentPage + "&status=1";
        if (currentPage >= 4) {
            targetUrl = "http://app.joylive.tv/room/getRooms?currentPage=" + (currentPage - 4) + "&status=0&limit=30";
            if(currentPage > 6) {
                Notification.Toast("Ups... Can't get more than that!");
                inProgress = false;
                return;
            }
        }

        Log.i(TAG, "Page: " + currentPage);
        Notification.Toast("Getting more users");

        RequestBody body = RestBuilder.body.build();
        Request request = RestBuilder.request
                .url(targetUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage());
                inProgress = false;
                onRoomsListener.onError();
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    inProgress = false;
                    String jsonRaw = response.body().string();
                    Log.i(TAG, jsonRaw);

                    Gson gson = new Gson();
                    JoyRooms obj = gson.fromJson(jsonRaw, JoyRooms.class);

                    if (obj.errno == -100) {
                        Log.e(TAG, obj.msg);
                        onRoomsListener.onLoginRequired();
                        return;
                    }

                    currentPage++;
                    onRoomsListener.onSuccess(obj.data);
                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    onRoomsListener.onError();
                }
            }
        });
    }
}
