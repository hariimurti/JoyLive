package net.harimurti.joylive.Rest;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import net.harimurti.joylive.App;

import okhttp3.CookieJar;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RestBuilder {
    private static CookieJar cookieJar = new PersistentCookieJar(
            new SetCookieCache(),
            new SharedPrefsCookiePersistor(App.getContext())
    );

    public static OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .cookieJar(cookieJar)
            .build();

    public static MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("androidVersion", App.AndroidVersion)
            .addFormDataPart("packageId", "3")
            .addFormDataPart("channel", "developer-default")
            .addFormDataPart("version", App.GogoLiveVersion)
            .addFormDataPart("deviceName", App.DeviceName)
            .addFormDataPart("platform", "android");

    public static Request.Builder request = new Request.Builder()
            .addHeader("Host", "app.joylive.tv")
            .addHeader("Connection", "keep-alive")
            .addHeader("User-Agent", App.GogoLiveAgent)
            .addHeader("Content-Type", "application/x-www-form-urlencoded");
}
