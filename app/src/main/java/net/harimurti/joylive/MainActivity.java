package net.harimurti.joylive;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

import net.harimurti.joylive.Classes.Preferences;
import net.harimurti.joylive.JsonClass.JsonRoom;
import net.harimurti.joylive.Classes.Menu;
import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.JsonClass.User;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private Activity activity;
    private boolean doubleBackToExitPressedOnce;
    private SwipeRefreshLayout swipeRefresh;
    private static ArrayList<User> listUser = new ArrayList<>();
    private static MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);

        activity = this;
        mainAdapter = new MainAdapter(this, listUser);
        final ListView listView = findViewById(R.id.list_content);
        listView.setAdapter(mainAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int amountVisible, int totalItems) {
                //leave me alone
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() -
                        listView.getFooterViewsCount()) >= (mainAdapter.getCount() - 1)) {

                    //listView has hit the bottom
                    GetNextRooms();
                }
            }
        });

        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ResetList();
            }
        });

        //load users for first time
        GetRooms(1);
    }

    private void ResetList() {
        listUser.clear();
        GetRooms(1);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AuthDialog().execute(this);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Intent intent = new Intent(this, FavoriteActivity.class);
                startActivity(intent);
                break;

            case R.id.action_reset:
                ResetList();
                break;

            case R.id.action_about:
                Menu.About(this);
                break;

            case R.id.action_exit:
                Menu.Exit(this);
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Menu.Exit(this);
        }
        else {
            Notification.Toast("Aduh, kok udahan si om??\nPencet sekali lagi dong ahh..");
        }

        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private int currentPage = 0;
    private boolean isWorking = false;
    private void GetRooms(int page) {
        if (isWorking) return;
        if (page == 0) page = 1;

        isWorking = true;
        currentPage = page;

        String sPage = Integer.toString(page);
        Notification.Toast("Loading page " + sPage + " ...");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("page", sPage)
                .build();
        Request request = new Request.Builder()
                .url("http://m.joylive.tv/index/getRoomInfo")
                .post(body)
                .addHeader("Host", "m.joylive.tv")
                .addHeader("Connection","keep-alive")
                .addHeader("Accept", "application/json")
                .addHeader("Origin", "http://m.joylive.tv")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("User-Agent", App.MobileAgent)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Referer", "http://m.joylive.tv/")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "en,id;q=0.9")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("GetRooms", e.getMessage());
                Notification.Toast("Something went wrong! Try again later.");

                isWorking = false;
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        Log.e("GetRooms","Unexpected code " + response);
                        Notification.Toast("Unexpected code " + response);
                        return;
                    }

                    Gson gson = new Gson();
                    JsonRoom joyObject = gson.fromJson(responseBody.string(), JsonRoom.class);
                    User[] users = joyObject.data.rooms;

                    int count = 0;
                    for (User user: users) {
                        // show female & unknown, skip male
                        if (user.sex.equals("1")) continue;

                        if (!User.isContainInList(listUser, user)) {
                            listUser.add(user);
                            count++;
                        }
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainAdapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                        }
                    });

                    Log.d("GetRooms", "Found " + Integer.toString(count) + " new users");
                    Notification.Toast("Found " + Integer.toString(count) + " new users");
                }
                catch (IOException e) {
                    Log.e("GetRooms", e.getMessage());
                    Notification.Toast("Something went wrong! Try again later.");
                }

                isWorking = false;
            }
        });
    }

    private void GetNextRooms() {
        if (isWorking) return;

        currentPage++;
        GetRooms(currentPage);
    }
}
